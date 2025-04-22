package com.example.transfer.dbf.extractor;

import com.example.transfer.dbf.domain.DbfTable;

public interface DbfDataExtractor {

    public DbfTable extract(String path);
}
