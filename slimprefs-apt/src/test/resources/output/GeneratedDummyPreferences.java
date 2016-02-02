import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.PreferenceValue;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Date;

class GeneratedDummyPreferences implements DummyPreferences {
    private final PreferenceValue<Date> installationDatePreference;
    private final PreferenceValue<Integer> agePreference;
    private final PreferenceValue<Boolean> registeredPreference;
    private final PreferenceValue<String> namePreference;


    public GeneratedDummyPreferences(PreferenceProvider provider) {
        installationDatePreference = provider.getPreference("DummyPreferences.installationDate", Date.class);
        agePreference = provider.getPreference("DummyPreferences.age", Integer.class);
        registeredPreference = provider.getPreference("DummyPreferences.registered", Boolean.class);
        namePreference = provider.getPreference("DummyPreferences.name", String.class);
    }

    @Override
    public Date getInstallationDate() {
        return installationDatePreference.get();
    }

    @Override
    public void setInstallationDate(Date installationDate) {
        installationDatePreference.set(installationDate);
    }

    @Override
    public int getAge() {
        return agePreference.get();
    }

    @Override
    public void setAge(int age) {
        agePreference.set(age);
    }

    @Override
    public boolean isRegistered() {
        return registeredPreference.get();
    }

    @Override
    public void setRegistered(boolean registered) {
        registeredPreference.set(registered);
    }

    @Override
    public String getName() {
        return namePreference.get();
    }

    @Override
    public void setName(String name) {
        namePreference.set(name);
    }
}
