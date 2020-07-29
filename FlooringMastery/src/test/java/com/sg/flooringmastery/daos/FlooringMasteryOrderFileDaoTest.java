/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
public class FlooringMasteryOrderFileDaoTest {

    AnnotationConfigApplicationContext ctx;
    FlooringMasteryOrderFileDao toTest;

    public FlooringMasteryOrderFileDaoTest() throws IOException {
        ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(
                new ResourcePropertySource(new ClassPathResource("application.properties")));
        //tells where to start looking (which folder/package)
        ctx.scan("com.sg.flooringmastery");
        //loads up the beans
        ctx.refresh();
        toTest = ctx.getBean(FlooringMasteryOrderFileDao.class);
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        //define the paths to the files
        Path testFolder = Paths.get("TestOrders");
        Path seedFolder = Paths.get("SeedOrders");
        //convert to file
        File testFile = testFolder.toFile();
        File seedFile = seedFolder.toFile();
        //get list of all the files in the folder
        File[] testFiles = testFile.listFiles();
        File[] seedFiles = seedFile.listFiles();
        //delete all files in TestFolder
        for (File toDelete : testFiles) {
            toDelete.delete();
        }
        //copy the files from the seed into TestFolder
        for (File toCopy : seedFiles) {
            Files.copy(toCopy.toPath(), Paths.get(testFolder.toString(), toCopy.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetOrdersGoldenPath() {
        LocalDate date = LocalDate.of(2023, 3, 20);
        List<Order> toCheck = toTest.getOrders(date);
        assertEquals(2, toCheck.size());
        assertEquals(1, toCheck.get(0).getOrderNumber());
        assertEquals("Cherry Hardwood", toCheck.get(0).getProduct().getName());
        assertEquals(new BigDecimal("3882.38"), toCheck.get(0).getTotalCost());
        assertEquals(3, toCheck.get(1).getOrderNumber());
        assertEquals("Marble Tile", toCheck.get(1).getProduct().getName());
        assertEquals(new BigDecimal("1583.71"), toCheck.get(1).getTotalCost());
    }

    @Test
    public void testGetOrdersMissingFile() throws IOException {
        List<Order> toCheck = toTest.getOrders(LocalDate.of(2222, 1, 1));
        assertTrue(toCheck.isEmpty());
    }

    @Test
    public void testAddOrderGoldenPath() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        t.setStateAbv("MN");
        t.setState("Minnesota");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));
        toAdd.setCalculations();

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            List<Order> allOrders = toTest.getOrders(LocalDate.of(2023, 3, 20));
            //asserts
            assertEquals(4, allOrders.get(2).getOrderNumber());
            assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(2).getDate());
            assertEquals("Company X", allOrders.get(2).getName());
            assertEquals("MN", allOrders.get(2).getTax().getStateAbv());
            assertEquals("Cherry Hardwood", allOrders.get(2).getProduct().getName());
            assertEquals(new BigDecimal("100"), allOrders.get(2).getArea());
            assertEquals(new BigDecimal("855.31"), allOrders.get(2).getTotalCost());
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Shoud not hit FlooringMasteryOrderDaoException on golden path addOrder.\n");
        }
    }
    
    @Test
    public void testAddOrderNullName() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName(null);
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null name addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }

    @Test
    public void testAddOrderNullDate() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(null);
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null date addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullState() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv(null);
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null state addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullProduct() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName(null);
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null product addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullArea() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(null);
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null area addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullMaterialCost() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(null);
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null material cost addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullLaborCost() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(null);
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null labor cost addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullTaxCost() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(null);
        toAdd.setTotalCost(new BigDecimal("855.31"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null tax cost addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testAddOrderNullTotalCost() {
        Order toAdd = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toAdd.setProduct(p);
        toAdd.setTax(t);
        toAdd.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setMaterialCost(new BigDecimal("3.50"));
        toAdd.setLaborCost(new BigDecimal("4.55"));
        toAdd.setTaxCost(new BigDecimal("27.30"));
        toAdd.setTotalCost(null);
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Cherry Hardwood");
        p.setProductCost(new BigDecimal("3.50"));
        p.setLaborCost(new BigDecimal("4.55"));

        try {
            //add the order and get the order by date
            toTest.addOrder(toAdd);
            fail("Should hit FlooringMasteryOrderDaoException on null total cost addOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderGoldenPath() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        t.setStateAbv("MN");
        t.setState("Minnesota");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));
        toEdit.setCalculations();

        try {
            //check order before edit method is called
            List<Order> allOrders = toTest.getOrders(LocalDate.of(2023, 3, 20));
            assertEquals(1, allOrders.get(0).getOrderNumber());
            assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(0).getDate());
            assertEquals("Company Y", allOrders.get(0).getName());
            assertEquals("MN", allOrders.get(0).getTax().getStateAbv());
            assertEquals("Cherry Hardwood", allOrders.get(0).getProduct().getName());
            assertEquals(new BigDecimal("315"), allOrders.get(0).getArea());
            assertEquals(new BigDecimal("3882.38"), allOrders.get(0).getTotalCost());
            //action
            toTest.editOrder(toEdit);
            //check orders after edit method is called
            allOrders = toTest.getOrders(LocalDate.of(2023, 3, 20));
            assertEquals(1, allOrders.get(0).getOrderNumber());
            assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(0).getDate());
            assertEquals("Company Y", allOrders.get(0).getName());
            assertEquals("MN", allOrders.get(0).getTax().getStateAbv());
            assertEquals("Marble Tile", allOrders.get(0).getProduct().getName());
            assertEquals(new BigDecimal("300"), allOrders.get(0).getArea());
            assertEquals(new BigDecimal("3359.63"), allOrders.get(0).getTotalCost());
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Shoud not hit FlooringMasteryOrderDaoException on golden path editOrder.\n");
        }
    }
    
    @Test
    public void testEditOrderNullName() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName(null);
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null name editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullDate() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(null);
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null date editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullState() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv(null);
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null state editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullProductName() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName(null);
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null product name editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullArea() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(null);
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null area editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullProductCost() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(null);
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null product cost editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullLaborCost() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(null);
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null labor cost editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullTaxCost() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(null);
        toEdit.setTotalCost(new BigDecimal("3359.63"));
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null tax cost editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }
    
    @Test
    public void testEditOrderNullTotalCost() {
        Order toEdit = new Order();
        Product p = new Product();
        Tax t = new Tax();
        toEdit.setProduct(p);
        toEdit.setTax(t);
        toEdit.setOrderNumber(1);
        toEdit.setDate(LocalDate.parse("03/20/2023",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toEdit.setName("Company Y");
        toEdit.setArea(new BigDecimal("300"));
        toEdit.setMaterialCost(new BigDecimal("5.55"));
        toEdit.setLaborCost(new BigDecimal("4.99"));
        toEdit.setTaxCost(new BigDecimal("27.30"));
        toEdit.setTotalCost(null);
        t.setStateAbv("MN");
        t.setTaxRate(new BigDecimal("6.25"));
        p.setName("Marble Tile");
        p.setProductCost(new BigDecimal("5.55"));
        p.setLaborCost(new BigDecimal("4.99"));

        try {
            //action
            toTest.editOrder(toEdit);
            fail("Should hit FlooringMasteryOrderDaoException on null total cost editOrder test.\n");
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }

    @Test
    public void testDeleteOrderGoldenPath() {

        List<Order> allOrders = toTest.getOrders(LocalDate.of(2023, 3, 20));
        assertEquals(2, allOrders.size());
        assertEquals(1, allOrders.get(0).getOrderNumber());
        assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(0).getDate());
        assertEquals("Company Y", allOrders.get(0).getName());
        assertEquals(new BigDecimal("3882.38"), allOrders.get(0).getTotalCost());
        assertEquals(3, allOrders.get(1).getOrderNumber());
        assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(1).getDate());
        assertEquals("Name Co.", allOrders.get(1).getName());
        assertEquals(new BigDecimal("1583.71"), allOrders.get(1).getTotalCost());
        try {
            toTest.deleteOrder(LocalDate.of(2023, 3, 20), 3);
            allOrders = toTest.getOrders(LocalDate.of(2023, 3, 20));
            assertEquals(1, allOrders.size());
            assertEquals(1, allOrders.get(0).getOrderNumber());
            assertEquals(LocalDate.of(2023, 3, 20), allOrders.get(0).getDate());
            assertEquals("Company Y", allOrders.get(0).getName());
            assertEquals(new BigDecimal("3882.38"), allOrders.get(0).getTotalCost());
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit FlooringMasteryOrderDaoException on golden path deleteOrder.\n");
        }
    }
    
    @Test
    public void testDeleteOrderDoesntExist() {        
        try {
            toTest.deleteOrder(LocalDate.of(2023, 3, 20), 10);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
        }
    }

}
