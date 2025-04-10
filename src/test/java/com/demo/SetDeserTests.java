package com.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetDeserTests {
    @Test
    public void test() throws Exception {
        var om = new ObjectMapper();
        var s = "{\"values\":[\"a\",\"b\",\"a\"]}";
        var a = om.readValue(s, A.class);
        assertEquals(2, a.values.size());

        s = "{\"values\":[\"a\",\"b\",\"c\"]}";
        a = om.readValue(s, A.class);
        assertEquals(3, a.values.size());
    }

    @Getter
    @Setter
    static class A {
        Set<String> values;
    }
}
