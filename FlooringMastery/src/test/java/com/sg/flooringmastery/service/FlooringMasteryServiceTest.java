/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.service;

import com.sg.flooringmastery.daos.FlooringMasteryInMemOrderDao;
import com.sg.flooringmastery.daos.FlooringMasteryInMemProductDao;
import com.sg.flooringmastery.daos.FlooringMasteryInMemTaxDao;
import com.sg.flooringmastery.daos.FlooringMasteryOrderDaoException;
import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author codedchai
 */
public class FlooringMasteryServiceTest {

    AnnotationConfigApplicationContext ctx;
    FlooringMasteryService toTest = new FlooringMasteryService(new FlooringMasteryInMemOrderDao(),
            new FlooringMasteryInMemProductDao(), new FlooringMasteryInMemTaxDao());

//    public FlooringMasteryServiceTest() {
//        ctx = new AnnotationConfigApplicationContext();
//        //tells where to start looking (which folder/package)
//        ctx.scan("com.sg.flooringmastery");
//        //loads up the beans
//        ctx.refresh();
//        toTest = ctx.getBean(FlooringMasteryService.class, FlooringMasteryInMemOrderDao.class,
//                FlooringMasteryInMemProductDao.class, FlooringMasteryInMemTaxDao.class);
//    }
    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testGetOrders() {
    }

    @Test
    public void testAddOrderGoldenPath() {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //create a second order for the same day
        Order toAdd2 = new Order();
        toAdd2.setTax(new Tax());
        toAdd2.setProduct(new Product());
        toAdd2.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd2.setName("Company Y");
        toAdd2.getTax().setState("Colorado");
        toAdd2.getTax().setStateAbv("CO");
        toAdd2.getTax().setTaxRate(new BigDecimal("7.08"));
        toAdd2.getProduct().setName("Marble Tile");
        toAdd2.getProduct().setLaborCost(new BigDecimal("7.60"));
        toAdd2.getProduct().setProductCost(new BigDecimal("6.86"));
        toAdd2.setArea(new BigDecimal("500"));
        toAdd2.setCalculations();
        try {
            //add the two orders and get the orders by date
            toTest.addOrder(toAdd);
            toTest.addOrder(toAdd2);
            List<Order> allOrders = toTest.getOrders(LocalDate.of(2021, 2, 11));
            //check first addition to allOrders
            assertEquals(1, allOrders.get(0).getOrderNumber());
            assertEquals(LocalDate.of(2021, 2, 11), allOrders.get(0).getDate());
            assertEquals("Company X", allOrders.get(0).getName());
            assertEquals("Minnesota", allOrders.get(0).getTax().getState());
            assertEquals("MN", allOrders.get(0).getTax().getStateAbv());
            assertEquals("Minnesota", allOrders.get(0).getTax().getState());
            assertEquals(new BigDecimal("6.25"), allOrders.get(0).getTax().getTaxRate());
            assertEquals("Cherry Hardwood", allOrders.get(0).getProduct().getName());
            assertEquals(new BigDecimal("100"), allOrders.get(0).getArea());
            assertEquals(new BigDecimal("990.25"), allOrders.get(0).getTotalCost());
            //check second addition to allOrders
            assertEquals(2, allOrders.get(1).getOrderNumber());
            assertEquals(LocalDate.of(2021, 2, 11), allOrders.get(1).getDate());
            assertEquals("Company Y", allOrders.get(1).getName());
            assertEquals("Colorado", allOrders.get(1).getTax().getState());
            assertEquals("CO", allOrders.get(1).getTax().getStateAbv());
            assertEquals("Colorado", allOrders.get(1).getTax().getState());
            assertEquals(new BigDecimal("7.08"), allOrders.get(1).getTax().getTaxRate());
            assertEquals("Marble Tile", allOrders.get(1).getProduct().getName());
            assertEquals(new BigDecimal("500"), allOrders.get(1).getArea());
            assertEquals(new BigDecimal("7741.88"), allOrders.get(1).getTotalCost());

        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in Golden Path test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in Golden Path test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in Golden Path test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in Golden Path test.\n");
        }
    }

    @Test
    public void testAddOrderInvalidNameInput() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company, X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid input test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid input test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testAddOrderInvalidState() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Hawaii");
        toAdd.getTax().setStateAbv("HI");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit InvalidInputException.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid state test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid state test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testAddOrderInvalidProduct() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Bamboo");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            toTest.addOrder(toAdd);
            fail("Should hit InvalidInputException.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid product test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid product test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testAddOrderInvalidArea() throws FlooringMasteryOrderDaoException {
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("99"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit InsufficientAreaException.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid area test.\n");
        } catch (InsufficientAreaException ex) {
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in invalid area test.\n");
        }
    }

