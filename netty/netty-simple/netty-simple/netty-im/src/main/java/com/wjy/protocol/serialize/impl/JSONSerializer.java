/*
 * Copyright (c) Travelsky Corp.
 * All Rights Reserved.
 */
package com.wjy.protocol.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.wjy.protocol.serialize.Serializer;
import com.wjy.protocol.serialize.SerializerAlgorithm;

/**
 * @author wangjiayou 2019/7/1
 * @version ORAS v1.0
 */
public class JSONSerializer implements Serializer {
    @Override
    public byte getSerializerAlgorithm() {
        return SerializerAlgorithm.JSON;
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
