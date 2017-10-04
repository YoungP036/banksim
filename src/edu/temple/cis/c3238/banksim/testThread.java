/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cis.c3238.banksim;

/**
 *
 * @author fury
 */
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
    }
    @Override
    public void run(){
	int sum = 0;
	
	try{bank.transferingSem.acquire(10);
	////////////////CS START////////////////
        for (Account account : accounts) {
            System.out.printf("%s %s%n", 
                    Thread.currentThread().toString(), account.toString());
            sum += account.getBalance();
        }
	////////////////CS END/////////////////
	}catch(InterruptedException e){}
	finally{bank.transferingSem.release(10);}
        
	System.out.println(Thread.currentThread().toString() + 
                "test #" + bank.testCount++ +" Sum: " + sum);
        if (sum != numAccounts * initialBalance) {
            System.out.println(Thread.currentThread().toString() + 
                    " Money was gained or lost");
            System.exit(1);
        } else {
            System.out.println(Thread.currentThread().toString() + 
                    " The bank is in balance");
        }
    }
}
