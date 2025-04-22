package com.example.transfer.dbf.extractor;

import com.example.transfer.dbf.domain.DbfColumn;
import com.example.transfer.dbf.domain.DbfTable;
import com.example.transfer.dbf.exception.DbfException;
import com.example.transfer.dbf.extractor.impl.DbfDataExtractorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DbfDataExtractorImplTest {

    @Autowired
    private DbfDataExtractorImpl dbfDataExtractor;

    @Test
    void extract_ShouldReturnDbfTable_WhenFileIsValid() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        assertEquals(4, result.getColumns().size(), "There should be 4 columns");

        List<DbfColumn> columns = result.getColumns();
        assertEquals("NUMERIC", columns.get(0).getType(), "First column type should be NUMERIC");
        assertEquals("CHAR", columns.get(1).getType(), "Second column type should be CHAR");
        assertEquals("DATE", columns.get(2).getType(), "Third column type should be DATE");
        assertEquals("LOGICAL", columns.get(3).getType(), "Fourth column type should be LOGICAL");

        List<Map<String, Object>> rows = result.getRows();
        assertFalse(rows.isEmpty(), "Rows should not be empty");
        for (Map<String, Object> row : rows) {
            assertNotNull(row.get("ID"), "ID should not be null");
            assertNotNull(row.get("NAME"), "NAME should not be null");
            assertNotNull(row.get("DATE"), "DATE should not be null");
            assertNotNull(row.get("ISGOOD"), "ISGOOD should not be null");
        }
    }

    @Test
    void extract_ShouldThrowDbfException_WhenFileNotFound() {
        String invalidFilePath = "nonexistent.dbf";

        DbfException exception = assertThrows(DbfException.class, () -> dbfDataExtractor.extract(invalidFilePath));
        assertTrue(exception.getMessage().contains("Ошибка при чтении файла: " + invalidFilePath),
                "Exception message should indicate file not found");
    }

    @Test
    void extract_ShouldThrowDbfException_WhenFileIsCorrupted() {
        String corruptedFilePath = Paths.get("src", "test", "resources", "dbf", "corrupted.dbf").toString();
        File file = new File(corruptedFilePath);
        if (!file.exists()) {
            fail("Corrupted DBF file does not exist!");
        }

        DbfException exception = assertThrows(DbfException.class, () -> dbfDataExtractor.extract(corruptedFilePath));
        assertTrue(exception.getMessage().contains("Ошибка при чтении файла: " + corruptedFilePath),
                "Exception message should indicate file corruption");
    }

    @Test
    void extract_ShouldReturnCorrectColumns_WhenFileIsValid() {
        // Arrange
        String filePath = Paths.get("src", "test", "resources", "dbf", "test.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file does not exist!");

        // Act
        DbfTable result = dbfDataExtractor.extract(filePath);

        // Assert
        assertNotNull(result, "Result should not be null");
        List<DbfColumn> columns = result.getColumns();
        assertEquals(4, columns.size(), "There should be 4 columns");

        // Проверка первого столбца (ID)
        DbfColumn idColumn = columns.get(0);
        assertEquals("ID", idColumn.getName(), "First column name should be 'ID'");
        assertEquals("NUMERIC", idColumn.getType(), "First column type should be NUMERIC");

        // Проверка второго столбца (NAME)
        DbfColumn nameColumn = columns.get(1);
        assertEquals("NAME", nameColumn.getName(), "Second column name should be 'NAME'");
        assertEquals("CHAR", nameColumn.getType(), "Second column type should be CHAR");

        // Проверка третьего столбца (DATE)
        DbfColumn dateColumn = columns.get(2);
        assertEquals("DATE", dateColumn.getName(), "Third column name should be 'DATE'");
        assertEquals("DATE", dateColumn.getType(), "Third column type should be DATE");

        // Проверка четвертого столбца (ISGOOD)
        DbfColumn isGoodColumn = columns.get(3);
        assertEquals("ISGOOD", isGoodColumn.getName(), "Fourth column name should be 'ISGOOD'");
        assertEquals("LOGICAL", isGoodColumn.getType(), "Fourth column type should be LOGICAL");
    }

    @Test
    void extract_ShouldReturnCorrectColumns_WhenFileIsEmpty() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test_empty.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<DbfColumn> columns = result.getColumns();
        assertEquals(4, columns.size(), "There should be 4 columns");

        DbfColumn idColumn = columns.get(0);
        assertEquals("ID", idColumn.getName(), "First column name should be 'ID'");
        assertEquals("NUMERIC", idColumn.getType(), "First column type should be NUMERIC");

        DbfColumn nameColumn = columns.get(1);
        assertEquals("NAME", nameColumn.getName(), "Second column name should be 'NAME'");
        assertEquals("CHAR", nameColumn.getType(), "Second column type should be CHAR");

        DbfColumn dateColumn = columns.get(2);
        assertEquals("DATE", dateColumn.getName(), "Third column name should be 'DATE'");
        assertEquals("DATE", dateColumn.getType(), "Third column type should be DATE");

        DbfColumn isGoodColumn = columns.get(3);
        assertEquals("ISGOOD", isGoodColumn.getName(), "Fourth column name should be 'ISGOOD'");
        assertEquals("LOGICAL", isGoodColumn.getType(), "Fourth column type should be LOGICAL");
    }

    @Test
    void extract_ShouldReturnCorrectSingleRow_WhenFileIsValidWithDosEncoding() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test_one_row.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file with a single row does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<Map<String, Object>> rows = result.getRows();
        assertEquals(1, rows.size(), "There should be exactly 1 row");

        Map<String, Object> row = rows.get(0);

        Object idValue = row.get("ID");
        assertNotNull(idValue, "ID value should not be null");
        assertTrue(idValue instanceof Number, "ID should be a numeric value");
        assertEquals(1, ((Number) idValue).intValue(), "ID value should be 12345678");

        String nameValue = (String) row.get("NAME");
        assertNotNull(nameValue, "NAME value should not be null");
        assertTrue(true, "NAME should be a string value");
        assertEquals("Виктор", nameValue.trim(), "NAME value should be 'Test Name'");

        Object dateValue = row.get("DATE");
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormat.format((java.util.Date) dateValue);
        assertEquals("20020402", dateString, "DATE value should be '20250211'");

        Object isGoodValue = row.get("ISGOOD");
        assertNotNull(isGoodValue, "ISGOOD value should not be null");
        assertTrue(isGoodValue instanceof Boolean || isGoodValue instanceof Character,
                "ISGOOD should be a boolean or character value");
        assert isGoodValue instanceof Boolean;
        assertTrue((boolean) ((Boolean) isGoodValue), "ISGOOD value should be 'T' or true");
    }

    @Test
    void extract_ShouldReturnCorrectSingleRow_WhenFileIsValidWithEmptyString() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test_one_empty_row.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file with a single row does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<Map<String, Object>> rows = result.getRows();
        assertEquals(1, rows.size(), "There should be exactly 1 row");

        Map<String, Object> row = rows.get(0);

        Object idValue = row.get("ID");
        assertNotNull(idValue, "ID value should not be null");
        assertTrue(idValue instanceof Number, "ID should be a numeric value");
        assertEquals(0, ((Number) idValue).intValue(), "ID value should be 0");

        String nameValue = (String) row.get("NAME");
        assertNotNull(nameValue, "NAME value should not be null");
        assertTrue(true, "NAME should be a string value");
        assertEquals("", nameValue.trim(), "NAME value should be 'Test Name'");

        Object dateValue = row.get("DATE");
        assertNull(dateValue, "DATE value should be null");


        Object isGoodValue = row.get("ISGOOD");
        assertNotNull(isGoodValue, "ISGOOD value should not be null");
        assertTrue(isGoodValue instanceof Boolean || isGoodValue instanceof Character,
                "ISGOOD should be a boolean or character value");
        assert isGoodValue instanceof Boolean;
        assertFalse((boolean) ((Boolean) isGoodValue), "ISGOOD value should be 'F'");
    }

    @Test
    void extract_ShouldReturnCorrectSingleRow_WhenFileIsEmpty() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test_empty.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file with a single row does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<Map<String, Object>> rows = result.getRows();
        assertEquals(0, rows.size(), "There should be exactly 0 row");
    }

    @Test
    void extract_ShouldReturnThreeRows_WhenFileIsValidWithThreeRows() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file with a single row does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<Map<String, Object>> rows = result.getRows();
        assertEquals(3, rows.size(), "There should be exactly 3 row");
    }

    @Test
    void extract_ShouldReturnAllRows_WhenFileHasManyRecords() {
        String filePath = Paths.get("src", "test", "resources", "dbf", "test_many_rows.dbf").toString();
        File file = new File(filePath);
        assertTrue(file.exists(), "Test DBF file with a single row does not exist!");

        DbfTable result = dbfDataExtractor.extract(filePath);

        assertNotNull(result, "Result should not be null");
        List<Map<String, Object>> rows = result.getRows();
        assertEquals(18485, rows.size(), "There should be exactly 18485 row");
    }

    @Test
    void extract_ShouldThrowDbfException_WhenHeaderIsCorrupted() {
        // Arrange
        String corruptedHeaderFilePath = Paths.get("src", "test", "resources", "dbf", "corrupted_header.dbf").toString();
        File file = new File(corruptedHeaderFilePath);
        if (!file.exists()) {
            fail("DBF file with corrupted header does not exist!");
        }

        DbfException exception = assertThrows(DbfException.class, () -> dbfDataExtractor.extract(corruptedHeaderFilePath));
        assertTrue(exception.getMessage().contains("Ошибка при чтении файла"),
                "Exception message should indicate corrupted header");
    }
}
