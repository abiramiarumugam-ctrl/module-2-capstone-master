package com.techelevator.tenmo.consoleGUI;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.ActiveService;
import com.techelevator.tenmo.services.TransferService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SearchAllUsersPanel extends JPanel {
    private final Font MENU_FONT = new Font("Arial", Font.PLAIN, 30);

    private final Font RESULTS_FONT = new Font("Arial", Font.PLAIN, 25);
    private final JPanel accountDisplayPanel;
    private final JLabel accountPanelGreeting;
    private final JLabel accountNumber;
    private final JLabel accountBalance;
    private int currentUserId;
    private List<String> contactUsernames = new ArrayList<>();
    private List<String> allUsernames = new ArrayList<>();
    private List<String> resultsUsernames = new ArrayList<>();
    private List<User> resultsUserList = new ArrayList<>();
    private List<Integer> resultsIds = new ArrayList<>();
    private TransferService transferService;
    private JTextField searchBarTextField;
    private JPanel searchPanel;
    private JPanel resultsPanel;
    private List<String> usernamesList = new ArrayList<>();

    private List<User> allUsers = new ArrayList<>();

    private JButton resultsSendButton;
    private JButton resultsRequestButton;
    private JButton resultsAddToContactsButton;

    private ActiveService activeService;
    private AuthenticatedUser currentUser;

    public SearchAllUsersPanel(ActiveService activeService, TransferService transferService, AuthenticatedUser currentUser) {
        this.activeService = activeService;
        this.transferService = transferService;
        this.currentUser = currentUser;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshLists();
                updateResults();
                refreshBalance();
            }
        });

        currentUserId = currentUser.getUser().getId();

        ///ACCOUNT PANEL
        accountDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        accountDisplayPanel.setPreferredSize(new Dimension(540, 180));
        accountDisplayPanel.setOpaque(false);

        accountPanelGreeting = new JLabel("Hello, " + currentUser.getUser().getUsername());
        accountPanelGreeting.setFont(new Font("Arial", Font.PLAIN, 30));
        accountPanelGreeting.setForeground(new Color(0, 50, 40));
        accountPanelGreeting.setPreferredSize(new Dimension(500, 40));

        accountNumber = new JLabel("Account #: " + activeService.userToAccount(currentUser.getUser()));
        accountBalance = new JLabel("Balance: " + activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser())));
        accountNumber.setPreferredSize(new Dimension(500, 40));
        accountNumber.setForeground(new Color(0, 50, 40));
        accountBalance.setPreferredSize(new Dimension(500, 40));
        accountBalance.setForeground(new Color(0, 50, 40));

        accountNumber.setFont(new Font("Arial", Font.PLAIN, 30));
        accountBalance.setFont(new Font("Arial", Font.PLAIN, 30));
        accountDisplayPanel.add(accountPanelGreeting);
        accountDisplayPanel.add(accountNumber);
        accountDisplayPanel.add(accountBalance);
        add(accountDisplayPanel);


        ///Build the panel
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel = new JPanel();

        searchBarTextField = new JTextField(19);
        searchBarTextField.setFont(MENU_FONT);
        searchBarTextField.setForeground(new Color(50, 60, 60));
        searchBarTextField.setPreferredSize(new Dimension(100, 40));

        searchBarTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateResults();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateResults();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateResults();
            }
        });


        searchPanel.setBackground(new Color(50, 150, 100));
        searchPanel.add(searchBarTextField);

        //************************************RESULTS PANEL
        resultsPanel = new JPanel() {
        };
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setMinimumSize(new Dimension(520, 500));
        resultsPanel.setMaximumSize(new Dimension(520, 500));
        JScrollPane resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultsScrollPane.setPreferredSize(new Dimension(540, 460));
        resultsScrollPane.setBorder(null);
        resultsScrollPane.setOpaque(false);
        resultsPanel.setBackground(new Color(10, 120, 120));
        resultsPanel.setOpaque(false);
        resultsScrollPane.getViewport().setOpaque(false);

        add(searchPanel);
        add(resultsScrollPane);
    }

    private void updateResults() {

        String searchTerm = searchBarTextField.getText();
        List<String> filteredResults = new ArrayList<>();
        resultsPanel.removeAll();
        for (String username : allUsernames) {
            if (!Objects.equals(currentUser.getUser().getUsername(), username)) {
                if (username.toLowerCase().contains(searchTerm.toLowerCase())) {
                    filteredResults.add(username);
                }
            }
        }
        Collections.sort(filteredResults);

        if (filteredResults.size() > 0) {
            int colorCounter = 0;
            int resultCounter = 0;

            for (String username : filteredResults) {
                if (resultCounter < 30 && resultCounter < filteredResults.size()) {
                    JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)) {
                    };
                    usernamePanel.setPreferredSize(new Dimension(520, 40));
                    usernamePanel.setMaximumSize(new Dimension(520, 40));
                    usernamePanel.setMaximumSize(new Dimension(520, 40));
                    if (colorCounter % 2 == 0) {
                        usernamePanel.setBackground(new Color(100, 255, 180));
                    } else {
                        usernamePanel.setBackground(new Color(100, 255, 200));
                    }
                    JLabel usernameLabel = new JLabel(username);
                    usernameLabel.setFont(RESULTS_FONT);
                    usernamePanel.add(usernameLabel);
                    usernameLabel.setPreferredSize(new Dimension(375, 40));

                    resultsSendButton = new JButton();
                    resultsSendButton.setIcon(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-money-transfer-25.png"));
                    resultsSendButton.setToolTipText("Send TE Bucks to " + username);
                    resultsSendButton.addActionListener(e -> {
                        String amount = JOptionPane.showInputDialog(SearchAllUsersPanel.this, "How much would you like to send to " + username,
                                "Send Transfer", JOptionPane.PLAIN_MESSAGE);
                        if (amount != null && amount.length() > 0) {
                            BigDecimal bigDAmount = BigDecimal.valueOf(Double.parseDouble(amount));
                            BigDecimal currentUserBalance = activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser()));
                            if (currentUserBalance.compareTo(bigDAmount) < 0) {
                                JOptionPane.showMessageDialog(this, "Your balance is insufficient to cover this transfer", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (bigDAmount.compareTo(BigDecimal.valueOf(4.99)) <= 0) {
                                    JOptionPane.showMessageDialog(this, "The minimum transfer amount is $5.00", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                                } else if (bigDAmount.compareTo(BigDecimal.valueOf(100000.00)) >= 0) {
                                    JOptionPane.showMessageDialog(this, "The maximum transfer amount is $99,999.99", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    Transfer transfer = new Transfer(2, 2, activeService.userToAccount(currentUser.getUser()),
                                            activeService.userToAccount((activeService.getUserByName(username))), bigDAmount);
                                    transferService.makeTransfer(transfer);
                                    transferService.doTransfer(transfer);
                                    refreshBalance();
                                }
                            }
                        }
                    });

                    resultsRequestButton = new JButton();
                    resultsRequestButton.setIcon(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-request-money-25.png"));
                    resultsRequestButton.setToolTipText("Request TE Bucks from " + username);
                    resultsRequestButton.addActionListener(e -> {
                        InputVerifier inputVerifier = new InputVerifier() {
                            @Override
                            public boolean verify(JComponent input) {
                                String inputText = ((JTextField) input).getText();
                                try {
                                    BigDecimal bigDAmount = new BigDecimal(inputText);
                                    if (bigDAmount.compareTo(BigDecimal.valueOf(100000.00)) >= 0) {
                                        JOptionPane.showMessageDialog(input, "The maximum you can request is $99,999.99", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                                        return false;
                                    } else if (bigDAmount.compareTo(BigDecimal.valueOf(4.99)) <= 0) {
                                        JOptionPane.showMessageDialog(input, "The minimum you can request is $5.00", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                                        return false;
                                    }
                                    return true;
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(input, "Invalid input", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                                    return false;
                                }
                            }
                        };

                        JTextField requestAmountText = new JTextField(10);
                        requestAmountText.setInputVerifier(inputVerifier);

                        JPanel sendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                        sendPanel.add(new JLabel("How much would you like to request from " + username));
                        sendPanel.add(requestAmountText);

                        int amount = JOptionPane.showConfirmDialog(SearchAllUsersPanel.this, sendPanel,
                                "Send Request", JOptionPane.DEFAULT_OPTION);
                        if (amount == JOptionPane.OK_OPTION) {
                            String inputText = requestAmountText.getText();
                            BigDecimal bigDAmount = new BigDecimal(inputText);

                            Transfer transfer = new Transfer(1, 1,
                                    activeService.userToAccount(activeService.getUserByName(username)),
                                    activeService.userToAccount(currentUser.getUser()), bigDAmount);
                            transferService.makeTransfer(transfer);
                        }
                    });

                    resultsAddToContactsButton = new JButton();
                    resultsAddToContactsButton.setToolTipText("Add " + username + " to your contacts");
                    if (contactUsernames.contains(username)) {
                        resultsAddToContactsButton.setIcon(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-friends-25.png"));
                    } else {
                        resultsAddToContactsButton.setIcon(new ImageIcon("tenmo-client/src/main/resources/Images/icons8-address-book-25.png"));
                    }
                    resultsAddToContactsButton.addActionListener(e -> {
                        int contactId = 0;
                        for (User user : allUsers) {
                            if (user.getUsername().equals(username)) {
                                contactId = user.getId();
                            }
                        }
                        if (contactUsernames.contains(username)) {
                            System.out.println("This user is already in your contacts");
                        } else {
                            activeService.addUserToContacts(currentUserId, contactId);
                            refreshLists();
                            updateResults();
                        }
                    });

                    resultsSendButton.setPreferredSize(new Dimension(35, 35));
                    resultsRequestButton.setPreferredSize(new Dimension(35, 35));
                    resultsAddToContactsButton.setPreferredSize(new Dimension(35, 35));

                    usernamePanel.add(resultsSendButton);
                    usernamePanel.add(resultsRequestButton);
                    usernamePanel.add(resultsAddToContactsButton);

                    resultsPanel.add(usernamePanel);
                    colorCounter++;
                    resultCounter++;
                }
            }
        }
        resultsPanel.revalidate();
        resultsPanel.repaint();

    }

    public void refreshLists() {
        allUsers.clear();
        resultsIds.clear();
        resultsUserList.clear();
        contactUsernames.clear();
        allUsernames.clear();

        allUsers.addAll(activeService.getAllUsers(currentUser));
        resultsIds.addAll(activeService.getContactsList(currentUserId));

        for (User user : allUsers) {
            allUsernames.add(user.getUsername());
            if (resultsIds.contains(user.getId())) {
                resultsUserList.add(user);
                resultsUsernames.add(user.getUsername());
            }
            if (activeService.getContactsList(currentUserId).contains(user.getId())) {
                contactUsernames.add(user.getUsername());
            }
        }
    }

    public void refreshBalance() {
        accountBalance.setText("Balance: " + activeService.getAccountBalance(activeService.userToAccount(currentUser.getUser())));
    }
}
