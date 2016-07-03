package com.github.boukefalos.arduino;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import base.work.Listen;
import base.work.ReflectiveListen;
import base.worker.Worker;

public abstract class AbstractArduino extends ReflectiveListen implements Arduino {
    public static final int BUFFER_SIZE = 1024;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected ArrayList<Listen<Object>> listenList;

    public AbstractArduino() {
        super(Worker.Type.DIRECT);
        listenList = new ArrayList<Listen<Object>>();
    }

    public void register(ReflectiveListen listen) {
        listenList.add(listen);        
    }

    public void remove(ReflectiveListen listen) {
        listenList.remove(listen);        
    }

    public void start() {}

    public void stop() {}

    public void exit() {
        stop();
    }

    public void input(byte[] buffer) {
        logger.error("Input was not parsed in port!");
    }
}
