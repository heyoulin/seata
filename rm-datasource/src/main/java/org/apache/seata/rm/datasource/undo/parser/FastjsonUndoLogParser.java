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
package org.apache.seata.rm.datasource.undo.parser;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;
import com.alibaba.fastjson2.JSONWriter.Feature;
import org.apache.seata.common.Constants;
import org.apache.seata.common.executor.Initialize;
import org.apache.seata.common.loader.LoadLevel;
import org.apache.seata.rm.datasource.undo.BranchUndoLog;
import org.apache.seata.rm.datasource.undo.UndoLogParser;

/**
 * The type Json based undo log parser.
 *
 */
@LoadLevel(name = FastjsonUndoLogParser.NAME)
public class FastjsonUndoLogParser implements UndoLogParser, Initialize {

    public static final String NAME = "fastjson";

    private final SimplePropertyPreFilter filter = new SimplePropertyPreFilter();

    @Override
    public void init() {
        filter.getExcludes().add("tableMeta");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] getDefaultContent() {
        return "{}".getBytes(Constants.DEFAULT_CHARSET);
    }

    @Override
    public byte[] encode(BranchUndoLog branchUndoLog) {
        return JSON.toJSONBytes(branchUndoLog,new Filter[]{filter}, Feature.WriteClassName);
    }

    @Override
    public BranchUndoLog decode(byte[] bytes) {
        return JSON.parseObject(bytes, BranchUndoLog.class, JSONReader.Feature.SupportClassForName,JSONReader.Feature.SupportAutoType);
    }
}
