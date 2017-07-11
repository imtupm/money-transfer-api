package com.genericmethod.moneymate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import io.dropwizard.validation.OneOf;
import io.dropwizard.validation.ValidationMethod;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public class Transfer {

    @NotNull
    @DecimalMin("0.01")
    private Double amount;

    @NotBlank
    @OneOf({"EUR","USD"})
    private String currency;

    @NotNull
    private Integer sourceAccountId;

    @NotNull
    private Integer destinationAccountId;

    public Transfer() {}

    public Transfer(Double amount, String currency, Integer sourceAccountId, Integer destinationAccountId) {
        this.amount = amount;
        this.currency = currency;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
    }

    @JsonProperty
    public Double getAmount() {
        return amount;
    }

    @JsonProperty
    public String getCurrency() {
        return currency;
    }

    @JsonProperty
    public Integer getSourceAccountId() {
        return sourceAccountId;
    }

    @JsonProperty
    public Integer getDestinationAccountId() {
        return destinationAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer that = (Transfer) o;

        return Objects.equal(this.amount, that.amount) &&
                Objects.equal(this.currency, that.currency) &&
                Objects.equal(this.sourceAccountId, that.sourceAccountId) &&
                Objects.equal(this.destinationAccountId, that.destinationAccountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(amount, currency, sourceAccountId, destinationAccountId);
    }

    @JsonIgnore
    @ValidationMethod(message = "source account cannot be the same as destination account")
    public boolean isSourceNotEqualToDestination(){
        return sourceAccountId != destinationAccountId;
    }
}
