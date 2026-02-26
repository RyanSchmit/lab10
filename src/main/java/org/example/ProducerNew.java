package org.example;

import java.util.ArrayList;

public class ProducerNew implements Runnable {
    private final Repository repository;

    public ProducerNew(Repository repository) {
        this.repository = repository;

    }

    // Method to produce and add jobs to the repository
    public void produceJob(String equation) throws InterruptedException {
        repository.produces(equation);



    }

    @Override
    public void run() {
        // Where should equations be stored
        ArrayList<String> equations = new ArrayList<>();
        equations.add("5 * 3");
        equations.add("10 / 2");
        equations.add("5 + 13");


        try {
            //make some really fast so that there are more in the repository, otherwise outsourcer only can
            //dispatch 1 at a time despite capacity
            for(int i = 0; i < 4; i++) {
                for (String equation : equations) {
                    produceJob(equation);
                }
            }
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
