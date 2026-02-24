package org.example;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    public static void main(String[] args) throws InterruptedException, MqttException {

        Outsourcer outsourcer = new Outsourcer("tcp://broker.emqx.io:1883");
        Repository repository = Repository.getInstance();

        //Thread producer = new Thread(new Producer(repository));
        Thread producer = new Thread(new Producer(outsourcer));
        Thread worker = new Thread(new LocalWorker(repository));


        producer.start();
        worker.start();
        outsourcer.scheduleReassignTask();
    }
}