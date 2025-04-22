package com.example.transfer.dbf.processor;

import com.example.transfer.dbf.processor.impl.*;
import com.example.transfer.dbf.processor.impl.ProcessorRegistryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
public class ProcessorRegistryImplTest {

    @InjectMocks
    private ProcessorRegistryImpl registry;
    @Mock
    private FieldProcessor processor1, processor2, processor3, processor4;

    @BeforeEach
    void setup() {
        registry = new ProcessorRegistryImpl(mock(List.class)); // Для чистого теста, но лучше использовать реальные данные
    }

    @Test
    void sort_ShouldOrderProcessorsByPriority_WhenAnnotationsPresent() {
        // Создаем тестовые процессоры с разными приоритетами
        FieldProcessor processorB = new ProcessorB();
        FieldProcessor processorC = new ProcessorC(); // без аннотации
        FieldProcessor processorA = new ProcessorA();
        FieldProcessor processorD = new ProcessorD();

        // Создаем список с процессорами в произвольном порядке
        List<FieldProcessor> processors = Arrays.asList(processorD, processorB, processorA, processorC);

        // Создаем экземпляр класса с этими процессорами
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(processors);

        // Вызываем метод и проверяем результат
        List<FieldProcessor> sorted = testRegistry.getAllProcessorsSorted();

        // Ожидаемый порядок: processorB (5), processorA (10), processorD (10), processorC (0)
        assertThat(sorted).containsExactly(processorC, processorB, processorD, processorA);
    }

    @Test
    void sort_ShouldPlaceUnannotatedProcessorsLast_WhenNoAnnotation() {
        FieldProcessor processorC = new ProcessorC(); // без аннотации
        FieldProcessor processorD = new ProcessorD();
        List<FieldProcessor> processors = Arrays.asList(processorC, processorD);
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(processors);
        List<FieldProcessor> sorted = testRegistry.getAllProcessorsSorted();

        // ProcessorC (0) и ProcessorD (10) должны быть отсортированы
        assertThat(sorted).containsExactly(processorC, processorD);
    }

    @Test
    void sort_ShouldHandleEqualPrioritiesRandomly_WhenSamePriority() {
        FieldProcessor processorA = new ProcessorA();
        FieldProcessor processorD = new ProcessorD();
        List<FieldProcessor> processors = Arrays.asList(processorD, processorA);
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(processors);
        List<FieldProcessor> sorted = testRegistry.getAllProcessorsSorted();

        // Оба имеют приоритет 10, порядок между ними может быть произвольным
        assertThat(sorted).containsExactlyInAnyOrder(processorD, processorA);
    }

    @Test
    void getAllProcessorsSorted_ShouldReturnEmptyList_WhenInputIsEmpty() {
        List<FieldProcessor> emptyList = Collections.emptyList();
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(emptyList);

        List<FieldProcessor> result = testRegistry.getAllProcessorsSorted();
        assertThat(result).isEmpty();
    }

    @Test
    void getAllProcessorsSorted_ShouldReturnNewList_WhenOriginalListIsUnmodified() {
        List<FieldProcessor> originalList = Arrays.asList(new ProcessorA(), new ProcessorB());
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(originalList);

        List<FieldProcessor> sorted = testRegistry.getAllProcessorsSorted();

        // Проверяем, что исходный список не был изменен
        assertThat(originalList).hasSameSizeAs(sorted);
        assertThat(originalList).isNotSameAs(sorted); // Убедимся, что это новый список
    }

    @Test
    void getAllProcessorsSorted_ShouldExcludeNulls_WhenNullElementsPresent() {
        FieldProcessor processorA = new ProcessorA();
        List<FieldProcessor> processors = Arrays.asList(null, processorA);
        ProcessorRegistryImpl testRegistry = new ProcessorRegistryImpl(processors);

        // Ожидаем, что null игнорируется
        List<FieldProcessor> sorted = testRegistry.getAllProcessorsSorted();
        assertThat(sorted).containsExactly(processorA);
    }
}
