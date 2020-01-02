package fun.stgoder.psmgr.common.db;

import fun.stgoder.psmgr.common.db.annotation.tbl;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Sql {
    private static Map<Class, String> tableNameMap;

    static {
        tableNameMap = new HashMap<>();
    }

    private static <T> String getTableName(Class<T> cls) {
        String tableName = tableNameMap.get(cls);
        if (tableName != null) {
            return tableName;
        } else {
            tbl tblanno = cls.getAnnotation(tbl.class);
            if (tblanno == null || StringUtils.isBlank(tblanno.value())) {
                tableName = cls.getSimpleName();
            } else {
                tableName = tblanno.value();
            }
            tableNameMap.put(cls, tableName);
            return tableName;
        }
    }

    private String sql = "";

    public Sql select(String cols) {
        sql += "select " + cols;
        return this;
    }

    public Sql from(String tbl) {
        sql += " from " + tbl + " ";
        return this;
    }

    public <T> Sql from(Class<T> tbl) {
        sql += " from " + Sql.getTableName(tbl) + " ";
        return this;
    }

    public Sql leftJoin(String tbl) {
        sql += " left join " + tbl + " ";
        return this;
    }

    public <T> Sql leftJoin(Class<T> tbl) {
        sql += " left join " + Sql.getTableName(tbl) + " ";
        return this;
    }

    public Sql innerJoin(String tbl) {
        sql += " inner join " + tbl + " ";
        return this;
    }

    public <T> Sql innerJoin(Class<T> tbl) {
        sql += " inner join " + Sql.getTableName(tbl) + " ";
        return this;
    }

    public Sql alias(String alias) {
        sql += " " + alias + " ";
        return this;
    }

    public Sql on(String tmp) {
        sql += " on " + tmp + " ";
        return this;
    }

    public Sql where(String tmp) {
        sql += " where " + tmp + " ";
        return this;
    }

    public Sql and(String tmp) {
        sql += " and " + tmp + " ";
        return this;
    }

    public Sql or(String tmp) {
        sql += " or " + tmp + " ";
        return this;
    }

    public Sql insert(String tbl) {
        sql += "insert into " + tbl;
        return this;
    }

    public <T> Sql insert(Class<T> tbl) {
        sql += "insert into " + Sql.getTableName(tbl);
        return this;
    }

    public Sql cols(String cols) {
        sql += "(" + cols + ") ";
        return this;
    }

    public Sql values(String values) {
        sql += " values(" + values + ")";
        return this;
    }

    public Sql delete(String tbl) {
        sql += "delete from " + tbl + " ";
        return this;
    }

    public <T> Sql delete(Class<T> tbl) {
        sql += "delete from " + Sql.getTableName(tbl) + " ";
        return this;
    }

    public Sql update(String tbl) {
        sql += "update " + tbl + " ";
        return this;
    }

    public <T> Sql update(Class<T> tbl) {
        sql += "update " + Sql.getTableName(tbl) + " ";
        return this;
    }

    public Sql set(String tmp) {
        sql += " set " + tmp + " ";
        return this;
    }

    public Sql orderBy(String tmp) {
        sql += " order by " + tmp + " ";
        return this;
    }

    public String sql() {
        return sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}
