/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Product;
import com.sg.flooringmastery.dtos.Tax;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
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
public class FlooringMasteryProductFileDao implements FlooringMasteryProductDao {
    
    @Value("${productDao.path}")
    String path;

    @Override
    public List<Product> getProducts() {
        List<Product> toReturn = new ArrayList<>();
        try {
            Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(path)));
            //header
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String row = fileScanner.nextLine();
                Product toAdd = stringToProduct(row);
                toReturn.add(toAdd);
            }
        } catch (FileNotFoundException ex) {
        }
        return toReturn;
    }

    private Product stringToProduct(String row) {
        Product toReturn = new Product();
        String[] toConvert = row.split(",");
        toReturn.setName(toConvert[0]);
        toReturn.setProductCost(new BigDecimal(toConvert[1]));
        toReturn.setLaborCost(new BigDecimal(toConvert[2]));
        return toReturn;
    }
    
}
