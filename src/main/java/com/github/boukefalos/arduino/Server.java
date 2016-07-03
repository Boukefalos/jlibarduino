package com.github.boukefalos.arduino;

import java.io.IOException;

import base.Control;
import base.Duplex;
import base.Receiver;
import base.exception.worker.ActivateException;
import base.exception.worker.DeactivateException;
import base.work.ReflectiveListen;

import com.github.boukefalos.arduino.exception.ArduinoException;

public class Server extends ReflectiveListen implements Control, Receiver {
    protected static final boolean DIRECT = false;

    protected Arduino arduino;
    protected Duplex duplex;
    protected boolean direct;

    public Server(Arduino arduino, Duplex duplex) {
        this(arduino, duplex, DIRECT);
    }

    public Server(Arduino tm1638, Duplex duplex, boolean direct) {
        this.arduino = tm1638;
        this.duplex = duplex;
        this.direct = direct;
        arduino.register(this); // Arduino > [input()]
        duplex.register(this); // Client > [receive()]
    }

    public void activate() throws ActivateException {
        duplex.start();
        super.activate();
    }
    
    public void deactivate() throws DeactivateException {
        duplex.stop();
        super.deactivate();
    }

    public void receive(byte[] buffer) {
        // Client > [Server] > Arduino
        if (direct) {
            try {
                arduino.send(buffer);
            } catch (ArduinoException e) {
                logger.error("", e);
            }
        } else {
            // option to decode() in derivatives?
        }
    }

    public void input(byte[] buffer) {
        // Arduino > [Server] > Client
        try {
            duplex.send(buffer);
        } catch (IOException e) {
            logger.error("", e);
        }
    }
}