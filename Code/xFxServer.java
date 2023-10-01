
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class xFxServer {

	public static void main(String[] args) throws Exception {

		try (ServerSocket ss = new ServerSocket(80)) {
			while (true) {
				System.out.println("Server waiting...");
				Socket connectionFromClient = ss.accept();
				System.out.println(
						"Server got a connection from a client whose port is: " + connectionFromClient.getPort());

				try {
					InputStream in = connectionFromClient.getInputStream();
					OutputStream out = connectionFromClient.getOutputStream();

					String errorMessage1 = "NOT FOUND\n";

					BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
					BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));
					PrintWriter printout = new PrintWriter(out, true);

					DataInputStream dataIn = new DataInputStream(in);
					DataOutputStream dataOut = new DataOutputStream(out);

					String header = headerReader.readLine();
					StringTokenizer strk = new StringTokenizer(header, " ");

					String command = strk.nextToken();

					if (command.equals("download")) {
						try {
							String fileName = strk.nextToken();
							FileInputStream fileIn = new FileInputStream("ServerShare/" + fileName);
							int fileSize = fileIn.available();
							header = "OK " + fileSize + "\n";

							headerWriter.write(header, 0, header.length());
							headerWriter.flush();

							byte[] bytes = new byte[fileSize];
							fileIn.read(bytes);

							fileIn.close();

							dataOut.write(bytes, 0, fileSize);

						} catch (Exception ex) {
							headerWriter.write(errorMessage1, 0, errorMessage1.length());
							headerWriter.flush();

						} finally {
							connectionFromClient.close();
						}
					} else if (command.equals("check")) {
						try {
							String fileName = strk.nextToken();
							FileInputStream fileIn = new FileInputStream("ServerShare/" + fileName);
							int fileSize = fileIn.available();
							String clientCkecksum = strk.nextToken();
							// the following line reads the bytes from the file that has the similar name in
							// the servershare
							byte[] data = Files.readAllBytes(Paths.get("ServerShare/" + fileName));
							// this line calculates the SHA-256 checksum for the file in the servershare
							byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
							// convert the hash to hexadecimal bytes
							String serverChecksum = new BigInteger(1, hash).toString(16);
							// the follwing code checks if the file are the same from there checksum value
							if (serverChecksum.equals(clientCkecksum)) {
								header = "DUPLICATED FILE" + "\n";
								headerWriter.write(header, 0, header.length());
								headerWriter.flush();
							} else {
								// if there are not the same, we download the file
								header = "NOT_SAME " + fileSize + "\n";

								headerWriter.write(header, 0, header.length());
								headerWriter.flush();

								byte[] bytes = new byte[fileSize];
								fileIn.read(bytes);

								fileIn.close();

								dataOut.write(bytes, 0, fileSize);
							}

						} catch (Exception ex) {
							headerWriter.write(errorMessage1, 0, errorMessage1.length());
							headerWriter.flush();

						} finally {
							connectionFromClient.close();
						}

					} else if (command.equals("upload")) {

						// To do
						String fileName = strk.nextToken();
						String fileSize = strk.nextToken();
						long size = Long.parseLong(fileSize);

						byte[] space = new byte[(int) size];

						dataIn.readFully(space);

						try (FileOutputStream fileOut = new FileOutputStream("ServerShare/" + fileName)) {
							fileOut.write(space, 0, (int) size);
							fileOut.close();
						}

					} else if (command.equals("getList")) {

						File sharableFiles = new File("ServerShare/");
						// the following code stores all shareable files in an array
						File[] Flist = sharableFiles.listFiles();
						// the following code check if there is no shareable files in the servershare
						// directory
						if (Flist == null || Flist.length == 0) {
							// if true, it sends the following header to the client
							header = "NO SHAREABLE FILES" + "\n";
							headerWriter.write(header, 0, header.length());
							headerWriter.flush();
						} else {// if not, it informs the server that there are some shareable files
							header = "OK" + "\n";
							headerWriter.write(header, 0, header.length());
							headerWriter.flush();
							for (File file : Flist) {// for each file in the file list array, we checkk if file is not a
														// directory for security reasons
								if (file.isFile()) {
									// then the list for shareable files to the client
									printout.println(file.getName());
								}
							}
						}
						// after the list of all shareable files are sent, close the output stream
						out.close();

					} else if (command.equals("resume")) {
						try {
							String fileName = strk.nextToken();
							FileInputStream fileIn = new FileInputStream("ServerShare/" + fileName);
							int fileSize = fileIn.available();
							// get an info about the already downlowded bytes
							long dBytes = Long.valueOf(strk.nextToken());
							// the following if, checks if the file is already downloaded
							if (dBytes == fileSize) {
								headerWriter.write("ALREADY_DOWNLOADED");
								headerWriter.flush();
							} else {
								header = "RESUME " + (fileSize - dBytes) + "\n";

								headerWriter.write(header, 0, header.length());
								headerWriter.flush();
								// read the reamining bytes of the file to the buffer
								byte[] buffer_bytes = new byte[2048];
								int bytesToRead = fileIn.read(buffer_bytes);
								// continue reading until it reaches the end of the file
								while (bytesToRead != -1) {
									dataOut.write(buffer_bytes, 0, bytesToRead);
								}
								fileIn.close();
							}

						} catch (Exception ex) {
							headerWriter.write(errorMessage1, 0, errorMessage1.length());
							headerWriter.flush();

						} finally {
							connectionFromClient.close();
						}
					} else {

						System.out.println("Connection got from an incompatible client");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
