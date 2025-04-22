package com.example.transfer.dbf.migrator;

public interface DbfDatabaseMigrator {
    public <T> void migrateToDatabase(Class<T> entityClass);
}
