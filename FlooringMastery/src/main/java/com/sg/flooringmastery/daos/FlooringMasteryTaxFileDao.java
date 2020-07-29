/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

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
public class FlooringMasteryTaxFileDao implements FlooringMasteryTaxDao {
    
    @Value("${taxDao.path}")
    String path;

    @Override
    public List<Tax> getTaxes() {
        List<Tax> toReturn = new ArrayList<>();
        try {
            Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(path)));
            //header
            fileScanner.nextLine();
            while (fileScanner.hasNextLine()) {
                String row = fileScanner.nextLine();
                Tax toAdd = stringToTax(row);
                toReturn.add(toAdd);
            }
        } catch (FileNotFoundException ex) {
        }
        return toReturn;
    }

    private Tax stringToTax(String row) {
        Tax toReturn = new Tax();
        String[] toConvert = row.split(",");
        toReturn.setStateAbv(toConvert[0]);
        toReturn.setState(toConvert[1]);
        toReturn.setTaxRate(new BigDecimal(toConvert[2]));
        return toReturn;
    }
    
}
