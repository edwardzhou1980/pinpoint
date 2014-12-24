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

package com.navercorp.pinpoint.web.util;

import com.navercorp.pinpoint.web.vo.Range;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 
 * @author netspider
 * 
 */
public class TimeWindow implements Iterable<Long> {

    private final long windowSlotSize;

    private final Range range;

    private final Range windowRange;

    public TimeWindow(Range range) {
        this(range, TimeWindowDownSampler.SAMPLER);
    }

    public TimeWindow(Range range, TimeWindowSampler sampler) {
        if (range == null) {
            throw new NullPointerException("range must not be null");
        }
        if (sampler == null) {
            throw new NullPointerException("sampler must not be null");
        }
        this.windowSlotSize = sampler.getWindowSize(range);
        this.range = range;
        this.windowRange = createWindowRange();
    }

    public Iterator<Long> iterator() {
        return new Itr();
    }

    /**
     * timestamp를 윈도우 사이즈에 맞는 timestamp로 변환.
     * 
     * @param timestamp
     * @return
     */
    public long refineTimestamp(long timestamp) {
        long time = (timestamp / windowSlotSize) * windowSlotSize;
        return time;
    }

    public Range getWindowRange() {
        return windowRange;
    }

    public long getWindowSlotSize() {
        return windowSlotSize;
    }

    public long getWindowRangeCount() {
        return (windowRange.getRange() / windowSlotSize) + 1;
    }

    private Range createWindowRange() {
        long from = refineTimestamp(range.getFrom());
        long to = refineTimestamp(range.getTo());
        return new Range(from, to);
    }

    public int getWindowIndex(long time) {
        long index = (time - windowRange.getFrom()) / this.windowSlotSize;
        return (int)index;
    }

    private class Itr implements Iterator<Long> {

        private long cursor;

        public Itr() {
            this.cursor = windowRange.getFrom();
        }

        @Override
        public boolean hasNext() {
            if (cursor > windowRange.getTo()) {
                return false;
            }
            return true;
        }

        @Override
        public Long next() {
            long current = cursor;
            if (hasNext()) {
                cursor += windowSlotSize;
                return current;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
