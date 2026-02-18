package org.example;

public class Producer {
    private final Repository repository;

    public Producer(Repository repository) {
        this.repository = repository;
    }

    // Method to produce and add jobs to the repository
    public void produceJob(String equation) {
        repository.addJob(equation);
        System.out.println("Produced job: " + equation);
    }
}