/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.dtos;

import java.math.BigDecimal;

/**
 *
 * @author codedchai
 */
public class Tax {
    
    private String stateAbv;
    private String state;
    private BigDecimal taxRate;

    public String getStateAbv() {
        return stateAbv;
    }

    public String getState() {
        return state;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setStateAbv(String stateAbv) {
        this.stateAbv = stateAbv;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
}
