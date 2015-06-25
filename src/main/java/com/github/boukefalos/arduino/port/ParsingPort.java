package com.github.boukefalos.arduino.port;

import java.io.InputStream;
import java.lang.reflect.Method;

import purejavacomm.SerialPortEvent;
import base.work.Listen;

public class ParsingPort extends Port {
	protected Class<?> messageClass;

	protected ParsingPort(Class<?> messageClass) {
		this.messageClass = messageClass;
	}

	public static Port getInstance(Class<?> messageClass) {
    	if (port == null) {
    		port = new ParsingPort(messageClass);
    	}
    	return port;
	}

	public void serialEvent(SerialPortEvent event) {
		try {
			Method m = messageClass.getMethod("parseDelimitedFrom", InputStream.class);
			Object object = m.invoke(null, inputStream);
			for (Listen<Object> listen : listenList) {
				listen.add(object);
			}
		} catch (Exception e) {
			logger.error("", e);
		} catch (Throwable e) {
			logger.error("", e);
		}
	}
}
