package com.company;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField tbUsername;
    private JPasswordField tbPassword;
    private Connection con;
    private PreparedStatement pst;
    private ResultSet rs;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginForm frame = new LoginForm();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public LoginForm() {

        Connect();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 409, 255);
        // Setting icon for window
        ImageIcon logo = new ImageIcon(getClass().getClassLoader().getResource("virtual_global_campus_logo_small.png"));
        setIconImage(logo.getImage());

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        tbUsername = new JTextField();
        tbUsername.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        tbUsername.setColumns(10);
        tbUsername.setBounds(97, 51, 283, 31);
        contentPane.add(tbUsername);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblPassword.setBounds(25, 129, 62, 14);
        contentPane.add(lblPassword);

        JLabel lblNewLabel = new JLabel("Username");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(25, 53, 62, 14);
        contentPane.add(lblNewLabel);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Check all controls or fields has some data
                if (checkControls() == true) {

                    try {

                        String pass=JavaWindowsFormSecurityLayer.encryptPassword(String.copyValueOf(tbPassword.getPassword()));
                        String query="select * from users where username='"+tbUsername.getText()+"' and password='"+pass+"'";
                        pst = con.prepareStatement(query);
                        rs = pst.executeQuery();

                        //check for any record, if the first call to next() returns false then there is no data in the ResultSet.
                        if (rs.next() == false)
                            JavaWindowsFormUserInformers.showMsgWithJPane("Username or password is wrong.");
                        else
                        {
                            //Show the main screen or something else
                            UsersForm.main(null);
                            setVisible(false);
                        }
                    } catch (SQLException e1) {

                        e1.printStackTrace();
                    }
                }
            }
        });
        btnLogin.setBounds(95, 168, 89, 23);
        contentPane.add(btnLogin);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.exit(0);
            }
        });
        btnCancel.setBounds(194, 168, 89, 23);
        contentPane.add(btnCancel);

        tbPassword = new JPasswordField();
        tbPassword.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        tbPassword.setBounds(97, 121, 283, 31);
        contentPane.add(tbPassword);

        JLabel lblNewLabel_1 = new JLabel("");
        lblNewLabel_1.setIcon(logo);
        lblNewLabel_1.setBounds(0, 0, 403, 226);
        contentPane.add(lblNewLabel_1);
    }

    public boolean checkControls() {
        if (JavaWindowsFormValidations.validateOnlyLength("Username", tbUsername.getText(), 1, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyLength("Password", String.copyValueOf(tbPassword.getPassword()),1, 250) == false)
            return false;
        else
            return true;
    }

    /*
     * This function will be used for connection with database
     */

    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost/db_vgc_mubasher_zeb_khan_21694?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                    "root", "");

        } catch (ClassNotFoundException exe) {
            exe.printStackTrace();
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
    }
}

