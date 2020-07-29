/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.dtos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 *
 * @author codedchai
 */
public class Order {
    //fields
    private int orderNumber;
    private String name;
//    private String state;
//    private String productName;
    private BigDecimal area;
    private Tax tax;
    private Product product;
    
    private LocalDate date;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal taxCost;
    private BigDecimal totalCost;

    //setters and getters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }
    
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public BigDecimal getTaxCost() {
        return taxCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public void setTaxCost(BigDecimal taxCost) {
        this.taxCost = taxCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    
    public void setCalculations() {
        this.materialCost = product.getProductCost().multiply(area)
                .setScale(2, RoundingMode.HALF_UP);
        this.laborCost = product.getLaborCost().multiply(area)
                .setScale(2, RoundingMode.HALF_UP);
        this.taxCost = (materialCost.add(laborCost)).multiply(tax.getTaxRate().divide(new BigDecimal("100")))
                .setScale(2, RoundingMode.HALF_UP);
        this.totalCost = materialCost.add(laborCost).add(taxCost)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
