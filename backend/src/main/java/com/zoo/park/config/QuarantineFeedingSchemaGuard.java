package com.zoo.park.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 启动时兜底：保证隔离加餐单的“同一只动物一个日历日只落一张”唯一索引在。
 *
 * <p>全新库由 schema.sql 建好；若是挂着旧数据卷升级上来，schema.sql 不会重跑，
 * Hibernate 的 ddl-auto=update 会补建 quarantine_feeding 表却不会补唯一索引——
 * 同日第二张（含两人同时记）全靠这条索引挡住，缺不得，所以这里幂等补一下。
 */
@Component
public class QuarantineFeedingSchemaGuard implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public QuarantineFeedingSchemaGuard(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Integer tableExists = jdbc.queryForObject(
                "select count(*) from information_schema.tables"
                        + " where table_schema = database() and table_name = 'quarantine_feeding'",
                Integer.class);
        if (tableExists == null || tableExists == 0) {
            return;
        }
        Integer indexExists = jdbc.queryForObject(
                "select count(*) from information_schema.statistics"
                        + " where table_schema = database()"
                        + " and table_name = 'quarantine_feeding'"
                        + " and index_name = 'uk_qf_animal_day'",
                Integer.class);
        if (indexExists == null || indexExists == 0) {
            jdbc.execute("alter table quarantine_feeding"
                    + " add constraint uk_qf_animal_day unique (animal_id, feed_date)");
        }
    }
}
