package com.example.transfer.dbf.converter;

import java.util.List;

public interface DbfConverter {

    public <T> List<T> convert(Class<T> entityClass);
}
