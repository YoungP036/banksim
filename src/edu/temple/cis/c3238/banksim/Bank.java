package edu.temple.cis.c3238.banksim;
import java.util.concurrent.Semaphore;
/**
 * My implementation leverages the fact that semaphores can be initialized
 * with multiple charges. By initializing as many charges as we have threads
 * I can provide the desired sync using a single object.
 */

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
    public Semaphore transferingSem;
    public int testCount;
    private Thread testThread;
    public Bank(int numAccounts, int initialBalance) {
        open = true;
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        ntransacts = 0;
	transferingSem=new Semaphore(this.numAccounts);
	testCount=0;	
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);
        if (!open) return;
	try{
	    transferingSem.acquire();
	    ///////////CS START////////////////////
	    if (accounts[from].withdraw(amount)) {
		accounts[to].deposit(amount);
	    }
	    //////////CS END///////////////////
	}
	catch(InterruptedException e){}
	finally{transferingSem.release();}
        if (shouldTest()) test();
    }

    public void test() {
        testThread=new testThread(this,accounts, numAccounts,initialBalance);
	testThread.start();
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
