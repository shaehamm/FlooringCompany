/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.daos.FlooringMasteryOrderDaoException;
import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.service.FlooringMasteryService;
import com.sg.flooringmastery.service.IncompleteOrderException;
import com.sg.flooringmastery.service.InsufficientAreaException;
import com.sg.flooringmastery.service.InvalidInputException;
import com.sg.flooringmastery.ui.FlooringMasteryView;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public class FlooringMasteryController {

    FlooringMasteryService service;
    FlooringMasteryView view;

    @Autowired
    public FlooringMasteryController(FlooringMasteryService service,
            FlooringMasteryView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        //global variables
        boolean exit = false;
        //loops until the user exits the program
        while (!exit) {
            view.displayMenu();
            int userChoice = view.getMenuChoice();
            //based on the user's input
            try {
                switch (userChoice) {
                    case 1:
                        displayAllOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        deleteOrder();
                        break;
                    case 5:
                        exit = true;
                        break;
                }
            } catch (FlooringMasteryOrderDaoException | IncompleteOrderException |
                    InvalidInputException | InsufficientAreaException ex) {
                view.displayErrorMessage(ex.getMessage());
            }
        }
    }

    //displays all orders for a specified date
    private void displayAllOrders() {
        LocalDate date = view.getDate();
        List<Order> toDisplay = service.getOrders(date);
        if (toDisplay.isEmpty()) {
            view.displayNoOrders(date);
        } else {
            view.displayOrders(toDisplay);
        }
    }

    //gets order info from the user and adds it to an order file
    private void addOrder() throws FlooringMasteryOrderDaoException, IncompleteOrderException, 
            InsufficientAreaException, InvalidInputException {
        //need available states and products from the files
        List<String> validStates = service.getStates();
        List<Product> validProducts = service.getProducts();
        //get user input from view and check validity
        Order toAdd = view.getOrder(validStates, validProducts);
        //display order and check if user wants to add the order or not
        service.completeOrder(toAdd);
        String userResponse = view.displayOrder(toAdd, "place");
        if (userResponse.equalsIgnoreCase("yes")) {
            service.addOrder(toAdd);
            view.comfirmMessage(toAdd, "added");
        } else {
            view.comfirmMessage(toAdd, " not added");
        }
    }

    //gets existing order and allows user to edit name, state, product, area
    private void editOrder() throws FlooringMasteryOrderDaoException, IncompleteOrderException, 
            InsufficientAreaException, InvalidInputException {
        //need available states and products from the files
        List<String> validStates = service.getStates();
        List<Product> validProducts = service.getProducts();
        LocalDate date = view.getDate();
        //List<Order> orders = service.getOrder(date);
        int orderNumber = view.getOrderNumber();
        Order toEdit = service.getOrder(date, orderNumber);
        Order newOrder = view.editOrder(toEdit, validStates, validProducts);
        service.completeOrder(newOrder);
        String userResponse = view.displayOrder(newOrder, "edit");
        if (userResponse.equalsIgnoreCase("yes")) {
            service.editOrder(newOrder);
            view.comfirmMessage(newOrder, "edited");
        } else {
            view.comfirmMessage(newOrder, "not edited");
        }
    }

    private void deleteOrder() throws FlooringMasteryOrderDaoException {
        LocalDate date = view.getDate();
    //    List<Order> orders = service.getOrder(date);
     //   Order toDelete = view.getOrderByNumber(orders);
     int orderNumber = view.getOrderNumber();
     Order toDelete = service.getOrder(date, orderNumber);
        String userResponse = view.displayOrder(toDelete, "remove");
        if (userResponse.equalsIgnoreCase("yes")) {
            service.deleteOrder(date, orderNumber);
            view.comfirmMessage(toDelete, "deleted");
        } else {
            view.comfirmMessage(toDelete, "not deleted");
        }
    }
}
