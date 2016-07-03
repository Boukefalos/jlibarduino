package com.github.boukefalos.arduino;

import base.Control;
import base.work.ReflectiveListen;

import com.github.boukefalos.arduino.exception.ArduinoException;

public interface Arduino extends Control {
    public void register(ReflectiveListen listen);
    public void remove(ReflectiveListen listen);

    public void send(byte[] buffer) throws ArduinoException;
}