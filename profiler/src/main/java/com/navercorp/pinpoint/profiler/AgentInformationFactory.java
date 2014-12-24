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

package com.navercorp.pinpoint.profiler;

import com.navercorp.pinpoint.bootstrap.util.NetworkUtils;
import com.navercorp.pinpoint.common.PinpointConstants;
import com.navercorp.pinpoint.common.ServiceType;
import com.navercorp.pinpoint.common.Version;
import com.navercorp.pinpoint.common.util.BytesUtils;
import com.navercorp.pinpoint.profiler.util.RuntimeMXBeanUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author emeroad
 */
public class AgentInformationFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AgentInformationFactory() {
    }

    public AgentInformation createAgentInformation(ServiceType serverType) {
        if (serverType == null) {
            throw new NullPointerException("serverType must not be null");
        }
        // TODO 일단 임시로 호환성을 위해 agentid에 machinename을 넣도록 하자
        // TODO 박스 하나에 서버 인스턴스를 여러개 실행할 때에 문제가 될 수 있음.
        final String machineName = NetworkUtils.getHostName();
        final String hostIp = NetworkUtils.getHostIp();
        final String agentId = getId("pinpoint.agentId", machineName, PinpointConstants.AGENT_NAME_MAX_LEN);
        final String applicationName = getId("pinpoint.applicationName", "UnknownApplicationName", PinpointConstants.APPLICATION_NAME_MAX_LEN);
        final long startTime = RuntimeMXBeanUtils.getVmStartTime();
        final int pid = RuntimeMXBeanUtils.getPid();
        return new AgentInformation(agentId, applicationName, startTime, pid, machineName, hostIp, serverType.getCode(), Version.VERSION);
    }

    private String getId(String key, String defaultValue, int maxlen) {
        String value = System.getProperty(key, defaultValue);
        validateId(value, key, maxlen);
        return value;
    }

    private void validateId(String id, String idName, int maxlen) {
		if (id == null) {
			throw new NullPointerException("id must not be null");
		}
        // 에러 체크 로직을 bootclass 앞단으로 이동시켜야 함.
        // 아니면 여기서 체크해서 실패시 agent동작을 하지 않도록 하던가 하는 추가 동작을 해야함.
        final byte[] bytes = BytesUtils.toBytes(id);
		if (bytes.length > maxlen) {
            logger.warn("{} is too long(1~24). value={}", idName, id);
        }
    }
}
