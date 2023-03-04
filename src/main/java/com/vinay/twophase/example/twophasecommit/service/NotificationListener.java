package com.vinay.twophase.example.twophasecommit.service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import static com.vinay.twophase.example.twophasecommit.Constants.DESTINATION;

@Service
public class NotificationListener {
    @JmsListener(destination = DESTINATION)
    public void onMessage(String message) {
        System.out.println("Received message - " + message);
    }
}
