package directory;

import directory.health.DirectoryHealthCheck;
import directory.resources.CompaniesResource;
import directory.resources.CompanyResource;
import directory.resources.ExchangeResource;
import directory.resources.ExchangesResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DirectoryApp extends Application<DirectoryConfig> {
    public static void main(String[] args) throws Exception {
        new DirectoryApp().run(args);
    }

    @Override
    public String getName() { return "Directory"; }

    @Override
    public void initialize(Bootstrap<DirectoryConfig> bootstrap) { }

    @Override
    public void run(DirectoryConfig configuration,
                    Environment environment) {

        environment.jersey().register(new CompaniesResource(configuration.companies));
        environment.jersey().register(new CompanyResource(configuration.companies));
        environment.jersey().register(new ExchangesResource(configuration.exchanges));
        environment.jersey().register(new ExchangeResource(configuration.exchanges));
        environment.healthChecks().register("directory",
                new DirectoryHealthCheck(configuration.companies, configuration.exchanges));
    }

}

