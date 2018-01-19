package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class Company {

    @NotEmpty
    private String name;
    private float openingValue;
    private float closingValue;
    private float minimumValue;
    private float maximumValue;
    private int exchangeId;

    @JsonCreator
    public Company(@JsonProperty("name") String name,
                   @JsonProperty("openingValue") float openingValue,
                   @JsonProperty("closingValue") float closingValue,
                   @JsonProperty("minimumValue") float minimumValue,
                   @JsonProperty("maximumValue") float maximumValue,
                   @JsonProperty("exchangeId") int exchangeId) {

        this.name = name;
        this.openingValue = openingValue;
        this.closingValue = closingValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.exchangeId = exchangeId;
    }

    public void update(Company company) {
        this.closingValue = company.closingValue;
        this.openingValue = company.openingValue;
        this.minimumValue = company.minimumValue;
        this.maximumValue = company.maximumValue;
    }

    public int getExchangeId() {
        return exchangeId;
    }
    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public float getOpeningValue() {
        return openingValue;
    }

    public void setOpeningValue(float openingValue) {
        this.openingValue = openingValue;
    }

    public float getClosingValue() {
        return closingValue;
    }

    public void setClosingValue(float closingValue) {
        this.closingValue = closingValue;
    }

    public float getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(float minimumValue) {
        this.minimumValue = minimumValue;
    }

    public float getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(float maximumValue) {
        this.maximumValue = maximumValue;
    }
}
