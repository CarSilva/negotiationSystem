package directory.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import directory.representations.Company;
import directory.representations.Exchange;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/exchange")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeResource {

    Map<Integer, Exchange> exchanges;

    public ExchangeResource(Map<Integer, Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    @GET
    @Path("/{id}")
    public Response infoExchange(@PathParam("id") int id) {
        synchronized (exchanges) {
            Exchange exchange = exchanges.get(id);
            if (exchange != null)
                return Response.ok(exchange).build();
        }
        // 404 - Not found, no such resource
        return Response.status(404).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateExchange(Exchange exchange) {
        synchronized (exchanges) {
            Exchange exch = exchanges.get(exchange.id);
            if(exch == null)
                exchanges.put(exchange.id, exchange);
            else {
                int id = exchanges.values().size() + 1;
                exchanges.put(id, exchange);
            }
        }
        return Response.ok().build();
    }

}
