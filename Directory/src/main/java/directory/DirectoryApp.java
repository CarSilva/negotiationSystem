package directory;

import directory.health.TemplateHealthCheck;
import directory.resources.CompaniesResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DirectoryApp extends Application<HelloConfiguration> {
    public static void main(String[] args) throws Exception {
        new DirectoryApp().run(args);
    }

    @Override
    public String getName() { return "Directory"; }

    @Override
    public void initialize(Bootstrap<HelloConfiguration> bootstrap) { }

    @Override
    public void run(HelloConfiguration configuration,
                    Environment environment) {

        environment.jersey().register(new CompaniesResource());
        environment.healthChecks().register("template",
                new TemplateHealthCheck(configuration.template));
    }

}

