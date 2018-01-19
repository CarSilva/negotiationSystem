package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeId {
    public int exchangeId;
    @JsonCreator
    public ExchangeId(@JsonProperty("exchangeId") int exchangeId) {
        this.exchangeId = exchangeId;
    }
}
