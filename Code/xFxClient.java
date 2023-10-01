
import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

class xFxClient {
	public static void main(String[] args) throws Exception {
		String command = args[0];

		try (Socket connectionToServer = new Socket("localhost", 80)) {

			// I/O operations

			InputStream in = connectionToServer.getInputStream();
			OutputStream out = connectionToServer.getOutputStream();

			BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
			BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));
			DataInputStream dataIn = new DataInputStream(in);
			DataOutputStream dataOut = new DataOutputStream(out);

			if (command.equals("d")) {
				String fileName = args[1];
				File fToDownload = new File("ClientShare/" + fileName);
				String header = "download " + fileName + "\n";

				headerWriter.write(header, 0, header.length());
				headerWriter.flush();

				header = headerReader.readLine();
				// first I check if the file specified already exist in ClientShare directory
				if (fToDownload.exists()) {
					System.out.println(
							"Please type c followed by filename to check if you want to download the same file.\n");
				}

				else if (header.equals("NOT FOUND")) {
					System.out.println("We're extremely sorry, the file you specified is not available!");
				} else {
					StringTokenizer strk = new StringTokenizer(header, " ");

					String status = strk.nextToken();

					if (status.equals("OK")) {

						String temp = strk.nextToken();

						int size = Integer.parseInt(temp);

						byte[] space = new byte[size];

						dataIn.readFully(space);

						try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(space, 0, size);
						}

					} else {
						System.out.println("You're not connected to the right Server!");
					}

				}

			} else if (command.equals("u")) {
				// To do
				String fileName = args[1];
				File fUpload = new File("ClientShare/" + fileName);
				long fileSize = fUpload.length();
				String header = "upload " + fileName + " " + fileSize + "\n";

				headerWriter.write(header, 0, header.length());
				headerWriter.flush();

				FileInputStream fileIn = new FileInputStream(fUpload);

				byte[] bytes = new byte[(int) fileSize];
				fileIn.read(bytes);

				fileIn.close();

				dataOut.write(bytes, 0, (int) fileSize);

			} else if (command.equals("g")) {
				// Send the following header o get list of shareable file between the client
				// and server
				String header = "getList " + "\n";
				headerWriter.write(header, 0, header.length());
				headerWriter.flush();
				// reading the header sent by the server
				header = headerReader.readLine();

				if (header.equals("NO SHAREABLE FILES")) {
					System.out.println("Sorry! There arew no shareable files in the shareable directory.");

				} else if (header.equals("OK")) {
					String fName;
					int i = 1;
					// reading each file name sent from the server side and print it
					while ((fName = headerReader.readLine()) != null) {
						System.out.println("File " + i + ": " + fName);
						i++;
					}
				}

			} else if (command.equals("r")) {
				String fileName = args[1];
				File fDownloaded = new File("ClientShare/" + fileName);
				// getting the downloaded bytes
				long downloadedBytes = fDownloaded.length();
				String header = "resume " + fileName + " " + downloadedBytes + "\n";

				headerWriter.write(header, 0, header.length());
				headerWriter.flush();
				header = headerReader.readLine();
				// Check in case the specified file is no long in the servershare directory
				if (header.equals("NOT FOUND")) {
					System.out.println("We're extremely sorry, the file you specified is not available!");
					// Check in case the file is already downloded but the end user entered a wrong
					// command or filename
				} else if (header.equals("ALREADY_DOWNLOADED")) {
					System.out.println(
							"The file is already downloaded.\nIs seems you entered the wrong file Name or wrong command");
					// if not we resume from the last byte dowloaded
				} else {
					StringTokenizer strk = new StringTokenizer(header, " ");

					String status = strk.nextToken();

					if (status.equals("RESUME")) {

						String temp = strk.nextToken();

						int size = Integer.parseInt(temp);

						byte[] space = new byte[size];

						dataIn.readFully(space);

						try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(space, 0, size);
						}
					} else {
						System.out.println("You're not connected to the right Server!");
					}

				}
			} else if (command.equals("c")) {
				String fileName = args[1];
				// the following line reads the bytes from the file to be downloaded
				byte[] data = Files.readAllBytes(Paths.get("ClientShare/" + fileName));
				// this line calculates the SHA-256 checksum, so that we can compare it with the
				// checksum of the same named file in the server directory
				byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
				// the following line convert the hash to hexadecimal string
				String clientChecksum = new BigInteger(1, hash).toString(16);
				// this is the header sent to the server to check for content similarity
				String header1 = "check " + fileName + " " + clientChecksum + "\n";
				headerWriter.write(header1, 0, header1.length());
				headerWriter.flush();
				// we read theresponse from the server
				header1 = headerReader.readLine();
				if (header1.equals("DUPLICATED FILE")) {
					System.out.println("No need to download the file, you already have it");
				} else {
					StringTokenizer strk = new StringTokenizer(header1, " ");

					String status = strk.nextToken();

					if (status.equals("NOT_SAME")) {

						String temp = strk.nextToken();

						int size = Integer.parseInt(temp);

						byte[] space = new byte[size];

						dataIn.readFully(space);

						try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
							fileOut.write(space, 0, size);
						}

					} else {
						System.out.println("You're not connected to the right Server!");
					}
				}
			} else {
				System.out.println(
						"Please follow the following instructions if you want to download or upload a file:\n" +
								"1. To download type: d FileName\n2.To Uplode type: u FileName\n3.To get list of shareable file: g\n4.To resume downloading: r fileName\n5.To check dirty copy: c filename");
			}
		}

	}

}
