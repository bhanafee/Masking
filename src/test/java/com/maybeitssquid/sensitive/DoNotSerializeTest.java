package com.maybeitssquid.sensitive;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class DoNotSerializeTest {

    @Test
    void get() {
        String value = "secret";
        DoNotSerialize<String> wrapper = new DoNotSerialize<>(value);
        assertEquals(value, wrapper.get());
        assertSame(value, wrapper.get());
    }

    @Test
    void getWithNull() {
        DoNotSerialize<String> wrapper = new DoNotSerialize<>(null);
        assertNull(wrapper.get());
    }

    @Test
    void serializationFails() {
        String value = "secret";
        DoNotSerialize<String> wrapper = new DoNotSerialize<>(value);

        assertThrows(NotSerializableException.class, () -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(wrapper);
        });
    }
}
