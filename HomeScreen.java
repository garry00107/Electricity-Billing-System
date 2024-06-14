import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    public HomeScreen() {
        setTitle("Electricity Billing System");
        setSize(600, 510);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton customerLoginButton = new JButton("Customer Login");
        JButton newCustomerButton = new JButton("New Customer");
        JButton adminLoginButton = new JButton("Admin Login");

        // Style the buttons
        customerLoginButton.setFont(new Font("Arial", Font.BOLD, 16));
        newCustomerButton.setFont(new Font("Arial", Font.BOLD, 16));
        adminLoginButton.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(customerLoginButton);
        panel.add(newCustomerButton);
        panel.add(adminLoginButton);

        add(panel);

        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("/Users/ramdorak/Desktop/CODE/ElecBillingSystem/image.jpg"); // Replace with the actual image file path
        imageLabel.setIcon(imageIcon);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(imageLabel, BorderLayout.CENTER);
        mainPanel.add(panel, BorderLayout.SOUTH);

        add(mainPanel);

        customerLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerLoginScreen();
            }
        });

        newCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerRegistrationScreen();
            }
        });

        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminLoginScreen();
            }
        });
    }

    private void openCustomerLoginScreen() {
        CustomerLogin customerLogin = new CustomerLogin();
        customerLogin.setVisible(true);
        setVisible(false);
    }

    private void openCustomerRegistrationScreen() {
        CustomerRegistration customerRegistration = new CustomerRegistration();
        customerRegistration.setVisible(true);
        setVisible(false);
    }

    private void openAdminLoginScreen() {
        AdminLogin adminLogin = new AdminLogin();
        adminLogin.setVisible(true);
        setVisible(false);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            HomeScreen homeScreen = new HomeScreen();
            homeScreen.setVisible(true);
        });
    }
}
