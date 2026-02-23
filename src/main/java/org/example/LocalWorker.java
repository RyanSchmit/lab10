package org.example;

public class LocalWorker {
    private final Repository repository;

    public LocalWorker(Repository repository) {
        this.repository = repository;
    }

    public void processNextJob() {
        String job = repository.getNextJob();

        if (job == null || job.isBlank()) {
            System.out.println("No jobs available to process.");
            return;
        }

        try {
            double result = ExpressionEvaluator.eval(job);
            System.out.println("Processed job: " + job + " = " + result);
        } catch (IllegalArgumentException ex) {
            System.err.println("Failed to process job: " + job);
            System.err.println("Parse error: " + ex.getMessage());
        } catch (ArithmeticException ex) {
            System.err.println("Failed to process job: " + job);
            System.err.println("Math error: " + ex.getMessage());
        }
    }
}