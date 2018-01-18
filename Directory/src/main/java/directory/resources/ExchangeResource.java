package directory.resources;

import directory.representations.Exchange;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeResource {

    Map<Integer, Exchange> exchanges;

    public ExchangeResource() {
        exchanges = new HashMap<Integer, Exchange>();
    }

    @GET
    @Path("/{id}")
    public Exchange infoCompany(@PathParam("id") int id) {
        return new Exchange("host", 2, 1);
    }

    @POST
    public Response newCompany() {
        return Response.ok().build();
    }

}
