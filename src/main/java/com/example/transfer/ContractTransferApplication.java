package com.example.transfer;

import com.example.transfer.dbf.service.impl.DbfMigrationSchedulerImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.transfer")
@EnableJpaRepositories(basePackages = "com.example.transfer.repository")
@EnableScheduling
public class ContractTransferApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ContractTransferApplication.class, args);
        DbfMigrationSchedulerImpl migrationService = context.getBean(DbfMigrationSchedulerImpl.class);
        migrationService.migrateAllEntities();
    }
}
