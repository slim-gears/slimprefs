import com.slimgears.slimprefs.BindPreference;

import java.util.Date;

class DummyInjectionTarget {
    boolean receivedRegistered;
    String receivedName;

    @BindPreference int age;
    @BindPreference(key = "explicit_key_name") String name;
    @BindPreference(keyRes = 1234) Date installationDate;
    @BindPreference(twoWay = true) int runCounter;

    @BindPreference
    void onRegisteredChanged(boolean registered) {
        this.receivedRegistered = registered;
    }

    @BindPreference(key = "explicit_key_name")
    void onNameChanged(String name) {
        this.receivedName = name;
    }
}
