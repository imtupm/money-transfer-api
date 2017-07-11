package com.genericmethod.moneymate.dao.mapper;

import com.genericmethod.moneymate.model.Account;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountMapper implements ResultSetMapper<Account>{
    @Override
    public Account map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new Account(resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("balance").doubleValue(),
                resultSet.getString("currency"));
    }
}
