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

public class Doctor implements Person, MedicalStaff, Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String gender;
    private Date dateOfBirth;
    private String specialization;

    public Doctor(String name, String gender, Date dateOfBirth, String specialization) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.specialization = specialization;
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

    @Override
    public String getSpecialization() {
        return specialization;
    }

    @Override
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public static void registerDoctor(Doctor doctor) throws DuplicateDoctorException {
        String fileName = generateFileName(doctor, "doctors");

        if (isDoctorFileExists(fileName)) {
            throw new DuplicateDoctorException("Doctor with the same name and specialization already exists.");
        }

        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            objectOutputStream.writeObject(doctor);
            System.out.println("Doctor registered successfully. File created: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateFileName(Doctor doctor, String folder) {
        return folder + "/" + "Dr." + doctor.getName() + "_" + doctor.getSpecialization() + "_" +
                new SimpleDateFormat("yyyyMMdd").format(doctor.getDateOfBirth()) + ".ser";
    }

    private static boolean isDoctorFileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static Doctor deserializeDoctor(String fileName) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("doctors/" + fileName))) {
            return (Doctor) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerDoctorToDB(Doctor doctor, DatabaseConnector databaseConnector)
            throws DuplicateDoctorException, IllegalArgumentException {
        try {
            InputValidation.validateNameFormat(doctor.getName());
            InputValidation.validateGender(doctor.getGender());
            InputValidation.validateDateFormat(doctor.getDateOfBirth());
            InputValidation.validateSpecialization(doctor.getSpecialization());

            if (isDuplicateDoctor(doctor, databaseConnector)) {
                throw new DuplicateDoctorException("Doctor with the same name and specialization already exists.");
            }

            try (Connection connection = databaseConnector.connect()) {
                String sql = "INSERT INTO doctors (name, gender, date_of_birth, specialization) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, doctor.getName());
                    preparedStatement.setString(2, doctor.getGender());
                    preparedStatement.setDate(3, new java.sql.Date(doctor.getDateOfBirth().getTime()));
                    preparedStatement.setString(4, doctor.getSpecialization());
                    preparedStatement.setInt(5, 2); // user_id to 2 (staff) by default

                    preparedStatement.executeUpdate();
                    System.out.println("Doctor registered successfully.");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (DuplicateDoctorException e) {
            throw e;
        } catch (SQLException e) {
            handleSQLException(e, "Doctor with the same name and specialization already exists.");
        }
    }

    private static boolean isDuplicateDoctor(Doctor doctor, DatabaseConnector databaseConnector) throws SQLException {
        try (Connection connection = databaseConnector.connect()) {
            String sql = "SELECT COUNT(*) FROM doctors WHERE name = ? AND specialization = ? AND date_of_birth = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, doctor.getName());
                preparedStatement.setString(2, doctor.getSpecialization());
                preparedStatement.setDate(3, new java.sql.Date(doctor.getDateOfBirth().getTime()));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1) > 0;
                }
            }
        }
    }


    private static void handleSQLException(SQLException e, String errorMessage) throws DuplicateDoctorException {
        if (e.getMessage().contains("Duplicate entry")) {
            throw new DuplicateDoctorException(errorMessage);
        } else {
            e.printStackTrace();
        }
    }

    public static List<Doctor> getAllDoctorsFromDB(DatabaseConnector databaseConnector) throws SQLException {
        List<Doctor> doctors = new ArrayList<>();

        try (Connection connection = databaseConnector.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM doctors")) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String gender = resultSet.getString("gender");
                Date dateOfBirth = resultSet.getDate("date_of_birth");
                String specialization = resultSet.getString("specialization");

                Doctor doctor = new Doctor(name, gender, dateOfBirth, specialization);
                doctors.add(doctor);
            }
        }
        return doctors;
    }

}
