package by.skarulskaya.multithreading.entity;

import com.sun.jmx.remote.internal.ArrayQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CashRegisterBase {
    private static final Logger logger = LogManager.getLogger();
    private static CashRegisterBase instance;
    private static final ReentrantLock instanceLocker = new ReentrantLock();
    private static AtomicBoolean isInstanceCreated = new AtomicBoolean(false);
    private static ReentrantLock orderLock = new ReentrantLock();
    private static ArrayList<ArrayDeque<Condition>> orderQueues;
    private static ArrayDeque<CashRegister> cashRegisters;

    private CashRegisterBase(int ordersCount) {
        orderQueues = new ArrayList<>(ordersCount);
        cashRegisters = new ArrayDeque<>(ordersCount);
        for(int i=0; i<ordersCount; i++) {
            ArrayDeque<Condition> orderQueue = new ArrayDeque<>(5);
            orderQueues.add(orderQueue);
            CashRegister register = new CashRegister();
            cashRegisters.add(register);
        }
    }

    public static CashRegisterBase getInstance() {
        if (!isInstanceCreated.get()) {
            try {
                instanceLocker.lock();
                if (instance == null) {
                    instance = new CashRegisterBase();
                    isInstanceCreated.set(true);
                }
            } finally {
                instanceLocker.unlock();
            }
        }
        return instance;
    }

    public CashRegister getInLine() {
        try {
            orderLock.lock();
            TimeUnit.SECONDS.sleep(1);
            if(cashRegisters.isEmpty()) {
                Condition condition = orderLock.newCondition();
                ArrayDeque<Condition> smaller = orderQueues.stream()
                        .min((o1, o2) -> {
                            int size1 = o1.size();
                            int size2 = o2.size();
                            return Integer.compare(size1, size2);
                        }).get();
                smaller.add(condition);
            }
            CashRegister register = cashRegisters.poll();
            return register;
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            orderLock.unlock();
        }
    }
}
