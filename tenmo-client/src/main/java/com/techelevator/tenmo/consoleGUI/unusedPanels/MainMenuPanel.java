package com.techelevator.tenmo.consoleGUI.unusedPanels;

import com.techelevator.tenmo.model.Transfer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {

    final static String MAIN_MENU = "Main Menu";
    final static String SEARCH_MENU = "Search Menu";
    final static String CONTACTS_MENU = "Contacts Menu";
    final static String TRANSFERS_MENU = "Transfer Menu";
    final static String ACCOUNT_MENU = "Account Menu";


    private final Dimension MENU_OPTION_DIMENSION = new Dimension(540, 100);

    private final Font MENU_FONT = new Font("Arial", Font.PLAIN, 32);
    public MainMenuPanel(JPanel cardPanel){
        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10 ,10));
        setSize(560, 600);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        addMenuItem("View Account Details",
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-money-box-50.png"),
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
            // Perform action for option 1
            System.out.println("Option 1 clicked!");
    });
        addMenuItem("Send TEBucks",
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-money-transfer-50.png"),
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
            // Perform action for option 1
            System.out.println("Option 2 clicked!");
        });
        addMenuItem("Request TEBucks",
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-request-money-50.png"),
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
            // Perform action for option 1
            System.out.println("Option 3 clicked!");
        });
//        addMenuItem("View Received Requests",
//                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-man-raising-hand-icon-50.png"),
//                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
//            // Perform action for option 1
//            System.out.println("Option 4 clicked!");
//        });
//        addMenuItem("View Sent Requests",
//                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-sent-50.png"),
//                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
//            // Perform action for option 1
//            System.out.println("Option 5 clicked!");
//        });
        addMenuItem("View All Transfers ",
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-infinity-50.png"),
                new ImageIcon("tenmo-client\\src\\main\\resources\\Images\\icons8-next-page-50.png"), () -> {
            cardLayout.show(cardPanel, TRANSFERS_MENU);

            //setVisible(false);
        });

//        NavigationButtonPanel navigationButtonPanel = new NavigationButtonPanel();
//        add(navigationButtonPanel);
    }


    private void addMenuItem(String text, ImageIcon iconLeft, ImageIcon iconRight, Runnable onClick) {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text);

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(3,0,3,0));
        bottomPanel.setOpaque(false);
        label.setIconTextGap(20);
        label.setIcon(iconLeft);
        label.setFont(MENU_FONT);
        label.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
        panel.add(label, BorderLayout.CENTER);
        panel.setMaximumSize(MENU_OPTION_DIMENSION);
        panel.setBackground(new Color(150,255,200));
        Border innerBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        //Border emptyBorder = BorderFactory.createEmptyBorder(9, 0, 9, 0);
        //Border compoundBorder = BorderFactory.createCompoundBorder(emptyBorder, innerBorder);
        panel.setBorder(innerBorder);
        //panel.setOpaque(false);


        JLabel actionLabel = new JLabel(iconRight);
        actionLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
        panel.add(actionLabel, BorderLayout.EAST);

        bottomPanel.add(panel);

        bottomPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(100,255,175));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(new Color(150,255,200));
            }
        });

        add(bottomPanel);
    }
}
