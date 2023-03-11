package com.techelevator.tenmo;

import com.techelevator.tenmo.consoleGUI.LogInPage;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.ActiveService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private final ActiveService activeService = new ActiveService(API_BASE_URL, currentUser);
    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);

    //private final String token = currentUser.getToken();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            }else if(menuSelection == 3){
                LogInPage logInPage = new LogInPage();
            }else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        try {
            transferService.setCurrentUser(currentUser);
            transferService.setAuthToken(currentUser.getToken());
            activeService.setCurrentUser(currentUser);
            activeService.setAuthToken(currentUser.getToken());
        } catch (NullPointerException e) {
            System.out.println("\u001B[31m\nUsername or Password Invalid.\u001B[0m");
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        activeService.setCurrentUser(currentUser);
        BigDecimal currentBalance = activeService.getUserBalance(currentUser, currentUser.getUser().getId());
        System.out.println("\033[32m+---------------+---------------+\033[0m");
        System.out.printf("Current Balance%2s \u001B[1m\033[36m%s\033[0m\u001B[0m |\n", "|",("$" + currentBalance));
        System.out.println("\033[32m+---------------+---------------+\033[0m");

    }

    private void viewTransferHistory() {
        // TODO Auto-generated method stub
        consoleService.printHistory(activeService.userToAccount(currentUser.getUser()), activeService);

    }

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        int accountFrom = activeService.userToAccount(currentUser.getUser());
        Transfer[] transfers = transferService.getPendingRequests(accountFrom);
        int transferId = consoleService.printPendingRequests(transfers, activeService);
        if (transferId > 3000) {
            AtomicReference<Transfer> approveTransfer = new AtomicReference<>();//makes atomic transfer so i can use it in the lambda
            Arrays.stream(transfers).forEach(transfer -> {
                if (transfer.getTransferId() == transferId) approveTransfer.set(transfer);
            });// sets a transfer object based on the one the use picks from the array
            int response = consoleService.promptForInt("Would you like to approve to do?\n" +
                    " 1) Approve Request\n" +
                    " 2) Deny Request\n" +
                    " 3) Leave as Pending\n");
            if (response == 1) {
                if (activeService.getAccountBalance(approveTransfer.getPlain().getAccountFrom()).compareTo(approveTransfer.getPlain().getAmount()) >= 0) {//reworded to use the approvedTransfer Object
                    transferService.doTransfer(transferService.getTransferByTransferId(transferId));
                    transferService.updateTransferStatus(2, transferId);
                    System.out.println("Approved successfully!\n");
                } else {
                    System.err.println("\nInsufficient Balance");
                }
            } else if (response == 2) {
                transferService.updateTransferStatus(3, transferId);
                System.out.println("\u001B[32mRequest Denied\u001B[0m");
            } else if (response == 3) {
                System.out.println("\u001B[32mRequest saved for later\u001B[0m");
            } else {
                System.out.println("Invalid Response");
            }
        }
    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        activeService.setCurrentUser(currentUser);
        consoleService.printUsers(activeService, currentUser);
        String recipient = consoleService.promptForString("Type the name of the account you would like to send to: ");

        if (recipient.toLowerCase().equals(currentUser.getUser().getUsername())) {
            System.err.println("\nYou can not send to yourself");

        } else if (activeService.getUserByName(recipient) == null) {
            System.err.println("\nPlease enter valid username.");

        }else {

            BigDecimal amount = consoleService.promptForBigDecimal("How much would you like to send?: ");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Value must be more than $0.00");
            } else {
                    Transfer transfer = new Transfer(2, 2, activeService.userToAccount(currentUser.getUser()),
                            activeService.userToAccount(activeService.getUserByName(recipient)),
                            amount);

                    if (activeService.getAccountBalance(transfer.getAccountFrom()).compareTo(amount) >= 0) {
                        transfer.setTransferId(transferService.makeTransfer(transfer));
                        System.out.println("\u001B[32mSuccesfully sent $" + amount + ".00 to " + recipient + "\u001B[0m");

                    } else {
                        System.err.println("Insufficient Balance");
                        consoleService.printMainMenu();
                    }
            }
        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        activeService.setCurrentUser(currentUser);
        consoleService.printUsers(activeService, currentUser);
        String recipient = consoleService.promptForString("Type the name of the account you would like to send request to: ");
        if (recipient.toLowerCase().equals(currentUser.getUser().getUsername())) {
            System.err.println("\nYou can not request from yourself");

        } else if (activeService.getUserByName(recipient) == null){
            System.err.println("\nPlease enter valid username.");

        } else {
            BigDecimal amount = consoleService.promptForBigDecimal("How much would you like to request?: ");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Value must be more than $0.00");

            } else {
                    Transfer transfer = new Transfer(1, 1,
                            activeService.userToAccount(activeService.getUserByName(recipient)),
                            activeService.userToAccount(currentUser.getUser()), amount);

                    transfer.setTransferId(transferService.makeTransfer(transfer));
                    if (transfer.getTransferId() > 3000) {
                        System.out.println("\u001B[32mRequest sent to " + recipient + "\u001B[0m");
                    }
            }

        }
    }
}
