/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.common;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.rocketmq.remoting.protocol.RemotingSerializable;
/**
 * 封装数据版本类
 * @author yuyang
 * @date 2018年5月27日
 */
public class DataVersion extends RemotingSerializable {
    private long timestamp = System.currentTimeMillis();
    private AtomicLong counter = new AtomicLong(0);

    public void assignNewOne(final DataVersion dataVersion) {
        this.timestamp = dataVersion.timestamp;
        this.counter.set(dataVersion.counter.get());
    }
    /**
     * 获取下一个版本，同一个类，但是数据已经变了，时间戳和count
     *      
     * @return void      
     * @throws
     */
    public void nextVersion() {
        this.timestamp = System.currentTimeMillis();
        this.counter.incrementAndGet();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AtomicLong getCounter() {
        return counter;
    }

    public void setCounter(AtomicLong counter) {
        this.counter = counter;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final DataVersion that = (DataVersion) o;

        if (timestamp != that.timestamp) {
            return false;
        }

        if (counter != null && that.counter != null) {
            return counter.longValue() == that.counter.longValue();
        }

        return (null == counter) && (null == that.counter);
    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        if (null != counter) {
            long l = counter.get();
            result = 31 * result + (int) (l ^ (l >>> 32));
        }
        return result;
    }
}
