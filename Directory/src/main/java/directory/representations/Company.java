package directory.representations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class Company {

    @NotEmpty
    private String name;
    private int exchangeId;
    private float openingValue;
    private float closingValue;
    private float minimumValue;
    private float maximumValue;
    // Yesterday values
    private float openingValueY;
    private float closingValueY;
    private float minimumValueY;
    private float maximumValueY;


    @JsonCreator
    public Company(@JsonProperty("name") String name,
                   @JsonProperty("openingValue") float openingValue,
                   @JsonProperty("closingValue") float closingValue,
                   @JsonProperty("minimumValue") float minimumValue,
                   @JsonProperty("maximumValue") float maximumValue,
                   @JsonProperty("openingValueY") float openingValueY,
                   @JsonProperty("closingValueY") float closingValueY,
                   @JsonProperty("minimumValueY") float minimumValueY,
                   @JsonProperty("maximumValueY") float maximumValueY,
                   @JsonProperty("exchangeId") int exchangeId) {

        this.name = name;
        this.openingValue = openingValue;
        this.closingValue = closingValue;
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.openingValueY = openingValueY;
        this.closingValueY = closingValueY;
        this.minimumValueY = minimumValueY;
        this.maximumValueY = maximumValueY;
        this.exchangeId = exchangeId;
    }

    public void update(Company company) {
        if (company.maximumValue > 0)
            this.maximumValue = company.maximumValue;
        if (company.minimumValue > 0)
            this.minimumValue = company.minimumValue;
        if (company.openingValue > 0)
            this.openingValue = company.openingValue;
        if (company.closingValue > 0) {
            this.closingValue = company.closingValue;

            openingValueY = openingValue;
            closingValueY = closingValue;
            minimumValueY = minimumValue;
            maximumValueY = maximumValue;

            openingValue = 0;
            closingValue = 0;
            minimumValue = 0;
            maximumValue = 0;
        }
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

    public float getOpeningValueY() {
        return openingValueY;
    }

    public void setOpeningValueY(float openingValueY) {
        this.openingValueY = openingValueY;
    }

    public float getClosingValueY() {
        return closingValueY;
    }

    public void setClosingValueY(float closingValueY) {
        this.closingValueY = closingValueY;
    }

    public float getMinimumValueY() {
        return minimumValueY;
    }

    public void setMinimumValueY(float minimumValueY) {
        this.minimumValueY = minimumValueY;
    }

    public float getMaximumValueY() {
        return maximumValueY;
    }

    public void setMaximumValueY(float maximumValueY) {
        this.maximumValueY = maximumValueY;
    }
}
