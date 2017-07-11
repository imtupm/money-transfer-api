package com.genericmethod.moneymate.resources;

import com.codahale.metrics.annotation.Timed;
import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.dao.UserDao;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.User;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/v1/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserDao userDao;
    private AccountDao accountDao;

    public UserResource(UserDao userDao, AccountDao accountDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    @GET
    @Timed
    public List<User> getAll() {
        return userDao.getAllUsers();
    }

    @GET
    @Timed
    @Path("/{username}")
    public User get(@PathParam("username") String username) {
        final User user = userDao.getUserByUsername(username);
        if(user == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return user;
    }

    @GET
    @Timed
    @Path("/{username}/account")
    public Account account(@PathParam("username") @NotEmpty String username){

        final User user = userDao.getUserByUsername(username);

        if(user == null){
            throw new WebApplicationException("user was not found", Response.Status.NOT_FOUND);
        }

        final Account userAccount = accountDao.getUserAccount(username);

        if (userAccount == null){
            throw new WebApplicationException("account was not found", Response.Status.NOT_FOUND);
        }

        return userAccount;
    }

    @POST
    @Timed
    public User create(@Valid User user) {
        if(userDao.getUserByUsername(user.getUsername()) != null){
            throw new WebApplicationException("username is not available", Response.Status.BAD_REQUEST);
        }
        final int userId = userDao.createUser(user);
        return userDao.getUserById(userId);
    }

    @PUT
    @Timed
    @Path("/{id}")
    public User update(@PathParam("id") int id, @Valid User user) {
        final int userId = userDao.updateUser(user);
        return userDao.getUserById(userId);
    }

    @DELETE
    @Timed
    @Path("/{id}")
    public void delete(@PathParam("id") int id) {
        userDao.deleteUser(id);
    }

}