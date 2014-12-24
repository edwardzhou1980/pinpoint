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

package com.navercorp.pinpoint.profiler.monitor.metric;

import com.navercorp.pinpoint.common.ServiceType;
import com.navercorp.pinpoint.profiler.monitor.metric.DynamicAcceptHistogram;
import com.navercorp.pinpoint.profiler.monitor.metric.StaticAcceptHistogram;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WasStaticAcceptHistogramTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testLookUp() throws Exception {
        StaticAcceptHistogram table = new StaticAcceptHistogram();
        Assert.assertTrue(table.addResponseTime("abc", ServiceType.TOMCAT.getCode(), 1000));
        Assert.assertTrue(table.addResponseTime("abc", ServiceType.BLOC.getCode(), 1000));
        Assert.assertTrue(table.addResponseTime("abc", ServiceType.STAND_ALONE.getCode(), 1000));
        Assert.assertTrue(table.addResponseTime("abc", ServiceType.BLOC.getCode(), 1000));

        Assert.assertFalse(table.addResponseTime("abc", ServiceType.ARCUS.getCode(), 1000));
    }


    public void performanceTest () throws InterruptedException {
//        63519 static table
//        72350 dynamic table
//        로 static table 빠름.
        // InthashMap ConcurrentHashMap의 성능 차이 비교;
        // 차이는 미비한데. 이중구조로 할지 아니면. single data로 할지.
        // dynamic table의 경우 추가 ResponseKey객체도 추가로 생성함.
        final StaticAcceptHistogram table = new StaticAcceptHistogram();
        execute(new Runnable() {
            @Override
            public void run() {
                table.addResponseTime("test", ServiceType.TOMCAT.getCode(), 1000);
            }
        });

        final DynamicAcceptHistogram hashTable = new DynamicAcceptHistogram();
        execute(new Runnable() {
            @Override
            public void run() {
                hashTable.addResponseTime("test", ServiceType.TOMCAT.getCode(), 1000);
            }
        });

    }

    private void execute(final Runnable runnable) throws InterruptedException {
        long l = System.currentTimeMillis();
        ExecutorService executors = Executors.newFixedThreadPool(20);
        for(int x = 0; x< 100; x++) {
            final int count = 1000000;
            final CountDownLatch latch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                executors.execute(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                        latch.countDown();
                    }
                });
            }
            latch.await();
        }
        logger.debug("{}", System.currentTimeMillis() - l);
        executors.shutdown();
    }

}