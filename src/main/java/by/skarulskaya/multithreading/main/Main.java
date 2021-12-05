package by.skarulskaya.multithreading.main;

import by.skarulskaya.multithreading.entity.Visitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            ArrayList<Future<String>> listFuture = new ArrayList<>();
            for (int i = 0; i<5; i++) {
                Visitor visitor = new Visitor(i, i % 3 == 0);
                listFuture.add(executor.submit(visitor));
            }
            executor.shutdown();
            for (Future<String> future : listFuture) {
                logger.info(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("CustomException or InterruptedException", e);
        }
    }
}
