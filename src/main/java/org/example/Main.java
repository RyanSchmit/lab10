package org.example;

public class Main {
    public static void main(String[] args) {
        Repository repository = new Repository();
        Producer producer = new Producer(repository);
        LocalWorker worker = new LocalWorker(repository);

        // Simulate producing jobs
        producer.produceJob("2 + 2");
        producer.produceJob("5 * 3");
        producer.produceJob("10 / 2");

        // Simulate processing jobs
        worker.processNextJob();

    }
}