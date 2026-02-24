package org.example;

import java.util.ArrayList;

public class Producer implements Runnable {
    //private final Repository repository;
    private final Outsourcer outsourcer;

    //public Producer(Repository repository) {
    //    this.repository = repository;
    //}

    public Producer(Outsourcer outsourcer)
    {
        this.outsourcer = outsourcer;
    }

    // Method to produce and add jobs to the repository
    public void produceJob(String equation) throws InterruptedException {
        //repository.produces(equation);

        // Add jobs to outsourcer instead
        outsourcer.addJob(equation);
        //System.out.println("Produced job: " + equation);
    }

    @Override
    public void run() {
        // Where should equations be stored
        ArrayList<String> equations = new ArrayList<>();
        equations.add("5 * 3");
        equations.add("10 / 2");
        equations.add("5 + 13");

        try {
            while (true) {
                for (String equation : equations) {
                    produceJob(equation);
                    Thread.sleep(1000); // Simulate time taken to produce a job
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer was interrupted");
        }
    }
}