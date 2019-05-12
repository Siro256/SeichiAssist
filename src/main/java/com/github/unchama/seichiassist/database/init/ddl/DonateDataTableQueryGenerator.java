package com.github.unchama.seichiassist.database.init.ddl;

public class DonateDataTableQueryGenerator implements TableInitializationQueryGenerator {

    private final String tableReferenceName;

    public DonateDataTableQueryGenerator(String tableReferenceName) {
        this.tableReferenceName = tableReferenceName;
    }

    @Override
    public String generateTableCreationQuery() {
        //テーブルが存在しないときテーブルを新規作成
        return "CREATE TABLE IF NOT EXISTS " + tableReferenceName +
                "(id int auto_increment unique)";
    }

    @Override
    public String generateColumnCreationQuery() {
        return "alter table " + tableReferenceName +
                " add column if not exists playername varchar(20) default null" +
                ",add column if not exists playeruuid varchar(128) default null" +
                ",add column if not exists effectnum int default null" +
                ",add column if not exists effectname varchar(20) default null" +
                ",add column if not exists getpoint int default 0" +
                ",add column if not exists usepoint int default 0" +
                ",add column if not exists date datetime default null";
    }
}
