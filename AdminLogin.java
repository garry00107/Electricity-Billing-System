import javax.swing.* ;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton homeButton;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        homeButton = new JButton("Home");

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        loginButton = new JButton("Login");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(homeButton);
        
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(buttonPanel);

        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                String url = "jdbc:mysql://localhost:3306/ramdb";
                String dbUsername = "root";
                String dbPassword = "Pass@321";

                String query = "SELECT * FROM admin_table WHERE username = ? AND password = ?";

                try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, username);
                    preparedStatement.setString(2, password);

                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        AdminDashboard adminDashboard = new AdminDashboard();
                        adminDashboard.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(AdminLogin.this,
                                "Invalid admin credentials. Please try again.",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(AdminLogin.this,
                            "An error occurred while processing your request.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                usernameField.setText("");
                passwordField.setText("");

                
            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirect to the home page
                openHomeScreen();
            }
        });
    }

    private void openHomeScreen() {
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.setVisible(true);
        dispose(); // Close the AdminLogin window
    }

public static void main (String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> {
        AdminLogin adminLoginScreen = new AdminLogin();
        adminLoginScreen.setVisible(true);
    });
}
}
