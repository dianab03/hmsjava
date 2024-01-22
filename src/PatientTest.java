import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientTest {

    @Test
    public void testParameterizedConstructor() {
        String name = "John_Doe";
        String gender = "Male";
        Date dateOfBirth = new Date();
        Patient patient = new Patient(name, gender, dateOfBirth);

        assertEquals(name, patient.getName());
        assertEquals(gender, patient.getGender());
        assertEquals(dateOfBirth, patient.getDateOfBirth());
    }

    @Test
    public void testGetName() {
        String name = "Alice";
        Patient patient = new Patient(name, "Female", new Date());

        String result = patient.getName();

        assertEquals(name, result);
    }

    @Test
    public void testSetName() {
        String initialName = "Bob";
        String newName = "Charlie";
        Patient patient = new Patient(initialName, "Male", new Date());

        patient.setName(newName);

        assertEquals(newName, patient.getName());
    }

    @Test
    public void testGetGender() {
        String gender = "Female";
        Patient patient = new Patient("Sam", gender, new Date());


        String result = patient.getGender();

        assertEquals(gender, result);
    }

    @Test
    public void testSetGender() {
        String initialGender = "Male";
        String newGender = "Female";
        Patient patient = new Patient("Alex", initialGender, new Date());
        patient.setGender(newGender);
        assertEquals(newGender, patient.getGender());
    }

    @Test
    public void testGetDateOfBirth() throws ParseException {

        String dobString = "1990-01-01";
        Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dobString);
        Patient patient = new Patient("Eve", "Female", dateOfBirth);

        Date result = patient.getDateOfBirth();
        assertEquals(dateOfBirth, result);
    }

    @Test
    public void testSetDateOfBirth() throws ParseException {
        Date initialDateOfBirth = new Date();
        String newDobString = "1980-05-10";
        Date newDateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(newDobString);
        Patient patient = new Patient("Frank", "Male", initialDateOfBirth);

        patient.setDateOfBirth(newDateOfBirth);

        assertEquals(newDateOfBirth, patient.getDateOfBirth());
    }

    @Test
    public void testRegisterPatient() throws ParseException, DuplicatePatientException {
        String name = "TestPatient4";
        String gender = "Male";
        Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-02");
        Patient patient = new Patient(name, gender, dateOfBirth);

        try {
            Patient.registerPatient(patient);
        } catch (DuplicatePatientException e) {
            fail("Unexpected DuplicatePatientException");
        }
    }
}