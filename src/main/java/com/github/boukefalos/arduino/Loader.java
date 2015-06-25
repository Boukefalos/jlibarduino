package com.github.boukefalos.arduino;

import java.util.Properties;

import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.parameters.ConstantParameter;

import base.exception.LoaderException;
import base.loader.AbstractLoader;

import com.github.boukefalos.arduino.exception.ArduinoException;
import com.github.boukefalos.arduino.implementation.Local;
import com.github.boukefalos.arduino.implementation.Remote;

public class Loader extends AbstractLoader<Loader> {
    protected static final String PROPERTIES_FILE = "arduino.properties";

    public Loader(Properties properties) throws LoaderException {
    	this(Local.class, Remote.class, Server.class, properties);
    }

	public Loader(Class<?> localClass, Class<?> remoteClass, Class<?> serverClass, Properties properties) throws LoaderException {
		/* Add implementation */
		switch (properties.getProperty("implementation")) {
			case "local":
				pico.addComponent(localClass);
				break;				
			case "remote":
				pico.addComponent(remoteClass);

				/* Add remote duplex implementation */
				try {
					String protocol = properties.getOrDefault("protocol", "tcp").toString();
					String implementation = properties.getOrDefault("tcp.implementation", "socket").toString();
					String host = properties.getProperty("remote.host");
					int port = Integer.valueOf(properties.getProperty("remote.port"));					
					addClientDuplex(protocol, implementation, host, port);
				} catch (NumberFormatException e) {
					throw new LoaderException("Failed to parse remote.port");
				}				
				break;
		}

		/* Add server */
		if (properties.getProperty("server") != null) {
			boolean direct = Boolean.parseBoolean(properties.getOrDefault("server.direct", Server.DIRECT).toString());
			pico.addComponent(serverClass, serverClass, new Parameter[] {
				new ComponentParameter(),
				new ComponentParameter(),
				new ConstantParameter(direct)});

			/* Add server forwarder implementation */
			try {
				String protocol = properties.getOrDefault("server.protocol", "tcp").toString();
				String implementation = properties.getOrDefault("tcp.implementation", "socket").toString();
				int port = Integer.valueOf(properties.getProperty("server.port"));
				addServerDuplex(protocol, implementation, port);
			} catch (NumberFormatException e) {
				throw new LoaderException("Failed to parse server.port");
			}
		}
	}

	public Arduino getArduino() throws ArduinoException {
		try {
			return (Arduino) pico.getComponent(Arduino.class);
		} catch (PicoCompositionException e) {
			throw new ArduinoException("Failed to load");
		}
    }

    public Server getServer() throws ArduinoException {
    	try {
    		return pico.getComponent(Server.class);
		} catch (PicoCompositionException e) {
			throw new ArduinoException("Failed to load");
		}
    }
}
