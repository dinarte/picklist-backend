package com.quebecteh.commons.reflaction.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;

public class ClassFinderTest {

    @Entity
    private static class TestEntity1 {
    }

    @Entity
    private static class TestEntity2 {
    }

    @SuppressWarnings("unused")
    private static class TestEntity3 {
    }

    @Test
    public void testGetClasses() {
        // Arrange
        String pacote = "us.bave.commons.reflaction.utils";
        
        // Act
        Class<?>[] classes = EntityUtils.getClasses(pacote);

        // Assert
        assertNotNull(classes);
        assertArrayEquals(new Class<?>[]{TestEntity2.class, TestEntity1.class}, classes);
    }
}