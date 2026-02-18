package org.example;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    private final List<String> jobs;

    public Repository() {
        this.jobs = new ArrayList<>();
    }

    // Add a new job (math equation) to the repository
    public void addJob(String equation) {
        jobs.add(equation);
    }

    // Retrieve and remove the next job from the repository
    public String getNextJob() {
        if (!jobs.isEmpty()) {
            return jobs.remove(0);
        }
        return null; // No jobs available
    }

    // Get the total number of jobs in the repository
    public int getJobCount() {
        return jobs.size();
    }
}