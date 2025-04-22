package com.example.transfer.dbf.migrator.impl;

import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.processor.EntityProcessor;
import com.example.transfer.dbf.converter.DbfConverter;
import com.example.transfer.dbf.migrator.DbfDatabaseMigrator;
import com.example.transfer.s02015.entity.Dogovor;
import com.example.transfer.s02015.processor.PackIdLookupProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DbfDatabaseMigratorImpl implements DbfDatabaseMigrator {

    private static final Logger logger = LoggerFactory.getLogger(DbfDatabaseMigratorImpl.class);

    @PersistenceContext
    private final EntityManager entityManager;

    private final DbfConverter dbfConverter;
    private final EntityProcessor entityProcessor;


    @Override
    @Transactional
    public <T> void migrateToDatabase(Class<T> entityClass) {
        try {
            List<T> entities = dbfConverter.convert(entityClass);
            entityProcessor.processEntities(entities);

            for (T entity : entities) {
                persistOrUpdateEntity(entity);
            }

        } catch (Exception e) {
            throw new DbfException(e.getMessage(), e);
        }
    }


    private <T> void persistOrUpdateEntity(T entity) {
        entityManager.merge(entity);
    }
}
