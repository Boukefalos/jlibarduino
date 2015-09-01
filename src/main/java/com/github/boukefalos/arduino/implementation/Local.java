package com.github.boukefalos.arduino.implementation;

import java.io.IOException;
import java.io.OutputStream;

import base.work.Listen;

import com.github.boukefalos.arduino.AbstractArduino;
import com.github.boukefalos.arduino.exception.ArduinoException;
import com.github.boukefalos.arduino.port.Port;

public class Local extends AbstractArduino {
    protected Port arduino;
    protected OutputStream outputStream;

    public Local() throws Exception {
        this(Port.getInstance());
    }

    public Local(Port arduino) throws ArduinoException {
        this.arduino = arduino;
        outputStream = arduino.getOutputStream();
    }

    public void register(Listen<Object> listen) {
        arduino.register(listen);        
    }

    public void remove(Listen<Object> listen) {
        arduino.remove(listen);        
    }

    public void stop() {
        arduino.close();        
    }

    public void send(byte[] buffer) throws ArduinoException {
        try {
        outputStream.write(buffer);
        } catch (IOException e) {
            throw new ArduinoException("Failed to write to arduino");
        }
    }
}
