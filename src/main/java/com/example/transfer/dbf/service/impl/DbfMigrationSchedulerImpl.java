package com.example.transfer.dbf.service.impl;

import com.example.transfer.dbf.annotation.DbfSource;
import com.example.transfer.dbf.email.impl.EmailNotificationService;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.handler.ErrorHandler;
import com.example.transfer.dbf.service.DbfMigrationScheduler;
import com.example.transfer.dbf.service.MigrationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.example.transfer.dbf.util.ExceptionHandler.handleDataIntegrityViolationException;

@Service
@RequiredArgsConstructor
public class DbfMigrationSchedulerImpl implements DbfMigrationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DbfMigrationSchedulerImpl.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    private final MigrationService migrationService;
    private final EmailNotificationService emailNotificationService;
    private final ErrorHandler errorHandler;
    @Value("${migration.interval.hours}")
    private double intervalHours; // Интервал в часах (например, 0.5 для 30 минут)

    @Value("${scan.base.package}")
    private String BASE_PACKAGE;

    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    @PostConstruct
    public void init() {
        taskScheduler.setPoolSize(1); // Размер пула потоков
        taskScheduler.initialize();
        scheduleMigrationTask();
    }

    private void scheduleMigrationTask() {
        long intervalMillis = (long) (intervalHours * 60 * 60 * 1000); // Преобразуем часы в миллисекунды
        taskScheduler.scheduleWithFixedDelay(this::migrateAllEntities, intervalMillis);
    }

    @Override
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
            emailNotificationService.markErrorAsResolved();
        } catch (DataIntegrityViolationException e) {
            handleDataIntegrityViolationException(e);
        } catch (Exception e) {
            errorHandler.handleException(e);
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
            throw new DbfException("Не удалось загрузить класс: " + className, e);
        }
    }

    private void handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("Ошибка целостности данных: " + e.getMessage());
        emailNotificationService.sendErrorNotification("Ошибка целостности данных: " + e.getMessage());
    }
}