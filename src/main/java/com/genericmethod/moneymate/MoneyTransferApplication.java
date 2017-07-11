package com.genericmethod.moneymate;

import com.genericmethod.moneymate.config.DbConfiguration;
import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.dao.UserDao;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.User;
import com.genericmethod.moneymate.resources.AccountResource;
import com.genericmethod.moneymate.resources.TransferResource;
import com.genericmethod.moneymate.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.util.Currency;


public class MoneyTransferApplication extends Application<DbConfiguration> {

    public static void main(String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    @Override
    public void run(DbConfiguration moneyMateDbConfiguration, Environment environment) throws Exception {

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, moneyMateDbConfiguration.getDataSourceFactory(), "h2");
        final UserDao userDao = jdbi.onDemand(UserDao.class);
        final AccountDao accountDao = jdbi.onDemand(AccountDao.class);
        userDao.createTable();
        accountDao.createTable();

        userDao.createUser(new User("Sumit","sumit.roys@gmail.com"));
        userDao.createUser(new User("Rohit","rohit.roys@gmail.com"));

        accountDao.createAccount(new Account("Sumit","salary account",
                888.00,
                Currency.getInstance("EUR").getCurrencyCode()));

        accountDao.createAccount(new Account("Rohit","salary account",
                888.00,
                Currency.getInstance("EUR").getCurrencyCode()));

        UserResource userResource = new UserResource(userDao, accountDao);
        AccountResource accountResource = new AccountResource(accountDao);
        TransferResource transferResource = new TransferResource(userDao, accountDao);

        environment.jersey().register(userResource);
        environment.jersey().register(accountResource);
        environment.jersey().register(transferResource);

    }
}