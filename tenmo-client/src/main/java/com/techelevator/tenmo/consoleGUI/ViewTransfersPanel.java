package com.techelevator.tenmo.consoleGUI;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.ActiveService;
import com.techelevator.tenmo.services.TransferService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ViewTransfersPanel extends JPanel {

    private final JPanel accountDisplayPanel;
    private final JLabel accountPanelGreeting;
    private final JLabel accountNumber;
    private final JLabel accountBalance;
    private JPanel transfersDisplayPanel;
    private ActiveService activeService;
    private TransferService transferService;
    private AuthenticatedUser currentUser;
    java.util.List<Transfer> sentTransfers = new ArrayList<>();
    java.util.List<Transfer> transfersSentToUser = new ArrayList<>();
    java.util.List<Transfer> sentRequestsStillPending = new ArrayList<>();
    java.util.List<Transfer> sentRequestsApproved = new ArrayList<>();
    java.util.List<Transfer> sentRequestsRejected = new ArrayList<>();
    java.util.List<Transfer> receivedRequestsPending = new ArrayList<>();
    java.util.List<Transfer> receivedRequestsApproved = new ArrayList<>();
    java.util.List<Transfer> receivedRequestsRejected = new ArrayList<>();
    java.util.List<Transfer> allReceivedRequests = new ArrayList<>();

    private java.util.List<Transfer> transfers = new ArrayList<>();
    private JPanel transferPanel;

    private JPanel filterButtons;
    private JButton approveTransferButton;
    private JButton rejectTransferButton;
    private JLabel transferLabel;
    BigDecimal currentUserAccountBalance;

    public ViewTransfersPanel(ActiveService activeService, TransferService transferService, AuthenticatedUser currentUser) {

        this.activeService = activeService;
        this.transferService = transferService;
        this.currentUser = currentUser;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {

                refreshBalance();
                createAndSortTransactions(currentUser.getUser().getId());
                printTransfersToScreen(transfers);

            }
        });

        ///BUILD PANEL

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        ///ACCOUNT PANEL
        accountDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5,5));
        accountDisplayPanel.setPreferredSize(new Dimension(540, 180));
        accountDisplayPanel.setOpaque(false);

        accountPanelGreeting = new JLabel("Hello, " + currentUser.getUser().getUsername());
        accountPanelGreeting.setFont(new Font("Arial", Font.PLAIN, 30 ));
        accountPanelGreeting.setForeground(new Color(0,50,40));
        accountPanelGreeting.setPreferredSize(new Dimension(500,40));

        accountNumber = new JLabel("Account #: " + activeService.userToAccount(currentUser.getUser()));
        accountBalance = new JLabel(  "Balance: " + activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser())));
        accountNumber.setPreferredSize(new Dimension(500,40));
        accountNumber.setForeground(new Color(0,50,40));
        accountBalance.setPreferredSize(new Dimension(500,40));
        accountBalance.setForeground(new Color(0,50,40));

        accountNumber.setFont(new Font("Arial", Font.PLAIN, 30 ));
        accountBalance.setFont(new Font("Arial", Font.PLAIN, 30 ));
        accountDisplayPanel.add(accountPanelGreeting);
        accountDisplayPanel.add(accountNumber);
        accountDisplayPanel.add(accountBalance);




        ///TRANSFERS PANEL

        transfersDisplayPanel = new JPanel();

        transfersDisplayPanel.setLayout(new BoxLayout(transfersDisplayPanel, BoxLayout.Y_AXIS));
        JScrollPane resultsScrollPane = new JScrollPane(transfersDisplayPanel);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setPreferredSize(new Dimension(540, 460));
        resultsScrollPane.setBorder(null);
        transfersDisplayPanel.setBackground(new Color(10, 120, 120));
        transfersDisplayPanel.setOpaque(false);
        resultsScrollPane.getViewport().setOpaque(false);
        resultsScrollPane.setOpaque(false);

        filterButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        filterButtons.setPreferredSize(new Dimension(520, 50));
        JButton allButton = new JButton("All");
        allButton.setToolTipText("View All Transfers");
        JButton sentRequestsButton = new JButton("Sent Requests");
        sentRequestsButton.setToolTipText("View All Sent Requests");
        JButton receivedRequestsButton = new JButton("Received Requests");
        receivedRequestsButton.setToolTipText("View Received Requests");
        JButton sentButton = new JButton("Sent");
        sentButton.setToolTipText("View All Sent Transfers");
        JButton receivedButton = new JButton("Received");
        receivedButton.setToolTipText("View All Received Transfers");
        filterButtons.setOpaque(false);

        allButton.addActionListener(e -> {
            printTransfersToScreen(transfers);
        });
        receivedRequestsButton.addActionListener(e -> {
            printTransfersToScreen(allReceivedRequests);
        });
        sentRequestsButton.addActionListener(e -> {
            java.util.List<Transfer> allSentRequests = new ArrayList<>();
            allSentRequests.addAll(sentRequestsStillPending);
            allSentRequests.addAll(sentRequestsRejected);
            allSentRequests.addAll(sentRequestsRejected);
            printTransfersToScreen(allSentRequests);
            printTransfersToScreen(sentRequestsStillPending);
        });
        sentButton.addActionListener(e -> {
            printTransfersToScreen(sentTransfers);
        });
        receivedButton.addActionListener(e -> {
            printTransfersToScreen(transfersSentToUser);
        });

        filterButtons.add(allButton);
        filterButtons.add(sentRequestsButton);
        filterButtons.add(receivedRequestsButton);
        filterButtons.add(sentButton);
        filterButtons.add(receivedButton);

        /// CONSTRUCT PAGE PANEL

        add(accountDisplayPanel);
        add(filterButtons);
        add(resultsScrollPane);

    }

    public void createAndSortTransactions(int userId) {
        sentTransfers.clear();
        transfersSentToUser.clear();
        sentRequestsStillPending.clear();
        sentRequestsApproved.clear();
        sentRequestsRejected.clear();
        receivedRequestsPending.clear();
        receivedRequestsApproved.clear();
        receivedRequestsRejected.clear();
        allReceivedRequests.clear();
        transfers.clear();

        transfers.addAll(transferService.getAllUserTransfers(activeService.userToAccount(currentUser.getUser())));
        for (Transfer transfer : transfers) {
            if (transfer.getTransferType() == 1) {              // IF THE TRANSFER TYPE IS REQUESTED------------------------------- TYPE CHECK
                if (transfer.getTransferStatus() == 1) {         //IF THE TRANSFER IS PENDING ------------------------------------Status Check
                    if (transfer.getAccountTo() == activeService.userToAccount(currentUser.getUser())) { // IF REQUEST SENT BY USER --------------Sender Check
                        sentRequestsStillPending.add(transfer);
                    } else {                                                            //ELSE THE USER WAS RECIPIENT OF REQUEST
                        receivedRequestsPending.add(transfer);
                    }
                } else if (transfer.getTransferStatus() == 2) {
                    if (transfer.getAccountTo() == activeService.userToAccount(currentUser.getUser())) {//----- REQUEST SENT BY USER AND APPROVED
                        sentRequestsApproved.add(transfer);
                    } else {                                                            /// REQUEST SENT TO USER and APPROVED
                        receivedRequestsApproved.add(transfer);
                    }
                } else if (transfer.getTransferStatus() == 3) {
                    if (transfer.getAccountTo() == activeService.userToAccount(currentUser.getUser())) {//----- REQUEST SENT BY USER AND APPROVED
                        sentRequestsRejected.add(transfer);
                    } else {                                                            /// REQUEST SENT TO USER and APPROVED
                        receivedRequestsRejected.add(transfer);
                    }
                }
            } else if (transfer.getTransferType() == 2) {// IF THE TRANSFER TYPE IS SENT---------------------------- TYPE CHECK
                if (transfer.getAccountTo() == activeService.userToAccount(currentUser.getUser())) {   //IF THE SENDER IS USER
                    sentTransfers.add(transfer);
                } else {
                    transfersSentToUser.add(transfer);
                }
            }


        }
        allReceivedRequests.addAll(receivedRequestsPending);
        allReceivedRequests.addAll(receivedRequestsApproved);
        allReceivedRequests.addAll(receivedRequestsRejected);
    }

    public java.util.List<Transfer> returnTransactionsOfType(int type, int status, boolean didUserSend) {
        if (type == 1) {
            if (status == 1) { //REQUESTED TRANSFERS
                if (didUserSend) {
                    return sentRequestsStillPending;
                } else {
                    return receivedRequestsPending;
                }
            } else if (status == 2) {
                if (didUserSend) {
                    return sentRequestsApproved;
                } else {
                    return receivedRequestsApproved;
                }
            } else if (status == 3) {
                if (didUserSend) {
                    return sentRequestsRejected;
                } else {
                    return receivedRequestsRejected;
                }
            }
        } else if (status == 2) { //SENT TRANSFERS
            if (didUserSend) {
                return sentTransfers;
            } else {
                return transfersSentToUser;
            }
        }
        return new ArrayList<>();
    }

    public void printTransfersToScreen(java.util.List<Transfer> transfersToPrint) {
        int colorCounter = 0;
        int resultCounter = 0;

        transfersDisplayPanel.removeAll();

        for (Transfer transfer : transfersToPrint) {
            JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            transferPanel.setPreferredSize(new Dimension(520, 40));
            transferPanel.setMaximumSize(new Dimension(520, 40));
            transferPanel.setMaximumSize(new Dimension(520, 40));


            if (colorCounter % 2 == 0) {
                transferPanel.setBackground(new Color(100, 255, 180));
            } else {
                transferPanel.setBackground(new Color(100, 255, 200));
            }
            if (transfersToPrint.equals(allReceivedRequests)) {
                transferLabel = new JLabel(transfer.toRequestLabelString());
                transferLabel.setPreferredSize(new Dimension(430, 40));
            } else {
                transferLabel = new JLabel(transfer.toLabelString());
                transferLabel.setPreferredSize(new Dimension(500, 40));
            }
            transferLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            transferPanel.add(transferLabel);

            if (transfersToPrint.equals(allReceivedRequests) && transfer.getTransferType() == 1 && transfer.getTransferStatus() == 1 && transfer.getAccountFrom() == activeService.userToAccount(currentUser.getUser())) {
                approveTransferButton = new JButton(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-sent-25.png"));
                approveTransferButton.addActionListener(e -> {
                    currentUserAccountBalance = activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser()));
                    if(transfer.getAmount().compareTo(currentUserAccountBalance) < 0) {
                        transferService.doTransfer(transfer);
                        transferService.updateTransferStatus(2, transfer.getTransferId());
                        refreshBalance();
                        createAndSortTransactions(currentUser.getUser().getId());
                        printTransfersToScreen(allReceivedRequests);
                    } else {
                        JOptionPane.showMessageDialog(this, "Your balance is insufficient to cover this transfer", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                rejectTransferButton = new JButton(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-close-25.png"));
                rejectTransferButton.addActionListener(e -> {
                    transferService.updateTransferStatus(3, transfer.getTransferId());
                    createAndSortTransactions(currentUser.getUser().getId());
                    printTransfersToScreen(allReceivedRequests);
                });
                approveTransferButton.setPreferredSize(new Dimension(35, 35));
                rejectTransferButton.setPreferredSize(new Dimension(35, 35));

                transferPanel.add(approveTransferButton);
                transferPanel.add(rejectTransferButton);
            }

            transfersDisplayPanel.add(transferPanel);
            colorCounter++;
            resultCounter++;

        }
        transfersDisplayPanel.revalidate();
        transfersDisplayPanel.repaint();
    }

    public void refreshBalance(){
        accountBalance.setText("Balance: " + activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser())));
    }
}


