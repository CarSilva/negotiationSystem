package directory.resources;


import directory.representations.Company;
import directory.representations.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        synchronized (companies) {
            return new ArrayList<Company>(companies.values());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCompany(Company company) {
        synchronized (companies) {
            if (companies.get(company.getName()) == null) {
                companies.put(company.getName(), company);
                return Response.ok().build();
            }
        }
        // Conflict - resource already exists
        return Response.status(409).build();
    }
}
