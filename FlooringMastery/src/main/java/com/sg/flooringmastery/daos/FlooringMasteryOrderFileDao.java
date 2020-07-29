/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Order;
import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import com.sg.flooringmastery.service.IncompleteOrderException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public class FlooringMasteryOrderFileDao implements FlooringMasteryOrderDao {

    @Value("${orderDao.path}")
    String folder;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");

    @Override
    public List<Order> getOrders(LocalDate date) {
        List<Order> toReturn = new ArrayList<>();
        String path = folder + File.separator + "Orders_" + formatter.format(date) + ".txt";
        try {
            Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(path)));
            if (fileScanner.hasNextLine()) {
                String header = fileScanner.nextLine();
            }
            //loop until no more data
            while (fileScanner.hasNextLine()) {
                String row = fileScanner.nextLine();
                Order toAdd = convertLineToOrder(row);
                toAdd.setDate(date);
                toReturn.add(toAdd);
            }
        } catch (FileNotFoundException ex) {
            //catch because will create the file if it doesn't exist
        }
        return toReturn;
    }

    private Order convertLineToOrder(String row) {
        String[] toConvert = row.split(",");
        Order toReturn = new Order();
        
        Product p = new Product();
        p.setName(toConvert[4]);
        p.setProductCost(new BigDecimal(toConvert[6]));
        p.setLaborCost(new BigDecimal(toConvert[7]));
        
        Tax t = new Tax();
        t.setStateAbv(toConvert[2]);
        t.setTaxRate(new BigDecimal(toConvert[3]));
        
        toReturn.setProduct(p);
        toReturn.setTax(t);
        toReturn.setOrderNumber(Integer.parseInt(toConvert[0]));
        toReturn.setName(toConvert[1]);
        toReturn.setArea(new BigDecimal(toConvert[5]));
        toReturn.setMaterialCost(new BigDecimal(toConvert[8]));
        toReturn.setLaborCost(new BigDecimal(toConvert[9]));
        toReturn.setTaxCost(new BigDecimal(toConvert[10]));
        toReturn.setTotalCost(new BigDecimal(toConvert[11]));
        return toReturn;
    }

    @Override
    public void addOrder(Order toAdd) throws FlooringMasteryOrderDaoException {
        validateOrder(toAdd);
        List<Order> allOrders = getOrders(toAdd.getDate());
        int orderNumber = 0;
        for (Order order : allOrders) {
            if (order.getOrderNumber() > orderNumber) {
                orderNumber = order.getOrderNumber();
            }
        }
        toAdd.setOrderNumber(orderNumber + 1);
        allOrders.add(toAdd);
        writeToFile(allOrders);
    }

    private void writeToFile(List<Order> toWrite) throws FlooringMasteryOrderDaoException {
        LocalDate date = toWrite.get(0).getDate();
        String path = folder + File.separator + "Orders_" + formatter.format(date) + ".txt";
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(path));
            //header
            writer.println("OrderNumber,CustomerName,State,TaxRate,ProductType,"
                    + "Area,CostPerSquareFoot,LaborCostPerSquareFoot,"
                    + "MaterialCost,LaborCost,Tax,Total");
            for (Order o : toWrite) {
                String line = convertOrderToLine(o);
                writer.println(line);
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new FlooringMasteryOrderDaoException("Count not open file "
                    + path + ".\n");
        }

    }

    private String convertOrderToLine(Order o) {
        return o.getOrderNumber() + "," + o.getName() + ","
                + o.getTax().getStateAbv() + "," + o.getTax().getTaxRate() + ","
                + o.getProduct().getName() + "," + o.getArea() + ","
                + o.getProduct().getProductCost() + "," + o.getProduct().getLaborCost() + ","
                + o.getMaterialCost() + "," + o.getLaborCost() + ","
                + o.getTaxCost() + "," + o.getTotalCost();

    }

    @Override
    public void editOrder(Order newOrder) throws FlooringMasteryOrderDaoException {
        validateOrder(newOrder);
        List<Order> allOrders = getOrders(newOrder.getDate());
        int index = 0;
        for(int i = 0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getOrderNumber() == newOrder.getOrderNumber()) {
                index = i;
            }
        }
        allOrders.set(index, newOrder);
        writeToFile(allOrders);
    }

    @Override
    public void deleteOrder(LocalDate date, int orderNumber) throws FlooringMasteryOrderDaoException {
        List<Order> allOrders = getOrders(date);
        int index = -1;
        for(int i = 0; i < allOrders.size(); i++) {
            if (allOrders.get(i).getOrderNumber() ==  orderNumber) {
                index = i;
            }
        }
        if (index == -1) {
            throw new FlooringMasteryOrderDaoException("The order was not found.\n");
        }
        allOrders.remove(index);
        writeToFile(allOrders);
    }

    private void validateOrder(Order toCheck) throws FlooringMasteryOrderDaoException {
        if (toCheck.getDate() == null || toCheck.getName() == null
                || toCheck.getProduct() == null || toCheck.getProduct().getLaborCost() == null
                || toCheck.getProduct().getName() == null || toCheck.getProduct().getProductCost() == null
                || toCheck.getArea() == null || toCheck.getTotalCost() == null
                || toCheck.getTax() == null || toCheck.getTax().getState() == null
                || toCheck.getTax().getStateAbv() == null || toCheck.getTax().getTaxRate() == null
                || toCheck.getTaxCost() == null || toCheck.getLaborCost() == null
                || toCheck.getMaterialCost() == null) {
            throw new FlooringMasteryOrderDaoException("Fields cannot contain null.\n");
        }
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
