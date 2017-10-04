package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 */
public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long ntransacts = 0;
    private final int initialBalance;
    private final int numAccounts;
    private boolean open;
    public Lock accountLock;
    public Condition testing;
    public Semaphore transferingSem;
    private int testCount;

    public Bank(int numAccounts, int initialBalance) {
        open = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
	accountLock=new ReentrantLock();
	testing = accountLock.newCondition();
	transferingSem=new Semaphore(10);
	testCount=0;
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);
        if (!open) return;
	try{
	    transferingSem.acquire();
	    if (accounts[from].withdraw(amount)) {
		accounts[to].deposit(amount);
	    }
	}
	catch(InterruptedException e){}
	finally{transferingSem.release();}
        if (shouldTest()) test();
    }

    public void test() {
        int sum = 0;
	
	try{transferingSem.acquire(10);
	////////////////CS START////////////////
        for (Account account : accounts) {
            System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), account.toString());
            sum += account.getBalance();
        }
	////////////////CS END/////////////////
	}catch(InterruptedException e){}
	finally{transferingSem.release(10);}
        
	System.out.println(Thread.currentThread().toString() + 
                "test " + testCount++ +" Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + 
                    " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + 
                    " The bank is in balance");
        }
    }

    public int size() {
        return accounts.length;
    }
    
    public synchronized boolean isOpen() {return open;}
    
    public void closeBank() {
        synchronized (this) {
            open = false;
        }
        for (Account account : accounts) {
            synchronized(account) {
                account.notifyAll();
            }
        }
    }
    
    public synchronized boolean shouldTest() {
        return ++ntransacts % NTEST == 0;
    }

}
