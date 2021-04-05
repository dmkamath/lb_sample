package com.dk.lb.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringGeneratorTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void generate() {
        StringGenerator sg = new StringGenerator();
        String gvalue = sg.generate(15, true);
        assertNotNull(gvalue);
        assert gvalue.length() == 15 : "String length is not 15";
    }

    @Test
    void generatePersonName() {
        StringGenerator sg = new StringGenerator();
        sg.generatePersonName();
    }

    @Test
    void generateCompanyName() {
        StringGenerator sg = new StringGenerator();
        sg.generateCompanyName();
    }

    @Test
    void generateSimpleName() {
        StringGenerator sg = new StringGenerator();
        sg.genarateSimpleEnglishWord();
    }}