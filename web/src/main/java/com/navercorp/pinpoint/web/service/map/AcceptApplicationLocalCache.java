/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.service.map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author emeroad
 */
public class AcceptApplicationLocalCache {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private boolean isDebug = logger.isDebugEnabled();

    private final Map<RpcApplication, Set<AcceptApplication>> acceptApplicationLocalCache = new HashMap<RpcApplication, Set<AcceptApplication>>();

    public Set<AcceptApplication> get(RpcApplication findKey) {
        final Set<AcceptApplication> hit = this.acceptApplicationLocalCache.get(findKey);
        if (hit != null) {
            if (isDebug) {
                logger.debug("acceptApplicationLocalCache hit {}:{}", findKey, hit);
            }
            return hit;
        }
        if (isDebug) {
            logger.debug("acceptApplicationLocalCache miss {}", findKey);
        }
        return Collections.emptySet();
    }


    public void put(RpcApplication findKey, Set<AcceptApplication> acceptApplicationSet) {
        if (CollectionUtils.isEmpty(acceptApplicationSet)) {
            // 비어 있는 값에 대해서도 생성해야 함.
            this.acceptApplicationLocalCache.put(findKey, acceptApplicationSet);
            return;
        }
        logger.debug("findAcceptApplication:{}", acceptApplicationSet);
        // build cache
        // url 별로 AcceptApplicationData를 모은다.
        for (AcceptApplication acceptApplication : acceptApplicationSet) {
            // acceptApplicationSet 데이터는 받은 url과 accept node의 applicationName을 저장하고 있음.
            // 호출 application과 url을 기준으로 조회 키를 다시 생성해야 한다.
            RpcApplication newKey = new RpcApplication(acceptApplication.getHost(), findKey.getApplication());
            Set<AcceptApplication> findSet = this.acceptApplicationLocalCache.get(newKey);
            if (findSet == null) {
                findSet = new HashSet<AcceptApplication>();
                this.acceptApplicationLocalCache.put(newKey, findSet);
            }
            findSet.add(acceptApplication);
        }
    }
}
