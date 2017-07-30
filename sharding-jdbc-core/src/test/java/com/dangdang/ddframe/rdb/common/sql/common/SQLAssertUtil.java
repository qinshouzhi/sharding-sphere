package com.dangdang.ddframe.rdb.common.sql.common;

import com.dangdang.ddframe.rdb.common.jaxb.SqlAssert;
import com.dangdang.ddframe.rdb.common.jaxb.SqlAsserts;
import com.dangdang.ddframe.rdb.common.sql.base.AbstractSqlAssertTest;
import com.dangdang.ddframe.rdb.sharding.constant.DatabaseType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SQLAssertUtil {
    
    public static Collection<Object[]> getDataParameters(final String assertFilePath) {
        Collection<Object[]> result = new ArrayList<>();
        URL url = AbstractSqlAssertTest.class.getClassLoader().getResource(assertFilePath);
        if (null == url) {
            return Collections.emptyList();
        }
        File filePath = new File(url.getPath());
        if (!filePath.exists()) {
            return Collections.emptyList();
        }
        File[] files = filePath.listFiles();
        if (null == files) {
            return Collections.emptyList();
        }
        for (File each : files) {
            result.addAll(dataParameters(each));
        }
        return result;
    }
    
    private static Collection<Object[]> dataParameters(final File file) {
        SqlAsserts asserts = loadSqlAsserts(file);
        Object[][] result = new Object[asserts.getSqlAsserts().size()][1];
        for (int i = 0; i < asserts.getSqlAsserts().size(); i++) {
            result[i] = getDataParameter(asserts.getSqlAsserts().get(i));
        }
        return Arrays.asList(result);
    }
    
    private static SqlAsserts loadSqlAsserts(final File file) {
        try {
            return (SqlAsserts) JAXBContext.newInstance(SqlAsserts.class).createUnmarshaller().unmarshal(file);
        } catch (final JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static Object[] getDataParameter(final SqlAssert sqlAssert) {
        final Object[] result = new Object[4];
        result[0] = sqlAssert.getId();
        result[1] = sqlAssert.getSql();
        if (null == sqlAssert.getTypes()) {
            result[2] = Collections.emptySet();
        } else {
            Set<DatabaseType> types = new HashSet<>();
            for (String each : sqlAssert.getTypes().split(",")) {
                types.add(DatabaseType.valueOf(each));
            }
            result[2] = types;
        }
        result[3] = sqlAssert.getSqlShardingRules();
        return result;
    }
}