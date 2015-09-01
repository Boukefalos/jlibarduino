package com.github.boukefalos.arduino.port;

import java.util.Scanner;

import purejavacomm.SerialPortEvent;
import base.work.Listen;

public class StringPort extends Port {
    public static Port getInstance() {
        if (port == null) {
            port = new StringPort();
        }
        return port;
    }

    public void serialEvent(SerialPortEvent event) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            for (Listen<Object> listen : listenList) {
                listen.add(line);
            }
        }
        scanner.close();
    }
}
