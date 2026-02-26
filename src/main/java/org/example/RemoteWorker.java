package org.example;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class RemoteWorker implements Runnable {

    private final MqttClient client;
    private final String tempId;
    private String workerId;
    private final int capacity;

    public RemoteWorker(String brokerUrl, int capacity) throws MqttException {

        this.tempId = "temp-" + UUID.randomUUID();
        this.capacity = capacity;

        client = new MqttClient(brokerUrl, tempId);

        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Worker connection lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);

                // Registration acknowledgment
                if (topic.equals("worker/register/ack/" + tempId)) {

                    workerId = payload;
                    System.out.println("Assigned worker ID: " + workerId);

                    client.subscribe("job/assign/" + workerId, 2);

                    requestWork();
                    return;
                }

                // Job assignment
                if (topic.equals("job/assign/" + workerId)) {

                    String[] parts = payload.split("\\|");
                    String jobId = parts[0];
                    String equation = parts[1];

                    double result = ExpressionEvaluator.eval(equation);

                    String resultPayload = jobId + "|" + result;
                    String resultTopic = "job/result/" + workerId;

                    client.publish(resultTopic,
                            new MqttMessage(resultPayload.getBytes(StandardCharsets.UTF_8)));

                    System.out.println(workerId + " completed " + jobId);

                    requestWork();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}
        });
    }

    @Override
    public void run() {
        try {
            client.connect();

            client.subscribe("worker/register/ack/" + tempId, 2);

            client.publish("worker/register",
                    new MqttMessage(tempId.getBytes(StandardCharsets.UTF_8)));

            System.out.println("Worker sent registration request.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestWork() throws MqttException {

        if (workerId == null) return;

        String payload = workerId + "|" + capacity;

        client.publish("job/request",
                new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));

        System.out.println(workerId + " requested work.");
    }
}