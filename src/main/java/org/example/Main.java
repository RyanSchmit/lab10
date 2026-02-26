package org.example;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    public static void main(String[] args) throws InterruptedException, MqttException {
        final String BROKERURL = "tcp://broker.emqx.io:1883";
        Outsourcer outsourcer = new Outsourcer(BROKERURL);
        Repository repository = Repository.getInstance();


        Thread remoteWorker = new Thread(new RemoteWorker(BROKERURL, 3));
        Thread producer = new Thread(new Producer(repository, outsourcer));
        Thread worker = new Thread(new LocalWorker(repository));


        producer.start();
        worker.start();
        remoteWorker.start();
        outsourcer.scheduleReassignTask();
    }
}