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

package com.alibaba.fluss.fs.token;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.Optional;

/** Container for obtained security token. */
public final class ObtainedSecurityToken {

    /** Serialized form of security token. */
    private final byte[] token;

    /** The scheme of filesystem the token is for. */
    private final String scheme;

    /** Additional information for accessing filesystem. */
    private final Map<String, String> additionInfos;

    /** Time until the tokens are valid, if valid forever, it should be null. */
    @Nullable private final Long validUntil;

    public ObtainedSecurityToken(
            String scheme,
            byte[] token,
            @Nullable Long validUntil,
            Map<String, String> additionInfos) {
        this.scheme = scheme;
        this.token = token;
        this.validUntil = validUntil;
        this.additionInfos = additionInfos;
    }

    public String getScheme() {
        return scheme;
    }

    public byte[] getToken() {
        return token;
    }

    public Optional<Long> getValidUntil() {
        return Optional.ofNullable(validUntil);
    }

    public Map<String, String> getAdditionInfos() {
        return additionInfos;
    }
}