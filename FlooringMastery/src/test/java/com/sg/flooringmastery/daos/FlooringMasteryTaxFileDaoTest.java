/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Tax;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 *
 * @author codedchai
 */
public class FlooringMasteryTaxFileDaoTest {
    
    AnnotationConfigApplicationContext ctx;
    FlooringMasteryTaxFileDao toTest;

    public FlooringMasteryTaxFileDaoTest() throws IOException {
        ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(
                new ResourcePropertySource(new ClassPathResource("application.properties")));
        //tells where to start looking (which folder/package)
        ctx.scan("com.sg.flooringmastery");
        //loads up the beans
        ctx.refresh();
        toTest = ctx.getBean(FlooringMasteryTaxFileDao.class);
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() throws IOException {
        Path testPath = Paths.get("testtaxes.txt");
        Path seedPath = Paths.get("seedtaxes.txt");
        Files.deleteIfExists(testPath);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetTaxesGoldenPath() {
        List<Tax> toCheck = toTest.getTaxes();
        assertEquals(4, toCheck.size());
        assertEquals("MN", toCheck.get(0).getStateAbv());
        assertEquals(new BigDecimal("6.25"), toCheck.get(0).getTaxRate());
        assertEquals("Minnesota", toCheck.get(0).getState());
        assertEquals("CO", toCheck.get(1).getStateAbv());
        assertEquals(new BigDecimal("7.08"), toCheck.get(1).getTaxRate());
        assertEquals("HI", toCheck.get(2).getStateAbv());
        assertEquals(new BigDecimal("10.15"), toCheck.get(2).getTaxRate());
        assertEquals("AZ", toCheck.get(3).getStateAbv());
        assertEquals(new BigDecimal("7.10"), toCheck.get(3).getTaxRate());
        assertEquals("Arizona", toCheck.get(3).getState());
    }
    
    @Test
    public void testGetTaxesMissingFile() throws IOException {
        AnnotationConfigApplicationContext ctxBad = new AnnotationConfigApplicationContext();
        ctxBad.getEnvironment().getPropertySources().addFirst(
                new ResourcePropertySource(new ClassPathResource("badsettings.properties")));
        //tells where to start looking (which folder/package)
        ctxBad.scan("com.sg.flooringmastery");
        //loads up the beans
        ctxBad.refresh();
        FlooringMasteryTaxFileDao toTestBad = ctxBad.getBean(FlooringMasteryTaxFileDao.class);
        List<Tax> toCheck = toTestBad.getTaxes();
        assertTrue(toCheck.isEmpty());
    }
    
}
