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
public class Patient implements Person, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String gender;
    private Date dateOfBirth;

    public Patient(String name, String gender, Date dateOfBirth) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public static void registerPatient(Patient patient) throws DuplicatePatientException {
        String fileName = generateFileName(patient, "patients");

        if (isPatientFileExists(fileName)) {
            throw new DuplicatePatientException("Patient already exists.");
        }

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            objectOutputStream.writeObject(patient);
            System.out.println("Patient registered successfully. File created: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateFileName(Patient patient, String folder) {
        return folder + "/" + patient.getName() + "_" + patient.getGender() + "_" +
                new SimpleDateFormat("yyyyMMdd").format(patient.getDateOfBirth()) + ".ser";
    }

    private static boolean isPatientFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static Patient deserializePatient(String fileName) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("patients/" + fileName))) {
            return (Patient) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerPatientToDB(Patient patient, DatabaseConnector databaseConnector) throws DuplicatePatientException, IllegalArgumentException {
        try {
            InputValidation.validateNameFormat(patient.getName());
            InputValidation.validateGender(patient.getGender());
            InputValidation.validateDateFormat(patient.getDateOfBirth());

            if (isDuplicatePatient(patient, databaseConnector)) {
                throw new DuplicatePatientException("Patient with the same name already exists.");
            }

            try (Connection connection = databaseConnector.connect()) {
                String sql = "INSERT INTO patients (name, gender, date_of_birth) VALUES (?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, patient.getName());
                    preparedStatement.setString(2, patient.getGender());
                    preparedStatement.setDate(3, new java.sql.Date(patient.getDateOfBirth().getTime()));

                    preparedStatement.executeUpdate();
                    System.out.println("Patient registered successfully.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SQLException e) {
            handleSQLException(e, "Patient already exists.");
        }
    }

    private static boolean isDuplicatePatient(Patient patient, DatabaseConnector databaseConnector) throws SQLException {
        try (Connection connection = databaseConnector.connect();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM patients WHERE name = ? AND date_of_birth = ?")) {

            preparedStatement.setString(1, patient.getName());
            preparedStatement.setDate(2, new java.sql.Date(patient.getDateOfBirth().getTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }


    private static void handleSQLException(SQLException e, String errorMessage) throws DuplicatePatientException {
        if (e.getMessage().contains("Duplicate entry")) {
            throw new DuplicatePatientException(errorMessage);
        } else {
            e.printStackTrace();
        }
    }
    public static List<Patient> getAllPatientsFromDB(DatabaseConnector databaseConnector) throws SQLException {
        List<Patient> patients = new ArrayList<>();

        try (Connection connection = databaseConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM patients")) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");           // input validation
                String gender = resultSet.getString("gender");
                Date dateOfBirth = resultSet.getDate("date_of_birth");

                Patient patient = new Patient(name, gender, dateOfBirth);
                patients.add(patient);
            }
        }

        return patients;
    }
}