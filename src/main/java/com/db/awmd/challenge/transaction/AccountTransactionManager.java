package com.db.awmd.challenge.transaction;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import java.lang.reflect.Proxy;
import java.util.Map;

public class AccountTransactionManager {
    private final AccountsRepository accountsRepository;
    private TransactionInvocationHandler<Account> handler;
    @Getter
    private boolean autoCommit = false;
    @Getter
    private AccountsRepository repoProxy;
    public AccountTransactionManager(AccountsRepository repository) {
        this.accountsRepository = repository;
        handler = new TransactionInvocationHandler<Account>(accountsRepository);
        repoProxy = (AccountsRepository) Proxy.newProxyInstance(AccountsRepository.class.getClassLoader()
                , new Class[]{AccountsRepository.class}, handler);
        }
        public void doInTransaction(TransactionalCallback callback) {
            TransactionalContext<Account, Account> context = new TransactionalContext<>();
            ThreadLocal<TransactionalContext<Account, Account>> localContext = handler.getLocalContext();
            localContext.set(context);
            try {
                callback.process();
                if(autoCommit) {
                    commit();
                }
            }catch(Exception e) {
                rollBack();
                throw e;
            }
        }

        //Saving points to get the balance
        public void commit() {
            TransactionalContext<Account, Account> localContext = handler.getLocalContext().get();
            Map<Account, Account> savePoints = localContext.getSavePoints();
            savePoints.entrySet().forEach(entry -> {
                Account key = entry.getKey();
                Account value = entry.getValue();
                value.setBalance(key.getBalance());
            });

        }
        //Destroy save points with same context
        public void rollBack() {
            // Destroy Save points within same transactional context
            TransactionalContext<Account, Account> localContext = handler.getLocalContext().get();
            localContext.getSavePoints().clear();
        }
}
