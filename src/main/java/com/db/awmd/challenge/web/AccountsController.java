package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.AmountTransactionException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value= "/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = {"application/json"})
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public ResponseEntity<Object> getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    final Account account ;
    try {
      account = this.accountsService.getAccount(accountId);
    }catch(AccountNotFoundException afe) {
      return new ResponseEntity<>(afe.getMessage(), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(account, HttpStatus.OK);

  }


  @PostMapping(path= "/transaction", consumes = {"application/json"})
  public ResponseEntity<Object> createTransaction(@RequestBody @Valid Transaction transaction) {

    try {
      this.accountsService.createTransaction(transaction.getAccountFrom(), transaction.getAccountTo(), transaction.getAmount());
    } catch (AmountTransactionException ate) {
      return new ResponseEntity<>(ate.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (AccountNotFoundException anfe) {
      return new ResponseEntity<>(anfe.getMessage(), HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>("Transaction Completes", HttpStatus.CREATED);
  }

}
