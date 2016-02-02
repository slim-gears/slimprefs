import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.ClassBinding;
import com.slimgears.slimprefs.internal.CompositePreferenceBinding;
import com.slimgears.slimprefs.internal.PreferenceObserver;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Date;

class GeneratedDummyInjectionTargetClassBinding implements ClassBinding<DummyInjectionTarget> {
    public static final ClassBinding<DummyInjectionTarget> INSTANCE = new GeneratedDummyInjectionTargetClassBinding();

    private GeneratedDummyInjectionTargetClassBinding() {}

    @Override
    public PreferenceBinding bind(PreferenceProvider provider, final DummyInjectionTarget target) {
        return CompositePreferenceBinding.create(
                provider.getPreference("DummyInjectionTarget.age", Integer.class)
                        .observe(new PreferenceObserver<Integer>() {
                            @Override
                            public void onChanged(Integer value) {
                        target.age = value;
                    }
                }),
                provider.getPreference("explicit_key_name", String.class).observe(new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                        target.name = value;
                    }
                }),
                provider.getPreference(0x4d2, Date.class).observe(new PreferenceObserver<Date>() {
                    @Override
                    public void onChanged(Date value) {
                        target.installationDate = value;
                    }
                }),
                provider.getPreference("DummyInjectionTarget.registered", Boolean.class).observe(new PreferenceObserver<Boolean>() {
                    @Override
                    public void onChanged(Boolean value) {
                        target.onRegisteredChanged(value);
                    }
                }),
                provider.getPreference("explicit_key_name", String.class).observe(new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                        target.onNameChanged(value);
                    }
                }));
    }
}
