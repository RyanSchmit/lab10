package org.example;

public class Job {
    private String jobId;
    private String equation;

    public Job(String jobId, String equation) {
        this.jobId = jobId;
        this.equation = equation;
    }

    public String getJobId() {
        return jobId;
    }

    public String getEquation() {
        return equation;
    }
}
