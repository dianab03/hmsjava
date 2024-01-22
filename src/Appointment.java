import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.sql.Statement;
import java.util.ArrayList;

    public class Appointment implements Serializable {
        private static final long serialVersionUID = 1L;
        private Patient patient;
        private Doctor doctor;
        private Date date;
        private String time;

        public Appointment(Patient patient, Doctor doctor, Date date, String time) {
            this.patient = patient;
            this.doctor = doctor;
            this.date = date;
            this.time = time;
        }

        public Patient getPatient() {
            return patient;
        }

        public Doctor getDoctor() {
            return doctor;
        }

        public Date getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public static void scheduleAppointment(Patient patient, Doctor doctor, Date date, String time)
                throws DuplicateAppointmentException {
            String fileName = generateFileName(patient, doctor, date, time);

            if (isAppointmentFileExists(fileName)) {
                throw new DuplicateAppointmentException("Appointment already exists for the specified date and time.");
            }

            Appointment appointment = new Appointment(patient, doctor, date, time);

            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("appointments/"
                    + fileName))) {
                objectOutputStream.writeObject(appointment);
                System.out.println("Appointment scheduled successfully. File created: " + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static String generateFileName(Patient patient, Doctor doctor, Date date, String time) {
            return patient.getName() + "_" + doctor.getName() + "_" +
                    new SimpleDateFormat("dd-MM-yyyy").format(date) + "_" + time + ".ser";
        }

        private static boolean isAppointmentFileExists(String fileName) {
            File file = new File("appointments/" + fileName);
            return file.exists();
        }

        public static Appointment deserializeAppointments(String fileName) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("appointments/"
                    + fileName))) {
                return (Appointment) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static void scheduleAppointmentToDB(Patient patient, Doctor doctor, Date date, String time,
                                                   DatabaseConnector databaseConnector) throws DuplicateAppointmentException,
                AppointmentConflictException {
            try (Connection connection = databaseConnector.connect()) {
                if (isDuplicateAppointment(patient.getName(), doctor.getName(), date, time, databaseConnector)) {
                    throw new DuplicateAppointmentException("Appointment already exists for the specified date and time.");
                }

                if (isAppointmentConflict(doctor.getName(), date, time, databaseConnector)) {
                    throw new AppointmentConflictException("Another appointment is already scheduled for the same doctor at the specified date and time.");
                }

                try {
                    time = InputValidation.validateTimeFormat(time);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error validating appointment time: " + e.getMessage());
                }

                String sql = "INSERT INTO appointments (patient_name, doctor_name, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, patient.getName());
                    preparedStatement.setString(2, doctor.getName());
                    preparedStatement.setDate(3, new java.sql.Date(date.getTime()));
                    preparedStatement.setString(4, time);

                    preparedStatement.executeUpdate();
                    System.out.println("Appointment scheduled successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Log the exception for debugging purposes
            }
        }


        private static boolean isDuplicateAppointment(String patientName, String doctorName, Date date, String time,
                                                      DatabaseConnector databaseConnector) throws SQLException {
            String sql = "SELECT COUNT(*) FROM appointments WHERE patient_name = ? AND doctor_name = ? AND appointment_date = ? AND appointment_time = ?";
            try (Connection connection = databaseConnector.connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, patientName);
                preparedStatement.setString(2, doctorName);
                preparedStatement.setDate(3, new java.sql.Date(date.getTime()));
                preparedStatement.setString(4, time);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        private static boolean isAppointmentConflict(String doctorName, Date date, String time,
                                                     DatabaseConnector databaseConnector) throws SQLException {
            String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_name = ? AND appointment_date = ? AND appointment_time = ?";
            try (Connection connection = databaseConnector.connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, doctorName);
                preparedStatement.setDate(2, new java.sql.Date(date.getTime()));
                preparedStatement.setString(3, time);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        public static List<Appointment> getAllAppointmentsFromDB(DatabaseConnector databaseConnector) throws SQLException {
            List<Appointment> appointments = new ArrayList<>();

            try (Connection connection = databaseConnector.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM appointments")) {

                while (resultSet.next()) {
                    String patientName = resultSet.getString("patient_name");
                    String doctorName = resultSet.getString("doctor_name");
                    Date date = resultSet.getDate("appointment_date");
                    String time = resultSet.getString("appointment_time");

                    Patient patient = new Patient(patientName, "", new Date());
                    Doctor doctor = new Doctor(doctorName, "", new Date(), "");

                    Appointment appointment = new Appointment(patient, doctor, date, time);
                    appointments.add(appointment);
                }
            }
            return appointments;
        }
    }
