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
package org.apache.seata.rm.datasource.undo;

import org.apache.seata.common.util.IOUtil;
import org.apache.seata.rm.datasource.ConnectionProxy;
import org.apache.seata.rm.datasource.DataSourceProxy;
import org.apache.seata.rm.datasource.DataSourceProxyTest;
import org.apache.seata.sqlparser.struct.ColumnMeta;
import org.apache.seata.rm.datasource.sql.struct.Field;
import org.apache.seata.rm.datasource.sql.struct.KeyType;
import org.apache.seata.rm.datasource.sql.struct.Row;
import org.apache.seata.sqlparser.struct.TableMeta;
import org.apache.seata.rm.datasource.sql.struct.TableRecords;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.store.fs.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;


public abstract class BaseH2Test {
    
    static BasicDataSource dataSource = null;

    static ConnectionProxy connection = null;

    static DataSourceProxy dataSourceProxy = null;

    static TableMeta tableMeta = null;
    
    @BeforeAll
    public static void start() throws SQLException {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:./db_store/test_undo");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSourceProxy = DataSourceProxyTest.getDataSourceProxy(dataSource);

        connection = dataSourceProxy.getConnection();

        tableMeta = mockTableMeta();
    }

    @AfterAll
    public static void stop() {
        IOUtil.close(connection);
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
            }
        }

        FileUtils.deleteRecursive("db_store", true);
    }

    @BeforeEach
    public void prepareTable() {
        execSQL("DROP TABLE IF EXISTS table_name");
        execSQL("CREATE TABLE table_name ( `id` int, `name` varchar(64), PRIMARY KEY (`id`))");
    }

    protected static void execSQL(String sql) {
        Statement s = null;
        try {
            s = connection.createStatement();
            s.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(s);
        }
    }

    protected static TableRecords execQuery(TableMeta tableMeta, String sql) throws SQLException {
        Statement s = null;
        ResultSet set = null;
        try {
            s = connection.createStatement();
            set = s.executeQuery(sql);
            return TableRecords.buildRecords(tableMeta, set);
        } finally {
            IOUtil.close(set, s);
        }
    }

    protected static TableMeta mockTableMeta() {
        TableMeta tableMeta = Mockito.mock(TableMeta.class);
        Mockito.when(tableMeta.getPrimaryKeyOnlyName()).thenReturn(Arrays.asList(new String[]{"ID"}));
        Mockito.when(tableMeta.getEscapePkNameList("h2")).thenReturn(Arrays.asList(new String[]{"ID"}));
        Mockito.when(tableMeta.getTableName()).thenReturn("table_name");
        ColumnMeta meta0 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta0.getDataType()).thenReturn(Types.INTEGER);
        Mockito.when(meta0.getColumnName()).thenReturn("ID");
        Mockito.when(tableMeta.getColumnMeta("ID")).thenReturn(meta0);
        ColumnMeta meta1 = Mockito.mock(ColumnMeta.class);
        Mockito.when(meta1.getDataType()).thenReturn(Types.VARCHAR);
        Mockito.when(meta1.getColumnName()).thenReturn("NAME");
        Mockito.when(tableMeta.getColumnMeta("NAME")).thenReturn(meta1);
        return tableMeta;
    }

    protected static Field addField(Row row, String name, int type, Object value) {
        Field field = new Field(name, type, value);
        if (name.equalsIgnoreCase("id")) {
            field.setKeyType(KeyType.PRIMARY_KEY);
        }
        row.add(field);
        return field;
    }
}
