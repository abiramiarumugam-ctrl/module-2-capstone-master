package com.techelevator.tenmo.consoleGUI;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.ActiveService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.TransferService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LogInPage extends JFrame {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private JPanel registerPanel;
    Image backgroundImage = Toolkit.getDefaultToolkit().getImage("tenmo-client/src/main/resources/Images/Backgrounds/LogInBackground small.png");
    ImageIcon backgroundIcon = new ImageIcon((backgroundImage.getScaledInstance(520, 600, Image.SCALE_SMOOTH)));
    JLabel backGroundLabel = new JLabel(backgroundIcon);
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private final ActiveService activeService = new ActiveService(API_BASE_URL, currentUser);
    private final TransferService transferService = new TransferService(API_BASE_URL, currentUser);


    JLabel passwordLabel;
    JPasswordField passwordField;
    JTextField userNameTextField;
    private JTextField registerUserNameTextField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;

    public LogInPage() {
        setContentPane(backGroundLabel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 600);
        setLocationRelativeTo(null);
        setLayout(null);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setSize(75, 40);
        usernameLabel.setLocation(90, 200);
        userNameTextField = new JTextField(30);
        userNameTextField.setSize(200, 40);
        userNameTextField.setLocation(160, 200);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setSize(75, 40);
        passwordLabel.setLocation(90, 270);

        passwordField = new JPasswordField(20);
        passwordField.setLocation(160, 270);
        passwordField.setSize(200, 40);


        JButton signInButton = new JButton("Sign In");
        signInButton.setLocation(210, 340);
        signInButton.setSize(100, 50);
        signInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiLogIn();
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setLocation(210, 400);
        registerButton.setSize(100, 50);

        registerButton.addActionListener(e -> {

            ///REGISTER PANEL
            registerUserNameTextField = new JTextField(18);
            registerPasswordField = new JPasswordField(18);
            registerConfirmPasswordField = new JPasswordField(18);

            registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,10));
            registerPanel.setPreferredSize(new Dimension(350,175));
            registerPanel.add(new JLabel("Username:  ", SwingConstants.RIGHT)).setPreferredSize(new Dimension(130, 40));
            registerPanel.add(registerUserNameTextField).setPreferredSize(new Dimension(150,40));
           // registerPanel.add(Box.createVerticalStrut(15));
            registerPanel.add(new JLabel("Password:  ", SwingConstants.RIGHT)).setPreferredSize(new Dimension(130, 40));
            registerPanel.add(registerPasswordField).setPreferredSize(new Dimension(150,40));
            //registerPanel.add(Box.createVerticalStrut(15));
            registerPanel.add(new JLabel("Confirm Password:  ", SwingConstants.RIGHT)).setPreferredSize(new Dimension(130, 40));
            registerPanel.add(registerConfirmPasswordField).setPreferredSize(new Dimension(150,40));

            int option = JOptionPane.showOptionDialog(
                    null,
                    registerPanel,
                    "Register",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Register", "Cancel"},
                    "Register"
            );

            if (option == JOptionPane.OK_OPTION) {
                String username = registerUserNameTextField.getText().toLowerCase();
                char[] passwordChars = registerPasswordField.getPassword();
                char[] confirmPasswordChars = registerConfirmPasswordField.getPassword();
                String registerPassword = new String(passwordChars);
                System.out.println(registerPassword);
                String registerConfirmPassword = new String(confirmPasswordChars);
                UserCredentials userCredentials = new UserCredentials(username, registerPassword);
                if (registerPassword.equals(registerConfirmPassword)) {
                    if (authenticationService.register(userCredentials)) {
                        JOptionPane.showMessageDialog(null, "Registration Successful", "", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Something went wrong.  Registration not successful", "", JOptionPane.PLAIN_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match", "", JOptionPane.PLAIN_MESSAGE);
                }

            }
        });

        add(usernameLabel);
        add(userNameTextField);
        add(passwordLabel);
        add(passwordField);
        add(signInButton);
        add(registerButton);

        setVisible(true);

    }

    public void guiLogIn() {

        String userName = userNameTextField.getText().toLowerCase();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        UserCredentials userCredentials = new UserCredentials(userName, password);
        currentUser = authenticationService.login(userCredentials);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Username or Password Incorrect", "Login Error", JOptionPane.ERROR_MESSAGE);
        } else {
            setVisible(false);
            transferService.setCurrentUser(currentUser);
            transferService.setAuthToken(currentUser.getToken());
            ConsoleGUI gui = new ConsoleGUI(currentUser);
        }
    }
}
