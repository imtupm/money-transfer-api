package com.genericmethod.moneymate.resources;

import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.enums.HttpStatus;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.CurrencyAmount;
import io.dropwizard.jersey.validation.ValidationErrorMessage;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class AccountWithdrawTests {


    private static final AccountDao accountDao = mock(AccountDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountResource(accountDao))
            .build();

    private final Account account1 = new Account(1,
            "Sumit",
            "description",
            new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
            Currency.getInstance("EUR").getCurrencyCode());

    private final Account account2 = new Account(2, "nikhil",
            "description",
            new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
            Currency.getInstance("EUR").getCurrencyCode());

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        reset(accountDao);
    }

    @Test
    public void testAccountWithdrawal() {

        Account account = new Account(1, "Sumit",
                "description",
                new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(), Currency.getInstance("EUR").getCurrencyCode());

        Account updatedAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(0).setScale(2, RoundingMode.UNNECESSARY).doubleValue(), Currency.getInstance("EUR").getCurrencyCode());

        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getUserAccountForUpdate("Sumit", "EUR")).thenReturn(account);
        when(accountDao.updateBalance(1, 0.00)).thenReturn(1);
        when(accountDao.getAccount(1)).thenReturn(updatedAccount);

        assertThat(resources.client().target("/v1/accounts/Sumit/withdraw").request().put(Entity.json(currencyAmount))
                .readEntity(Account.class))
                .isEqualTo(updatedAccount);

    }

    @Test
    public void testAccountWithdrawalNotEnoughFunds() {

        Account account = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(), Currency.getInstance("EUR").getCurrencyCode());

        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getUserAccountForUpdate("Sumit", "EUR")).thenReturn(account);
        Response put = resources.client().target("/v1/accounts/Sumit/withdraw").request().put(Entity.json(currencyAmount));
        assertThat(put.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
    }

    @Test
    public void testAccountWithdrawWithNegativeAmount() {

        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(-1.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        final Response put = resources.client().target("/v1/accounts/Sumit/withdraw").request().put(Entity.json(currencyAmount));
        ValidationErrorMessage msg = put.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("amount must be greater than or equal to 0.01");

    }

    @Test
    public void testAccountWithdrawWithZeroAmount() {

        final CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(0.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        final Response put = resources.client().target("/v1/accounts/Sumit/withdraw").request().put(Entity.json(currencyAmount));
        ValidationErrorMessage msg = put.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("amount must be greater than or equal to 0.01");

    }
}
