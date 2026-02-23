package org.example;

import java.util.LinkedList;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// AKA Buffer
public class Repository extends PropertyChangeSupport {
    private final Queue<String> jobs = new LinkedList<>();
    // What should I do with capacity?
    // Should I add it to the constructor and use it to limit the number of jobs in the repository?
    private final int capacity = 10; // Example capacity, can be adjusted as needed
    private static final Repository instance = new Repository();

    private final Semaphore empty = new Semaphore(capacity);
    private final Semaphore full = new Semaphore(0);

    private final Lock lock = new ReentrantLock();

    private Repository() {
       super(new Object());
    }

    public static Repository getInstance() {
        return instance;
    }

    // Add a new job to the repository
    public void produces(String equation) throws InterruptedException {
        empty.acquire();
        lock.lock();
        try {
            jobs.add(equation);
            firePropertyChange("jobs", null, equation);
            System.out.println("Produced: " + equation + " | Current jobs in repository: " + jobs.size());
        } finally {
            lock.unlock();
        }
        full.release();
    }

    // Retrieve and remove the next job from the repository
    public String getNextJob() throws InterruptedException {
        full.acquire();
        String equation;
        lock.lock();
        try {
            equation = jobs.remove();
            System.out.println("Consumed job: " + equation);
        } finally {
            lock.unlock();
        }
        empty.release();
        return equation;
    }
}