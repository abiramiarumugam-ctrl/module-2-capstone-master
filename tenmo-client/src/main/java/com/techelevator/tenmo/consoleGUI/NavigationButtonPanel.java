package com.techelevator.tenmo.consoleGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationButtonPanel extends JPanel {

    final static String MAIN_MENU = "Main Menu";
    final static String SEARCH_MENU = "Search Menu";
    final static String CONTACTS_MENU = "Contacts Menu";
    final static String ACCOUNT_MENU = "Account Menu";
    final static String TRANSFERS_MENU = "Transfer Menu";

    public NavigationButtonPanel(JPanel cardPanel){

        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setSize(560, 145);
        setOpaque(false);
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, MAIN_MENU);


        JButton homeButton = new JButton(new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-menu-rounded-50.png"));
        homeButton.setPreferredSize(new Dimension(125 , 125));
        add(homeButton);
        homeButton.addActionListener(e -> {
            cardLayout.show(cardPanel , TRANSFERS_MENU);
        });

//        JButton accountButton = new JButton(new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-money-bag-50.png"));
//        accountButton.setPreferredSize(new Dimension(125 , 125));
//        add(accountButton);
//        accountButton.addActionListener(e -> {
//            cardLayout.show(cardPanel ,ACCOUNT_MENU);
//        });

        JButton searchButton = new JButton(new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-search-50.png"));
        searchButton.setPreferredSize(new Dimension(125 , 125));
        add(searchButton);
        searchButton.addActionListener(e -> {
        cardLayout.show(cardPanel ,SEARCH_MENU);
        });

        JButton contactsButton = new JButton(new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-address-book-50.png"));
        contactsButton.setPreferredSize(new Dimension(125 , 125));
        contactsButton.addActionListener(e -> {
            cardLayout.show(cardPanel ,CONTACTS_MENU);
        });
        add(contactsButton);


    }

    public void changePanels(JPanel[] oldPanels, JPanel[] newPanels){
        for(JPanel panel : oldPanels){
            panel.setVisible(false);
        }
        for (JPanel panel : newPanels){
            panel.setVisible(true);
        }

    }

}
