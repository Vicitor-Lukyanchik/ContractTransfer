package com.example.transfer.dbf.processor.impl;

import com.example.transfer.dbf.annotation.ProcessingOrder;
import com.example.transfer.dbf.processor.FieldProcessor;
import com.example.transfer.dbf.processor.ProcessorRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProcessorRegistryImpl implements ProcessorRegistry {

    private final List<FieldProcessor> processors;

    @Override
    public List<FieldProcessor> getAllProcessorsSorted() {
        return processors.stream()
                .filter(Objects::nonNull)
                .sorted((p1, p2) -> {
                    int priority1 = getProcessorPriority(p1);
                    int priority2 = getProcessorPriority(p2);
                    return Integer.compare(priority1, priority2);
                })
                .collect(Collectors.toList());
    }

    private int getProcessorPriority(FieldProcessor processor) {
        if (processor == null) {
            return 0;
        }

        try {
            var clazz = processor.getClass();
            if (clazz.isAnnotationPresent(ProcessingOrder.class)) {
                var priorityAnnotation = clazz.getAnnotation(ProcessingOrder.class);
                return priorityAnnotation.value();
            }
        } catch (Exception ignored) {
        }
        return 0;
    }
}
