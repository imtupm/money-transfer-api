package com.genericmethod.moneymate.resources;

import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.enums.HttpStatus;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.CurrencyAmount;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AccountResourceTest {

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
    public void testGetAccount() {
        when(accountDao.getAccount(eq(1))).thenReturn(account1);
        assertThat(resources.client().target("/v1/accounts/1").request().get(Account.class))
                .isEqualTo(account1);
        verify(accountDao).getAccount(1);
    }

    @Test
    public void testGetAllAccounts() {

        final List<Account> accounts = Arrays.asList(account1, account2);
        when(accountDao.getAllAccounts()).thenReturn(accounts);

        List<Account> accountList = new ArrayList<>();
        assertThat(resources.client().target("/v1/accounts").request().get(accountList.getClass()).size())
                .isEqualTo(2);
    }

    @Test
    public void testGetAccountBalance() {

        CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccount(1)).thenReturn(account1);

        assertThat(resources.client().target("/v1/accounts/1/balance").request().get(CurrencyAmount.class))
                .isEqualTo(currencyAmount);
    }

    @Test
    public void testGetAccountBalanceAccountNotFound() {

        CurrencyAmount currencyAmount = new CurrencyAmount(new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccount(1)).thenReturn(null);

        try{
            resources.client().target("/v1/accounts/1/balance").request().get(CurrencyAmount.class);
        } catch (WebApplicationException webEx) {
            assertThat(webEx.getResponse().getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }
    }

    @Test
    public void testCreateAccount() {

        Account newAccount = new Account("Sumit",
                "description",
                new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        Account savedAccount = new Account(1,
                "Sumit",
                "description",
                new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.createAccount(newAccount)).thenReturn(1);
        when(accountDao.getAccount(1)).thenReturn(savedAccount);

        assertThat(resources.client().target("/v1/accounts").request().post(Entity.json(newAccount))
                .readEntity(Account.class))
                .isEqualTo(savedAccount);

        verify(accountDao).createAccount(newAccount);
    }

    @Test
    public void testUpdateAccount() {

        Account updatedAccount = new Account(1,"Sumit",
                "description",
                new BigDecimal(123.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.updateAccount(updatedAccount)).thenReturn(1);
        when(accountDao.getAccount(1)).thenReturn(updatedAccount);

        assertThat(resources.client().target("/v1/accounts/1").request().put(Entity.json(updatedAccount))
                .readEntity(Account.class))
                .isEqualTo(updatedAccount);

        verify(accountDao).updateAccount(updatedAccount);
    }

    @Test
    public void testDeleteAccount() {

        doNothing().when(accountDao).deleteAccount("1");
        assertThat(resources.client().target("/v1/accounts/1").request().delete().getStatusInfo().getStatusCode())
                .isEqualTo(204);
        verify(accountDao).deleteAccount("1");
    }

}