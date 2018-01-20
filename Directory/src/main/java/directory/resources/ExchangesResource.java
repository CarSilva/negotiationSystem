package directory.resources;


import directory.representations.Company;
import directory.representations.Exchange;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/exchanges")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangesResource {

    Map<Integer, Exchange> exchanges;

    public ExchangesResource(Map<Integer, Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    @GET
    public List<Exchange> listExchanges() {
        synchronized (exchanges) {
            return new ArrayList<Exchange>(exchanges.values());
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newExchange(Exchange exchange) {
        int id;
        synchronized (exchanges) {
            id = exchanges.values().size() + 1;
            exchanges.put(id, exchange);
        }
        exchange.id = id;
        return Response.ok(exchange).build();
    }
}
