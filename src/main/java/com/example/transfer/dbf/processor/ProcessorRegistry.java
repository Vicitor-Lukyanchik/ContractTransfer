package com.example.transfer.dbf.processor;

import java.util.List;

public interface ProcessorRegistry {

    List<FieldProcessor> getAllProcessorsSorted();

}
