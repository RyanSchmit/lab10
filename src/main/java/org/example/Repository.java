package org.example;

import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyChangeSupport;

public class Repository extends PropertyChangeSupport {
    private final List<String> jobs = new ArrayList<>();
    private static final Repository instance = new Repository();

    private Repository() {
       super(new Object());
    }

    public static Repository getInstance() {
        return instance;
    }

    // Add a new job to the repository
    public void addJob(String equation) {
        jobs.add(equation);
        firePropertyChange("jobs", null, equation);
    }

    // Retrieve and remove the next job from the repository
    public String getNextJob() {
        if (!jobs.isEmpty()) {
            // Should I add firePropertyChange here?
            return jobs.remove(0);
        }
        return null; // No jobs available
    }
}