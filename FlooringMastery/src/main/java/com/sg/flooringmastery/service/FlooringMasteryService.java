/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.service;

import com.sg.flooringmastery.daos.FlooringMasteryOrderDao;
import com.sg.flooringmastery.daos.FlooringMasteryOrderDaoException;
import com.sg.flooringmastery.daos.FlooringMasteryProductDao;
import com.sg.flooringmastery.daos.FlooringMasteryTaxDao;
import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public class FlooringMasteryService {

    FlooringMasteryOrderDao orderDao;
    FlooringMasteryProductDao productDao;
    FlooringMasteryTaxDao taxDao;

    @Autowired
    public FlooringMasteryService(FlooringMasteryOrderDao orderDao,
            FlooringMasteryProductDao productDao,
            FlooringMasteryTaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }

    public List<Order> getOrders(LocalDate date) {
        return orderDao.getOrders(date);
    }

    public void addOrder(Order toAdd) throws FlooringMasteryOrderDaoException, 
            IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        validateOrderInfo(toAdd);
        orderDao.addOrder(toAdd);
    }

    public void editOrder(Order newOrder) throws FlooringMasteryOrderDaoException, 
            IncompleteOrderException, InsufficientAreaException, InvalidInputException {
        validateOrderInfo(newOrder);
        orderDao.editOrder(newOrder);
    }

    public void deleteOrder(LocalDate date, int orderNumber) throws FlooringMasteryOrderDaoException {
        //validation
        orderDao.deleteOrder(date, orderNumber);
    }

    public List<String> getStates() {
        List<String> states = new ArrayList<>();
        List<Tax> taxes = taxDao.getTaxes();
        for (int i = 0; i < taxes.size(); i++) {
            states.add(taxes.get(i).getStateAbv());
            states.add(taxes.get(i).getState());
        }
        //returns list of abbreviated and actual valid state names
        return states;
    }

    public List<Product> getProducts() {
        return productDao.getProducts();
    }

    public void completeOrder(Order order) throws IncompleteOrderException, 
            InsufficientAreaException, InvalidInputException {
        if (order.getProduct().getName() == null || order.getTax().getStateAbv() == null) {
            throw new IncompleteOrderException("Order input cannot be null or blank.\n");
        }
        List<Product> products = getProducts();
        List<Tax> taxes = taxDao.getTaxes();
        String productName = order.getProduct().getName();
        String stateName = order.getTax().getStateAbv();

        for (Product p : products) {
            if (productName.equalsIgnoreCase(p.getName())) {
                order.setProduct(p);
            }
        }
        for (Tax t : taxes) {
            if (stateName.equalsIgnoreCase(t.getState())
                    || stateName.equalsIgnoreCase(t.getStateAbv())) {
                order.setTax(t);
            }
        }
        validateOrderInfo(order);
        order.setCalculations();
    }

    private void validateOrderInfo(Order order) throws IncompleteOrderException,
            InsufficientAreaException, InvalidInputException {
        if (order.getDate() == null || order.getName() == null
                || order.getProduct() == null || order.getTax() == null
                || order.getTax().getState() == null || order.getTax().getStateAbv() == null
                || order.getArea() == null || order.getProduct().getName() == null) {
            throw new IncompleteOrderException("All fields need to be filled "
                    + "out properly in order to complete an order.\n");
        }
        if (order.getDate().toString().isBlank() || order.getName().isBlank()
                || order.getProduct().getName().isBlank() || order.getTax().getState().isBlank()
                || order.getTax().getStateAbv().isBlank() || order.getArea().toString().isBlank()) {
            throw new IncompleteOrderException("All fields need to be filled "
                    + "out in order to complete an order.\n");
        }
        //info is not blank nor null, so can compare against business logic
        //validate date
        if (!order.getDate().isAfter(LocalDate.now())) {
            throw new InvalidInputException("The date entered cannot be today's"
            + " date or be in the past.\n");
        }
        //validate name
        if (order.getName().contains(",")) {
            throw new InvalidInputException("The name cannot contain a comma.\n");
        }
        //validate area
        if (order.getArea().compareTo(new BigDecimal("100")) == -1) {
            throw new InsufficientAreaException("A minimum area of 100 sq ft is"
                    + " required.\n");
        }
        //validate state
        boolean validState = false;
        List<Tax> taxes = taxDao.getTaxes();
        for (Tax t : taxes) {
            if (t.getState().equalsIgnoreCase(order.getTax().getState())
                    || t.getStateAbv().equalsIgnoreCase(order.getTax().getStateAbv())) {
                validState = true;
            }
        }
        if (!validState) {
            throw new InvalidInputException("We cannot sell at the selected state"
                    + " or it does not exist.\n");
        }
        //validate product
        boolean validProduct = false;
        List<Product> products = getProducts();

        for (Product p : products) {
            if (order.getProduct().getName().equalsIgnoreCase(p.getName())) {
                validProduct = true;
            }
        }
        if (!validProduct) {
            throw new InvalidInputException("The product selected is not available"
                    + " or does not exits.\n");
        }
    }
    

    public Order getOrder(LocalDate date, int orderNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
