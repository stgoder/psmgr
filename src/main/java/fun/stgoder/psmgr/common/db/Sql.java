package fun.stgoder.psmgr.common.db;

public class Sql {

    private String sql = "";

    public Sql select(String cols) {
        sql += "select " + cols;
        return this;
    }

    public Sql from(String tbl) {
        sql += " from " + tbl + " ";
        return this;
    }

    public Sql leftJoin(String tbl) {
        sql += " left join " + tbl + " ";
        return this;
    }

    public Sql innerJoin(String tbl) {
        sql += " inner join " + tbl + " ";
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

    public Sql update(String tbl) {
        sql += "update " + tbl + " ";
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
