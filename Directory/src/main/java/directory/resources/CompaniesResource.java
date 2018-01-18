package directory.resources;


import directory.representations.Company;
import directory.representations.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
public class CompaniesResource {
    Map<String, Company> companies;

    public CompaniesResource(Map<String, Company> companies) {
        this.companies = companies;
    }

    @GET
    public List<Company> listCompanies() {
        return new ArrayList<Company>(companies.values());
    }

}
