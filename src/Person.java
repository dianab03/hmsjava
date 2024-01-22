import java.util.Date;
public interface Person {
    String getName();
    void setName(String name);

    String getGender();
    void setGender(String gender);

    Date getDateOfBirth();
    void setDateOfBirth(Date dateOfBirth);
}