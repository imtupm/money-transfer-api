package com.genericmethod.moneymate.dao.mapper;


import com.genericmethod.moneymate.model.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    @Override
    public User map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new User(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("email"));
    }
}
