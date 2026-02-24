package org.example;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Outsourcer
{
    private Queue<String> jobs = new LinkedList<>(); // Unassigned Jobs
    private Map<String, String> inFlight = new HashMap<>(); // Currently processing jobs, jobID, WorkerID
    private Lock lock = new ReentrantLock(); // Prevent overriding data for Map and Queue

    // Job ID management
    private int jobCount = -1;
    private String generateJobId()
    {
        jobCount++;
        return "job-" + jobCount;
    }

    private MqttClient client;
    public Outsourcer(String brokerUrl) throws MqttException {
        client = new MqttClient(brokerUrl, MqttClient.generateClientId());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection lost: " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                if (topic.equals("job/request"))
                {
                    String[] parts = payload.split("\\|");
                    String workerID = parts[0];
                    int capacity = Integer.parseInt(parts[1]);
                    handleJobRequest(workerID, capacity);
                }
                else if (topic.startsWith("job/result"))
                {
                    String workerID = topic.substring(11);
                    String[] parts = payload.split("\\|");
                    String jobId = parts[0];
                    lock.lock();
                    inFlight.remove(jobId);
                    System.out.println("Outsourcer: Job Completed: " + jobId + " by " + workerID);
                    lock.unlock();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.connect();
        client.subscribe("job/request", 2); // Listen to job requests
        client.subscribe("job/result", 2); // Listen for worker results
    }

    public void addJob(String equation)
    {
        lock.lock();
        try {
            jobs.add(equation);
            System.out.println("Outsourcer: Job Added: " + equation);
        } finally {
            lock.unlock();
        }
    }

    public void handleJobRequest(String workerID, int capacity)
    {
        lock.lock();
        try {
            while (capacity > 0 && !jobs.isEmpty()) {
                String equation = jobs.poll();
                String jobId = generateJobId();
                inFlight.put(jobId, workerID);

                String topic = "job/assign/" + workerID;
                String payload = jobId + "|" + equation;

                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(2);

                client.publish(topic, message);

                System.out.println("Assigned " + jobId + " to: " + workerID);

                capacity--;
            }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                lock.unlock();
            }
        }

    public void scheduleReassignTask()
    {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);

                    lock.lock();
                    if (!inFlight.isEmpty())
                        System.out.println("Currently in-flight: " + inFlight);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        });
        thread.start();
    }
    }
