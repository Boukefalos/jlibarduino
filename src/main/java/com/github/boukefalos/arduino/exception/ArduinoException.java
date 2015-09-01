package com.github.boukefalos.arduino.exception;

import java.io.IOException;

public class ArduinoException extends IOException {
    protected static final long serialVersionUID = 1L;
    
    public ArduinoException(String message) {
        super(message);
    }
}
