package com.genericmethod.moneymate.dao;

import com.genericmethod.moneymate.dao.mapper.AccountMapper;
import com.genericmethod.moneymate.model.Account;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(AccountMapper.class)
public interface AccountDao {

    @SqlUpdate("CREATE TABLE account (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
            " username varchar(25)," +
            " description varchar(100)," +
            " currency varchar(300)," +
            " balance decimal(20,2))")
    void createTable();

    @SqlQuery("SELECT * FROM account WHERE id = :id")
    Account getAccount(@Bind("id") int id);

    @SqlQuery("SELECT * FROM account WHERE id = :id FOR UPDATE")
    Account getAccountForUpdate(@Bind("id") int id);

    @SqlQuery("SELECT * FROM account")
    List<Account> getAllAccounts();

    @SqlQuery("SELECT * FROM account WHERE username = :username")
    Account getUserAccount(@Bind("username") String username);

    @SqlQuery("SELECT * FROM account WHERE username = :username AND currency = :currency")
    Account getUserAccount(@Bind("username") String username, @Bind("currency") String currency);

    @SqlQuery("SELECT * FROM account WHERE username = :username AND currency = :currency FOR UPDATE")
    Account getUserAccountForUpdate(@Bind("username") String username, @Bind("currency") String currency);

    @SqlUpdate("INSERT INTO account (username, description, currency, balance) " +
            "values (:a.username, :a.description, :a.currency, :a.balance)")
    @GetGeneratedKeys
    @Transaction
    int createAccount(@BindBean("a") Account account);

    @SqlUpdate("UPDATE account SET username = :a.username," +
            " description = :a.description," +
            " currency = :a.currency," +
            " balance = :a.balance WHERE id = :a.id")
    int updateAccount(@BindBean("a") Account account);

    @SqlUpdate("UPDATE account SET balance = :balance WHERE id = :id")
    int updateBalance(@Bind("id")int id, @Bind("balance") double balance);

    @SqlUpdate("DELETE FROM account WHERE username = :username")
    void deleteAccount(@Bind("username") String username);

}
