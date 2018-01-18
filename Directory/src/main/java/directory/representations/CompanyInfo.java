package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyInfo {
    public final String name;
    public final float stockValue;

    @JsonCreator
    public CompanyInfo(@JsonProperty("name") String name,
                       @JsonProperty("stockValue") float value) {
        this.name = name;
        this.stockValue = value;
    }
}
