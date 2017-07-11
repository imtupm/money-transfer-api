package com.genericmethod.moneymate.resources;

import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.dao.UserDao;
import com.genericmethod.moneymate.enums.HttpStatus;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.Transfer;
import io.dropwizard.jersey.validation.ValidationErrorMessage;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.*;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TransferResourceTest {

    private static final AccountDao accountDao = mock(AccountDao.class);
    private static final UserDao userDao = mock(UserDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransferResource(userDao,accountDao))
            .build();

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        reset(userDao);
        reset(accountDao);
    }

    @Test
    public void testTransfer() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);

        Account sourceAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        Account destinationAccount = new Account(2, "nikhil",
                "description",
                new BigDecimal(0.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());


        final BigDecimal newSourceAmount = new BigDecimal(sourceAccount.getBalance()).setScale(2, BigDecimal.ROUND_UNNECESSARY)
                .subtract(new BigDecimal(transfer.getAmount()).setScale(2, BigDecimal.ROUND_UNNECESSARY));

        final BigDecimal newDestinationAmount = new BigDecimal(destinationAccount.getBalance()).setScale(2, BigDecimal.ROUND_UNNECESSARY)
                .add(new BigDecimal(transfer.getAmount()).setScale(2, BigDecimal.ROUND_UNNECESSARY));

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(sourceAccount);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(destinationAccount);

        when(accountDao.updateBalance(sourceAccountId, newSourceAmount.doubleValue())).thenReturn(sourceAccountId);
        when(accountDao.updateBalance(destinationAccountId, newDestinationAmount.doubleValue())).thenReturn(destinationAccountId);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(204);

        verify(accountDao).updateBalance(sourceAccountId, newSourceAmount.doubleValue());
        verify(accountDao).updateBalance(destinationAccountId, newDestinationAmount.doubleValue());
    }

    @Test
    public void testTransferAmountGreaterThanSourceAmount() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(1000).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);


        Account sourceAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        Account destinationAccount = new Account(2, "nikhil",
                "description",
                new BigDecimal(0.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(sourceAccount);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(destinationAccount);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());

    }

    @Test
    public void testTransferCurrencyAndSourceAccountCurrencyDoNotMatch() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("USD").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);


        Account sourceAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        Account destinationAccount = new Account(2, "nikhil",
                "description",
                new BigDecimal(0.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(sourceAccount);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(destinationAccount);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
    }

    @Test
    public void testTransferDestinationCurrencyAndSourceAccountCurrencyDoNotMatch() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);


        Account sourceAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        Account destinationAccount = new Account(2, "nikhil",
                "description",
                new BigDecimal(0.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("USD").getCurrencyCode());

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(sourceAccount);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(destinationAccount);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
    }

    @Test
    public void testTransferSameSourceAccountAndDestinationAccount() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 1;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(422);
        ValidationErrorMessage msg = post.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("source account cannot be the same as destination account");
    }

    @Test
    public void testTransferSourceAccountDoesNotExist() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);

        Account destinationAccount = new Account(2, "nikhil",
                "description",
                new BigDecimal(0.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(null);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(destinationAccount);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());

    }

    @Test
    public void testTransferDestinationAccountDoesNotExist() {

        Integer sourceAccountId = 1;
        Integer destinationAccountId = 2;
        Transfer transfer = new Transfer(new BigDecimal(10).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(),
                sourceAccountId,
                destinationAccountId);

        Account sourceAccount = new Account(1, "Sumit",
                "description",
                new BigDecimal(100.00).setScale(2, RoundingMode.UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        when(accountDao.getAccountForUpdate(sourceAccountId)).thenReturn(sourceAccount);
        when(accountDao.getAccountForUpdate(destinationAccountId)).thenReturn(null);

        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.getCode());
    }





    @Test
    public void testTransferMissingSourceAccountId() {
        Transfer transfer = new Transfer(new BigDecimal(123).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(), null, 2);
        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(422);
        ValidationErrorMessage msg = post.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("sourceAccountId may not be null");
    }

    @Test
    public void testTransferMissingDestinationAccountId() {
        Transfer transfer = new Transfer(new BigDecimal(123).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode(), 1, null);
        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(422);
        ValidationErrorMessage msg = post.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("destinationAccountId may not be null");
    }

    @Test
    public void testTransferMissingAmount() {
        Transfer transfer = new Transfer(null, Currency.getInstance("EUR").getCurrencyCode(), 1, 2);
        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(422);
        ValidationErrorMessage msg = post.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("amount may not be null");
    }

    @Test
    public void testTransferMissingCurrency() {
        Transfer transfer = new Transfer(new BigDecimal(123).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(), null, 1, 2);
        final Response post = resources.client().target("/v1/transfers").request().post(Entity.json(transfer));
        assertThat(post.getStatus()).isEqualTo(422);
        ValidationErrorMessage msg = post.readEntity(ValidationErrorMessage.class);
        assertThat(msg.getErrors()).containsOnly("currency may not be empty");
    }
}