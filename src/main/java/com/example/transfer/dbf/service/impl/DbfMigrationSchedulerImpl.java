package com.example.transfer.dbf.service.impl;

import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.service.DbfMigrationScheduler;
import com.example.transfer.dbf.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbfMigrationSchedulerImpl implements DbfMigrationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DbfMigrationSchedulerImpl.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final MigrationService migrationService;
    @Value("${scan.base.package}")
    private String BASE_PACKAGE;

    @Override
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void migrateAllEntities() {
        if (isRunning.get()) {
            return;
        }
        try {
            isRunning.set(true);
            logger.info("Начало миграции...");

            Set<Class<?>> entityClasses = findEntityClassesWithDbfSource();
            if (entityClasses.isEmpty()) {
                logger.info("Нет сущностей с аннотацией @DbfSource для миграции.");
                return;
            }

            migrationService.migrateEntities(entityClasses);
            logger.info("Миграция завершена успешно.\n\n");
        } catch (Exception e) {
            logger.error("Ошибка при автоматической миграции данных: " + e.getMessage() + "\n\n");
        } finally {
            isRunning.set(false);
        }
    }

    private Set<Class<?>> findEntityClassesWithDbfSource() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(DbfSource.class));
        return scanner.findCandidateComponents(BASE_PACKAGE).stream()
                .map(BeanDefinition::getBeanClassName)
                .map(this::loadClass)
                .filter(clazz -> clazz.isAnnotationPresent(DbfSource.class))
                .collect(Collectors.toSet());
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Не удалось загрузить класс: " + className, e);
        }
    }
}