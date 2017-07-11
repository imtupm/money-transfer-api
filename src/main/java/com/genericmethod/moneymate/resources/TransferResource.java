package com.genericmethod.moneymate.resources;


import com.codahale.metrics.annotation.Timed;
import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.dao.UserDao;
import com.genericmethod.moneymate.enums.HttpStatus;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.Transfer;
import org.skife.jdbi.v2.sqlobject.Transaction;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

@Path("/v1/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

    private UserDao userDao;
    private AccountDao accountDao;

    public TransferResource(UserDao userDao, AccountDao accountDao) {

        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @POST
    @Timed
    @Transaction
    public void transfer(@Valid Transfer transfer) {

        //lock accounts for update
        final Account sourceAccount = accountDao.getAccountForUpdate(transfer.getSourceAccountId());
        final Account destinationAccount = accountDao.getAccountForUpdate(transfer.getDestinationAccountId());

        if (sourceAccount == null) {
            throw new WebApplicationException("source account not found", HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }

        if (destinationAccount == null) {
            throw new WebApplicationException("destination account not found", HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }

        if (!transfer.getCurrency().equals(sourceAccount.getCurrency())){
            throw new WebApplicationException("currencies do not match", HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }

        if (!sourceAccount.getCurrency().equals(destinationAccount.getCurrency())){
            throw new WebApplicationException("account currencies do not match", HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }

        if (transfer.getAmount() > sourceAccount.getBalance()){
            throw new WebApplicationException("not enough funds available for transfer", HttpStatus.UNPROCESSABLE_ENTITY.getCode());
        }

        final BigDecimal newSourceAmount = new BigDecimal(sourceAccount.getBalance()).setScale(2, BigDecimal.ROUND_UNNECESSARY)
                .subtract(new BigDecimal(transfer.getAmount()).setScale(2, BigDecimal.ROUND_UNNECESSARY));

        final BigDecimal newDestinationAmount = new BigDecimal(destinationAccount.getBalance()).setScale(2, BigDecimal.ROUND_UNNECESSARY)
                .add(new BigDecimal(transfer.getAmount()).setScale(2, BigDecimal.ROUND_UNNECESSARY));

        accountDao.updateBalance(transfer.getSourceAccountId(), newSourceAmount.doubleValue());
        accountDao.updateBalance(transfer.getDestinationAccountId(), newDestinationAmount.doubleValue());

    }
}

