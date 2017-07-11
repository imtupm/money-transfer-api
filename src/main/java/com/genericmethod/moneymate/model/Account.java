package com.genericmethod.moneymate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.dropwizard.validation.OneOf;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Account {

    private int id;

    @NotEmpty
    @Size(min = 4, max = 25, message = "username length must be between 5 and 25")
    private String username;

    @NotEmpty
    @Size(min = 4, max = 100, message = "description must be between 5 and 100")
    private String description;

    @NotNull
    private Double balance;

    @NotEmpty
    @OneOf({"EUR","USD"})
    private String currency;

    public Account() {}

    public Account(String username, String description, Double balance, String currency) {
        this.username = username;
        this.description = description;
        this.balance = balance;
        this.currency = currency;
    }

    public Account(int id, String username, String description, Double balance, String currency) {
        this.id = id;
        this.username = username;
        this.description = description;
        this.balance = balance;
        this.currency = currency;
    }

    @JsonProperty
    public int getId() {
        return id;
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @JsonProperty
    public Double getBalance() {
        return balance;
    }

    @JsonProperty
    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account that = (Account) o;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.username, that.username) &&
                Objects.equal(this.description, that.description) &&
                Objects.equal(this.balance, that.balance) &&
                Objects.equal(this.currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, username, description, balance, currency);
    }
}
