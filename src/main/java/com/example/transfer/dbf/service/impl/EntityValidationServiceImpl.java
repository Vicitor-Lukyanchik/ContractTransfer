package com.example.transfer.dbf.service.impl;

import com.example.transfer.dbf.service.EntityValidationService;
import com.example.transfer.dbf.util.ProcessorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EntityValidationServiceImpl implements EntityValidationService {

    private final Validator validator;
    private final ProcessorUtils processorUtils;

    /**
     * Валидирует список сущностей.
     */
    @Override
    public <T> void validateEntities(List<T> entities) {
        for (T entity : entities) {
            Errors errors = new BeanPropertyBindingResult(entity, entity.getClass().getName());
            validator.validate(entity, errors);

            if (errors.hasErrors()) {
                String errorMessage = buildErrorMessage(entity, errors);
                throw new RuntimeException(errorMessage);
            }
        }
    }

    private <T> String buildErrorMessage(T entity, Errors errors) {
        Map<Field, String> problems = new HashMap<>();

        errors.getFieldErrors().forEach(fieldError -> {
            try {
                Field field = entity.getClass().getDeclaredField(fieldError.getField());
                String problem = fieldError.getDefaultMessage();
                problems.put(field, problem);
            } catch (NoSuchFieldException e) {
                // Если поле не найдено, добавляем общее сообщение
                problems.put(null, "Не удалось найти поле: " + fieldError.getField());
            }
        });

        if (!problems.isEmpty()) {
            return processorUtils.buildErrorMessage(entity, problems);
        }

        return null;
    }
}