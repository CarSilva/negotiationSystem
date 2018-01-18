package directory.resources;


import directory.representations.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
public class CompaniesResource {
    Map<String, Company> companies;

    class Company {
        String name;
        float stockValue;
    }

    public CompaniesResource() {
        companies = new HashMap<String, Company>();
    }

    @GET
    public Saying listCompanies() {
        return new Saying(0, "todas as empresas");
    }

}
