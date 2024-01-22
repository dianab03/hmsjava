import java.util.Scanner;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.*;
import java.sql.*;
import java.util.List;


public class Application {

    private static DatabaseConnector databaseConnector = new DatabaseConnector();

    public static void main(String[] args) {
        if (databaseConnector.connect() != null) {
            run(args);
            closeConnection();
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }

    public static void run(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Register a new patient");
            System.out.println("2. Register a new doctor");
            System.out.println("3. Schedule a new appointment");
            System.out.println("4. Show all registered patients");
            System.out.println("5. Show all registered doctors");
            System.out.println("6. Show all scheduled appointments");
            System.out.println("7. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    runPatientRegistration();
                    break;
                case 2:
                    runDoctorRegistration();
                    break;
                case 3:
                    runAppointmentScheduling();
                    break;
                case 4:
                    showAllPatientsFromDB();
                    break;
                case 5:
                    showAllDoctorsFromDB();
                    break;
                case 6:
                    showAllAppointmentsFromDB();
                    break;
                case 7:
                    System.out.println("Exiting the application. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    public static void runPatientRegistration() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter patient details:");

        String name;
        while (true) {
            System.out.print("Name (Format: FirstName_LastName): ");
            name = scanner.nextLine().trim();

            if (!name.matches("[A-Za-z]+_[A-Za-z]+")) {
                System.out.println("Error: Invalid name format. Please use 'FirstName_LastName'.");
            } else {
                break;
            }
        }

        String gender;
        while (true) {
            System.out.print("Gender (Female/Male/Non-Binary): ");
            gender = scanner.nextLine().trim().toLowerCase();

            if (!gender.matches("female|male|non-binary")) {
                System.out.println("Error: Invalid gender. Please use 'Female', 'Male', or 'Non-Binary'.");
            } else {
                break;
            }
        }

        Date dateOfBirth = null;
        while (true) {
            System.out.print("Date of Birth (DD-MM-YYYY): ");
            String dob = scanner.nextLine().trim();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.setLenient(false);
                dateOfBirth = dateFormat.parse(dob);
                break;
            } catch (ParseException e) {
                System.out.println("Error: Invalid date format. Please use 'DD-MM-YYYY'.");
            }
        }

        Patient patient = new Patient(name, gender, dateOfBirth);

        try {
            Patient.registerPatientToDB(patient, databaseConnector);
        } catch (DuplicatePatientException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void runDoctorRegistration() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter doctor details:");

        String name;
        while (true) {
            System.out.print("Name (Format: FirstName_LastName): ");
            name = scanner.nextLine().trim();

            if (!name.matches("[A-Za-z]+_[A-Za-z]+")) {
                System.out.println("Error: Invalid name format. Please use 'FirstName_LastName'.");
            } else {
                break;
            }
        }

        String gender;
        while (true) {
            System.out.print("Gender (Female/Male/Non-Binary): ");
            gender = scanner.nextLine().trim().toLowerCase();

            if (!gender.matches("female|male|non-binary")) {
                System.out.println("Error: Invalid gender. Please use 'Female', 'Male', or 'Non-Binary'.");
            } else {
                break;
            }
        }

        Date dateOfBirth = null;
        while (true) {
            System.out.print("Date of Birth (DD-MM-YYYY): ");
            String dob = scanner.nextLine().trim();

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dateFormat.setLenient(false);
                dateOfBirth = dateFormat.parse(dob);
                break;
            } catch (ParseException e) {
                System.out.println("Error: Invalid date format. Please use 'DD-MM-YYYY'.");
            }
        }

        String specialization;
        System.out.print("Specialization: ");
        specialization = scanner.nextLine().trim();

        Doctor doctor = new Doctor(name, gender, dateOfBirth, specialization);

        try {
            Doctor.registerDoctorToDB(doctor, databaseConnector);
        } catch (DuplicateDoctorException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

        public static void runAppointmentScheduling() {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Select a patient for the appointment:");
            Patient selectedPatient = selectPatientFromDB();

            if (selectedPatient == null) {
                System.out.println("Error: Patient not found.");
                return;
            }

            System.out.println("Select a doctor for the appointment:");
            Doctor selectedDoctor = selectDoctorFromDB();

            if (selectedDoctor == null) {
                System.out.println("Error: Doctor not found.");
                return;
            }

            Date date = null;
            while (true) {
                System.out.print("Enter date (DD-MM-YYYY): ");
                String dateString = scanner.nextLine().trim();

                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    dateFormat.setLenient(false);
                    date = dateFormat.parse(dateString);
                    break;
                } catch (ParseException e) {
                    System.out.println("Error: Invalid date format. Please use 'DD-MM-YYYY'.");
                }
            }

            String time;
            while (true) {
                System.out.print("Enter time (HH-mm): ");
                time = scanner.nextLine().trim();

                try {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");
                    timeFormat.setLenient(false);
                    timeFormat.parse(time);
                    break;
                } catch (ParseException e) {
                    System.out.println("Error: Invalid time format. Please use 'HH-mm'.");
                }
            }

            try {
                Appointment.scheduleAppointmentToDB(selectedPatient, selectedDoctor, date, time, databaseConnector);
                System.out.println("Appointment scheduled successfully.");
            } catch (DuplicateAppointmentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (AppointmentConflictException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    private static Patient selectPatientFromDB() {
        System.out.println("All Registered Patients:");

        try {
            List<Patient> patients = Patient.getAllPatientsFromDB(databaseConnector);

            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }

            System.out.print("Enter the number corresponding to the patient: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            if (choice > 0 && choice <= patients.size()) {
                return patients.get(choice - 1);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve patients from the database.");
        }

        return null;
    }

    private static Doctor selectDoctorFromDB() {
        System.out.println("All Registered Doctors:");

        try {
            List<Doctor> doctors = Doctor.getAllDoctorsFromDB(databaseConnector);

            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i).getName());
            }

            System.out.print("Enter the number corresponding to the doctor: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            if (choice > 0 && choice <= doctors.size()) {
                return doctors.get(choice - 1);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve doctors from the database.");
        }

        return null;
    }

    public static void showAllPatientsFromDB() {
        try {
            List<Patient> patients = Patient.getAllPatientsFromDB(databaseConnector);

            System.out.println("\nAll Registered Patients (Alphabetically):");  // input validation

            for (Patient patient : patients) {
                System.out.println("Name: " + patient.getName() +
                        ", Gender: " + patient.getGender() +
                        ", Date of Birth: " + new SimpleDateFormat("dd-MM-yyyy").format(patient.getDateOfBirth()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve patients from the database.");
        }
    }

    public static void showAllDoctorsFromDB() {
        try {
            List<Doctor> doctors = Doctor.getAllDoctorsFromDB(databaseConnector);

            System.out.println("\nAll Registered Doctors:");

            for (Doctor doctor : doctors) {
                System.out.println("Name: " + doctor.getName() +
                        ", Gender: " + doctor.getGender() +
                        ", Date of Birth: " + new SimpleDateFormat("dd-MM-yyyy").format(doctor.getDateOfBirth()) +
                        ", Specialization: " + doctor.getSpecialization());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve doctors from the database.");
        }
    }

    public static void showAllAppointmentsFromDB() {
        try {
            List<Appointment> appointments = Appointment.getAllAppointmentsFromDB(databaseConnector);

            System.out.println("\nAll Scheduled Appointments (Sorted by Date):");

            TreeMap<Date, List<Appointment>> appointmentsMap = new TreeMap<>();

            for (Appointment appointment : appointments) {
                Date date = appointment.getDate();
                appointmentsMap.computeIfAbsent(date, k -> new ArrayList<>()).add(appointment);
            }

            for (Map.Entry<Date, List<Appointment>> entry : appointmentsMap.entrySet()) {
                Date date = entry.getKey();
                List<Appointment> appts = entry.getValue();

                System.out.println("Date: " + new SimpleDateFormat("dd-MM-yyyy").format(date));

                for (Appointment appt : appts) {
                    System.out.println("  Patient: " + appt.getPatient().getName() +
                            ", Doctor: " + appt.getDoctor().getName() +
                            ", Time: " + appt.getTime());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve appointments from the database.");
        }
    }

    public static void closeConnection() {
        databaseConnector.closeConnection();
    }
}
