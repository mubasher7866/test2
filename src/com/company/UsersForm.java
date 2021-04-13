package com.company;


import com.toedter.calendar.JDateChooser;
import net.proteanit.sql.DbUtils;

import java.awt.EventQueue;
import javax.swing.JFrame;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.JSeparator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UsersForm {

    private JFrame frame;
    private JComboBox<String> cbUserTypeOrRole;
    private JDateChooser dtpDOB;
    private JButton btnSave;
    private JButton btnDelete;
    private static Connection con;
    private static PreparedStatement pst;
    private static ResultSet rs;
    private JTextField tbUsername;
    private JTable table;
    private JTextField tbName;
    private JPasswordField tbPassword;
    private JPasswordField tbRe_enterPassword;
    private JTextField tbEmail;
    private JTextField tbMobileNo;
    private JTextField tbAddress;
    private String recordID = "";
    private JTextField tbSearchRecords;
    private JTextField tbStudentFacultyID;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    UsersForm window = new UsersForm();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public UsersForm() {
        initialize();
        Connect();
        addRolesInComboBox();
        loadUsersRecords("List", "");
        clearAllControls();

    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 797, 423);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        // Setting icon for window
        ImageIcon logo = new ImageIcon(getClass().getClassLoader().getResource("virtual_global_campus_logo_small.png"));
        frame.setIconImage(logo.getImage());
        frame.setTitle("Users");

        JLabel lblNewLabel = new JLabel("Username");
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel.setBounds(378, 15, 62, 14);
        frame.getContentPane().add(lblNewLabel);

        tbUsername = new JTextField();
        tbUsername.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbUsername.setBounds(450, 13, 128, 20);
        frame.getContentPane().add(tbUsername);
        tbUsername.setColumns(10);

        btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (checkControls() == true) {

                    if (btnSave.getText().toLowerCase().equals("save") == true) {
                        if (isUsernameExist(tbUsername.getText()) == true) {
                            try {

                                // Start saving records in users table
                                pst = con.prepareStatement(
                                        "insert into Users(name,roleID,username,password,email,mobileNo,DOB,address,createdBy,isActive)values(?,?,?,?,?,?,?,?,?,?)");
                                pst.setString(1, tbName.getText());
                                pst.setInt(2, cbUserTypeOrRole.getSelectedIndex());
                                pst.setString(3, tbUsername.getText());
                                pst.setString(4, JavaWindowsFormSecurityLayer
                                        .encryptPassword(String.copyValueOf(tbRe_enterPassword.getPassword())));
                                pst.setString(5, tbEmail.getText());
                                pst.setString(6, tbMobileNo.getText());
                                pst.setString(7, DateFormat.getDateInstance(DateFormat.SHORT).format(dtpDOB.getDate()));
                                pst.setString(8, tbAddress.getText());
                                pst.setString(9, "System");
                                pst.setBoolean(10, true);
                                pst.executeUpdate();
                                // End saving records in users table

                                // extra record to save if user is student or faculty member
                                if (cbUserTypeOrRole.getSelectedItem().equals("Student") == true || cbUserTypeOrRole.getSelectedItem().equals("Faculty") == true) {

                                    // checks for ID uniqueness and queries
                                    if (cbUserTypeOrRole.getSelectedItem().equals("Student") == true && isStudentIDExist(tbStudentFacultyID.getText()) == true)
                                        pst = con.prepareStatement("insert into student(studentID,createdBy,isActive)values(?,?,?)");
                                    else if (cbUserTypeOrRole.getSelectedItem().equals("Faculty") == true && isFacultyIDExist(tbStudentFacultyID.getText()) == true)
                                        pst = con.prepareStatement("insert into faculty(FacultyID,createdBy,isActive)values(?,?,?)");
                                    else
                                        return;

                                    // Start saving records in student or faculty table
                                    pst.setString(1, tbStudentFacultyID.getText());
                                    pst.setString(2, "System");
                                    pst.setBoolean(3, true);
                                    pst.executeUpdate();
                                    // End saving records in student or faculty table

                                }

                                JOptionPane.showMessageDialog(null, "Record is added successfully!");
                                clearAllControls();
                                loadUsersRecords("List", "");
                            } catch (SQLException exe) {
                                exe.printStackTrace();
                            }
                        }
                    } else if (btnSave.getText().toLowerCase().equals("update") == true) {
                        try {

                            pst = con.prepareStatement(
                                    "update Users set name= ?,roleID=?,username=?,email=?,mobileNo=?,DOB=?,address=?,updatedBy=? where id =?");
                            pst.setString(1, tbName.getText());
                            pst.setInt(2, cbUserTypeOrRole.getSelectedIndex());
                            pst.setString(3, tbUsername.getText());
                            pst.setString(4, tbEmail.getText());
                            pst.setString(5, tbMobileNo.getText());
                            pst.setString(6, DateFormat.getDateInstance(DateFormat.SHORT).format(dtpDOB.getDate()));
                            pst.setString(7, tbAddress.getText());
                            pst.setString(8, "System");
                            pst.setString(9, recordID);
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Record is updated successfully!");
                            clearAllControls();
                            loadUsersRecords("List", "");
                        } catch (SQLException exe) {
                            exe.printStackTrace();
                        }
                    }
                }
            }
        });
        btnSave.setBounds(275, 155, 89, 23);
        frame.getContentPane().add(btnSave);

        JScrollPane dgvUsers = new JScrollPane();
        dgvUsers.setBounds(10, 214, 761, 159);
        frame.getContentPane().add(dgvUsers);

        table = new JTable() {
            private static final long serialVersionUID = 1L;

            // This will make a non editable JTabel.
            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                // getting the selected row
                int rowIndex = table.getSelectedRow();
                TableModel model = table.getModel();

                recordID = model.getValueAt(rowIndex, 0).toString();
                tbName.setText(model.getValueAt(rowIndex, 1).toString());
                int anIndex = Integer.parseInt(model.getValueAt(rowIndex, 2).toString());
                cbUserTypeOrRole.setSelectedIndex(anIndex);
                tbUsername.setText(model.getValueAt(rowIndex, 3).toString());
                tbUsername.setEditable(false);
                tbPassword.setText(model.getValueAt(rowIndex, 4).toString());
                tbRe_enterPassword.setText(model.getValueAt(rowIndex, 4).toString());
                tbPassword.setEditable(false);
                tbRe_enterPassword.setEditable(false);
                tbEmail.setText(model.getValueAt(rowIndex, 4).toString());
                tbMobileNo.setText(model.getValueAt(rowIndex, 5).toString());

                try {
                    java.util.Date date = new SimpleDateFormat("dd/MM/yyyy")
                            .parse(model.getValueAt(rowIndex, 6).toString());

                    dtpDOB.setDate(date);

                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                tbAddress.setText(model.getValueAt(rowIndex, 7).toString());

                btnSave.setText("Update");
                // making btnDelete hidden
                btnDelete.setVisible(true);
            }
        });
        dgvUsers.setViewportView(table);

        tbName = new JTextField();
        tbName.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbName.setColumns(10);
        tbName.setBounds(55, 11, 128, 20);
        frame.getContentPane().add(tbName);

        JLabel lblNewLabel_1 = new JLabel("Name");
        lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblNewLabel_1.setBounds(10, 15, 48, 14);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblPassword.setBounds(588, 17, 62, 14);
        frame.getContentPane().add(lblPassword);

        tbPassword = new JPasswordField();
        tbPassword.setBounds(643, 15, 118, 20);
        frame.getContentPane().add(tbPassword);

        tbRe_enterPassword = new JPasswordField();
        tbRe_enterPassword.setBounds(112, 60, 136, 20);
        frame.getContentPane().add(tbRe_enterPassword);

        JLabel lblReenterPassword = new JLabel("Re-enter Password");
        lblReenterPassword.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblReenterPassword.setBounds(10, 60, 103, 14);
        frame.getContentPane().add(lblReenterPassword);

        JLabel lblUserType = new JLabel("User Type");
        lblUserType.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblUserType.setBounds(193, 15, 62, 14);
        frame.getContentPane().add(lblUserType);

        tbEmail = new JTextField();
        tbEmail.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbEmail.setColumns(10);
        tbEmail.setBounds(296, 60, 162, 20);
        frame.getContentPane().add(tbEmail);

        JLabel lblEmail = new JLabel("E-mail");
        lblEmail.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblEmail.setBounds(258, 62, 62, 14);
        frame.getContentPane().add(lblEmail);

        tbMobileNo = new JTextField();
        tbMobileNo.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbMobileNo.setColumns(10);
        tbMobileNo.setBounds(530, 62, 231, 20);
        frame.getContentPane().add(tbMobileNo);

        JLabel lblMobile = new JLabel("Mobile #");
        lblMobile.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblMobile.setBounds(476, 64, 62, 14);
        frame.getContentPane().add(lblMobile);

        JLabel lblDob = new JLabel("DOB");
        lblDob.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblDob.setBounds(10, 117, 35, 14);
        frame.getContentPane().add(lblDob);

        tbAddress = new JTextField();
        tbAddress.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbAddress.setColumns(10);
        tbAddress.setBounds(265, 115, 496, 20);
        frame.getContentPane().add(tbAddress);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setFont(new Font("Times New Roman", Font.BOLD, 12));
        lblAddress.setBounds(193, 117, 62, 14);
        frame.getContentPane().add(lblAddress);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                clearAllControls();
            }
        });
        btnCancel.setBounds(394, 155, 89, 23);
        frame.getContentPane().add(btnCancel);

        dtpDOB = new JDateChooser();
        dtpDOB.setBounds(55, 115, 128, 20);
        frame.getContentPane().add(dtpDOB);
        dtpDOB.setDateFormatString("dd-MM-yyyy");

        cbUserTypeOrRole = new JComboBox<String>();
        // check if student or faculty item is selected
        cbUserTypeOrRole.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (cbUserTypeOrRole.getItemCount() > 0 && cbUserTypeOrRole.getSelectedItem() != null) {
                    if (cbUserTypeOrRole.getSelectedItem().equals("Student") == true) {
                        tbStudentFacultyID.setVisible(true);
                        tbStudentFacultyID.setToolTipText("Enter Student college ID");
                    } else if (cbUserTypeOrRole.getSelectedItem().equals("Faculty") == true) {
                        tbStudentFacultyID.setVisible(true);
                        tbStudentFacultyID.setToolTipText("Enter employee ID");
                    } else {
                        tbStudentFacultyID.setVisible(false);
                    }
                }
            }
        });
        cbUserTypeOrRole.setBounds(258, 11, 110, 22);
        frame.getContentPane().add(cbUserTypeOrRole);

        JSeparator separator = new JSeparator();
        separator.setBounds(10, 142, 761, 2);
        frame.getContentPane().add(separator);

        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    pst = con.prepareStatement("update Users set isActive=?, updatedBy=? where id =?");
                    pst.setBoolean(1, false);
                    pst.setString(2, "System");
                    pst.setString(3, recordID);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record is deleted successfully!");
                    clearAllControls();
                    loadUsersRecords("List", "");
                } catch (SQLException exe) {
                    exe.printStackTrace();
                }
            }
        });
        btnDelete.setBounds(508, 155, 89, 23);
        frame.getContentPane().add(btnDelete);

        tbSearchRecords = new JTextField();
        tbSearchRecords.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                loadUsersRecords("Search", tbSearchRecords.getText());
            }
        });
        tbSearchRecords.setText("");
        tbSearchRecords.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbSearchRecords.setColumns(10);
        tbSearchRecords.setBounds(10, 189, 761, 20);
        frame.getContentPane().add(tbSearchRecords);

        tbStudentFacultyID = new JTextField();
        tbStudentFacultyID.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        tbStudentFacultyID.setEditable(true);
        tbStudentFacultyID.setColumns(10);
        tbStudentFacultyID.setBounds(258, 32, 110, 20);
        frame.getContentPane().add(tbStudentFacultyID);
        tbStudentFacultyID.setToolTipText("Enter Student college ID");
        tbStudentFacultyID.setVisible(false);
        // making btnDelete hidden
        btnDelete.setVisible(false);
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

    // This function will be used for clearing or refresh all control fields
    void clearAllControls() {

        tbName.setText("");
        cbUserTypeOrRole.setSelectedIndex(-1);
        tbUsername.setText("");
        tbUsername.setEditable(true);
        tbPassword.setText("");
        tbRe_enterPassword.setText("");
        tbEmail.setText("");
        tbMobileNo.setText("");
        dtpDOB.setDate(null);
        tbAddress.setText("");
        tbName.requestFocus();
        tbPassword.setEditable(true);
        tbRe_enterPassword.setEditable(true);
        btnSave.setText("Save");
        // making btnDelete hidden
        btnDelete.setVisible(false);
        loadUsersRecords("List", "");
    }

    public void loadUsersRecords(String loadType, String searchKey) {
        try {
            String query = null;
            if (loadType.toLowerCase().equals("search") == true)
                query = "select ID, name as 'Name',roleID,username as 'Username',email as 'E-mail',mobileNo as 'Mobile#',DOB,address as 'Address' from users where name like '"
                        + searchKey + "%' and isActive=1 order by name asc;";
            else if (loadType.toLowerCase().equals("list") == true)
                query = "select ID, name as 'Name',roleID,username as 'Username',email as 'E-mail',mobileNo as 'Mobile#',DOB,address as 'Address' from users where isActive=1  order by name asc";

            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            table.setModel(DbUtils.resultSetToTableModel(rs));
            show_hide_column(0, "hide");// Hiding the ID column
            show_hide_column(2, "hide");// Hiding the roleID column
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRolesInComboBox() {
        try {

            String query = "select * from roles where isActive=1";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            while (rs.next()) {

                cbUserTypeOrRole.addItem(rs.getString("roleName"));
            }
        } catch (SQLException exe) {
            exe.printStackTrace();
        }
    }

    // This function will be used for hiding and showing any j-table column
    // visibility
    private void show_hide_column(int index, String operation) {

        if (operation.toLowerCase().equals("hide") == true) {
            TableColumn column = table.getColumnModel().getColumn(index);
            column.setMinWidth(0);
            column.setMaxWidth(0);
            column.setWidth(0);
            column.setPreferredWidth(0);
        } else if (operation.toLowerCase().equals("show") == true) {
            final int width = 250;
            TableColumn column = table.getColumnModel().getColumn(index);
            column.setMinWidth(width);
            column.setMaxWidth(width);
            column.setWidth(width);
            column.setPreferredWidth(width);
        }
    }

    public boolean checkControls() {

        if (JavaWindowsFormValidations.validateOnlyTextBoxLetters("Name", tbName.getText(), 1, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.validateComboBox("User Type", cbUserTypeOrRole.getSelectedIndex()) == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyLength("Username", tbUsername.getText(), 1, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyLength("Password", String.copyValueOf(tbPassword.getPassword()),
                8, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyLength("Re-enter Password",
                String.copyValueOf(tbRe_enterPassword.getPassword()), 8, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.compareTwoStrings(String.copyValueOf(tbPassword.getPassword()),
                String.copyValueOf(tbRe_enterPassword.getPassword()), "Passwords are not matched.") == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyEmail("E-mail", tbEmail.getText(), 1, 250) == false)
            return false;
        else if (JavaWindowsFormValidations.validateOnlyTextBoxDigits("Mobile #", tbMobileNo.getText(), 11,
                13) == false)
            return false;
        else {
            try {

                if (JavaWindowsFormValidations.validateOnlyLength("DOB",
                        DateFormat.getDateInstance(DateFormat.SHORT).format(dtpDOB.getDate()), 1, 12) == false)
                    return false;
                if (JavaWindowsFormValidations.validateOnlyLength("Address", tbAddress.getText().toString(), 5,
                        255) == false)
                    return false;
                else {

                    if (tbStudentFacultyID.isVisible() == true) {
                        if (JavaWindowsFormValidations.validateOnlyTextBoxDigits("Student or Faculty ID",
                                tbStudentFacultyID.getText(), 1, 16) == false)
                            return false;
                        else
                            return true;
                    }
                }
            } catch (Exception exe) {
                JavaWindowsFormUserInformers.showMsgWithJPane("Please choose DOB.");
                return false;
            }
        }
        return false;
    }

    public static boolean isUsernameExist(String username) {
        String query = "select username from users where username=?";
        try {
            pst = con.prepareStatement(query);
            pst.setString(1, username);
            rs = pst.executeQuery();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            // check for any user-name already in use or not
            // check for any record, if the first call to next() returns false then there is
            // no data in the ResultSet.
            if (rs.next() == false) {
                JavaWindowsFormUserInformers
                        .showMsgWithJPane("The username already exists, please choose the other one.");
                return false;
            } else
                return true;
        } catch (SQLException e) {

            e.printStackTrace();

        }
        return false;
    }

    public static boolean isStudentIDExist(String studentID) {
        String query = "select studentID from student where studentID=?";
        try {
            pst = con.prepareStatement(query);
            pst.setString(1, studentID);
            rs = pst.executeQuery();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            // check for any studentID already in use or not
            // check for any record, if the first call to next() returns false then there is
            // no data in the ResultSet.
            if (rs.next() == false) {
                JavaWindowsFormUserInformers
                        .showMsgWithJPane("This student ID already exists, please choose the other one.");
                return false;
            } else
                return true;
        } catch (SQLException e) {

            e.printStackTrace();

        }
        return false;
    }

    public static boolean isFacultyIDExist(String facultyID) {
        String query = "select facultyID from faculty where facultyID=?";
        try {
            pst = con.prepareStatement(query);
            pst.setString(1, facultyID);
            rs = pst.executeQuery();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            // check for any facultyID already in use or not
            // check for any record, if the first call to next() returns false then there is
            // no data in the ResultSet.
            if (rs.next() == false) {
                JavaWindowsFormUserInformers
                        .showMsgWithJPane("This faculty ID already exists, please choose the other one.");
                return false;
            } else
                return true;
        } catch (SQLException e) {

            e.printStackTrace();

        }
        return false;
    }

}

