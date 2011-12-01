package proxy.tray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

public class PlinkProcess {
	private Process process;

	public PlinkProcess() throws IOException {
		restart();
	}

	public void monitor() throws FileNotFoundException {
		final PrintWriter log = new PrintWriter("proxy.log");
		log.println(new Date());
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(
				process.getOutputStream()));
		final BufferedReader error = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		new Thread() {
			@Override
			public void run() {
				try {
					String line;
					while ((line = reader.readLine()) != null) {
						log.println(line);
						log.flush();
						line = line.toLowerCase();
						if (line.endsWith("(y/n)")) {
							writer.println("y");
						} else if (line.endsWith("(yes/no)")) {
							writer.println("yes");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();

		new Thread() {
			@Override
			public void run() {
				try {
					String line;
					while ((line = error.readLine()) != null) {
						log.println(line);
						log.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void restart() throws IOException {
		if (process != null) {
			process.destroy();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Properties properties = new Properties();
		properties.load(new FileReader("proxy.properties"));
		String host = properties.getProperty("host");
		if (host == null) {
			throw new IOException("proxy.properties: need host property");
		}

		String port = properties.getProperty("port");
		if (port == null) {
			throw new IOException("proxy.properties: need port property");
		}

		String user = properties.getProperty("user");
		if (user == null) {
			throw new IOException("proxy.properties: need user property");
		}

		String password = properties.getProperty("password");
		if (password == null) {
			throw new IOException("proxy.properties: need password property");
		}

		process = Runtime.getRuntime().exec(
				String.format("plink -N -v %s@%s -D 127.0.0.1:%s -pw %s", user,
						host, port, password));
		
	}

	public void exit() {
		process.destroy();
	}
}
