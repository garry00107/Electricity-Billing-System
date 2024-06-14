// Loading packages
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDashboard extends JFrame {
    private String customerName;
    private double consumption;
    private String username;
    
    private JButton payButton;
    private JButton homeButton;
    private JButton messagesButton;

    public CustomerDashboard(String customerName, double consumption, String username) {
        this.customerName = customerName;
        this.consumption = consumption;
        this.username = username;

        setTitle("Customer Dashboard");
        setSize(500, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        // mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Labels panel
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel("Welcome, " + customerName + "!");
        JLabel consumptionLabel = new JLabel("Your monthly consumption: " + consumption + " kWh");

        double bill = calculateBill(consumption);
        JLabel billLabel = new JLabel("Your total bill: Rs." + formatDecimal(bill));
        JLabel baseBillLabel = new JLabel("Your base consumption: Rs" + formatDecimal(baseBill(consumption)));
        JLabel taxLabel = new JLabel("Tax " + (tax(consumption) * 100) + "% = Rs" + formatDecimal(tax(consumption) * consumption));

        messagesButton = new JButton("Messages");

        labelsPanel.add(nameLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
        labelsPanel.add(consumptionLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
        labelsPanel.add(billLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
        labelsPanel.add(baseBillLabel);
        labelsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        labelsPanel.add(taxLabel);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton raiseComplaintButton = new JButton("Raise Complaint");
        payButton = new JButton("Pay Bill");
        homeButton = new JButton("Logout");

        buttonsPanel.add(raiseComplaintButton);
        buttonsPanel.add(payButton);
        buttonsPanel.add(homeButton);
        buttonsPanel.add(messagesButton); 

        mainPanel.add(labelsPanel);
        mainPanel.add(buttonsPanel);
        add(mainPanel);

        raiseComplaintButton.addActionListener(e -> {
            openRaiseComplaintDialog();
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHomeScreen();
            
            JOptionPane.showMessageDialog(
                CustomerDashboard.this,
                "You have been logged out",
                "LogOut",
                JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                updateConsumptionInDatabase(username, consumption);

            JOptionPane.showMessageDialog(
                CustomerDashboard.this,
                "Payment successful, re-login to see changes",
                "Payment Success",
                JOptionPane.INFORMATION_MESSAGE
            );
            }
        });
        messagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMessagesScreen();
            }
        });
    }
    private void openMessagesScreen() {
        JFrame messagesFrame = new JFrame("Customer Messages");
        messagesFrame.setSize(400, 300);
        messagesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        messagesFrame.setLocationRelativeTo(null);

        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BorderLayout());

        JTextArea messagesTextArea = new JTextArea();
        messagesTextArea.setEditable(false);

        try {
            String url = "jdbc:mysql://localhost:3306/ramdb";
            String dbUsername = "root";
            String dbPassword = "Pass@321";

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String selectQuery = "SELECT cust_name, message, timestamp FROM adminmessage WHERE cust_name = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String adminName = resultSet.getString("cust_name");
                String message = resultSet.getString("message");
                String timestamp = resultSet.getString("timestamp");

                messagesTextArea.append("From: Administrator\n");
                messagesTextArea.append("Message: " + message + "\n");
                messagesTextArea.append("Timestamp: "+ timestamp + "\n\n");
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            messagesTextArea.setText("An error occurred while retrieving messages.");
        }

        messagesPanel.add(new JScrollPane(messagesTextArea), BorderLayout.CENTER);
        messagesFrame.add(messagesPanel);
        messagesFrame.setVisible(true);
    }
    private void openRaiseComplaintDialog() {
        String complaint = JOptionPane.showInputDialog(
                CustomerDashboard.this,
                "Enter your complaint:",
                "Raise Complaint",
                JOptionPane.QUESTION_MESSAGE
        );
    
        if (complaint != null && !complaint.isEmpty()) {
        try {
            String url = "jdbc:mysql://localhost:3306/ramdb";
            String dbUsername = "root";
            String dbPassword = "Pass@321";

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String insertQuery = "INSERT INTO complaints (cust_name, complaints) VALUES (?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, complaint);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(
                        CustomerDashboard.this,
                        "Complaint raised successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        CustomerDashboard.this,
                        "Failed to raise the complaint.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    CustomerDashboard.this,
                    "An error occurred while raising the complaint.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    }

    private double calculateBill(double consumption) {
        // tax slabs
        double tax = 0;
        if(consumption<=100){
            tax = 0;
        }
        else if(consumption>100 && consumption<=200){
            tax = 0.05;
        }
        else if(consumption>200 && consumption<=300){
            tax = 0.1;
        }
        else{
            tax = 0.2;
        }
        return consumption * 0.15 + consumption * tax;
    }

    private double baseBill(double consumption){
        //0.15 is the consumption cost per unit (1kWh)
        return consumption*0.15;// returns basic consumption cost
    }

    private double tax(double consumption){
        double tax = 0;
        if(consumption<=100){
            tax = 0;
        }
        else if(consumption>100 && consumption<=200){
            tax = 0.05;
        }
        else if(consumption>200 && consumption<=300){
            tax = 0.1;
        }
        else{
            tax = 0.2;
        }
        return tax;
    }

    private String formatDecimal(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value);
    }

    private void openHomeScreen() {
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.setVisible(true);
        dispose();
    }

    private void updateConsumptionInDatabase(String username, double consumption) {
        String updateQuery = "UPDATE cust_table SET consumption = ? WHERE username = ?";
        String url = "jdbc:mysql://localhost:3306/ramdb";
        String dbUsername = "root";
        String dbPassword = "Pass@321";
        consumption = 0.00;
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, consumption);
            preparedStatement.setString(2, username);
            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.println(preparedStatement);
            if (rowsUpdated > 0) {
                System.out.println("Consumption updated in the database.");
            } else {
                System.out.println("Failed to update consumption in the database.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("An error occurred while updating consumption in the database.");
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CustomerDashboard dashboard = new CustomerDashboard("John Doe", 200.0, "JohnDoe");
            dashboard.setVisible(true);
        });
    }
}
