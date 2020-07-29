/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Order;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public interface FlooringMasteryOrderDao {
   
    public List<Order> getOrders(LocalDate date);
    
    public Order getOrder(LocalDate date, int orderNumber);
    
    public void addOrder(Order toAdd) throws FlooringMasteryOrderDaoException;
    
    public void editOrder(Order newOrder) throws FlooringMasteryOrderDaoException;

    public void deleteOrder(LocalDate date, int orderNumber) throws FlooringMasteryOrderDaoException;
    
}
