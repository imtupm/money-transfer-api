package com.genericmethod.moneymate.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferSerializationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {

        Transfer transfer = new Transfer(new BigDecimal(123).setScale(2,BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode()
                ,1,2);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/transfer.json"), Transfer.class));

        assertThat(MAPPER.writeValueAsString(transfer)).isEqualTo(expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        Transfer transfer = new Transfer(new BigDecimal(123).setScale(2,BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").toString(),
                1,2);
        assertThat(MAPPER.readValue(fixture("fixtures/transfer.json"), Transfer.class))
                .isEqualTo(transfer);
    }
}
