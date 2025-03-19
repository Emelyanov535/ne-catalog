package ru.necatalog.wildberriesparser.config;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

@Slf4j
public class TorController {

	private static final String TOR_HOST = "127.0.0.1";
	private static final int TOR_CONTROL_PORT = 9051;
	private static final List<Integer> torPorts = List.of(9050, 9060, 9070, 9080);
	private static final Random random = new Random();

	public static void changeIp() {
		try (Socket socket = new Socket(TOR_HOST, TOR_CONTROL_PORT);
			 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

			writer.println("AUTHENTICATE \" \"");
			writer.println("SIGNAL NEWNYM");
			writer.flush();

			log.info("Tor IP changed. Waiting before next request...");

		} catch (Exception e) {
			log.error("Error changing Tor IP: ", e);
		}
	}
}