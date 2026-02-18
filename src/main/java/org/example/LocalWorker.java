package org.example;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class LocalWorker {
    private final Repository repository;

    public LocalWorker(Repository repository) {
        this.repository = repository;
    }

    // Method to process the next job
    public void processNextJob() {
        String job = repository.getNextJob();
        if (job != null) {
            try {
                // Evaluate the math equation
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
                Object result = engine.eval(job);
                System.out.println("Processed job: " + job + " = " + result);
            } catch (ScriptException e) {
                System.err.println("Failed to process job: " + job);
            }
        } else {
            System.out.println("No jobs available to process.");
        }
    }
}