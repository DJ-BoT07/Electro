// Loading packages
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class CustomerDashboard extends JFrame {
    private String customerName;
    private double consumption;
    private String username;

    public CustomerDashboard(String customerName, double consumption, String username) {
        this.customerName = customerName;
        this.consumption = consumption;
        this.username = username;

        setTitle("Customer Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JLabel nameLabel = new JLabel("Welcome, " + customerName + "!");
        JLabel consumptionLabel = new JLabel("Your monthly consumption: " + consumption + " kWh");
        
        double bill = calculateBill(consumption);
        JLabel billLabel = new JLabel("Your bill: $" + formatDecimal(bill));

        JButton raiseComplaintButton = new JButton("Raise Complaint");

        panel.add(nameLabel);
        panel.add(consumptionLabel);
        panel.add(billLabel);
        panel.add(raiseComplaintButton);

        add(panel);

        raiseComplaintButton.addActionListener(e -> {
            // Open the complaint form or dialog here
            openRaiseComplaintDialog();
        }
        );
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
            // Establish a database connection
            String url = "jdbc:mysql://localhost:3306/ramdb";
            String dbUsername = "root";
            String dbPassword = "Ping@5858";

            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            // Define the SQL query to insert the complaint
            String insertQuery = "INSERT INTO complaints (cust_name, complaints) VALUES (?, ?)";

            // Create a PreparedStatement for the query
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            // Set the parameters (username and complaint_text)
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, complaint);

            // Execute the insert query
            int rowsInserted = preparedStatement.executeUpdate();

            // Check if the complaint was successfully inserted
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

            // Close the database resources
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
        // Replace this with your actual billing logic
        return consumption * 0.15; // Example calculation
    }

    private String formatDecimal(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(value);
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            CustomerDashboard dashboard = new CustomerDashboard("John Doe", 200.0, "JohnDoe");
            dashboard.setVisible(true);
        });
    }
}
