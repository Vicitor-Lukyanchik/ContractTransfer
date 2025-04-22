package com.example.transfer.dbf.service;

import java.util.Set;

public interface MigrationService {

    void migrateEntities(Set<Class<?>> entityClasses);
}
