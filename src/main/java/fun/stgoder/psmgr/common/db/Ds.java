package fun.stgoder.psmgr.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.ps.nginx.Nginx;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Ds {

    public static BaseDataSource sqlite0;

    public static void initSqlite0() {
        try {
            File dbFile = new File(Constants.DS_SQLITE0_DB_FILE_PATH);
            if (!dbFile.exists()) {
                try (final InputStream slqite0InputStream = Nginx.class.getResourceAsStream("/sqlite0.db")) {
                    if (slqite0InputStream == null)
                        throw new IOException("read sqlite0 db file in resources err");
                    FileUtils.copyInputStreamToFile(slqite0InputStream, dbFile);
                }
                System.out.println("db file not found, use empty db file");
            }
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + Constants.DS_SQLITE0_DB_FILE_PATH);
            config.setPoolName(Constants.DS_SQLITE0_POOL_NAME);
            config.setMaximumPoolSize(Constants.DS_SQLITE0_POOL_SIZE);
            HikariDataSource hikariDataSource = new HikariDataSource(config);
            sqlite0 = new BaseDataSource();
            sqlite0.init(hikariDataSource);
        } catch (Exception e) {
            System.out.println("init sqlite0: "
                    + Constants.DS_SQLITE0_DB_FILE_PATH + " failed, work without sqlite0 data source");
        }
    }

    public static void main(String[] args) {
        Ds.initSqlite0();
        Ds.sqlite0.dropTableIfExists("test_bean");
        Ds.sqlite0.createTableFromBean(TestBean.class);
        /*Ds.sqlite0.select(
                new Sql().
                        select("tb.id, tb.name, tb1.text")
                        .from("test_bean").alias("tb")
                        .leftJoin("test_bean1").alias("tb1").on("tb.id = tb1.tb_id")
                        .where("tb.id = :id").sql(),
                new Param("id", "5e016b8ec94c20126e69f67a"), TestBean.class);
        try (Connection conn = Ds.sqlite0.beginTransaction();) { // sqlite not support
            Ds.sqlite0.insert(
                    conn,
                    new Sql()
                            .insert("test_bean")
                            .cols("xx")
                            .values(":xx").sql());
            Ds.sqlite0.update(
                    conn,
                    new Sql()
                            .update("test_bean")
                            .set("name = :name")
                            .where("id = :id").sql()
                    , new Param()
                            .add("id", "5e016b8ec94c20126e69f67a"));
            Ds.sqlite0.delete(
                    conn,
                    new Sql()
                            .delete("test_bean")
                            .where("id = :id").sql(),
                    new Param("id", "5e016b8ec94c20126e69f67a"));
            conn.commit();
        }*/
        Ds.sqlite0.select(new Sql()
                .select("id, name, text, del, create_time as createTime")
                .from(TestBean.class).sql(), TestBean.class);
    }
}
