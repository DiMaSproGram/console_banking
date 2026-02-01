package org.example;

import org.example.listener.OperationsConsoleListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example")) {
            OperationsConsoleListener listener = context.getBean(OperationsConsoleListener.class);
            Thread thread = new Thread(listener);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted. Exiting.");
            }
        }
    }
}