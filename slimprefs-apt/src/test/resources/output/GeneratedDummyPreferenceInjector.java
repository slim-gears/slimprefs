import com.slimgears.slimprefs.PreferenceProvider;
import com.slimgears.slimprefs.internal.AbstractPreferenceInjector;

class GeneratedDummyPreferenceInjector extends AbstractPreferenceInjector implements DummyPreferenceInjector {
    public GeneratedDummyPreferenceInjector(PreferenceProvider provider) {
        super(provider);
        addBinding(DummyInjectionTarget.class, GeneratedDummyInjectionTargetClassBinding.INSTANCE);
    }
}
