/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery;

import com.sg.flooringmastery.controller.FlooringMasteryController;
import java.io.IOException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

/**
 *
 * @author codedchai
 */
public class App {
    
    public static void main(String[] args) throws IOException {
        
        AnnotationConfigApplicationContext ctx = new
            AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst
                (new ResourcePropertySource(new ClassPathResource("application.properties")));
        //tells where to start looking (which folder/package)
        ctx.scan("com.sg.flooringmastery");
        //loads up the beans
        ctx.refresh();
        FlooringMasteryController controller = 
                ctx.getBean("flooringMasteryController", 
                        FlooringMasteryController.class);
        //runs the program
        controller.run();
    }
    
}
