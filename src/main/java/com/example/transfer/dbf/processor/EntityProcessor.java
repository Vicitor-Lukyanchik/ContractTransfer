package com.example.transfer.dbf.processor;

import java.util.List;

public interface EntityProcessor {

    <T> void processEntities(List<T> entities);
}
