import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private JTextArea billingTextArea;
    private JTextArea customerDataTextArea;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(800, 600); // Increased the window size for better data display
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1)); // Adjusted the layout for the new button

        JButton viewCustomerDataButton = new JButton("View Customer Data");
        JButton setMonthlyConsumptionButton = new JButton("Set Monthly Consumption");
        JButton viewComplaintsButton = new JButton("View Complaints");
        JButton viewCustomerLoginDataButton = new JButton("View Customer Login Data");

        panel.add(viewCustomerDataButton);
        panel.add(setMonthlyConsumptionButton);
        panel.add(viewComplaintsButton);
        panel.add(viewCustomerLoginDataButton);

        add(panel);

        viewCustomerDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openViewCustomerDataDialog();
            }
        });

        setMonthlyConsumptionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSetMonthlyConsumptionDialog();
            }
        });

        viewComplaintsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openViewComplaintsDialog();
            }
        });

        viewCustomerLoginDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openViewCustomerLoginDataDialog();
            }
        });

        // Customize the style of the "Generate Bills" button
        JButton billingButton = new JButton("Generate Bills");
        billingButton.setFont(new Font("Arial", Font.BOLD, 16));
        billingButton.setForeground(Color.BLUE);
        billingButton.setBackground(Color.YELLOW);

        panel.add(billingButton);

        billingTextArea = new JTextArea(10, 30);
        JScrollPane billingScrollPane = new JScrollPane(billingTextArea);
        panel.add(billingScrollPane);

        customerDataTextArea = new JTextArea(10, 30);
        JScrollPane customerDataScrollPane = new JScrollPane(customerDataTextArea);
        panel.add(customerDataScrollPane);

        billingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBills();
            }
        });
    }

    private void generateBills() {
        // Implement billing generation logic here
        StringBuilder billText = new StringBuilder();

        // Connect to the database
        String url = "jdbc:mysql://localhost:3306/ramdb";
        String dbUsername = "root";
        String dbPassword = "Ping@5858";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String selectQuery = "SELECT * FROM billing";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String customerUsername = resultSet.getString("customer_username");
                int billingMonth = resultSet.getInt("billing_month");
                int billingYear = resultSet.getInt("billing_year");
                double consumption = resultSet.getDouble("consumption");
                double totalAmount = resultSet.getDouble("total_amount");

                // Append billing details to the text area
                billText.append("Customer: ").append(customerUsername).append("\n");
                billText.append("Billing Month: ").append(billingMonth).append("\n");
                billText.append("Billing Year: ").append(billingYear).append("\n");
                billText.append("Consumption: ").append(consumption).append(" kWh").append("\n");
                billText.append("Total Amount: $").append(totalAmount).append("\n\n");
            }

            // Close the resources
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    AdminDashboard.this,
                    "An error occurred while retrieving billing data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Display the generated bills in the text area
        billingTextArea.setText(billText.toString());
    }

    private void openSetMonthlyConsumptionDialog() {
        String customerUsername = JOptionPane.showInputDialog(
                AdminDashboard.this,
                "Enter customer username:",
                "Set Monthly Consumption",
                JOptionPane.QUESTION_MESSAGE
        );

        if (customerUsername != null && !customerUsername.isEmpty()) {
            String newConsumptionStr = JOptionPane.showInputDialog(
                    AdminDashboard.this,
                    "Enter new monthly consumption value for " + customerUsername + ":",
                    "Set Monthly Consumption",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (newConsumptionStr != null && !newConsumptionStr.isEmpty()) {
                try {
                    double newConsumption = Double.parseDouble(newConsumptionStr);
                    updateConsumptionInDatabase(customerUsername, newConsumption);
                    JOptionPane.showMessageDialog(
                            AdminDashboard.this,
                            "Monthly consumption updated successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            AdminDashboard.this,
                            "Invalid consumption value. Please enter a valid number.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    private void updateConsumptionInDatabase(String username, double consumption) {
        String updateQuery = "UPDATE cust_table SET consumption = ? WHERE username = ?";
        String url = "jdbc:mysql://localhost:3306/ramdb";
        String dbUsername = "root";
        String dbPassword = "Ping@5858";
        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setDouble(1, consumption);
            preparedStatement.setString(2, username);
            int rowsUpdated = preparedStatement.executeUpdate();
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

    private void openViewComplaintsDialog() {
        // Create a dialog to display complaints
        JFrame complaintsDialog = new JFrame("View Complaints");
        complaintsDialog.setSize(400, 300);

        JTextArea complaintsTextArea = new JTextArea();
        complaintsTextArea.setEditable(false); // Make it read-only

        // Assuming you have complaints stored in some data structure or database,
        // here we populate the text area with sample complaint data.
        complaintsTextArea.setText("Complaint 1: This is the first complaint.\n"
                + "Complaint 2: This is the second complaint.\n"
                + "Complaint 3: This is the third complaint.");

        JScrollPane scrollPane = new JScrollPane(complaintsTextArea);
        complaintsDialog.add(scrollPane);

        complaintsDialog.setVisible(true);
    }

    private void openViewCustomerDataDialog() {
        // Retrieve and display customer data
        String customerData = getCustomerDataFromDatabase(); // Implement this function

        // Create a dialog to display customer data
        JFrame customerDataDialog = new JFrame("View Customer Data");
        customerDataDialog.setSize(400, 300);

        JTextArea customerDataTextArea = new JTextArea();
        customerDataTextArea.setEditable(false); // Make it read-only

        // Display customer data
        customerDataTextArea.setText(customerData);

        JScrollPane scrollPane = new JScrollPane(customerDataTextArea);
        customerDataDialog.add(scrollPane);

        customerDataDialog.setVisible(true);
    }

    private String getCustomerDataFromDatabase() {
        // Implement the logic to retrieve customer data from your database here
        // You will need to connect to your database and fetch the data

        // Example code to fetch customer data
        StringBuilder customerData = new StringBuilder();

        String url = "jdbc:mysql://localhost:3306/yourdb";
        String dbUsername = "yourusername";
        String dbPassword = "yourpassword";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String selectQuery = "SELECT * FROM customer_data";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String customerName = resultSet.getString("customer_name");
                String customerEmail = resultSet.getString("customer_email");
                // Add more fields as needed

                // Append customer details to the text area
                customerData.append("Customer Name: ").append(customerName).append("\n");
                customerData.append("Customer Email: ").append(customerEmail).append("\n");
                // Append more fields as needed
                customerData.append("\n");
            }

            // Close the resources
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customerData.toString();
    }

    private void openViewCustomerLoginDataDialog() {
        // Retrieve and display customer login data
        String customerLoginData = getCustomerLoginDataFromDatabase(); // Implement this function

        // Create a dialog to display customer login data
        JFrame customerLoginDataDialog = new JFrame("View Customer Login Data");
        customerLoginDataDialog.setSize(400, 300);

        JTextArea customerLoginDataTextArea = new JTextArea();
        customerLoginDataTextArea.setEditable(false); // Make it read-only

        // Display customer login data
        customerLoginDataTextArea.setText(customerLoginData);

        JScrollPane scrollPane = new JScrollPane(customerLoginDataTextArea);
        customerLoginDataDialog.add(scrollPane);

        customerLoginDataDialog.setVisible(true);
    }

    private String getCustomerLoginDataFromDatabase() {
        // Implement the logic to retrieve customer login data from your database here
        // You will need to connect to your database and fetch the data

        // Example code to fetch customer login data
        StringBuilder customerLoginData = new StringBuilder();

        String url = "jdbc:mysql://localhost:3306/yourdb";
        String dbUsername = "yourusername";
        String dbPassword = "yourpassword";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String selectQuery = "SELECT * FROM customer_login_data";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String customerUsername = resultSet.getString("customer_username");
                String customerPassword = resultSet.getString("customer_password");
                // Add more fields as needed

                // Append customer login details to the text area
                customerLoginData.append("Customer Username: ").append(customerUsername).append("\n");
                customerLoginData.append("Customer Password: ").append(customerPassword).append("\n");
                // Append more fields as needed
                customerLoginData.append("\n");
            }

            // Close the resources
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customerLoginData.toString();
    }

    public static void main(String[] args) {
        try {
            // Set the Nimbus Look and Feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Handle any exceptions
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.setVisible(true);
        });
    }
}
