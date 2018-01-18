package directory.health;

import com.codahale.metrics.health.HealthCheck;
import directory.representations.Company;
import directory.representations.Exchange;

import java.util.Map;

public class DirectoryHealthCheck extends HealthCheck {
    private final Map<String, Company> company;
    private final Map<String, Exchange> exchange;

    public DirectoryHealthCheck(Map<String, Company> company, Map<String,Exchange> exchange) {
        this.company = company;
        this.exchange = exchange;
    }

    @Override
    protected Result check() {
        return Result.healthy();
    }
}

