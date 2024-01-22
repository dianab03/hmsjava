import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {

    private JComboBox<String> patientDropdown;
    private JComboBox<String> doctorDropdown;
    public AdminDashboard() {
        super("Admin Dashboard");

        JPanel panel = new JPanel(new GridBagLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        patientDropdown = new JComboBox<>();
        doctorDropdown = new JComboBox<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome, Administrator!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        JButton addDoctorButton = new JButton("Add Doctor");
        panel.add(addDoctorButton, gbc);

        gbc.gridx = 1;
        JButton addPatientButton = new JButton("Add Patient");
        panel.add(addPatientButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JButton makeAppointmentButton = new JButton("Make Appointment");
        panel.add(makeAppointmentButton, gbc);

        gbc.gridx = 1;
        JButton backButton = new JButton("Back to Login");
        panel.add(backButton, gbc);

        addDoctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDoctorForm();
            }
        });

        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatientForm();
            }
        });

        makeAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeAppointmentForm();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginGUI();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        initializeBackgroundThread();
    }

    private void addDoctorForm() {
        JFrame addDoctorFrame = new JFrame("Add Doctor");
        addDoctorFrame.setSize(400, 300);

        JPanel addDoctorPanel = new JPanel();
        addDoctorFrame.getContentPane().add(addDoctorPanel, BorderLayout.CENTER);
        addDoctorPanel.setLayout(new GridLayout(5, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        JTextField genderField = new JTextField();
        JLabel dobLabel = new JLabel("Date of Birth (yyyy-MM-dd):");
        JTextField dobField = new JTextField();
        JLabel specializationLabel = new JLabel("Specialization:");
        JTextField specializationField = new JTextField();

        JButton addButton = new JButton("Add Doctor");

        addDoctorPanel.add(nameLabel);
        addDoctorPanel.add(nameField);
        addDoctorPanel.add(genderLabel);
        addDoctorPanel.add(genderField);
        addDoctorPanel.add(dobLabel);
        addDoctorPanel.add(dobField);
        addDoctorPanel.add(specializationLabel);
        addDoctorPanel.add(specializationField);
        addDoctorPanel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String gender = genderField.getText();
                String dobText = dobField.getText();
                String specialization = specializationField.getText();

                try {
                    InputValidation.validateNameFormat(name);
                    InputValidation.validateGender(gender);
                    Date dob = InputValidation.validateDateFormat(new SimpleDateFormat("yyyy-MM-dd").parse(dobText));
                    InputValidation.validateSpecialization(specialization);

                    Doctor doctor = new Doctor(name, gender, dob, specialization);

                    DatabaseConnector databaseConnector = new DatabaseConnector();
                    Doctor.registerDoctorToDB(doctor, databaseConnector);

                    JOptionPane.showMessageDialog(addDoctorFrame, "Doctor added successfully!");
                    addDoctorFrame.dispose();
                } catch (ParseException | DuplicateDoctorException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(addDoctorFrame, "Error adding doctor: " + ex.getMessage());
                }
            }
        });
        addDoctorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addDoctorFrame.setLocationRelativeTo(null);
        addDoctorFrame.setVisible(true);
    }

    private void addPatientForm() {
        JFrame addPatientFrame = new JFrame("Add Patient");
        addPatientFrame.setSize(400, 300);

        JPanel addPatientPanel = new JPanel();
        addPatientFrame.getContentPane().add(addPatientPanel, BorderLayout.CENTER);
        addPatientPanel.setLayout(new GridLayout(4, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        JTextField genderField = new JTextField();
        JLabel dobLabel = new JLabel("Date of Birth (yyyy-MM-dd):");
        JTextField dobField = new JTextField();

        JButton addButton = new JButton("Add Patient");

        addPatientPanel.add(nameLabel);
        addPatientPanel.add(nameField);
        addPatientPanel.add(genderLabel);
        addPatientPanel.add(genderField);
        addPatientPanel.add(dobLabel);
        addPatientPanel.add(dobField);
        addPatientPanel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String gender = genderField.getText();
                String dobText = dobField.getText();

                try {
                    InputValidation.validateNameFormat(name);
                    InputValidation.validateGender(gender);
                    Date dob = InputValidation.validateDateFormat(new SimpleDateFormat("yyyy-MM-dd").parse(dobText));

                    Patient patient = new Patient(name, gender, dob);
                    DatabaseConnector databaseConnector = new DatabaseConnector();
                    Patient.registerPatientToDB(patient, databaseConnector);

                    JOptionPane.showMessageDialog(addPatientFrame, "Patient added successfully!");
                    addPatientFrame.dispose();
                } catch (ParseException | DuplicatePatientException | IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(addPatientFrame, "Error adding patient: " + ex.getMessage());
                }
            }
        });

        addPatientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addPatientFrame.setLocationRelativeTo(null);
        addPatientFrame.setVisible(true);
    }

    private void makeAppointmentForm() {
        JFrame makeAppointmentFrame = new JFrame("Make Appointment");
        makeAppointmentFrame.setSize(400, 300);

        JPanel makeAppointmentPanel = new JPanel();
        makeAppointmentFrame.getContentPane().add(makeAppointmentPanel, BorderLayout.CENTER);
        makeAppointmentPanel.setLayout(new GridLayout(5, 2));

        JLabel patientLabel = new JLabel("Select Patient:");
        JLabel doctorLabel = new JLabel("Select Doctor:");
        JLabel dateLabel = new JLabel("Date (yyyy-MM-dd):");
        JLabel timeLabel = new JLabel("Time:");

        DatabaseConnector databaseConnector = new DatabaseConnector();
        try {
            List<Patient> patients = Patient.getAllPatientsFromDB(databaseConnector);
            List<Doctor> doctors = Doctor.getAllDoctorsFromDB(databaseConnector);

            String[] patientNames = patients.stream().map(Person::getName).toArray(String[]::new);
            String[] doctorNames = doctors.stream().map(Person::getName).toArray(String[]::new);

            patientDropdown = new JComboBox<>(patientNames);
            doctorDropdown = new JComboBox<>(doctorNames);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(makeAppointmentFrame, "Error retrieving patients/doctors: " + ex.getMessage());
        }

        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();

        JButton makeAppointmentButton = new JButton("Make Appointment");

        makeAppointmentPanel.add(patientLabel);
        makeAppointmentPanel.add(patientDropdown);
        makeAppointmentPanel.add(doctorLabel);
        makeAppointmentPanel.add(doctorDropdown);
        makeAppointmentPanel.add(dateLabel);
        makeAppointmentPanel.add(dateField);
        makeAppointmentPanel.add(timeLabel);
        makeAppointmentPanel.add(timeField);
        makeAppointmentPanel.add(makeAppointmentButton);

        makeAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientName = (String) patientDropdown.getSelectedItem();
                String doctorName = (String) doctorDropdown.getSelectedItem();
                String dateText = dateField.getText();
                String time = timeField.getText();

                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateText);
                    Patient patient = new Patient(patientName, "", new Date());
                    Doctor doctor = new Doctor(doctorName, "", new Date(), "");

                    try {
                        Appointment.scheduleAppointmentToDB(patient, doctor, date, time, databaseConnector);
                        JOptionPane.showMessageDialog(makeAppointmentFrame, "Appointment scheduled successfully!");
                        makeAppointmentFrame.dispose();
                    } catch (DuplicateAppointmentException | AppointmentConflictException | IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(makeAppointmentFrame, "Error making appointment: " + ex.getMessage());
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(makeAppointmentFrame, "Error parsing date: " + ex.getMessage());
                }
            }
        });


        makeAppointmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        makeAppointmentFrame.setLocationRelativeTo(null);
        makeAppointmentFrame.setVisible(true);
    }

    private void initializeBackgroundThread() {
        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatabaseConnector databaseConnector = new DatabaseConnector();
                    List<Patient> patients = Patient.getAllPatientsFromDB(databaseConnector);
                    List<Doctor> doctors = Doctor.getAllDoctorsFromDB(databaseConnector);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            updateDropdowns(patients, doctors);
                        }
                    });

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        backgroundThread.start();
    }
    private void updateDropdowns(List<Patient> patients, List<Doctor> doctors) {
        String[] patientNames = patients.stream().map(Person::getName).toArray(String[]::new);
        String[] doctorNames = doctors.stream().map(Person::getName).toArray(String[]::new);

        patientDropdown.setModel(new DefaultComboBoxModel<>(patientNames));
        doctorDropdown.setModel(new DefaultComboBoxModel<>(doctorNames));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AdminDashboard();
            }
        });
    }
}
