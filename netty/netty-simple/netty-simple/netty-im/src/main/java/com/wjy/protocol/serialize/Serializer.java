/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.serialize;

import com.wjy.protocol.serialize.impl.JSONSerializer;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
public interface Serializer {

    /*
     * json 序列化
     */
    byte JSON_SERIALIZER = 1;

    Serializer DEFAULT = new JSONSerializer();

    /*
     * 获取序列化算法
     */
    byte getSerializerAlgorithm();

    /*
     * 序列化
     */
    byte[] serialize(Object object);

    /*
     * 反序列化
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
