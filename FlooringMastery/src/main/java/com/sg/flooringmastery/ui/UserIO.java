/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.dtos.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;
import static org.springframework.util.Assert.state;

/**
 *
 * @author codedchai
 */
@Component
public class UserIO {

    Scanner scn = new Scanner(System.in);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public void print(String message) {
        System.out.print(message);
    }

    public String readString(String prompt) {
        print(prompt);
        String userInput = scn.nextLine();
        return userInput;
    }

    public String readName(String prompt) {
        String userInput = "";
        boolean valid = false;
        while (!valid) {
            userInput = readString(prompt);
            if (!userInput.contains(",")) {
                valid = true;
            } else {
                print("Commas are not allowed, please reenter.\n");
            }
        }
        return userInput;
    }

    public String editName(String prompt, String originalValue) {
        String toReturn = readName(prompt);
        if (toReturn.isEmpty()) {
            toReturn = originalValue;
        }
        return toReturn;
    }

    public int readInt(String prompt) {
        int toReturn = Integer.MIN_VALUE;

        boolean valid = false;
        while (!valid) {
            String userInput = readString(prompt);

            try {
                toReturn = Integer.parseInt(userInput);
                valid = true;
            } catch (NumberFormatException ex) {

            }
        }
        return toReturn;
    }

    public int readInt(String prompt, int incMin, int incMax) {
        int toReturn = Integer.MIN_VALUE;

        boolean valid = false;
        while (!valid) {
            toReturn = readInt(prompt);
            valid = toReturn <= incMax && toReturn >= incMin;

        }
        return toReturn;

    }

    public LocalDate readLocalDate(String prompt) {
        LocalDate toReturn = null;
        boolean validInput = false;

        while (!validInput) {
            String userInput = readString(prompt);
            try {
                toReturn = LocalDate.parse(userInput, formatter);
                validInput = true;
            } catch (DateTimeParseException ex) {

            }
        }
        return toReturn;
    }

    public LocalDate editDate(String prompt, LocalDate originalData) {
        LocalDate toReturn = originalData;
        boolean validInput = false;

        while (!validInput) {
            String userInput = readString(prompt);
            if (userInput.isEmpty()) {
                validInput = true;
            } else {
                try {
                    toReturn = LocalDate.parse(userInput, formatter);
                    validInput = true;
                } catch (DateTimeParseException ex) {

                }
            }
        }
        return toReturn;
    }

    LocalDate readFutureLocalDate(String prompt) {
        LocalDate toReturn = null;
        boolean validInput = false;

        while (!validInput) {
            String userInput = readString(prompt);
            try {
                toReturn = LocalDate.parse(userInput, formatter);
                if (toReturn.isAfter(LocalDate.now())) {
                    validInput = true;
                } else {
                    print("You cannot enter a past date or today's date.\n");
                }
            } catch (DateTimeParseException ex) {

            }
        }
        return toReturn;
    }

    boolean validateState(List<String> states, String state) {
        boolean toReturn = false;
        for (String toCheck : states) {
            if (state.equalsIgnoreCase(toCheck)) {
                toReturn = true;
            }
        }
        if (!toReturn) {
            print("The state you entered is either invalid or we cannot sell "
                    + "there.\nPlease try again or type \"Help\" to see a list "
                    + "of available states.\n");
        }
        return toReturn;
    }

    String readState(String prompt, List<String> states) {
        String state = "";
        boolean valid = false;
        while (!valid) {
            state = readString(prompt);
            //displays all valid states
            if (state.equalsIgnoreCase("help")) {
                print("\nValid states:\n");
                for (int i = 0; i < states.size() - 1; i += 2) {
                    print("   " + states.get(i) + " (" + states.get(i + 1) + ")\n");
                }
            } else {
                valid = validateState(states, state);
            }
        }
        return state;
    }

    String readProduct(String prompt, List<Product> products) {
        String product = "";
        boolean valid = false;
        print("\nAvailable products (name - cost per sq ft, labor cost per sq ft)\n");
        //display all products and pricing info
        for (Product toDisplay : products) {
            print("   " + toDisplay.getName() + " - $" + toDisplay.getProductCost()
                    + ", $" + toDisplay.getLaborCost() + "\n");
        }
        //gets valid input from the user
        while (!valid) {
            product = readString(prompt);
            for (int i = 0; i < products.size(); i++) {
                if (product.equalsIgnoreCase(products.get(i).getName())) {
                    valid = true;
                }
            }
        }
        return product;
    }

    BigDecimal readArea(String prompt) {
        BigDecimal area = new BigDecimal("0");
        boolean valid = false;
        while (!valid) {
            String stringArea = readString(prompt);
            stringArea = initialAreaValidation(prompt, stringArea);
            Double.parseDouble(stringArea);
            area = new BigDecimal(stringArea);
            //the user area must be greater than or equal to 100 square feet
            if (area.compareTo(new BigDecimal("100")) > -1) {
                valid = true;
            } else {
                print("The minimum order size is 100 sq ft.\n");
            }

        }
        return area;
    }

    BigDecimal editArea(String prompt, BigDecimal area) {
        BigDecimal toReturn = area;
        boolean valid = false;
        while (!valid) {
            String stringArea = readString(prompt);
            if (!stringArea.isEmpty()) {
                toReturn = new BigDecimal(stringArea);
                //the user area must be greater than or equal to 100 square feet
                if (toReturn.compareTo(new BigDecimal("100")) > -1) {
                    valid = true;
                } else {
                    print("The minimum order size is 100 sq ft.\n");
                }
            } else {
                valid = true;
            }
        }
        return toReturn;
    }

//    LocalDate editFutureLocalDate(String prompt, LocalDate date) {
//        LocalDate toReturn = date;
//        boolean valid = false;
//
//        while (!valid) {
//            String userInput = readString(prompt);
//            if (!userInput.isEmpty()) {
//            try {
//                toReturn = LocalDate.parse(userInput, formatter);
//                if (toReturn.isAfter(LocalDate.now())) {
//                    valid = true;
//                } else {
//                    print("You cannot enter a past date or today's date.\n");
//                }
//            } catch (DateTimeParseException ex) {
//
//            }
//            } else {
//                valid = true;
//            }
//        }
//        return toReturn;
//    }
    private String initialAreaValidation(String prompt, String stringArea) {
        boolean valid = false;
        while (!valid) {
            if (!stringArea.isBlank() && stringArea != null) {
                try {
                    Double.parseDouble(stringArea);
                    valid = true;
                } catch (NumberFormatException ex) {
                    print("You must enter a valid number.\n");
                    stringArea = readString(prompt);
                }
            }
        }
        return stringArea;
    }
}
