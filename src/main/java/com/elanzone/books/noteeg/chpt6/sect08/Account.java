package com.elanzone.books.noteeg.chpt6.sect08;


import java.util.concurrent.atomic.AtomicLong;

public class Account {

    private AtomicLong balance;

    public Account() {
        balance = new AtomicLong();
    }

    public long getBalance() {
        return balance.get();
    }

    public void setBalance(long amount) {
        balance.set(amount);
    }

    public void addAmount(long amount) {
        balance.getAndAdd(amount);
    }

    public void subtractAmount(long amount) {
        this.balance.getAndAdd(-amount);
    }

}
