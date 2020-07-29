/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Product;
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
public class FlooringMasteryProductFileDaoTest {

    AnnotationConfigApplicationContext ctx;
    FlooringMasteryProductFileDao toTest;

    public FlooringMasteryProductFileDaoTest() throws IOException {
        ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(
                new ResourcePropertySource(new ClassPathResource("application.properties")));
        //tells where to start looking (which folder/package)
        ctx.scan("com.sg.flooringmastery");
        //loads up the beans
        ctx.refresh();
        toTest = ctx.getBean(FlooringMasteryProductFileDao.class);
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        Path testPath = Paths.get("testproducts.txt");
        Path seedPath = Paths.get("seedproducts.txt");
        Files.deleteIfExists(testPath);
        Files.copy(seedPath, testPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetProductsGoldenPath() {
        List<Product> toCheck = toTest.getProducts();
        assertEquals(3, toCheck.size());
        assertEquals("Cherry Hardwood", toCheck.get(0).getName());
        assertEquals(new BigDecimal("5.05"), toCheck.get(0).getProductCost());
        assertEquals(new BigDecimal("6.55"), toCheck.get(0).getLaborCost());
        assertEquals("Cheap Plastic", toCheck.get(2).getName());
        assertEquals(new BigDecimal("4.00"), toCheck.get(2).getLaborCost());
    }
    
    @Test
    public void testGetProductsMissingFile() throws IOException {
        AnnotationConfigApplicationContext ctxBad = new AnnotationConfigApplicationContext();
        ctxBad.getEnvironment().getPropertySources().addFirst(
                new ResourcePropertySource(new ClassPathResource("badsettings.properties")));
        //tells where to start looking (which folder/package)
        ctxBad.scan("com.sg.flooringmastery");
        //loads up the beans
        ctxBad.refresh();
        FlooringMasteryProductFileDao toTestBad = ctxBad.getBean(FlooringMasteryProductFileDao.class);
        List<Product> toCheck = toTestBad.getProducts();
        assertTrue(toCheck.isEmpty());
    }

}
