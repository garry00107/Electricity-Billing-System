import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ComplaintScreen extends JFrame {
    private String customerName;
    private JTextArea complaintTextArea;
    private JButton submitButton;
    private JButton homeButton;

    public ComplaintScreen(String customerName) {
        this.customerName = customerName;

        setTitle("Raise Complaint");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JLabel nameLabel = new JLabel("Complaint from: " + customerName);
        complaintTextArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(complaintTextArea);

        submitButton = new JButton("Submit");

        panel.add(nameLabel);
        panel.add(scrollPane);
        panel.add(submitButton);
        panel.add(homeButton);

        add(panel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String complaint = complaintTextArea.getText();

                String url = "jdbc:mysql://localhost:3306/ramdb";
                String dbUsername = "root";
                String dbPassword = "Pass@321";

                String query = "INSERT INTO complaints (customer_name, complaint) VALUES (?, ?)";

                try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                    preparedStatement.setString(1, customerName);
                    preparedStatement.setString(2, complaint);

                    int rowsInserted = preparedStatement.executeUpdate();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(ComplaintScreen.this,
                                "Complaint submitted successfully.",
                                "Complaint Submitted",
                                JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ComplaintScreen.this,
                                "Failed to submit complaint. Please try again.",
                                "Complaint Submission Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ComplaintScreen.this,
                            "An error occurred while processing your request.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHomeScreen();
            }
        });
    }

    private void openHomeScreen() {
        HomeScreen homeScreen = new HomeScreen();
        homeScreen.setVisible(true);
        dispose(); // Close the AdminLogin window
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ComplaintScreen complaintScreen = new ComplaintScreen("John Doe");
            complaintScreen.setVisible(true);
        });
    }
}
