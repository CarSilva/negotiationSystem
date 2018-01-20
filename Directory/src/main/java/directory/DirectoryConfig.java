package directory;

import directory.representations.Company;


import directory.representations.Exchange;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class DirectoryConfig extends Configuration {

    @NotNull
    public Map<String, Company> companies;

    @NotNull
    public Map<Integer, Exchange> exchanges;


}
