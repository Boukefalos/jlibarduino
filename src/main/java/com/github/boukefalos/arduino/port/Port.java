package com.github.boukefalos.arduino.port;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;
import purejavacomm.UnsupportedCommOperationException;
import base.work.Listen;

import com.github.boukefalos.arduino.exception.ArduinoException;
 
public class Port implements SerialPortEventListener {
	public static final int BUFFER_SIZE = 1024;
    public static final int TIME_OUT = 1000;
    public static final String PORT_NAMES[] = {
        "tty.usbmodem", // Mac OS X
        "usbdev",       // Linux
        "tty",          // Linux
        "serial",       // Linux
        "COM3",         // Windows
    };

	protected static Logger logger = LoggerFactory.getLogger(Port.class);
	protected static Port port;

	protected int bufferSize;
    protected SerialPort serialPort = null;
    protected InputStream inputStream = null;
    protected ArrayList<Listen<Object>> listenList = new ArrayList<Listen<Object>>();

    protected Port() {}

    protected Port(int bufferSize) {
    	this.bufferSize = bufferSize;
    }

	public void register(Listen<Object> listen) {
        listenList.add(listen);
	}

	public void remove(Listen<Object> listen) {
		listenList.remove(listen);
	}

    public static Port getInstance() {
    	return getInstance(BUFFER_SIZE);
    }

    public static Port getInstance(int bufferSize) {
    	if (port == null) {
    		port = new Port(bufferSize);
    	}
    	return port;
    }

    protected void connect() throws ArduinoException {
        CommPortIdentifier portid = null;
        Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            portid = (CommPortIdentifier) portEnum.nextElement();
            if (portid != null) {              
                System.out.println("Trying: " + portid.getName());
                for ( String portName: PORT_NAMES) {
                    if (portid.getName().equals(portName) || portid.getName().contains(portName)) {
                        try {
                        	serialPort = (SerialPort) portid.open("", TIME_OUT);
                        	serialPort.setSerialPortParams(19200, 8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	                        serialPort.setFlowControlMode(
	                        		SerialPort.FLOWCONTROL_XONXOFF_IN + 
	                        		SerialPort.FLOWCONTROL_XONXOFF_OUT);
	                        inputStream = serialPort.getInputStream();
	                        System.out.println("Connected on port: " + portid.getName());
	                        serialPort.addEventListener(this);
	                    } catch (UnsupportedCommOperationException | PortInUseException | IOException | TooManyListenersException e) {
	                    	throw new ArduinoException("Failed to connect");
	                    }
                        serialPort.notifyOnDataAvailable(true);
                        return;
                    }
                }
            }
        }
        throw new ArduinoException("No Arduino available");
    }
 
    public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		    case SerialPortEvent.DATA_AVAILABLE:
				byte[] buffer = new byte[bufferSize];
				try {
					inputStream.read(buffer);
					for (Listen<Object> listen : listenList) {
						listen.add(buffer);
					}
				} catch (IOException e) {
					logger.error("", e);
				}		    	
		        break; 
		    default:
		        break;
		}
    }

	public InputStream getInputStream() throws ArduinoException {		
		if (serialPort == null) {
			connect();	
		}
		try {
			return serialPort.getInputStream();
		} catch (IOException e) {
			throw new ArduinoException("Failed to get inputstream");
		}
	}

	public OutputStream getOutputStream() throws ArduinoException {
		if (serialPort == null) {
			connect();	
		}
		try {
			return serialPort.getOutputStream();
		} catch (IOException e) {
			throw new ArduinoException("Failed to get inputstream");
		}
	}

	public void close() {
		serialPort.close();		
	}
}