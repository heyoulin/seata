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
package org.apache.seata.core.protocol;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The type Rpc message test.
 *
 */
public class RpcMessageTest {

    private static final String BODY_FIELD = "test_body";
    private static final int ID_FIELD = 100;
    private static final byte CODEC_FIELD = 1;
    private static final byte COMPRESS_FIELD = 2;
    private static final byte MSG_TYPE_FIELD = 3;
    private static final HashMap<String, String> HEAD_FIELD = new HashMap<>();

    /**
     * Test field get set from json.
     */
    @Test
    @Disabled
    public void testFieldGetSetFromJson() {
        String fromJson = "{\n" +
            "\t\"body\":\"" + BODY_FIELD + "\",\n" +
            "\t\"codec\":" + CODEC_FIELD + ",\n" +
            "\t\"compressor\":" + COMPRESS_FIELD + ",\n" +
            "\t\"headMap\":" + HEAD_FIELD + ",\n" +
            "\t\"id\":" + ID_FIELD + ",\n" +
            "\t\"messageType\":" + MSG_TYPE_FIELD + "\n" +
            "}";
        RpcMessage fromJsonMessage = JSON.parseObject(fromJson, RpcMessage.class);
        assertThat(fromJsonMessage.getBody()).isEqualTo(BODY_FIELD);
        assertThat(fromJsonMessage.getId()).isEqualTo(ID_FIELD);

        RpcMessage toJsonMessage = new RpcMessage();
        toJsonMessage.setBody(BODY_FIELD);
        toJsonMessage.setId(ID_FIELD);
        toJsonMessage.setMessageType(MSG_TYPE_FIELD);
        toJsonMessage.setCodec(CODEC_FIELD);
        toJsonMessage.setCompressor(COMPRESS_FIELD);
        toJsonMessage.setHeadMap(HEAD_FIELD);
        String toJson = JSON.toJSONString(toJsonMessage, JSONWriter.Feature.PrettyFormat);
        assertThat(fromJson).isEqualTo(toJson);
    }
}
