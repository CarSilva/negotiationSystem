package directory.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import directory.representations.Company;
import directory.representations.Exchange;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    public Response infoCompany(@PathParam("name") String name) {
        Company company;
        synchronized (companies) {
            company = companies.get(name);
            if(company != null)
                return Response.ok(company).build();
        }
        // Resource not found
        return Response.status(404).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response updateCompany(Company company,
                                  @PathParam("name") String name) {

        synchronized (companies) {
            Company localCompany = companies.get(company.getName());
            if (localCompany != null)
                localCompany.update(company);
            else
                companies.put(company.getName(), company);
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{name}")
    public Response deleteCompany(@PathParam("name") String name) {
        if (companies.get(name).getExchangeId() < 0) {
            companies.remove(name);
            return Response.ok().build();
        }
        // Conflict -- company stocks traded on some exchange
        return Response.status(409).build();
    }

}
