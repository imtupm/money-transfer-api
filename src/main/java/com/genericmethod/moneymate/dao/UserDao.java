package com.genericmethod.moneymate.dao;

import com.genericmethod.moneymate.dao.mapper.UserMapper;
import com.genericmethod.moneymate.model.User;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(UserMapper.class)
public interface UserDao {

    @SqlUpdate("CREATE TABLE user (id INT PRIMARY KEY AUTO_INCREMENT NOT NULL," +
            " username varchar(25), " +
            "email varchar(25))")
    void createTable();

    @SqlQuery("SELECT * FROM user where id = :id")
    User getUserById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM user WHERE username = :username")
    User getUserByUsername(@Bind("username") String username);

    @SqlQuery("SELECT * from user")
    List<User> getAllUsers();

    @SqlUpdate("INSERT INTO user (username, email) VALUES (:u.username, :u.email)")
    @GetGeneratedKeys
    @Transaction
    int createUser(@BindBean("u") User user);

    @SqlUpdate("UPDATE user SET username = :u.username, email = :u.email WHERE id = :u.id")
    int updateUser(@BindBean("u") User user);

    @SqlUpdate("DELETE FROM user WHERE id = :id")
    void deleteUser(@Bind("id") int id);
}
