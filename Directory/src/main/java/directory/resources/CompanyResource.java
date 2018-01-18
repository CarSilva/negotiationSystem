package directory.resources;

import directory.representations.Saying;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private Map<String, Company> companies;

    class Company {
        String name;
        float openingValue;
        float closingValue;
        float minimumValue;
        float maximumValue;
    }

    public CompanyResource() {
        companies = new HashMap<String, Company>();
    }

    @GET
    public Saying listCompanies() {
        return new Saying(0, "todas as empresas");
    }

}
