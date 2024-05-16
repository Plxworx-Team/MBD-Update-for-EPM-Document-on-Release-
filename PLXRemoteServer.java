
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;                                                     
import java.io.IOException;                   

public class PLXRemoteServer {
	public static void main(String[] args) throws IOException {

		String PortNumber = null;
		Properties prop = new Properties();

		try (FileInputStream file = new FileInputStream(
				"C:" + File.separatorChar + "Plx_Works" + File.separatorChar + "PlxWorx3.0" + File.separatorChar + "CadCustomization.properties")) {
			prop.load(file);

		} catch (IOException e) {
			e.printStackTrace();
			throw e; // Re-throw the IOException
		}

		PortNumber = prop.getProperty("PortNumber");
		System.out.println("## port Name" + PortNumber);

		String myappPath = System.getenv("PATH_Plx");
		System.out.println("MYAPP_PATH environmental variable path:-" + myappPath);
final int SERVER_PORT = Integer.parseInt(PortNumber);
		//final int SERVER_PORT = 64128; // port
		try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
			System.out.println("Server started. Listening on port " + SERVER_PORT);
			while (true) {
				try (Socket clientSocket = serverSocket.accept()) { // Accept client connection
					System.out.println("Client connected.");
					BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String clientMessage = reader.readLine();
					System.out.println(" FileName " + clientMessage);

					try {
						String fileName = clientMessage;
						ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", myappPath, fileName);
						Process process = processBuilder.start();
						process.waitFor();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}

					OutputStream outputStream = clientSocket.getOutputStream();
					String response = "Hello from server!";
					outputStream.write(response.getBytes());
					outputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
