package org.example;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Outsourcer implements Runnable
{
    private Queue<String> jobs = new LinkedList<>(); // Unassigned Jobs
    private Map<String, String> inFlight = new HashMap<>(); // Currently processing jobs, jobID, WorkerID
    private Lock lock = new ReentrantLock(); // Prevent overriding data for Map and Queue
    private Repository repository = Repository.getInstance();
    // Job ID management
    private int jobCount = -1;
    public int workerCount = -1;
    private String generateJobId()
    {
        jobCount++;
        return "job-" + jobCount;
    }
    private String generateWorkerId(){
        workerCount++;
        return "worker-" + workerCount;
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
                if (topic.equals("worker/register")) {

                    String tempId = payload;
                    String workerId = generateWorkerId();

                    String ackTopic = "worker/register/ack/" + tempId;

                    client.publish(ackTopic,
                            new MqttMessage(workerId.getBytes(StandardCharsets.UTF_8)));

                    System.out.println("Registered worker: " + workerId);
                }

                else if (topic.equals("job/request"))
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
                    String result = parts[1];
                    lock.lock();
                    inFlight.remove(jobId);
                    System.out.println("Outsourcer: Job Completed: " + jobId + " by " + workerID + " result =" + result);
                    lock.unlock();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.connect();
        client.subscribe("job/request", 2); // Listen to job requests
        client.subscribe("job/result/+", 2); // Listen for worker results
        client.subscribe("worker/register", 2);
    }


    public void handleJobRequest(String workerID, int capacity)
    {
        lock.lock();
        try {
            while (capacity > 0) {

                String equation = repository.getNextJob();
                //String equation = repository.tryGetNextJob();
                if (equation == null) break;
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
                    //TODO: reassign
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
        });
        thread.start();
    }

    @Override
    public void run() {

    }
}
