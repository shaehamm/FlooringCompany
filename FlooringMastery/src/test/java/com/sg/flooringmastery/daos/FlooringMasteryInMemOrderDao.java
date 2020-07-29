/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Order;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author codedchai
 */
public class FlooringMasteryInMemOrderDao implements FlooringMasteryOrderDao {

    List<Order> allOrders = new ArrayList<>();

    @Override
    public List<Order> getOrders(LocalDate date) {
        List<Order> ordersForDate = new ArrayList<>();
        for (Order o: allOrders) {
            if (o.getDate().compareTo(date) == 0) {
                ordersForDate.add(o);
            }
        }
        return ordersForDate;
    }

    @Override
    public void addOrder(Order toAdd) throws FlooringMasteryOrderDaoException {
        List<Order> ordersForDate = getOrders(toAdd.getDate());
        int orderNumber = 0;
        for (Order order : ordersForDate) {
            if (order.getOrderNumber() > orderNumber) {
                orderNumber = order.getOrderNumber();
            }
        }
        toAdd.setOrderNumber(orderNumber + 1);
        allOrders.add(toAdd);
    }

    @Override
    public void editOrder(Order newOrder) throws FlooringMasteryOrderDaoException {
        int index = 0;
        for(int i = 0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getOrderNumber() == newOrder.getOrderNumber()
                    && allOrders.get(index).getDate().compareTo(newOrder.getDate()) == 0) {
                index = i;
            }
        }
        allOrders.set(index, newOrder);
    }

    @Override
    public void deleteOrder(LocalDate date, int orderNumber) throws FlooringMasteryOrderDaoException {
        int index = 0;
        for(int i = 0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getOrderNumber() == orderNumber && 
                    allOrders.get(i).getDate().equals(date)) {
                index = i;
            }
        }
        allOrders.remove(index);
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
