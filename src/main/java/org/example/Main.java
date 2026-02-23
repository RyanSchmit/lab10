package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Repository repository = Repository.getInstance();

        Thread producer = new Thread(new Producer(repository));
        Thread worker = new Thread(new LocalWorker(repository));

        producer.start();
        worker.start();
    }
}