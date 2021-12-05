package by.skarulskaya.multithreading.entity;

import com.sun.jmx.remote.internal.ArrayQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CashRegisterBase {
    private static final Logger logger = LogManager.getLogger();
    private static CashRegisterBase instance;
    private static final ReentrantLock instanceLocker = new ReentrantLock();
    private static final AtomicBoolean isInstanceCreated = new AtomicBoolean(false);
    private static ArrayList<Semaphore> checkouts;
    private static Semaphore orderCounter = new Semaphore(1, true);

    private CashRegisterBase(int checkoutCount) {
        checkouts = new ArrayList<>(checkoutCount);
        for(int i=0; i<checkoutCount; i++) {
            Semaphore checkout = new Semaphore(1, true);
            checkouts.add(checkout);
        }
    }

    public static CashRegisterBase getInstance() {
        if (!isInstanceCreated.get()) {
            try {
                instanceLocker.lock();
                if (instance == null) {
                    instance = new CashRegisterBase(3);
                    isInstanceCreated.set(true);
                }
            } finally {
                instanceLocker.unlock();
            }
        }
        return instance;
    }

    /*public CashRegister getInLine() {
        CashRegister register = null;// todo any other options?
        try {
            orderLock.lock();
            TimeUnit.SECONDS.sleep(1);
            if(cashRegisters.isEmpty()) { // все кассы заняты
                Condition condition = orderLock.newCondition();
                ArrayDeque<Condition> smaller = orderQueues.stream()
                        .min((o1, o2) -> {
                            int size1 = o1.size();
                            int size2 = o2.size();
                            return Integer.compare(size1, size2);
                        }).get();
                smaller.add(condition);
            }
            register = cashRegisters.poll();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } finally {
            orderLock.unlock();
        }
        return register;
    }*/

    public Semaphore getInLine(long visitorId) {
        Semaphore checkout = null;// todo any other options?
        try {
            TimeUnit.SECONDS.sleep(1);
            checkout = checkouts.stream().min((s1, s2) -> {
                int size1 = s1.getQueueLength();
                int size2 = s2.getQueueLength();
                return Integer.compare(size1, size2);
            }).get();
            logger.info("visitor standing in queue: "+visitorId+' '+checkout);
            checkout.acquire();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return checkout;
    }

    public void doOrderCheckout(Semaphore checkout, long visitorId) {
        try {
            TimeUnit.SECONDS.sleep((int)(Math.random() * 10));
            logger.info("Order doing: " +visitorId+' '+ checkout);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    public void releaseCashRegister(Semaphore checkout, long visitorId) {
        logger.info("Terminal is released: "+visitorId+' '+ checkout);
        if (checkout != null) {
            checkout.release();
        }
    }

    public void waitOrder(long visitorId) {
        try {
            TimeUnit.SECONDS.sleep((int)(Math.random() * 10));
            logger.info("Order waiting:"+visitorId);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    public void getOrder(long visitorId) {
        try {
            logger.info("Visitor waiting for get order: "+visitorId);
            orderCounter.acquire();
            TimeUnit.SECONDS.sleep(1);
            orderCounter.release();
            logger.info("Order done! "+ visitorId);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
