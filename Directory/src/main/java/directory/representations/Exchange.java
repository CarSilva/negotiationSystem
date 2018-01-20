package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class Exchange {
    @NotEmpty
    public String host;
    public int port;
    public int id;

    @JsonCreator
    public Exchange(@JsonProperty("host") String host,
                    @JsonProperty("port") int port,
                    @JsonProperty("id") int id) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public void update(Exchange exchange) {
        this.port = exchange.port;
        this.host = exchange.host;
    }
}
