package by.skarulskaya.multithreading.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Visitor implements Callable<String> {
    private static final Logger logger = LogManager.getLogger();
    private final long id;
    private final boolean preOrder;

    public Visitor(long id, boolean preOrder) {
        this.id = id;
        this.preOrder = preOrder;
    }

    public long getId() {
        return id;
    }

    public boolean isPreOrder() {
        return preOrder;
    }

    @Override
    public String call(){
        if(preOrder) {
            doPreOrder();
        }
        else {
            //todo стать в очередь в кассу, где меньше людей? или отправить посетителя в какой-то метод,
            //распределяющий их по кассам?
            //подождать пока заказ готовиться
            //забрать заказ с выдачи
        }
        return "Visitor id "+id;
    }

    private void doPreOrder() {
        try {
            TimeUnit.SECONDS.sleep((int)(Math.random() * 10));
        }
        catch(InterruptedException exception) {
            logger.error(exception.getMessage(), exception);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;

        Visitor visitor = (Visitor) o;

        if (id != visitor.id) return false;
        return preOrder == visitor.preOrder;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (preOrder ? 1 : 0);
        return result;
    }
}
