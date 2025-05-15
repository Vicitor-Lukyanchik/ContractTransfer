package com.example.transfer.dbf.service.impl;

import com.example.transfer.dbf.annotation.MigrationOrder;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.migrator.DbfDatabaseMigrator;
import com.example.transfer.dbf.processor.PostProcessor;
import com.example.transfer.dbf.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MigrationServiceImpl implements MigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DbfMigrationSchedulerImpl.class);

    private final DbfDatabaseMigrator dbfDatabaseMigrator;
    private final ApplicationContext applicationContext;

    @Value("${scan.base.package}")
    private String BASE_PACKAGE;

    @Override
    public void migrateEntities(Set<Class<?>> entityClasses) {
        List<Class<?>> sortedEntities = entityClasses.stream()
                .sorted(Comparator.comparingInt(this::getMigrationOrder))
                .collect(Collectors.toList());

        for (Class<?> entityClass : sortedEntities) {
            try {
                //logger.info("Мигрируем сущность: {}", entityClass.getSimpleName());
                dbfDatabaseMigrator.migrateToDatabase((Class<Object>) entityClass);

                executePostProcessors();
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

    public void executePostProcessors() {
        // Получаем все бины, реализующие интерфейс PostProcessor
        Map<String, PostProcessor> allPostProcessors = applicationContext.getBeansOfType(PostProcessor.class);

        // Фильтруем бины по указанному пакету
        List<PostProcessor> filteredPostProcessors = allPostProcessors.values().stream()
                .filter(postProcessor -> postProcessor.getClass().getPackageName().startsWith(BASE_PACKAGE))
                .collect(Collectors.toList());

        // Выполняем постпроцессоры
        for (PostProcessor postProcessor : filteredPostProcessors) {
            try {
                postProcessor.process();
            } catch (DbfException e) {
                throw new DbfException(e.getMessage());
            } catch (Exception e){
                throw new DbfException("Ошибка при выполнении постпроцессора: " + postProcessor.getClass().getSimpleName());
            }
        }
    }
}
