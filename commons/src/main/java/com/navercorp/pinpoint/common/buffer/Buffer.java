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

package com.navercorp.pinpoint.common.buffer;

/**
 * @author emeroad
 */
public interface Buffer {

    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static final byte[] EMPTY = new byte[0];

    public static final String UTF8 = "UTF-8";

    void putPadBytes(byte[] bytes, int totalLength);

    void putPrefixedBytes(byte[] bytes);

    void put2PrefixedBytes(byte[] bytes);

    void put4PrefixedBytes(byte[] bytes);

    void putPadString(String string, int totalLength);

    void putPrefixedString(String string);

    void put2PrefixedString(String string);

    void put4PrefixedString(String string);

    void put(byte v);

    void put(boolean v);

    void put(int v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수값에 강한 인코딩을 한다.
     * 음수값이 들어갈 경우 사이즈가 fixint 인코딩 보다 더 커짐, 음수값의 분포가 많을 경우 매우 비효율적임.
     * 이 경우 putSVar를 사용한다. putSVar에 비해서 zigzag연산이 없어 cpu를 약간 덜사용하는 이점 뿐이 없음.
     * 음수가 조금이라도 들어갈 가능성이 있다면 putSVar를 사용하는 것이 이득이다..
     * 1~10 byte사용
     * max : 5, min 10
     * @param v
     */
    void putVar(int v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수, 음수의 분포가 동일한 데이터 일 경우 사용한다.
     * 1~5 사용
     * max : 5, min :5
     * @param v
     */
    void putSVar(int v);

    void put(short v);

    void put(long v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수값에 강한 인코딩을 한다.
     * 음수값이 들어갈 경우 사이즈가 fixint 인코딩 보다 더 커짐
     * 이경우 putSVar를 사용한다.
     * @param v
     */
    void putVar(long v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수, 음수의 분포가 동일한 데이터 일 경우 사용한다.
     * @param v
     */
    void putSVar(long v);

    void put(double v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수값에 강한 인코딩을 한다.
     * 음수값이 들어갈 경우 사이즈가 fixint 인코딩 보다 더 커짐
     * 이경우 putSVar를 사용한다.
     * @param v
     */
    void putVar(double v);

    /**
     * 가변인코딩을 사용하여 저장한다.
     * 상수, 음수의 분포가 동일한 데이터 일 경우 사용한다.
     * @param v
     */
    void putSVar(double v);

    void put(byte[] v);

    byte readByte();

    int readUnsignedByte();

    boolean readBoolean();

    int readInt();

    int readVarInt();

    int readSVarInt();


    short readShort();

    long readLong();

    long readVarLong();

    long readSVarLong();

    double readDouble();

    double readVarDouble();

    double readSVarDouble();

    byte[] readPadBytes(int totalLength);

    String readPadString(int totalLength);

    String readPadStringAndRightTrim(int totalLength);

    byte[] readPrefixedBytes();

    byte[] read2PrefixedBytes();

    byte[] read4PrefixedBytes();

    String readPrefixedString();

    String read2PrefixedString();

    String read4PrefixedString();

    byte[] getBuffer();

    byte[] copyBuffer();

    byte[] getInternalBuffer();

    void setOffset(int offset);

    int getOffset();

    int limit();
}
