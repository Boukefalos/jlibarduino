package com.github.boukefalos.arduino;

import base.Control;
import base.work.Listen;

import com.github.boukefalos.arduino.exception.ArduinoException;

public interface Arduino extends Control {
    public void register(Listen<Object> listen);
    public void remove(Listen<Object> listen);

    public void send(byte[] buffer) throws ArduinoException;
}