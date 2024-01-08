package com.orzechazo.eshop.domain.helpers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdCreatorTest {

    @Test
    void createId() {
        String id1 = IdCreator.createId(new Object());
        String id2 = IdCreator.createId(new Object());

        assertNotEquals(id1,id2);
    }
}