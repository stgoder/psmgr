package fun.stgoder.psmgr.common.db;

import fun.stgoder.psmgr.common.db.annotation.col;
import fun.stgoder.psmgr.common.db.annotation.tbl;
import org.apache.commons.lang3.StringUtils;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* data source */
public class BaseDataSource {
    private DataSource dataSource;
    private Sql2o sql2o;

    public BaseDataSource() {
    }

    public BaseDataSource(DataSource dataSource) {
        init(dataSource);
    }

    public void init(DataSource dataSource) {
        this.dataSource = dataSource;
        this.sql2o = new Sql2o(dataSource);
    }

    public <T> List<T> select(String sql, Class<T> cls) {
        try (Connection conn = open()) {
            List<T> list = conn.createQuery(sql).executeAndFetch(cls);
            return list;
        }
    }

    public <T> List<T> select(String sql, Param param, Class<T> cls) {
        try (Connection conn = open()) {
            Query query = conn.createQuery(sql);
            for (Param.NameValue nv : param.params()) {
                query.addParameter(nv.getName(), nv.getValue());
            }
            List<T> list = query.executeAndFetch(cls);
            return list;
        }
    }

    public <T> List<T> select(Connection conn, String sql, Class<T> cls) {
        List<T> list = conn.createQuery(sql).executeAndFetch(cls);
        return list;
    }

