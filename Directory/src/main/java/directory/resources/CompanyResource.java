package directory.resources;

import directory.representations.Company;
import directory.representations.Saying;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {
    private Map<String, Company> companies;


    public CompanyResource(Map<String, Company> companies) {
        this.companies = companies;
    }

    @GET
    @Path("/{name}")
    public Company infoCompany(@PathParam("name") String name) {
        Company company = companies.get(name);
        if(company != null) return company;
        else return new Company("", 0, 0,
                            0,0, 1);
    }

    @GET
    @Path("/{name}/exchanges")
    public Response getExchange() {
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response updateCompany(Company company, @PathParam("name")String name) {
        return Response.ok().build();
    }

}
