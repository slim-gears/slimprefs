import com.slimgears.slimprefs.PreferenceInjectorFactory;
import com.slimgears.slimprefs.internal.AbstractPreferenceInjectorFactory;

class GeneratedDummyPreferenceInjectorFactory extends AbstractPreferenceInjectorFactory implements DummyPreferenceInjectorFactory {
    public static final PreferenceInjectorFactory INSTANCE = new GeneratedDummyPreferenceInjectorFactory();

    private GeneratedDummyPreferenceInjectorFactory() {
        addBinding(DummyInjectionTarget.class, GeneratedDummyInjectionTargetClassBinding.INSTANCE);
    }
}
