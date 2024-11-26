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

package com.alibaba.fluss.server.coordinator.event;

import com.alibaba.fluss.rpc.messages.NotifyLeaderAndIsrRequest;
import com.alibaba.fluss.server.entity.NotifyLeaderAndIsrResultForBucket;

import java.util.List;

/** An event for receive the response of {@link NotifyLeaderAndIsrRequest} from tablet server. */
public class NotifyLeaderAndIsrResponseReceivedEvent implements CoordinatorEvent {

    private final List<NotifyLeaderAndIsrResultForBucket> notifyLeaderAndIsrResultForBuckets;

    // the server id that return the response
    private final int responseServerId;

    public NotifyLeaderAndIsrResponseReceivedEvent(
            List<NotifyLeaderAndIsrResultForBucket> notifyLeaderAndIsrResultForBuckets,
            int responseServerId) {
        this.notifyLeaderAndIsrResultForBuckets = notifyLeaderAndIsrResultForBuckets;
        this.responseServerId = responseServerId;
    }

    public int getResponseServerId() {
        return responseServerId;
    }

    public List<NotifyLeaderAndIsrResultForBucket> getNotifyLeaderAndIsrResultForBuckets() {
        return notifyLeaderAndIsrResultForBuckets;
    }
}