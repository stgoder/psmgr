package fun.stgoder.psmgr.common.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fun.stgoder.psmgr.common.Constants;

public class Ds extends BaseDataSource {

    public static BaseDataSource sqlite0;

    public static void initSqlite0() {
        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl(Constants.DS_SQLITE0_URL);
            config.setPoolName(Constants.DS_SQLITE0_POOL_NAME);
            config.setMaximumPoolSize(Constants.DS_SQLITE0_POOL_SIZE);
            HikariDataSource hikariDataSource = new HikariDataSource(config);
            sqlite0 = new BaseDataSource();
            sqlite0.init(hikariDataSource);
        } catch (Exception e) {
            System.out.println("init sqlite0: " + Constants.DS_SQLITE0_URL + " failed, work without sqlite0 data source");
        }
    }

    public static void main(String[] args) {
        Ds.initSqlite0();
        Ds.sqlite0.dropTableIfExists("test_bean");
        Ds.sqlite0.createTableFromBean(TestBean.class);
    }
}
