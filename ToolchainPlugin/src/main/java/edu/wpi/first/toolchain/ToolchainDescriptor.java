package edu.wpi.first.toolchain;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.NamedDomainObjectSet;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.internal.DefaultNamedDomainObjectSet;
import org.gradle.api.provider.Property;
import org.gradle.internal.logging.text.DiagnosticsVisitor;
import org.gradle.internal.reflect.DirectInstantiator;

public class ToolchainDescriptor<T extends GccToolChain> implements ToolchainDescriptorBase {

    private String name;
    private String toolchainName;
    private String[] platforms;
    private Property<Boolean> optional;
    private NamedDomainObjectSet<ToolchainDiscoverer> discoverers;
    private DomainObjectSet<AbstractToolchainInstaller> installers;

    private ToolchainRegistrar<T> registrar;

    public ToolchainDescriptor(String name, String toolchainName, ToolchainRegistrar<T> registrar, Property<Boolean> optional) {
        this.name = name;
        this.platforms = null;
        this.optional = optional;
        this.registrar = registrar;
        this.toolchainName = toolchainName;
        this.discoverers = new DefaultNamedDomainObjectSet<ToolchainDiscoverer>(ToolchainDiscoverer.class, DirectInstantiator.INSTANCE, CollectionCallbackActionDecorator.NOOP);
        this.installers = new DefaultDomainObjectSet<AbstractToolchainInstaller>(AbstractToolchainInstaller.class, CollectionCallbackActionDecorator.NOOP);
    }

    @Override
    public void setToolchainPlatforms(String... platforms) {
        this.platforms = platforms;
    }

    @Override
    public NamedDomainObjectSet<ToolchainDiscoverer> getDiscoverers() {
        return discoverers;
    }

    @Override
    public DomainObjectSet<AbstractToolchainInstaller> getInstallers() {
        return installers;
    }

    @Override
    public ToolchainDiscoverer discover() {
        return discoverers.stream().filter(ToolchainDiscoverer::valid).findFirst().orElse(null);
    }

    @Override
    public AbstractToolchainInstaller getInstaller() {
        return installers.stream().filter(AbstractToolchainInstaller::installable).findFirst().orElse(null);
    }

    @Override
    public void explain(DiagnosticsVisitor visitor) {
        for (ToolchainDiscoverer discoverer : discoverers) {
            visitor.node(discoverer.getName());
            visitor.startChildren();
            discoverer.explain(visitor);
            visitor.endChildren();
        }
    }

    @Override
    public Property<Boolean> getOptional() {
        return optional;
    }

    @Override
    public String[] getToolchainPlatforms() {
        return platforms;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getToolchainName() {
        return toolchainName;
    }

    @Override
    public String getInstallTaskName() {
        return "install" + capitalize(getName()) + "Toolchain";
    }

    @Override
    public ToolchainRegistrar<T> getRegistrar() {
        return registrar;
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
