package com.techelevator.tenmo.consoleGUI;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.ActiveService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.consoleGUI.unusedPanels.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ConsoleGUI extends JFrame {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private AuthenticatedUser currentUser;

    Image backgroundImage = Toolkit.getDefaultToolkit().getImage("tenmo-client\\src\\main\\resources\\Images\\Backgrounds\\Untitled design (15).png");
    ImageIcon backgroundIcon = new ImageIcon((backgroundImage.getScaledInstance(560, 1000, Image.SCALE_SMOOTH)));
    JLabel backGroundLabel = new JLabel(backgroundIcon);

    //PANELS
    SearchAllUsersPanel searchAllUsersPanel;
    //MainMenuPanel mainMenuPanel;

    NavigationButtonPanel navigationButtonPanel;

    ContactsPanel contactsPanel;
    ViewTransfersPanel viewTransfersPanel;
    JPanel cardPanel;

    private int mouseX;
    private int mouseY;
    java.util.List<String> usernames;

    java.util.List<User> allUsers;

    //final static String MAIN_MENU = "Main Menu";
    final static String SEARCH_MENU = "Search Menu";
    final static String CONTACTS_MENU = "Contacts Menu";
    final static String TRANSFERS_MENU = "Transfer Menu";

    private final ActiveService activeService = new ActiveService(API_BASE_URL, currentUser);

    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);

    public ConsoleGUI(AuthenticatedUser currentUser) {
        super("TEnmo");


        this.currentUser = currentUser;
        activeService.setCurrentUser(currentUser);
        activeService.setAuthToken(currentUser.getToken());
        transferService.setCurrentUser(currentUser);
        transferService.setAuthToken(currentUser.getToken());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 1000);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        setLayout(null);
        setDefaultLookAndFeelDecorated(true);

        addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int deltaX = e.getX() - mouseX;
                int deltaY = e.getY() - mouseY;
                setLocation(getX() + deltaX, getY() + deltaY);
            }
        });

        setContentPane(backGroundLabel);

        allUsers = activeService.getAllUsers(currentUser);

        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBounds(0, 100, 560, 750);
        cardPanel.setOpaque(false);

        searchAllUsersPanel = new SearchAllUsersPanel(activeService, transferService, currentUser);
       // searchAllUsersPanel.setBounds(10, 100, 540, 500);
        searchAllUsersPanel.setBackground(new Color(10, 120, 120));
        searchAllUsersPanel.setOpaque(false);
        cardPanel.add(searchAllUsersPanel, SEARCH_MENU);

        contactsPanel = new ContactsPanel(activeService, transferService, currentUser);
        //contactsPanel.setBounds(10, 100, 540, 600);
        contactsPanel.setBackground(new Color(10, 120, 120));
        contactsPanel.setOpaque(false);
        cardPanel.add(contactsPanel, CONTACTS_MENU);

        viewTransfersPanel = new ViewTransfersPanel(activeService, transferService, currentUser);
        //viewTransfersPanel.setBounds(10, 200, 540, 520);

        viewTransfersPanel.setBackground(new Color(10, 222, 120));
        viewTransfersPanel.setOpaque(false);
        cardPanel.add(viewTransfersPanel, TRANSFERS_MENU);


        add(cardPanel);

        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
        cardLayout.show(cardPanel, TRANSFERS_MENU);

        //////NAVIGATION BUTTONS PANEL


        navigationButtonPanel = new NavigationButtonPanel(cardPanel) {
        };
        navigationButtonPanel.setLocation(0, 850);
        navigationButtonPanel.setBounds(0, 850, 560, 200);
        add(navigationButtonPanel);


        //EXIT AND (NEED TO ADD) MINIMIZE BUTTONS

        JButton exitButton = new JButton("X");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setSize(50, 50);
        exitButton.setMargin(new Insets(0, 0, 0, 0));

        JPanel exitPanel = new JPanel(new GridLayout());
        exitPanel.add(exitButton);
        exitPanel.setLocation(535, 5);
        exitPanel.setSize(20, 20);
        add(exitPanel);

        setVisible(true);
    }


}
