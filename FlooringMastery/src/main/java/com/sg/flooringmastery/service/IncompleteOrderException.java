/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.service;

/**
 *
 * @author codedchai
 */
public class IncompleteOrderException extends Exception {
    
    public IncompleteOrderException(String message) {
        super(message);
    }
    
    public IncompleteOrderException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