    @Test
    public void testAddOrderBlankNameInput() throws FlooringMasteryOrderDaoException {
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in blank input test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in blank input test.\n");
        }
    }

    @Test
    public void testAddOrderPastDateInput() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2019",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit InvalidInputException.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in past date test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in past date test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testAddOrderNullDate() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(null);
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null date test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null date test.\n");
        }
    }

    @Test
    public void testAddOrderNullName() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName(null);
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null name test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null name test.\n");
        }
    }

    @Test
    public void testAddOrderNullState() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState(null);
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null state test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null state test.\n");
        }
    }

    @Test
    public void testAddOrderNullProduct() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName(null);
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null product test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null product test.\n");
        }
    }

    @Test
    public void testAddOrderNullArea() throws FlooringMasteryOrderDaoException {
        //create an order
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setTaxRate(new BigDecimal("7.55"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.66"));
        toAdd.getProduct().setProductCost(new BigDecimal("3.66"));
        toAdd.setArea(null);
        try {
            //complete the order
            toTest.addOrder(toAdd);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null area test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null area test.\n");
        }
    }

    @Test
    public void testEditOrderGoldenPath() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            //get the edited order by date
            List<Order> allOrders = toTest.getOrders(LocalDate.of(2021, 2, 11));
            //check first addition to allOrders
            assertEquals(1, allOrders.get(0).getOrderNumber());
            assertEquals(LocalDate.of(2021, 2, 11), allOrders.get(0).getDate());
            assertEquals("Company Z", allOrders.get(0).getName());
            assertEquals("CO", allOrders.get(0).getTax().getStateAbv());
            assertEquals("Colorado", allOrders.get(0).getTax().getState());
            assertEquals(new BigDecimal("7.08"), allOrders.get(0).getTax().getTaxRate());
            assertEquals("Marble Tile", allOrders.get(0).getProduct().getName());
            assertEquals(new BigDecimal("150"), allOrders.get(0).getArea());
            assertEquals(new BigDecimal("1906.56"), allOrders.get(0).getTotalCost());
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in Golden Path edit test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in Golden Path edit test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in Golden Path edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in Golden Path edit test.\n");
        }
    }

    @Test
    public void testEditOrderInvalidName() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InvalidInputException, InsufficientAreaException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company, Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in invalid name edit test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid name edit test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid name edit test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testEditOrderInvalidState() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Vermont");
        newOrder.getTax().setStateAbv("VT");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in invalid state edit test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid state edit test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid state edit test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testEditOrderInvalidProduct() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Grass");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in invalid product edit test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid product edit test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid product edit test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testEditOrderInvalidArea() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("50"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in invalid area edit test.\n");
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid area edit test.\n");
        } catch (InsufficientAreaException ex) {
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in invalid area edit test.\n");
        }
    }

    @Test
    public void testEditOrderBlankNameInput() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in blank input edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in blank input edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in blank input edit test.\n");
        }
    }

    @Test
    public void testEditOrderNullDate() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(null);
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in null date edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null date edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null date edit test.\n");
        }
    }

    @Test
    public void testEditOrderNullName() throws IncompleteOrderException, InvalidInputException, InsufficientAreaException, FlooringMasteryOrderDaoException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName(null);
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in null name edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in name input edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null name edit test.\n");
        }
    }

    @Test
    public void testEditOrderNullState() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState(null);
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in null state edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in state input edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null state edit test.\n");
        }
    }

    @Test
    public void testEditOrderNullProduct() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName(null);
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(new BigDecimal("150"));
        newOrder.setCalculations();
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in null product edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null product edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null prodct edit test.\n");
        }
    }

    @Test
    public void testEditOrderNullArea() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();
        //edited information
        Order newOrder = new Order();
        newOrder.setTax(new Tax());
        newOrder.setProduct(new Product());
        newOrder.setOrderNumber(1);
        newOrder.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        newOrder.setName("Company Z");
        newOrder.getTax().setTaxRate(new BigDecimal("7.08"));
        newOrder.getTax().setState("Colorado");
        newOrder.getTax().setStateAbv("CO");
        newOrder.getProduct().setName("Marble Tile");
        newOrder.getProduct().setLaborCost(new BigDecimal("5.55"));
        newOrder.getProduct().setProductCost(new BigDecimal("6.32"));
        newOrder.setArea(null);
        //add the initial order 
        toTest.addOrder(toAdd);
        try {
            //edit the inital order with the new order            
            toTest.editOrder(newOrder);
            fail();
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in null area edit test.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null area edit test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null area edit test.\n");
        }
    }

    @Test
    public void testDeleteOrderGoldenPath() throws FlooringMasteryOrderDaoException, IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        //initial order information
        Order toAdd = new Order();
        toAdd.setTax(new Tax());
        toAdd.setProduct(new Product());
        toAdd.setDate(LocalDate.parse("02/11/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        toAdd.setName("Company X");
        toAdd.getTax().setStateAbv("MN");
        toAdd.getTax().setState("Minnesota");
        toAdd.getTax().setTaxRate(new BigDecimal("6.25"));
        toAdd.getProduct().setName("Cherry Hardwood");
        toAdd.getProduct().setLaborCost(new BigDecimal("5.55"));
        toAdd.getProduct().setProductCost(new BigDecimal("6.32"));
        toAdd.setArea(new BigDecimal("100"));
        toAdd.setCalculations();

        toTest.addOrder(toAdd);
        try {

            toTest.deleteOrder(LocalDate.of(2021, 2, 11), 1);
            //try and get the deleted order by date
            List<Order> allOrders = toTest.getOrders(LocalDate.of(2021, 2, 11));
            //asserts
            assertTrue(allOrders.isEmpty());
        } catch (FlooringMasteryOrderDaoException ex) {
            fail("Should not hit OrderDaoException in Golden Path delete test.\n");
        } 
    }

    @Test
    public void testGetStatesGoldenPath() {

        List<String> toCheck = toTest.getStates();
        assertEquals(4, toCheck.size());
        assertEquals("MN", toCheck.get(0));
        assertEquals("Minnesota", toCheck.get(1));
        assertEquals("CO", toCheck.get(2));
        assertEquals("Colorado", toCheck.get(3));
    }

    @Test
    public void testGetProductsGoldenPath() {
        List<Product> toCheck = toTest.getProducts();
        assertEquals(2, toCheck.size());
        assertEquals("Cherry Hardwood", toCheck.get(0).getName());
        assertEquals(new BigDecimal("5.05"), toCheck.get(0).getLaborCost());
        assertEquals(new BigDecimal("6.55"), toCheck.get(0).getProductCost());
        assertEquals("Marble Tile", toCheck.get(1).getName());
        assertEquals(new BigDecimal("5.80"), toCheck.get(1).getLaborCost());
        assertEquals(new BigDecimal("8.99"), toCheck.get(1).getProductCost());
    }

    @Test
    public void testCompleteOrderGoldenPath() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getTax().setState("Minnesota");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            //asserts
            assertEquals(LocalDate.of(2021, 11, 28), order.getDate());
            assertEquals("Name Co", order.getName());
            assertEquals("MN", order.getTax().getStateAbv());
            assertEquals("Minnesota", order.getTax().getState());
            assertEquals(new BigDecimal("6.25"), order.getTax().getTaxRate());
            assertEquals("Cherry Hardwood", order.getProduct().getName());
            assertEquals(new BigDecimal("100"), order.getArea());
            assertEquals(new BigDecimal("1232.50"), order.getTotalCost());
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in Golden Path complete order test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in Golden Path complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in Golden Path complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderPastDate() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/1990",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in past date complete order test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in past date complete order test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testCompleteOrderInvalidName() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name, Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid name complete order test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid name complete order test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testCompleteOrderInvalidState() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setState("Wyoming");
        order.getTax().setStateAbv("WY");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid state complete order test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid state complete order test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testCompleteOrderInvalidProduct() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cheap Tile");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid product complete order test.\n");
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in invalid product complete order test.\n");
        } catch (InvalidInputException ex) {
        }
    }

    @Test
    public void testCompleteOrderInvalidArea() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("-2"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
            fail("Should not hit IncompleteOrderException in invalid area complete order test.\n");
        } catch (InsufficientAreaException ex) {
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in invalid area complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderBlankInput() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail();
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in blank input complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in blank input complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderNullDate() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(null);
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null date complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null date complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderNullName() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName(null);
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null name complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null name complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderNullState() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setState(null);
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null state complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null state complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderNullProduct() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName(null);
        order.setArea(new BigDecimal("100"));
        try {
            //complete the order
            toTest.completeOrder(order);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null product complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null product complete order test.\n");
        }
    }

    @Test
    public void testCompleteOrderNullArea() {
        //order information
        Order order = new Order();
        order.setTax(new Tax());
        order.setProduct(new Product());
        order.setDate(LocalDate.parse("11/28/2021",
                DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        order.setName("Name Co");
        order.getTax().setStateAbv("MN");
        order.getProduct().setName("Cherry Hardwood");
        order.setArea(null);
        try {
            //complete the order
            toTest.completeOrder(order);
            fail("Should hit IncompleteOrderException.\n");
        } catch (IncompleteOrderException ex) {
        } catch (InsufficientAreaException ex) {
            fail("Should not hit InsufficientAreaException in null area complete order test.\n");
        } catch (InvalidInputException ex) {
            fail("Should not hit InvalidInputException in null area complete order test.\n");
        }
    }
}
