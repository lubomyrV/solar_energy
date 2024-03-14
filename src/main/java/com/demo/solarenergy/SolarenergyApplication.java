package com.demo.solarenergy;

import com.demo.solarenergy.service.SerialService;
import com.fazecast.jSerialComm.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class SolarenergyApplication {
	private static final int PAUSE = 60000;
	private static final int BAUD_RATE = 19200;
	private static final String PORT_LINUX = "/dev/ttyUSB0";
	private static final String PORT_WINDOWS = "COM4";
	public static void main(String[] args) {
		ConfigurableApplicationContext appContext = SpringApplication.run(SolarenergyApplication.class, args);
		Environment env = appContext.getBean(Environment.class);
		String databaseName = env.getProperty("databaseName");
		System.out.println("Database: " + databaseName);

		String system = System.getProperty("os.name");
		System.out.println("System: " + system);

		String port = system.contains("Linux") ? SolarenergyApplication.PORT_LINUX : SolarenergyApplication.PORT_WINDOWS;
		System.out.println("Opening port: " + port);

		SerialPort comPort = null;
		while (comPort == null) {
			try {
				comPort = SerialPort.getCommPort(port);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					System.err.println(ex.getMessage());
				}
			}
		}
		comPort.setComPortParameters(SolarenergyApplication.BAUD_RATE, 8, 1, SerialPort.NO_PARITY);
		comPort.openPort();
		System.out.println("Successfully opened port: " + comPort.toString());
		comPort.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
			@Override
			public void serialEvent(SerialPortEvent event) {
				byte[] receivedData = event.getReceivedData();
				new Thread(new SerialService(databaseName, receivedData)).start();
				try {
					Thread.sleep(SolarenergyApplication.PAUSE);
				} catch (InterruptedException e) {
					System.err.println(e);
				}
			}
		});
	}
}
