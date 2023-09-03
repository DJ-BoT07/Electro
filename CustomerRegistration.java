import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerRegistration extends JFrame {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField consumptionField;
    private JTextField contactField;
    private JTextField billingMonthField;
    private JTextField billingYearField;
    private JButton submitButton;
    private EntityCirclePanel entityCirclePanel;
    class Entity {
        private String name;
    
        public Entity(String name) {
            this.name = name;
        }
    
        public String getName() {
            return name;
        }
    }
    

    public CustomerRegistration() {
        setTitle("Customer Registration");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 10, 5, 10);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(250, 30));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 30));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 30));
        JLabel consumptionLabel = new JLabel("Monthly Consumption (kWh):");
        consumptionField = new JTextField();
        consumptionField.setPreferredSize(new Dimension(250, 30));
        JLabel contactLabel = new JLabel("Contact Information:");
        contactField = new JTextField();
        contactField.setPreferredSize(new Dimension(250, 30));
        JLabel billingMonthLabel = new JLabel("Billing Month (e.g., 1 for January):");
        billingMonthField = new JTextField();
        billingMonthField.setPreferredSize(new Dimension(250, 30));
        JLabel billingYearLabel = new JLabel("Billing Year:");
        billingYearField = new JTextField();
        billingYearField.setPreferredSize(new Dimension(250, 30));
        submitButton = new JButton("Submit");

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(nameLabel, constraints);
        constraints.gridx = 1;
        panel.add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(usernameLabel, constraints);
        constraints.gridx = 1;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(consumptionLabel, constraints);
        constraints.gridx = 1;
        panel.add(consumptionField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(contactLabel, constraints);
        constraints.gridx = 1;
        panel.add(contactField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(billingMonthLabel, constraints);
        constraints.gridx = 1;
        panel.add(billingMonthField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        panel.add(billingYearLabel, constraints);
        constraints.gridx = 1;
        panel.add(billingYearField, constraints);

        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 7;
        panel.add(submitButton, constraints);

        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBackground(Color.BLUE);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitForm();
            }
        });

        add(panel, BorderLayout.CENTER);

        entityCirclePanel = new EntityCirclePanel();
        add(entityCirclePanel, BorderLayout.SOUTH);
    }

    private class EntityCirclePanel extends JPanel {
        private ArrayList<Entity> entities;

        public EntityCirclePanel() {
            entities = new ArrayList<>();
        }

        public void addEntity(Entity entity) {
            entities.add(entity);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int circleRadius = 100;

            int entityCount = entities.size();
            double angleIncrement = 2 * Math.PI / entityCount;
            double currentAngle = 0;

            for (Entity entity : entities) {
                int x = (int) (centerX + circleRadius * Math.cos(currentAngle));
                int y = (int) (centerY + circleRadius * Math.sin(currentAngle));

                g.setColor(Color.BLACK);
                g.fillOval(x - 10, y - 10, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString(entity.getName(), x - 10, y + 5);

                currentAngle += angleIncrement;
            }
        }
    }

    private void submitForm() {
        String name = nameField.getText();
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        double consumption = Double.parseDouble(consumptionField.getText());
        String contactInfo = contactField.getText();
        int billingMonth = Integer.parseInt(billingMonthField.getText());
        int billingYear = Integer.parseInt(billingYearField.getText());

        double totalAmount = calculateTotalAmount(consumption);

        insertCustomerData(name, username, password, consumption, contactInfo);
        insertBillingData(username, billingMonth, billingYear, consumption, totalAmount);

        clearFormFields();
    }

    private double calculateTotalAmount(double consumption) {
        double billingRate = 0.15;
        double totalAmount = consumption * billingRate;
        return totalAmount;
    }

    private void insertCustomerData(String name, String username, String password, double consumption, String contactInfo) {
        String url = "jdbc:mysql://localhost:3306/your_database_name";
        String dbUsername = "your_database_username";
        String dbPassword = "your_database_password";
        String insertQuery = "INSERT INTO cust_table (name, username, password, consumption, contact_info) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.setDouble(4, consumption);
            preparedStatement.setString(5, contactInfo);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(CustomerRegistration.this,
                        "Customer data recorded successfully!",
                        "Registration Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(CustomerRegistration.this,
                        "Customer data recording failed. Please try again.",
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(CustomerRegistration.this,
                    "An error occurred while processing your request.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertBillingData(String username, int billingMonth, int billingYear, double consumption, double totalAmount) {
        String url = "jdbc:mysql://localhost:3306/your_database_name";
        String dbUsername = "your_database_username";
        String dbPassword = "your_database_password";
        String insertQuery = "INSERT INTO billing (customer_username, billing_month, billing_year, consumption, total_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, billingMonth);
            preparedStatement.setInt(3, billingYear);
            preparedStatement.setDouble(4, consumption);
            preparedStatement.setDouble(5, totalAmount);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(CustomerRegistration.this,
                        "Billing data recorded successfully!",
                        "Billing Data Recorded",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(CustomerRegistration.this,
                        "Billing data recording failed. Please try again.",
                        "Billing Data Recording Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(CustomerRegistration.this,
                    "An error occurred while processing billing data.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormFields() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        consumptionField.setText("");
        contactField.setText("");
        billingMonthField.setText("");
        billingYearField.setText("");
    }

    public static void main(String[] args) {
        try {
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
            CustomerRegistration registrationScreen = new CustomerRegistration();
            registrationScreen.setVisible(true);
        });
    }
}
