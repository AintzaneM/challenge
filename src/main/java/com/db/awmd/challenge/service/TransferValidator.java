package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.AmountTransactionException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class TransferValidator {
    void validate(final Account accountFrom, final Account accountTo, final BigDecimal amount)
        throws AccountNotFoundException {
        if(amount.compareTo(BigDecimal.ZERO)<0) {
            throw new AmountTransactionException("Transfer amount is not valid. It must be a valid amount");
        }
        if(accountFrom == null) {
            throw new AccountNotFoundException("Account " + accountFrom + "not found");
        }
        if(accountTo == null) {
            throw new AccountNotFoundException("Account " + accountTo + "not found");
        }
    }
}
