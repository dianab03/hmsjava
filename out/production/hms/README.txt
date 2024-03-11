////////////Hospital Management System in Java

This Java app is a simple Hospital management system which allows users to register patients, doctors and schedule
appointments. The users can also view all the registered information. The system utilizes object serialization for
storing patients, doctors and appointments.

////////////Features include:

1. Patient registration
Users can register new patients by providing their name, gender and date of birth.
Patient data is stored using object serialization in individual files.

2. Doctor registration
Users can register new doctors by providing their name, gender and date of birth and specialization.
Doctor data is stored using object serialization in individual files.

3. Appointment scheduling
Users can schedule appointments by selecting a patient, a doctor and providing the date and time.
Appointment data is stored using object serialization in individual files.

4. View registered information
Users can view all the registered patients, doctors and scheduled appointments.
Information is displayed in and organized and sorted manner.

5. Data persistence
Patient, doctor and appointment data is persisted between application runs through object serialization.


////////////How to run:

1. Compile the code
Compile the Java files using Java compiler
    javac Application.java

2. Run the application
Run the compiled application
    java Main

3. Follow the on-screen instructions
Use the application menu to navigate and perform various operations.
Follow the prompts and input data as required.

////////////File structure:

patients/:
Directory containing serialized patient data files.

doctors/:
Directory containing serialized doctor data files.

appointments/:

Directory containing serialized appointment data files.

////////////Dependencies:

The application uses standard Java libraries and does not require external libraries.

////////////Notes:

This application utilizes object serialization for data storage, data files are stored in corresponding directories.
To avoid data corruption, please do not modify the serialized data files manually.

Author:
Diana-Andreea Breaz

Version:
1.0