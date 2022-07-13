package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }
  @Test
  public void amountTransaction_TransactionCommit() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(20));
    this.accountsService.createAccount(accountFrom);
    Account accountTo = new Account("Id-1234");
    accountTo.setBalance(new BigDecimal(20));
    this.accountsService.createAccount(accountTo);
    this.accountsService.createTransaction("Id-123", "Id-1234", new BigDecimal(20));
    assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(this.accountsService.getAccount("Id-1234").getBalance()).isEqualTo(new BigDecimal(40));
  }

  @Test
  public void amountTransaction_TransactionRollBack() throws Exception {
    Account accountFrom = new Account("Id-123");
    accountFrom.setBalance(new BigDecimal(20));
    this.accountsService.createAccount(accountFrom);
    Account accountTo = new Account("Id-1234");
    accountTo.setBalance(new BigDecimal(20));
    this.accountsService.createAccount(accountTo);
    this.accountsService.createTransaction("Id-123", "Id-1234", new BigDecimal(20));

    try {

      this.accountsService.createTransaction("Id-123", "Id-1234", new BigDecimal(10));
    }catch(Exception e) {
      assertThat(e.getMessage()).isEqualTo("Insufficient balance");
    }
    assertThat(this.accountsService.getAccount("Id-123").getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(this.accountsService.getAccount("Id-1234").getBalance()).isEqualTo(new BigDecimal(40));
  }
}
