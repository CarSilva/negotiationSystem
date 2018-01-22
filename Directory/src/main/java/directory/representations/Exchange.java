package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class Exchange {
    @NotEmpty
    public String host;
    public int port;
    public int id;
    List<String> companies;

    @JsonCreator
    public Exchange(@JsonProperty("host") String host,
                    @JsonProperty("port") int port,
                    @JsonProperty("id") int id,
                    @JsonProperty("companies") List<String> companies) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.companies = companies;
    }

    public void update(Exchange exchange) {
        this.port = exchange.port;
        this.host = exchange.host;
    }

    public List<String> getCompanies() {
        return companies;
    }
}
