/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.daos;

import com.sg.flooringmastery.dtos.Tax;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author codedchai
 */
@Component
public interface FlooringMasteryTaxDao {
    
    public List<Tax> getTaxes();
    
}
