import com.slimgears.slimprefs.Preferences;

import java.util.Date;

@Preferences
public interface DummyPreferences {
    int getAge();
    void setAge(int age);
    void removeAge();

    String getName();
    void setName(String name);
    boolean hasName();
    void removeName();

    Date getInstallationDate();
    void setInstallationDate(Date date);
    boolean hasInstallationDate();

    boolean isRegistered();
    void setRegistered(boolean registered);
    boolean containsRegistered();
    void removeRegistered();
}
