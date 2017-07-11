package com.genericmethod.moneymate.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyAmountSerializationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {
        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/moneyamount.json"), CurrencyAmount.class));

        assertThat(MAPPER.writeValueAsString(currencyAmount)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        assertThat(MAPPER.readValue(fixture("fixtures/moneyamount.json"), CurrencyAmount.class))
                .isEqualTo(currencyAmount);
    }
}