    public <T> List<T> select(Connection conn, String sql, Param param, Class<T> cls) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        List<T> list = query.executeAndFetch(cls);
        return list;
    }

    public <T> Page<T> selectPage(String sql, Class<T> cls, int page, int pageSize) {
        try (Connection conn = open()) {
            List<T> datas = conn
                    .createQuery("SELECT * FROM ( " + sql + " ) tmp LIMIT " + (page - 1) * pageSize + "," + pageSize)
                    .executeAndFetch(cls);
            Long total = conn.createQuery("SELECT COUNT(1) FROM ( " + sql + " ) tmp").executeScalar(Long.class);
            Page<T> pageData = new Page<T>(page, pageSize, total, datas);
            return pageData;
        }
    }

    public <T> Page<T> selectPage(String sql, Param param, Class<T> cls, int page, int pageSize) {
        try (Connection conn = open()) {
            Query countQuery = conn.createQuery("SELECT COUNT(1) FROM ( " + sql + " ) tmp");
            Query dataQuery = conn
                    .createQuery("SELECT * FROM ( " + sql + " ) tmp2 LIMIT " + (page - 1) * pageSize + "," + pageSize);

            for (Param.NameValue nv : param.params()) {
                dataQuery.addParameter(nv.getName(), nv.getValue());
                countQuery.addParameter(nv.getName(), nv.getValue());
            }

            long total = countQuery.executeScalar(Long.class);
            List<T> datas = dataQuery.executeAndFetch(cls);

            Page<T> pageData = new Page<T>(page, pageSize, total, datas);
            return pageData;
        }
    }

    public <T> Page<T> selectPage(Connection conn, String sql, Param param, Class<T> cls, int page,
                                  int pageSize) {
        Query countQuery = conn.createQuery("SELECT COUNT(1) FROM ( " + sql + " ) tmp");
        Query dataQuery = conn
                .createQuery("SELECT * FROM ( " + sql + " ) tmp LIMIT " + (page - 1) * pageSize + "," + pageSize);

        for (Param.NameValue nv : param.params()) {
            dataQuery.addParameter(nv.getName(), nv.getValue());
            countQuery.addParameter(nv.getName(), nv.getValue());
        }

        long total = countQuery.executeScalar(Long.class);
        List<T> datas = dataQuery.executeAndFetch(cls);

        Page<T> pageData = new Page<T>(page, pageSize, total, datas);
        return pageData;
    }

    public <T> T selectOne(String sql, Class<T> cls) {
        try (Connection conn = open()) {
            T t = conn.createQuery(sql).executeAndFetchFirst(cls);
            return t;
        }
    }

    public <T> T selectOne(String sql, Param param, Class<T> cls) {
        try (Connection conn = open()) {
            Query query = conn.createQuery(sql);
            for (Param.NameValue nv : param.params()) {
                query.addParameter(nv.getName(), nv.getValue());
            }
            T t = query.executeAndFetchFirst(cls);
            return t;
        }
    }

    public <T> T selectOne(Connection conn, String sql, Class<T> cls) {
        T t = conn.createQuery(sql).executeAndFetchFirst(cls);
        return t;
    }

    public static <T> T selectOne(Connection conn, String sql, Param param, Class<T> cls) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        T t = query.executeAndFetchFirst(cls);
        return t;
    }

    public <T> T selectScalar(String sql, Class<T> cls) {
        try (Connection conn = open()) {
            T t = conn.createQuery(sql).executeScalar(cls);
            return t;
        }
    }

    public <T> T selectScalar(String sql, Param param, Class<T> cls) {
        try (Connection conn = open()) {
            Query query = conn.createQuery(sql);
            for (Param.NameValue nv : param.params()) {
                query.addParameter(nv.getName(), nv.getValue());
            }
            T t = query.executeScalar(cls);
            return t;
        }
    }

    public <T> T selectScalar(Connection conn, String sql, Class<T> cls) {
        T t = conn.createQuery(sql).executeScalar(cls);
        return t;
    }

    public <T> T selectScalar(Connection conn, String sql, Param param, Class<T> cls) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        T t = query.executeScalar(cls);
        return t;
    }

    public <T> List<T> selectScalarList(String sql, Class<T> cls) {
        try (Connection conn = open()) {
            List<T> list = conn.createQuery(sql).executeScalarList(cls);
            return list;
        }
    }

    public <T> List<T> selectScalarList(String sql, Param param, Class<T> cls) {
        try (Connection conn = open()) {
            Query query = conn.createQuery(sql);
            for (Param.NameValue nv : param.params()) {
                query.addParameter(nv.getName(), nv.getValue());
            }
            List<T> list = query.executeScalarList(cls);
            return list;
        }
    }

    public <T> List<T> selectScalarList(Connection conn, String sql, Class<T> cls) {
        List<T> list = conn.createQuery(sql).executeScalarList(cls);
        return list;
    }

    public <T> List<T> selectScalarList(Connection conn, String sql, Param param, Class<T> cls) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        List<T> list = query.executeScalarList(cls);
        return list;
    }

    public long count(String sql) {
        try (Connection conn = open()) {
            Long count = conn.createQuery(sql).executeScalar(Long.class);
            return count;
        }
    }

    public long count(String sql, Param param) {
        try (Connection conn = open()) {
            Query query = conn.createQuery(sql);
            for (Param.NameValue nv : param.params()) {
                query.addParameter(nv.getName(), nv.getValue());
            }
            Long count = query.executeScalar(Long.class);
            return count;
        }
    }

    public long count(Connection conn, String sql) {
        Long count = conn.createQuery(sql).executeScalar(Long.class);
        return count;
    }

    public long count(Connection conn, String sql, Param param) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        Long count = query.executeScalar(Long.class);
        return count;
    }

    public int insert(String sql) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql);
        }
    }

    public int insert(String sql, Param param) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql, param);
        }
    }

    public int insert(Connection conn, String sql) {
        return executeUpdate(conn, sql);
    }

    public int insert(Connection conn, String sql, Param param) {
        return executeUpdate(conn, sql, param);
    }

    public int insert(String sql, boolean returnGeneratedKeys) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql, returnGeneratedKeys);
        }
    }

    public int insert(String sql, Param param, boolean returnGeneratedKeys) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql, param, returnGeneratedKeys);
        }
    }

    public int insert(Connection conn, String sql, boolean returnGeneratedKeys) {
        return executeUpdate(conn, sql, returnGeneratedKeys);
    }

    public int insert(Connection conn, String sql, Param param, boolean returnGeneratedKeys) {
        return executeUpdate(conn, sql, param, returnGeneratedKeys);
    }


    public int delete(String sql) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql);
        }
    }

    public int delete(String sql, Param param) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql, param);
        }
    }

    public int delete(Connection conn, String sql) {
        return executeUpdate(conn, sql);
    }

    public int delete(Connection conn, String sql, Param param) {
        return executeUpdate(conn, sql, param);
    }

    public int update(String sql) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql);
        }
    }

    public int update(String sql, Param param) {
        try (Connection conn = open()) {
            return executeUpdate(conn, sql, param);
        }
    }

    public int update(Connection conn, String sql) {
        return executeUpdate(conn, sql);
    }

    public int update(Connection conn, String sql, Param param) {
        return executeUpdate(conn, sql, param);
    }

    public int executeUpdate(String sql) {
        try (Connection conn = open()) {
            conn.createQuery(sql).executeUpdate();
            int result = conn.getResult();
            return result;
        }
    }

    public int executeUpdate(Connection conn, String sql) {
        conn.createQuery(sql).executeUpdate();
        int result = conn.getResult();
        return result;
    }

    public int executeUpdate(Connection conn, String sql, Param param) {
        Query query = conn.createQuery(sql);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        query.executeUpdate();
        int result = conn.getResult();
        return result;
    }

    public int executeUpdate(String sql, boolean returnGeneratedKeys) {
        try (Connection conn = open()) {
            conn.createQuery(sql, returnGeneratedKeys).executeUpdate();
            int result = conn.getKey(Integer.class);
            return result;
        }
    }

    public int executeUpdate(Connection conn, String sql, boolean returnGeneratedKeys) {
        conn.createQuery(sql, returnGeneratedKeys).executeUpdate();
        int result = conn.getKey(Integer.class);
        return result;
    }

    public int executeUpdate(Connection conn, String sql, Param param, boolean returnGeneratedKeys) {
        Query query = conn.createQuery(sql, returnGeneratedKeys);
        for (Param.NameValue nv : param.params()) {
            query.addParameter(nv.getName(), nv.getValue());
        }
        query.executeUpdate();
        int result = conn.getKey(Integer.class);
        return result;
    }

    public <T> void createTableFromBean(Class<T> cls) {
        // get table name
        String tableName;
        tbl tblanno = cls.getAnnotation(tbl.class);
        if (tblanno == null || StringUtils.isBlank(tblanno.value())) {
            tableName = cls.getSimpleName();
        } else {
            tableName = tblanno.value();
        }
        StringBuilder sql = new StringBuilder("CREATE TABLE `" + tableName + "` (\n");
        Field[] declaredFields = cls.getDeclaredFields();
        List<String> colStrings = new ArrayList<>();
        List<String> pkStrings = new ArrayList<>();
        List<String> uqStrings = new ArrayList<>();
        for (Field f : declaredFields) {
            f.setAccessible(true);
            String colString = "";
            col colanno = f.getAnnotation(col.class);
            if (colanno == null)
                continue;
            String colName;
            if (StringUtils.isBlank(colanno.value())) {
                colName = f.getName();
            } else {
                colName = colanno.value();
            }
            colString += " `" + colName + "`";
            String type = "";
            if (f.getType().equals(long.class) || f.getType().equals(Long.class)) {
                type = "INT";
                int len = colanno.len();
                type += "(" + len + ")";
            } else if (f.getType().equals(int.class) || f.getType().equals(Integer.class)) {
                type = "INT";
                int len = colanno.len();
                type += "(" + len + ")";
            } else if (f.getType().equals(float.class) || f.getType().equals(Float.class)) {
                type = "FLOAT";
            } else if (f.getType().equals(double.class) || f.getType().equals(Double.class)) {
                type = "DOUBLE";
            } else if (f.getType().equals(BigInteger.class)) {
                type = "BIGINT";
            } else if (f.getType().equals(BigDecimal.class)) {
                type = "DECIMAL";
            } else if (f.getType().equals(String.class)) {
                type = "VARCHAR";
                int len = colanno.len();
                type += "(" + len + ")";
            } else if (f.getType().equals(Date.class)) {
                type = "DATETIME";
            } else if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
                type = "TINYINT(1)";
            } else {
                continue;
            }
            colString += " " + type;
            boolean pk = colanno.pk();
            if (pk)
                pkStrings.add(" PRIMARY KEY (`" + colName + "`)");
            boolean nn = colanno.nn();
            if (nn)
                colString += " NOT NULL";
            boolean uq = colanno.uq();
            if (uq)
                uqStrings.add(" UNIQUE INDEX `" + colName + "_uq` (`" + colName + "` ASC)");
            // boolean b = colanno.b();
            // boolean un = colanno.un();
            // boolean zf = colanno.zf();
            boolean ai = colanno.ai();
            if (ai)
                colString += " AUTO_INCREMENT";
            String expr = colanno.expr();
            if (StringUtils.isNotBlank(expr))
                colString += " " + expr;
            String comment = colanno.comment();
            if (StringUtils.isNotBlank(comment))
                colString += " COMMENT '" + comment + "'";
            colStrings.add(colString);
            f.setAccessible(false);
        }
        colStrings.addAll(pkStrings);
        colStrings.addAll(uqStrings);
        for (int i = 0; i < colStrings.size(); i++) {
            sql.append(colStrings.get(i));
            if (i != colStrings.size() - 1)
                sql.append(",\n");
        }
        sql.append(");");
        System.out.println(sql);
        executeUpdate(sql.toString());
    }

    public void dropTableIfExists(String tableName) {
        executeUpdate("drop table if exists " + tableName);
    }

    public Connection open() {
        return sql2o.open();
    }

    public Connection beginTransaction() {
        return sql2o.beginTransaction();
    }
}
