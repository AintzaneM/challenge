package com.db.awmd.challenge.transaction;

@FunctionalInterface

public interface TransactionalCallback {
    public void process();
}
