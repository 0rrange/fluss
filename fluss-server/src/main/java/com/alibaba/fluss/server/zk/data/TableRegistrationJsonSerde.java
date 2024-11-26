/*
 * Copyright (c) 2024 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.fluss.server.zk.data;

import com.alibaba.fluss.annotation.Internal;
import com.alibaba.fluss.metadata.TableDescriptor.TableDistribution;
import com.alibaba.fluss.shaded.jackson2.com.fasterxml.jackson.core.JsonGenerator;
import com.alibaba.fluss.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import com.alibaba.fluss.utils.json.JsonDeserializer;
import com.alibaba.fluss.utils.json.JsonSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Json serializer and deserializer for {@link TableRegistration}. */
@Internal
public class TableRegistrationJsonSerde
        implements JsonSerializer<TableRegistration>, JsonDeserializer<TableRegistration> {

    public static final TableRegistrationJsonSerde INSTANCE = new TableRegistrationJsonSerde();

    static final String TABLE_ID_NAME = "table_id";
    static final String COMMENT_NAME = "comment";
    static final String PARTITION_KEY_NAME = "partition_key";
    static final String BUCKET_KEY_NAME = "bucket_key";
    static final String BUCKET_COUNT_NAME = "bucket_count";
    static final String PROPERTIES_NAME = "properties";
    static final String CUSTOM_PROPERTIES_NAME = "custom_properties";

    private static final String VERSION_KEY = "version";
    private static final int VERSION = 1;

    @Override
    public void serialize(TableRegistration tableReg, JsonGenerator generator) throws IOException {
        generator.writeStartObject();

        // serialize data version.
        generator.writeNumberField(VERSION_KEY, VERSION);

        // serialize table id
        generator.writeNumberField(TABLE_ID_NAME, tableReg.tableId);

        // serialize comment.
        if (tableReg.comment != null) {
            generator.writeStringField(COMMENT_NAME, tableReg.comment);
        }

        // serialize partition key.
        generator.writeArrayFieldStart(PARTITION_KEY_NAME);
        for (String partitionKey : tableReg.partitionKeys) {
            generator.writeString(partitionKey);
        }
        generator.writeEndArray();

        // serialize tableDistribution.
        TableDistribution distribution = tableReg.tableDistribution;
        if (distribution != null) {
            generator.writeArrayFieldStart(BUCKET_KEY_NAME);
            for (String bucketKey : distribution.getBucketKeys()) {
                generator.writeString(bucketKey);
            }
            generator.writeEndArray();
            if (distribution.getBucketCount().isPresent()) {
                generator.writeNumberField(BUCKET_COUNT_NAME, distribution.getBucketCount().get());
            }
        }

        // serialize properties.
        generator.writeObjectFieldStart(PROPERTIES_NAME);
        for (Map.Entry<String, String> entry : tableReg.properties.entrySet()) {
            generator.writeObjectField(entry.getKey(), entry.getValue());
        }
        generator.writeEndObject();

        // serialize custom properties.
        generator.writeObjectFieldStart(CUSTOM_PROPERTIES_NAME);
        for (Map.Entry<String, String> entry : tableReg.customProperties.entrySet()) {
            generator.writeObjectField(entry.getKey(), entry.getValue());
        }
        generator.writeEndObject();

        generator.writeEndObject();
    }

    @Override
    public TableRegistration deserialize(JsonNode node) {
        long tableId = node.get(TABLE_ID_NAME).asLong();

        JsonNode commentNode = node.get(COMMENT_NAME);
        String comment = null;
        if (commentNode != null) {
            comment = commentNode.asText();
        }

        Iterator<JsonNode> partitionJsons = node.get(PARTITION_KEY_NAME).elements();
        List<String> partitionKeys = new ArrayList<>();
        while (partitionJsons.hasNext()) {
            partitionKeys.add(partitionJsons.next().asText());
        }

        TableDistribution distribution = null;
        if (node.has(BUCKET_KEY_NAME) || node.has(BUCKET_COUNT_NAME)) {
            Iterator<JsonNode> bucketJsons = node.get(BUCKET_KEY_NAME).elements();
            List<String> bucketKeys = new ArrayList<>();
            while (bucketJsons.hasNext()) {
                bucketKeys.add(bucketJsons.next().asText());
            }

            JsonNode bucketCountNode = node.get(BUCKET_COUNT_NAME);
            if (bucketCountNode != null) {
                distribution = new TableDistribution(bucketCountNode.asInt(), bucketKeys);
            } else {
                distribution = new TableDistribution(null, bucketKeys);
            }
        }

        Map<String, String> properties = deserializeProperties(node.get(PROPERTIES_NAME));
        Map<String, String> customProperties =
                deserializeProperties(node.get(CUSTOM_PROPERTIES_NAME));

        return new TableRegistration(
                tableId, comment, partitionKeys, distribution, properties, customProperties);
    }

    private Map<String, String> deserializeProperties(JsonNode node) {
        HashMap<String, String> properties = new HashMap<>();
        Iterator<String> optionsKeys = node.fieldNames();
        while (optionsKeys.hasNext()) {
            String key = optionsKeys.next();
            properties.put(key, node.get(key).asText());
        }
        return properties;
    }
}