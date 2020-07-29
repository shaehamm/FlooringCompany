/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author codedchai
 */
public class FlooringMasteryInMemProductDao implements FlooringMasteryProductDao {

    List<Product> allProducts = new ArrayList<>();

    public FlooringMasteryInMemProductDao() {
        Product toAdd = new Product();
        toAdd.setName("Cherry Hardwood");
        toAdd.setLaborCost(new BigDecimal("5.05"));
        toAdd.setProductCost(new BigDecimal("6.55"));
        
        Product toAdd2 = new Product();
        toAdd2.setName("Marble Tile");
        toAdd2.setLaborCost(new BigDecimal("5.80")); 
        toAdd2.setProductCost(new BigDecimal("8.99"));
        
        allProducts.add(toAdd);
        allProducts.add(toAdd2);
    }

    @Override
    public List<Product> getProducts() {
        return allProducts;
    }

}
