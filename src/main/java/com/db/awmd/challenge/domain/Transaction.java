package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class Transaction {
    @NotNull
    private String transactionId;
    @NotNull
    @Min(value = 0, message = "Amount transferred must be positive.")
    private BigDecimal amount;
    private String accountFrom;
    private String accountTo;

    public Transaction (String transactionId,String accountFrom, String accountTo  ) {
        this.transactionId = transactionId;
        this.amount = BigDecimal.ZERO;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
    }

    @JsonCreator
    public Transaction(
            @JsonProperty("transactionId") String transactionId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("accountFrom") String accountFrom,
            @JsonProperty("accountTo") String accountTo)  {
        this.transactionId = transactionId;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
    }
}
