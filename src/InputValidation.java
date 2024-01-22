import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputValidation {

    public static void validateSpecialization(String specialization) throws IllegalArgumentException {
        if (specialization.isEmpty()) {
            throw new IllegalArgumentException("Specialization cannot be empty.");
        }
    }

    public static void validateNameFormat(String name) throws IllegalArgumentException {
        if (!name.matches("[A-Za-z]+_[A-Za-z]+")) {
            throw new IllegalArgumentException("Invalid name format. Please use FirstName_LastName.");
        }
    }

    public static void validateGender(String gender) throws IllegalArgumentException {
        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")) {
            throw new IllegalArgumentException("Invalid gender. Please use Male or Female.");
        }
    }

    public static Date validateDateFormat(Date date) throws IllegalArgumentException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            String dateStr = dateFormat.format(date);
            Date parsedDate = dateFormat.parse(dateStr);

            if (!parsedDate.equals(date)) {
                throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
            }

            return date;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    public static String validateTimeFormat(String time) throws IllegalArgumentException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");

        try {
            Date parsedTime = timeFormat.parse(time);

            if (!timeFormat.format(parsedTime).equals(time)) {
                throw new IllegalArgumentException("Invalid time format. Please use HH:mm.");
            }

            return time;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid time format. Please use HH:mm.");
        }
    }
}
