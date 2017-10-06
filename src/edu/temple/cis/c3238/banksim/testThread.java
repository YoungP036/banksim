package edu.temple.cis.c3238.banksim;
public class testThread extends Thread{
    
    private final Bank bank;
    private final int numAccounts;
    private final int initialBalance;
    private final Account[] accounts;
    public testThread(Bank b,Account[] accounts,int aCount, int startBalance){
	   this.bank=b;
	   this.numAccounts=aCount;
	   this.initialBalance=startBalance;
	   this.accounts=accounts;
	   Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }
    
    @Override
    public void run(){
	int sum = 0;
	
	try{bank.transferingSem.acquireUninterruptibly(numAccounts);
	////////////////CS START////////////////
        for (Account account : accounts) {
            System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), account.toString());
            sum += account.getBalance();
        }
	////////////////CS END/////////////////
	}
	finally{bank.transferingSem.release(numAccounts);}
        
	System.out.println(Thread.currentThread().toString() + 
                "test #" + bank.testCount++ +" Sum: " + sum);
//        if (sum != numAccounts * initialBalance) {
//            System.out.println(Thread.currentThread().toString() + 
//                    " Money was gained or lost");
//            System.exit(1);
//        } else {
//            System.out.println(Thread.currentThread().toString() + 
//                    " The bank is in balance");
//        }
    }
}
//9681