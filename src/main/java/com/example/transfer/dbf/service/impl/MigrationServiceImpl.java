package com.example.transfer.dbf.service.impl;

import com.example.transfer.dbf.annotation.MigrationOrder;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.migrator.DbfDatabaseMigrator;
import com.example.transfer.dbf.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DbfMigrationSchedulerImpl.class);

    private final DbfDatabaseMigrator dbfDatabaseMigrator;

    @Override
    @Transactional
    public void migrateEntities(Set<Class<?>> entityClasses) {
        List<Class<?>> sortedEntities = entityClasses.stream()
                .sorted(Comparator.comparingInt(this::getMigrationOrder))
                .collect(Collectors.toList());

        for (Class<?> entityClass : sortedEntities) {
            try {
                logger.info("Мигрируем сущность: {}", entityClass.getSimpleName());
                dbfDatabaseMigrator.migrateToDatabase((Class<Object>) entityClass);
            } catch (Exception e) {
                throw new DbfException(e.getMessage());
            }
        }
    }

    private int getMigrationOrder(Class<?> clazz) {
        if (clazz.isAnnotationPresent(MigrationOrder.class)) {
            MigrationOrder annotation = clazz.getAnnotation(MigrationOrder.class);
            return annotation.value();
        }
        return Integer.MAX_VALUE;
    }
}
