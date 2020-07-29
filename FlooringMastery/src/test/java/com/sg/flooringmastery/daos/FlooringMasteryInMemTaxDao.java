/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Tax;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author codedchai
 */
public class FlooringMasteryInMemTaxDao implements FlooringMasteryTaxDao {

    List<Tax> allTaxes = new ArrayList<>();

    public FlooringMasteryInMemTaxDao() {
        Tax toAdd = new Tax();
        toAdd.setState("Minnesota");
        toAdd.setStateAbv("MN");
        toAdd.setTaxRate(new BigDecimal("6.25"));

        Tax toAdd2 = new Tax();
        toAdd2.setState("Colorado");
        toAdd2.setStateAbv("CO");
        toAdd2.setTaxRate(new BigDecimal("7.08"));

        allTaxes.add(toAdd);
        allTaxes.add(toAdd2);
    }

    @Override
    public List<Tax> getTaxes() {
        return allTaxes;
    }

}
