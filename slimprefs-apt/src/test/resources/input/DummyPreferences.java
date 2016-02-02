import com.slimgears.slimprefs.Preferences;

import java.util.Date;

@Preferences
public interface DummyPreferences {
    int getAge();
    void setAge(int age);

    String getName();
    void setName(String name);

    Date getInstallationDate();
    void setInstallationDate(Date date);

    boolean isRegistered();
    void setRegistered(boolean registered);
}
