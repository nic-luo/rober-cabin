package group.rober.runtime.support;

public interface BeanSelfAware {
    void setSelf(BeanSelfAware self);
    BeanSelfAware self();
}
