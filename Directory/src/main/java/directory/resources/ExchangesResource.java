package directory.resources;


import directory.representations.Company;
import directory.representations.Exchange;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/exchanges")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangesResource {

    Map<Integer, Exchange> exchanges;

    @GET
    public Exchange listExchanges() {
        return new Exchange("host", 1, 2);
    }

    @POST
    public Response newCompany() {
        return Response.ok().build();
    }
}
