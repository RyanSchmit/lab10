package org.example;

// AKA Consumer
public class LocalWorker implements Runnable {
    private final Repository repository;

    public LocalWorker(Repository repository) {
        this.repository = repository;
    }


    @Override
    public void run() {
        try {
            while (true) {
                //Slow down so remote worker gets some
                Thread.sleep(5000);
                String job = repository.getNextJob();
                if (job == null || job.isBlank()) {
                    System.out.println("No jobs available to process.");
                    return;
                }

                try {
                    double result = ExpressionEvaluator.eval(job);
                    System.out.println("Local Worker Consumed job: " + job + " = " + result);

                } catch (IllegalArgumentException ex) {
                    System.err.println("Failed to process job: " + job);
                    System.err.println("Parse error: " + ex.getMessage());
                } catch (ArithmeticException ex) {
                    System.err.println("Failed to process job: " + job);
                    System.err.println("Math error: " + ex.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Worker thread interrupted. Exiting.");
            }
        }

}