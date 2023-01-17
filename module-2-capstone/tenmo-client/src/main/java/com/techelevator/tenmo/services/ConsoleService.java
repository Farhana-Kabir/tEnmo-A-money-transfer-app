package com.techelevator.tenmo.services;


import com.techelevator.dto.TransferHistoryOfId;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {
    private String name;

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        //System.out.print(prompt);
        try {String n = JOptionPane.showInputDialog("Enter option number please:  ");
           // System.out.println(n);
            //menuSelection = Integer.parseInt(scanner.nextLine());
            menuSelection = Integer.parseInt(n);
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        //String password = promptForString("Password: ");
        JPasswordField pwd = new JPasswordField(10);
        int action = JOptionPane.showConfirmDialog(null, pwd,"Enter Password",JOptionPane.OK_CANCEL_OPTION);
        if(action < 0)JOptionPane.showMessageDialog(null,"Cancel, X or escape key selected");
        //else JOptionPane.showMessageDialog(null,"Your password is "+new String(pwd.getPassword()));
        String password = new String(pwd.getPassword());
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        String dialogBoxInput = JOptionPane.showInputDialog(prompt);
        //System.out.print(prompt+dialogBoxInput);
        System.out.println();
        return dialogBoxInput;
        //return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }



        public void printAllUser(User[] users){
        System.out.println("-----------------------------------");
        System.out.println("Users");
        System.out.println("Ids                     name");
        System.out.println("-----------------------------------");
        for(User us:users){
            System.out.print(us.getId()+"               "+us.getname()+"   ");
            if(us.getBalance()!=null){
                System.out.print(us.getBalance()+"  ");
            }
            System.out.println();
        }
        System.out.println("-----------------------------------");
    }

    public int printApproveOrRejectPendingTransfer() {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("----------------");

        System.out.println("Please choose an option: ");
        return promptForInt("");
    }

    public void printTransferHistoryOfId(TransferHistoryOfId body, String name) {
        System.out.println("-----------------------------");
        System.out.println("Transfer History");
        System.out.println("-----------------------------");
        System.out.println("Id: "+body.getTransferId());
        if(body.getAccountFrom().equals(name)){
            System.out.println("From: me "+body.getAccountFrom());
        }else{
            System.out.println("to: "+body.getAccountFrom());
        }
        if(body.getAccountTo().equals(name)){
            System.out.println("From: me "+body.getAccountTo());
        }else{
            System.out.println("To: "+body.getAccountTo());
        }
        System.out.println("Type: "+body.getTransferTypeId());
        System.out.println("Status: "+body.getTransferStatusId());
        System.out.println("Amount: "+body.getAmount());
    }


    class EraserThread implements Runnable {
        private boolean stop;

        public EraserThread(String prompt) {
            System.out.print(prompt);
        }

        /**
         * Begin masking...display asterisks (*)
         */
        public void run () {
            stop = true;
            while (stop) {
                System.out.print("\010*");
                try {
                    Thread.currentThread().sleep(1);
                } catch(InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        public void stopMasking() {
            this.stop = false;
        }
    }
}
