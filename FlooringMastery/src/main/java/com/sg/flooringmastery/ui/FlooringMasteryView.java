/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public class FlooringMasteryView {

    UserIO io = new UserIO();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public void displayMenu() {
        io.print("* * * * * *  * * * * *\n");
        io.print("*  Flooring Program  *\n");
        io.print("*  -  -  -  -  -  -  *\n");
        io.print("* 1. Display Orders  *\n");
        io.print("* 2. Add an Order    *\n");
        io.print("* 3. Edit an Order   *\n");
        io.print("* 4. Remove an Order *\n");
        io.print("* 5. Quit            *\n");
        io.print("* * * * * *  * * * * *\n");
    }

    public int getMenuChoice() {
        return io.readInt("Please select an option: ", 1, 5);
    }

    public LocalDate getDate() {
        return io.readLocalDate("What is the date would you like orders for?\n"
                + "Enter as MM/dd/yyyy: ");
    }

    public void displayNoOrders(LocalDate date) {
        io.print("No orders were found for " + formatter.format(date) + ".\n");
    }

    public void displayOrders(List<Order> toDisplay) {
        io.print("\nOrders for date " + formatter.format(toDisplay.get(0).getDate())
                + "\n");
        for (Order order : toDisplay) {
            io.print("   Order number: " + order.getOrderNumber() + "\n"
                    + "   Name: " + order.getName() + "\n"
                    + "   State: " + order.getTax().getStateAbv() + "\n"
                    + "   Product: " + order.getProduct().getName() + "\n"
                    + "   Area: " + order.getArea() + " sq ft\n"
                    + "   Total cost: " + order.getTotalCost() + "\n\n");
        }
    }

    public Order getOrder(List<String> states, List<Product> products) {
        Order toReturn = new Order();
        boolean valid = false;
        //get needed order information
        LocalDate date = io.readFutureLocalDate("What is the order date: ");
        String name = io.readName("Whose name is the order under: ");
        String state = io.readState("What state will the order be in: ", states);
        String product = io.readProduct("Which product would you like: ", products);
        BigDecimal area = io.readArea("What is the area you'd like to cover: ");
        //create order
        toReturn.setDate(date);
        toReturn.setName(name);
        toReturn.setTax(new Tax());
        toReturn.getTax().setState(state);
        toReturn.setProduct(new Product());
        toReturn.getProduct().setName(product);
        toReturn.setArea(area);
        return toReturn;
    }

    public String displayOrder(Order toDisplay, String action) {
        io.print("\nOrder summary:\n");
        io.print("   Date: " + formatter.format(toDisplay.getDate()) + "\n"
                + "   Name: " + toDisplay.getName() + "\n"
                + "   Product: " + toDisplay.getProduct().getName() + "\n"
                + "   State: " + toDisplay.getTax().getStateAbv() + "\n"
                + "   Area: " + toDisplay.getArea() + " sq ft\n"
                + "   Total cost: $" + toDisplay.getTotalCost() + "\n");
        return io.readString("Would you like to " + action + " this order? Yes or no: ");
    }

    public void comfirmMessage(Order toDisplay, String action) {
        io.print("The order for " + toDisplay.getName() + " with order number "
                + toDisplay.getOrderNumber() + " was " + action + ".\n");
    }

    public Order editOrder(Order toEdit, List<String> states, List<Product> products) {
        Order editedOrder = toEdit;
//        LocalDate date = io.editFutureLocalDate("Enter a new order date or press"
//                + " \"Enter\" to keep (" + formatter.format(toEdit.getDate()) +
//                "): ", toEdit.getDate());
        String name = io.readName("Enter a new order name or press "
                + "\"Enter\" to keep (" + toEdit.getName() + "): ");
        String state = io.readState("Enter a new state or press "
                + "\"Enter\" to keep (" + toEdit.getTax().getStateAbv() + "): ", states);
        String product = io.readProduct("Enter a new product name or press "
                + "\"Enter\" to keep (" + toEdit.getProduct().getName() + "): ", products);
        BigDecimal area = io.editArea("Enter a new area in sq ft or press "
                + "\"Enter\" to keep (" + toEdit.getArea() + " sq ft): ", toEdit.getArea());
//        if (!date.equals(toEdit.getDate())) {
//            editedOrder.setDate(date);
//        }
        if (!name.isEmpty()) {
            editedOrder.setName(name);
        }
        if (!state.isEmpty()) {
            editedOrder.getTax().setState(state);
        }
        if (!product.isEmpty()) {
            editedOrder.getProduct().setName(product);
        }
        if (!area.equals(toEdit.getArea())) {
            editedOrder.setArea(area);
        }
        
        return editedOrder;
    }

    public void displayErrorMessage(String message) {
        io.print(message + "\n");
    }

    public Order getOrderByNumber(List<Order> orders) {
        Order toReturn = new Order();
        boolean valid = false;
        int maxOrderNum = 0;
        for (Order o : orders) {
            if (maxOrderNum < o.getOrderNumber()) {
                maxOrderNum = o.getOrderNumber();
            }
        }
        while (!valid) {
            int orderNum = io.readInt("What is the order number: ", 1, maxOrderNum + 1);
            for (Order o : orders) {
                if (orderNum == o.getOrderNumber()) {
                    toReturn = o;
                    valid = true;
                }
            }
            if (!valid) {
                io.print("No matching order was found with order number " + orderNum
                        + ". Please reference a valid order.\n");
            }
        }
        return toReturn;
    }

    public int getOrderNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
