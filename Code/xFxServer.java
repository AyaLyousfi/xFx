
import java.net.*;
import java.nio.file.Files;
import java.io.*;
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
					PrintWriter pWriter = new PrintWriter(out, true);

					DataInputStream dataIn = new DataInputStream(in);
					DataOutputStream dataOut = new DataOutputStream(out);

					String header = headerReader.readLine();
					StringTokenizer strk = new StringTokenizer(header, " ");

					String command = strk.nextToken();

					//String fileName = strk.nextToken();

					if (command.equals("download")) {
						// try {
						// 	FileInputStream fileIn = new FileInputStream("ServerShare/" + fileName);
						// 	int fileSize = fileIn.available();
						// 	header = "OK " + fileSize + "\n";

						// 	headerWriter.write(header, 0, header.length());
						// 	headerWriter.flush();

						// 	byte[] bytes = new byte[fileSize];
						// 	fileIn.read(bytes);

						// 	fileIn.close();

						// 	dataOut.write(bytes, 0, fileSize);

						// } catch (Exception ex) {
						// 	headerWriter.write(errorMessage1, 0, errorMessage1.length());
						// 	headerWriter.flush();

						// } finally {
						// 	connectionFromClient.close();
						// }
					} else if (command.equals("upload")) {

						// To do
						// String fileSize = strk.nextToken();
                        // long size = Long.parseLong(fileSize);

						// byte[] space = new byte[(int)size];

						// dataIn.readFully(space);

						// try (FileOutputStream fileOut = new FileOutputStream("ServerShare/" + fileName)) {
						// 	fileOut.write(space, 0, (int)size);
						// 	fileOut.close();
						// }


					} else if (command.equals("getList")) {
						
							File sharableFiles = new File("C:/Users/AYA/Downloads/fx - Copy/ServerShare");
							File[] Flist= sharableFiles.listFiles();
							if (Flist == null || Flist.length == 0) {
								header = "NO SHAREABLE FILES" +"\n";
								headerWriter.write(header, 0, header.length());
								headerWriter.flush();
							} else {
								header = "OK" +"\n";
								headerWriter.write(header, 0, header.length());
								headerWriter.flush();
								for(File file: Flist) { 
									if(file.isFile()){
										pWriter.println(file.getName());
									}
								}
							}	
						
							out.close();

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
