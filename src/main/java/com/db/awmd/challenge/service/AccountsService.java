package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.AmountTransactionException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.repository.AccountsRepository;
import com.db.awmd.challenge.transaction.AccountTransactionManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
public class AccountsService {
  @Getter
  private final AccountsRepository accountsRepository;
  private AccountTransactionManager accountTransactionManager;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private TransferValidator transferValidator;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
    this.accountTransactionManager = new AccountTransactionManager(accountsRepository);
  }
  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }
  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  public void createTransaction(final String fromAccount, final String toAccount, final BigDecimal amount) throws AmountTransactionException, AccountNotFoundException, DuplicateAccountIdException {
    transferValidator.validate(getAccount(fromAccount), getAccount(toAccount), amount);
    accountTransactionManager.doInTransaction(() -> {
      this.debit(fromAccount, amount);
      this.credit(toAccount, amount);
    });
    accountTransactionManager.commit();
    notificationService.notifyAboutTransfer(getAccount(fromAccount), "the transfer to the account with ID " +toAccount+ " is complete for the amount of " + amount+ ".");
    notificationService.notifyAboutTransfer(getAccount(toAccount), "the account with ID " +fromAccount+ " has transferred " + amount+ " into your account");
  }
  private Account debit(String accountId, BigDecimal amount) throws AmountTransactionException {
    final Account account = accountTransactionManager.getRepoProxy().getAccount(accountId);
    if(account == null) {
      throw new AmountTransactionException("Account does not exist");
    }
    if(account.getBalance().compareTo(amount) == -1) {
      throw new AmountTransactionException("Insufficient balance");
    }
    BigDecimal balance= account.getBalance().subtract(amount);
    account.setBalance(balance);
            return account;
  }
  private Account credit(String accountId, BigDecimal amount) throws AmountTransactionException {
    final Account account = accountTransactionManager.getRepoProxy().getAccount(accountId);
    if(account == null) {
      throw new AmountTransactionException("Account does not exist");
    }
    BigDecimal balance = account.getBalance().add(amount);
    account.setBalance(balance);
    return account;
  }
}
