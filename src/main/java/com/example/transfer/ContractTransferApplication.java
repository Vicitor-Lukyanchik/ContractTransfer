package com.example.transfer;

import com.example.transfer.dbf.service.impl.DbfMigrationSchedulerImpl;
import com.example.transfer.manager.LogParser;
import com.example.transfer.manager.ProcessMonitor;
import com.example.transfer.s03000.entity.Rate;
import com.example.transfer.s03000.entity.RateId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "com.example.transfer")
@EnableJpaRepositories
@EnableScheduling
public class ContractTransferApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ContractTransferApplication.class, args);
        DbfMigrationSchedulerImpl migrationService = context.getBean(DbfMigrationSchedulerImpl.class);
        migrationService.migrateAllEntities();
    }

}

