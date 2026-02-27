package org.example;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    public static void main(String[] args) throws InterruptedException, MqttException {
        final String BROKERURL = "tcp://broker.emqx.io:1883";
        Outsourcer outsourcer = new Outsourcer(BROKERURL);
        Repository repository = Repository.getInstance();


        Thread remoteWorker = new Thread(new RemoteWorker(BROKERURL, 3));
        Thread remoteWorkerTwo = new Thread(new RemoteWorker(BROKERURL, 3));
        Thread remoteWorkerThree = new Thread(new RemoteWorker(BROKERURL, 3));

        Thread producerNew = new Thread(new Producer(repository));
        Thread worker = new Thread(new LocalWorker(repository));


        producerNew.start();
        worker.start();
        remoteWorker.start();
        remoteWorkerTwo.start();
        remoteWorkerThree.start();
        outsourcer.scheduleReassignTask();
    }
}