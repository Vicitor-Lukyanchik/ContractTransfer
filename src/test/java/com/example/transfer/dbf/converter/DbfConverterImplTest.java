package com.example.transfer.dbf.converter;

import com.example.transfer.dbf.converter.impl.DbfConverterImpl;
import com.example.transfer.dbf.domain.DbfColumn;
import com.example.transfer.dbf.domain.DbfTable;
import com.example.transfer.dbf.entity.*;
import com.example.transfer.dbf.exception.DbfConverterException;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.extractor.DbfDataExtractor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DbfConverterImplTest {

    @Mock
    private DbfDataExtractor dbfDataExtractor;

    @InjectMocks
    private DbfConverterImpl dbfConverter;

    @Test
    void convert_ShouldReturnListOfEntities_WhenDbfFileIsValid() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals("Test Name", entity.getName(), "NAME should be 'Test Name'");
        assertEquals(Date.valueOf(LocalDate.of(2025, 2, 11)), entity.getDate(), "DATE should be '2025-02-11'");
        assertTrue(entity.getIsGood(), "ISGOOD should be true");
    }

    @Test
    void convert_ShouldThrowDbfConverterException_WhenClassHasNoDbfSourceAnnotation() {
        Class<?> invalidClass = Object.class;

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> dbfConverter.convert(invalidClass));
        assertTrue(exception.getMessage().contains("Класс не содержит аннотацию @DbfSource"),
                "Exception message should indicate missing @DbfSource annotation");
    }

    @Test
    void convert_ShouldThrowDbfConverterException_WhenDbfFieldIsMissingInData() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> dbfConverter.convert(TestEntity.class));
        assertTrue(exception.getMessage().contains("Столбец 'NAME' не найден в данных .dbf-файла"),
                "Exception message should indicate missing field 'NAME'");
    }

    @Test
    void convert_ShouldHandleNullDate_WhenDbfDateFieldIsNull() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        row1.put("DATE", null);
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertNull(entity.getDate(), "DATE should be null");
    }

    @Test
    void convert_ShouldTrimStrings_WhenDbfCharFieldHasExtraSpaces() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "       Test Name       ");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals("Test Name", entity.getName(), "NAME should be trimmed to 'Test Name'");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfNumericFieldTypeIsIncompatible() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "a");
        row1.put("NAME", "Test Name");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> dbfConverter.convert(TestEntity.class));
        assertTrue(exception.getMessage().contains("Невозможно преобразовать значение"),
                "Exception message should indicate incompatible data types");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfDateFieldTypeIsIncompatible() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        row1.put("DATE", "20021115");
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> dbfConverter.convert(TestEntity.class));
        assertTrue(exception.getMessage().contains("Ошибка при преобразовании значения"),
                "Exception message should indicate incompatible data types");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfNumericFieldTypeIsEmpty() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "");
        row1.put("NAME", "Test Name");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> dbfConverter.convert(TestEntity.class));
        assertTrue(exception.getMessage().contains("Невозможно преобразовать значение"),
                "Exception message should indicate incompatible data types");
    }

    @Test
    void convert_ShouldReturnFalse_WhenDbfLogicalFieldTypeIsEmpty() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", "");
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);
        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        TestEntity entity = result.get(0);
        assertFalse(entity.getIsGood(), "ISGOOD should be false");
    }

    @Test
    void convert_ShouldReturnListOfEntities_WhenDbfFileIsValidInDbfMoreFieldsThanInClass() throws Exception {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("NAME2").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        row1.put("NAME2", "Test Name2");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals("Test Name", entity.getName(), "NAME should be 'Test Name'");
        assertEquals(Date.valueOf(LocalDate.of(2025, 2, 11)), entity.getDate(), "DATE should be '2025-02-11'");
        assertTrue(entity.getIsGood(), "ISGOOD should be true");
    }

    @Test
    void convert_ShouldHandleDecimalNumbers_WhenDbfNumericFieldHasDecimals() throws Exception {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("AMOUNT").type("NUMERIC").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("AMOUNT", "123.45"); // Число с десятичной точкой
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntityWithAmount> result = dbfConverter.convert(TestEntityWithAmount.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntityWithAmount entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals(123.45, entity.getAmount(), 0.001, "AMOUNT should be 123.45");
    }

    @Test
    void convert_ShouldThrowException_WhenFieldTypeIsIncompatibleWithDbfType() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("NUMERIC").build()); // DBF: NUMERIC, Java: String (несовместимо)

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "12345");
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(IncompatibleTypeEntity.class);
        });

        assertTrue(exception.getMessage().contains("Несовместимые типы данных"),
                "Exception message should indicate incompatible data types");
    }

    @Test
    void convert_ShouldHandleEmptyStrings_WhenDbfCharFieldIsEmpty() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("NAME2").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", ""); // Пустая строка
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertNull(entity.getName(), "NAME should be null for empty string");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfFieldHasUnknownType() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("UNKNOWN_FIELD").type("B").build()); // Неизвестный тип 'B'

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("UNKNOWN_FIELD", new byte[]{1, 2, 3}); // Двоичные данные
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(TestEntityWithUnknownField.class);
        });

        assertTrue(exception.getMessage().contains("Неизвестный тип данных"),
                "Exception message should indicate unknown data type");
    }

    @Test
    void convert_ShouldHandleMissingValues_WhenDbfFieldIsNull() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("NAME2").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", null);
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertNull(entity.getName(), "NAME should be null");
    }

    @Test
    void convert_ShouldTruncateLongStrings_WhenDbfCharFieldExceedsMaxLength() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "This is a very long name that exceeds the maximum length of 40 characters"); // Строка больше 40 символов
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act
        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntity entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals("This is a very long name that exceeds the maximum length of 40 characters", entity.getName(),
                "NAME should be truncated to 40 characters");
    }

    @Test
    void convert_ShouldIgnoreFieldsWithoutDbfFieldAnnotation_WhenEntityFieldIsNotAnnotated() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("NAME", "Test Name");
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act
        List<TestEntityWithIgnoredField> result = dbfConverter.convert(TestEntityWithIgnoredField.class);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntityWithIgnoredField entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals("Test Name", entity.getName(), "NAME should be 'Test Name'");
        assertNull(entity.getIgnoredField(), "Ignored field should be null");
    }

    @Test
    void convert_ShouldHandleVeryLargeStrings_WhenDbfCharFieldIsHuge() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("DESCRIPTION").type("CHAR").build());

        StringBuilder hugeString = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            hugeString.append("A"); // Создаем строку длиной 10,000 символов
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("DESCRIPTION", hugeString.toString()); // Очень длинная строка
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act
        List<TestEntityWithLargeString> result = dbfConverter.convert(TestEntityWithLargeString.class);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "There should be exactly 1 entity");

        TestEntityWithLargeString entity = result.get(0);
        assertEquals(1L, entity.getId(), "ID should be 1");
        assertEquals(hugeString.toString(), entity.getDescription(), "DESCRIPTION should contain the huge string");
    }

    @Test
    void convert_ShouldReturnEmptyList_WhenDbfFileHasNoRowsAndNoColumns() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>(); // Пустой список столбцов
        List<Map<String, Object>> rows = new ArrayList<>(); // Пустой список строк

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act
        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
    }

    @Test
    void convert_ShouldReturnEmptyList_WhenDbfFileHasNoRows() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>(); // Пустой список строк

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act
        List<TestEntity> result = dbfConverter.convert(TestEntity.class);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be an empty list");
    }


    @Test
    void convert_ShouldThrowException_WhenDbfRowHasMismatchedDataCount() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("NAME").type("CHAR").build());
        columns.add(DbfColumn.builder().name("DATE").type("DATE").build());
        columns.add(DbfColumn.builder().name("ISGOOD").type("LOGICAL").build());

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1"); // Отсутствует значение для NAME
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act & Assert
        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(TestEntity.class);
        });

        assertTrue(exception.getMessage().contains("Столбец 'NAME' не найден"),
                "Exception message should indicate missing field 'NAME'");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfColumnMetadataIsMissing() {
        // Arrange
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type(null).build()); // Отсутствует тип данных

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("DATE", Date.valueOf("2025-02-11"));
        row1.put("ISGOOD", Boolean.valueOf("true"));
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        // Act & Assert
        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(TestEntity.class);
        });

        assertTrue(exception.getMessage().contains("Ошибка при преобразовании строки данных:"),
                "Exception message should indicate missing column type");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfFileIsUnsupportedVersion() {
        when(dbfDataExtractor.extract(anyString())).thenThrow(new DbfException("Unsupported DBF version"));

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(TestEntity.class);
        });

        assertTrue(exception.getMessage().contains("Ошибка при чтении файла"),
                "Exception message should indicate unsupported DBF version");
    }

    @Test
    void convert_ShouldThrowException_WhenDbfFileHasDuplicateFieldNames() {
        List<DbfColumn> columns = new ArrayList<>();
        columns.add(DbfColumn.builder().name("ID").type("NUMERIC").build());
        columns.add(DbfColumn.builder().name("ID").type("CHAR").build()); // Дублирующееся имя поля

        List<Map<String, Object>> rows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("ID", "1");
        row1.put("ID", "Test Name"); // Конфликт значений
        rows.add(row1);

        DbfTable dbfTable = DbfTable.builder().columns(columns).rows(rows).build();

        when(dbfDataExtractor.extract(anyString())).thenReturn(dbfTable);

        DbfConverterException exception = assertThrows(DbfConverterException.class, () -> {
            dbfConverter.convert(TestEntity.class);
        });

        assertTrue(exception.getMessage().contains("Duplicate key ID"),
                "Exception message should indicate duplicate field names");
    }
}
