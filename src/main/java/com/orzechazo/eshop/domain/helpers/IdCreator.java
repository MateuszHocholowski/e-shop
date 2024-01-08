package com.orzechazo.eshop.domain.helpers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class IdCreator {
    private static long currentId = 0;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String createId(Object object) {
        LocalDateTime now = LocalDateTime.now();
        String id = object.getClass().getSimpleName().toUpperCase() + now.format(formatter) + currentId;
        currentId++;
        return id;
    }
}
