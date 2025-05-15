package com.example.transfer.dbf.service;

import java.util.List;

import java.util.List;

public interface EntityValidationService {
    /**
     * Валидирует список сущностей.
     *
     * @param entities список сущностей для валидации
     * @param <T>      тип сущности
     */
    <T> void validateEntities(List<T> entities);
}