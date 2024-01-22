import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        super("Login");

        JPanel panel = new JPanel(new GridBagLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());

                String role = validateLogin(username, password);
                if (role != null) {
                    showWelcomeMessage(role);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginGUI.this, "Invalid username or password. Please try again.");
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String validateLogin(String username, String password) {
        try {
            Connection connection = new DatabaseConnector().getConnection();
            String query = "SELECT role FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showWelcomeMessage(String role) {
        String welcomeMessage = "";
        if ("administrator".equals(role)) {
            welcomeMessage = "Welcome, Administrator! Perform admin tasks here.";
            new AdminDashboard();
        } else if ("staff".equals(role)) {
            welcomeMessage = "Welcome, Staff! Perform staff tasks here.";
            new StaffDashboard();
        }

        JOptionPane.showMessageDialog(LoginGUI.this, welcomeMessage);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginGUI();
            }
        });
    }
}
