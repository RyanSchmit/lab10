package org.example;

import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {
    public static void main(String[] args) throws InterruptedException, MqttException {
        final String BROKERURL = "tcp://broker.emqx.io:1883";
        OutsourcerNew outsourcer = new OutsourcerNew(BROKERURL);
        Repository repository = Repository.getInstance();


        Thread remoteWorker = new Thread(new RemoteWorker(BROKERURL, 3));
        Thread producerNew = new Thread(new ProducerNew(repository));
        Thread worker = new Thread(new LocalWorker(repository));


        producerNew.start();
        worker.start();
        remoteWorker.start();
        outsourcer.scheduleReassignTask();
    }
}