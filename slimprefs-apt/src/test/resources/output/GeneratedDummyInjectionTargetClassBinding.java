import com.slimgears.slimprefs.PreferenceBinding;
import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.AbstractClassBinding;
import com.slimgears.slimprefs.internal.ClassBinding;
import com.slimgears.slimprefs.internal.CompositePreferenceBinding;
import com.slimgears.slimprefs.internal.PreferenceObserver;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.util.Date;

class GeneratedDummyInjectionTargetClassBinding extends AbstractClassBinding<DummyInjectionTarget> {
    public static final ClassBinding<DummyInjectionTarget> INSTANCE = new GeneratedDummyInjectionTargetClassBinding();

    private GeneratedDummyInjectionTargetClassBinding() {}

    @Override
    public PreferenceBinding bind(PreferenceProvider provider, final DummyInjectionTarget target) {
        return CompositePreferenceBinding.create(
                bindMember(provider.getPreference("DummyInjectionTarget.age", Integer.class), target.age, 0, new PreferenceObserver<Integer>() {
                    @Override
                    public void onChanged(Integer value) {
                target.age = value;
            }
                }),
                bindMember(provider.getPreference("explicit_key_name", String.class), target.name, null, new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                        target.name = value;
                    }
                }),
                bindMember(provider.getPreference(0x4d2, Date.class), target.installationDate, null, new PreferenceObserver<Date>() {
                    @Override
                    public void onChanged(Date value) {
                        target.installationDate = value;
                    }
                }),
                bindMemberTwoWay(provider.getPreference("DummyInjectionTarget.runCounter", Integer.class),
                                 new ValueProvider<Integer>() {
                                     @Override
                                     public Integer get() {
                                         return target.runCounter;
                                     }
                                 },
                                 0,
                                 new PreferenceObserver<Integer>() {
                                     @Override
                                     public void onChanged(Integer value) {
                                         target.runCounter = value;
                                     }
                                 }),
                bindMember(provider.getPreference("DummyInjectionTarget.registered", Boolean.class), null, null, new PreferenceObserver<Boolean>() {
                    @Override
                    public void onChanged(Boolean value) {
                        target.onRegisteredChanged(value);
                    }
                }),
                bindMember(provider.getPreference("explicit_key_name", String.class), null, null, new PreferenceObserver<String>() {
                    @Override
                    public void onChanged(String value) {
                        target.onNameChanged(value);
                    }
                }));
    }
}
