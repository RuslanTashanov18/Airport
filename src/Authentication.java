import javax.swing.*;
import java.awt.*;

public class Authentication extends JFrame {

    private JTextField tfUser = new JTextField(12);
    private JPasswordField tfPass = new JPasswordField(12);

    public Authentication() {
        setTitle("Авторизация");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Логин:"), gbc);
        gbc.gridx = 1;
        add(tfUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Пароль:"), gbc);
        gbc.gridx = 1;
        add(tfPass, gbc);

        JButton btnLogin = new JButton("Войти");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(btnLogin, gbc);

        JButton btnExit = new JButton("Выход");
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        add(btnExit, gbc);

        btnLogin.addActionListener(e -> authenticate());

    }

    private void authenticate() {
        String username = tfUser.getText().trim();
        String password = new String(tfPass.getPassword());
        String role = null;

        // ===== РОЛИ =====

        if (username.equals("admin") && password.equals("1234")) {
            role = "admin";
        } else if (username.equals("user") && password.equals("1111")) {
            role = "user";
        }

        if (role != null) {
            new AirportSystemGUI(role).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка: неверный логин или пароль");
        }
    }
}

