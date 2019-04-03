package pwe.planner.testutil;

import pwe.planner.model.Application;
import pwe.planner.model.module.Module;

/**
 * A utility class to help with building application objects.
 * Example usage: <br>
 *     {@code Application ab = new ApplicationBuilder().withModule("John", "Doe").build();}
 */
public class ApplicationBuilder {

    private Application application;

    public ApplicationBuilder() {
        application = new Application();
    }

    public ApplicationBuilder(Application application) {
        this.application = application;
    }

    /**
     * Adds a new {@code Module} to the {@code Application} that we are building.
     */
    public ApplicationBuilder withModule(Module module) {
        application.addModule(module);
        return this;
    }

    public Application build() {
        return application;
    }
}
