package org.apache.helix.ui.util;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;

import java.io.File;

/**
 * A utility to run DropWizard (http://dropwizard.io/) applications in-process.
 */
public class DropWizardApplicationRunner {
    /**
     * Creates a Jetty server for an application that can be started / stopped in-process
     *
     * @param config           An application configuration instance (with properties set)
     * @param applicationClass The {@link io.dropwizard.Application} implementation class
     * @param <T>              The configuration class
     * @return A Jetty server
     */
    @SuppressWarnings("unchecked")
    public static <T extends Configuration>
    Server createServer(T config, Class<? extends Application<T>> applicationClass) throws Exception {
        // Create application
        final Application<T> application = applicationClass.getConstructor().newInstance();

        // Create bootstrap
        final ServerCommand<T> serverCommand = new ServerCommand<T>(application);
        final Bootstrap<T> bootstrap = new Bootstrap<T>(application);
        bootstrap.addCommand(serverCommand);
        application.initialize(bootstrap);

        // Write a temporary config file
        File tmpConfigFile = new File(
                System.getProperty("java.io.tmpdir"),
                config.getClass().getCanonicalName() + "_" + System.currentTimeMillis());
        tmpConfigFile.deleteOnExit();
        bootstrap.getObjectMapper().writeValue(tmpConfigFile, config);

        // Parse configuration
        ConfigurationFactory<T> configurationFactory
                = bootstrap.getConfigurationFactoryFactory()
                .create((Class<T>) config.getClass(),
                        bootstrap.getValidatorFactory().getValidator(),
                        bootstrap.getObjectMapper(),
                        "dw");
        final T builtConfig = configurationFactory.build(
                bootstrap.getConfigurationSourceProvider(), tmpConfigFile.getAbsolutePath());

        // Configure logging
        builtConfig.getLoggingFactory()
                .configure(bootstrap.getMetricRegistry(),
                        bootstrap.getApplication().getName());

        // Environment
        final Environment environment = new Environment(bootstrap.getApplication().getName(),
                bootstrap.getObjectMapper(),
                bootstrap.getValidatorFactory().getValidator(),
                bootstrap.getMetricRegistry(),
                bootstrap.getClassLoader());

        // Initialize environment
        builtConfig.getMetricsFactory().configure(environment.lifecycle(), bootstrap.getMetricRegistry());
        bootstrap.run(builtConfig, environment);
        application.run(builtConfig, environment);

        // Server
        final Server server = builtConfig.getServerFactory().build(environment);
        server.addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStopped(LifeCycle event) {
                builtConfig.getLoggingFactory().stop();
            }
        });

        return server;
    }
}
