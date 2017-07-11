package com.genericmethod.moneymate.resources;

import com.genericmethod.moneymate.dao.AccountDao;
import com.genericmethod.moneymate.dao.UserDao;
import com.genericmethod.moneymate.model.Account;
import com.genericmethod.moneymate.model.User;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    private static final UserDao userDao = mock(UserDao.class);
    private static final AccountDao accountDao = mock(AccountDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new UserResource(userDao, accountDao))
            .build();

    private final User user1 = new User(1, "Sumit", "sumit.roys@gmail.com");
    private final User user2 = new User(2, "nikhil", "nikolay@gmail.com");

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        reset(userDao);
    }

    @Test
    public void testGetUser() {
        when(userDao.getUserByUsername(eq("Sumit"))).thenReturn(user1);
        assertThat(resources.client().target("/v1/users/Sumit").request().get(User.class))
                .isEqualTo(user1);
        verify(userDao).getUserByUsername("Sumit");
    }

    @Test
    public void testGetAllUsers() {

        final List<User> users = Arrays.asList(user1, user2);
        when(userDao.getAllUsers()).thenReturn(users);

        List<User> userList = new ArrayList<>();
        assertThat(resources.client().target("/v1/users").request().get(userList.getClass()).size())
                .isEqualTo(2);
    }

    @Test
    public void testGetUserAccount() {

        Account account = new Account(1,
                "Sumit",
                "Sumit's Account",
                new BigDecimal(123.00).setScale(2, BigDecimal.ROUND_UNNECESSARY).doubleValue(),
                Currency.getInstance("EUR").getCurrencyCode());

        User newUser = new User("Sumit", "sumit.roys@gmail.com");

        when(userDao.getUserByUsername("Sumit")).thenReturn(newUser);
        when(accountDao.getUserAccount("Sumit")).thenReturn(account);

        assertThat(resources.client().target("/v1/users/Sumit/account").request().get(Account.class))
                .isEqualTo(account);
    }

    @Test
    public void testGetUserAccountUserNotFound() {
        when(userDao.getUserByUsername("Sumit")).thenReturn(null);

        try {
            resources.client().target("/v1/users/Sumit/account").request().get(Account.class);
        } catch (WebApplicationException webEx) {
            assertThat(webEx.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }
    }

    @Test
    public void testGetUserAccountNotFound() {

        when(userDao.getUserByUsername("Sumit")).thenReturn(user1);
        when(accountDao.getUserAccount("Sumit")).thenReturn(null);
        try {
            resources.client().target("/v1/users/Sumit/account").request().get(Account.class);
        } catch (WebApplicationException webEx) {
            assertThat(webEx.getResponse().getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        }

    }

    @Test
    public void testCreateUser() {

        User newUser = new User("Sumit", "cfarrugia@gmail.com");
        User savedUser = new User(1, "Sumit", "sumit.roys@gmail.com");

        when(userDao.createUser(newUser)).thenReturn(1);
        when(userDao.getUserById(1)).thenReturn(savedUser);
        assertThat(resources.client().target("/v1/users").request().post(Entity.json(newUser))
                .readEntity(User.class))
                .isEqualTo(savedUser);
        verify(userDao).createUser(newUser);
    }

    @Test
    public void testUpdateUser() {

        User userToUpdate = new User(1, "Sumit", "sumit.roys@gmail.com");
        when(userDao.getUserById(1)).thenReturn(userToUpdate);
        when(userDao.updateUser(userToUpdate)).thenReturn(1);
        assertThat(resources.client().target("/v1/users/1").request().put(Entity.json(userToUpdate))
                .readEntity(User.class))
                .isEqualTo(userToUpdate);

        verify(userDao).updateUser(userToUpdate);
    }

    @Test
    public void testDeleteUser() {

        doNothing().when(userDao).deleteUser(1);
        assertThat(resources.client().target("/v1/users/1").request().delete().getStatusInfo().getStatusCode())
                .isEqualTo(204);
        verify(userDao).deleteUser(1);
    }


}