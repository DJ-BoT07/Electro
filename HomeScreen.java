import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomeScreen extends JFrame {
    public HomeScreen() {
        setTitle("Electricity Billing System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton customerLoginButton = new JButton("Customer Login");
        JButton newCustomerButton = new JButton("New Customer");
        JButton adminLoginButton = new JButton("Admin Login");

        panel.add(customerLoginButton);
        panel.add(newCustomerButton);
        panel.add(adminLoginButton);

        add(panel);

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
            HomeScreen homeScreen = new HomeScreen();
            homeScreen.setVisible(true);
        });
    }
}
