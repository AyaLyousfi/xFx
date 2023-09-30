
import java.net.*;
import java.io.*;
import java.util.StringTokenizer;

class xFxClient {
	public static void main(String[] args) throws Exception {
		String command = args[0];
	    // String fileName = args[1];

		try (Socket connectionToServer = new Socket("localhost", 80)) {

			// I/O operations

			InputStream in = connectionToServer.getInputStream();
			OutputStream out = connectionToServer.getOutputStream();

			BufferedReader headerReader = new BufferedReader(new InputStreamReader(in));
			BufferedWriter headerWriter = new BufferedWriter(new OutputStreamWriter(out));
			DataInputStream dataIn = new DataInputStream(in);
			DataOutputStream dataOut = new DataOutputStream(out);

			if (command.equals("d")) {
				// // String header = "download " + fileName + "\n";
				// headerWriter.write(header, 0, header.length());
				// headerWriter.flush();

				// header = headerReader.readLine();

				// if (header.equals("NOT FOUND")) {
				// 	System.out.println("We're extremely sorry, the file you specified is not available!");
				// } else {
				// 	StringTokenizer strk = new StringTokenizer(header, " ");

				// 	String status = strk.nextToken();

				// 	if (status.equals("OK")) {

				// 		String temp = strk.nextToken();

				// 		int size = Integer.parseInt(temp);

				// 		byte[] space = new byte[size];

				// 		dataIn.readFully(space);

				// 		try (FileOutputStream fileOut = new FileOutputStream("ClientShare/" + fileName)) {
				// 			fileOut.write(space, 0, size);
				// 		}
				System.out.println("heeeeeeeeeee");

				// 	} else {
				// 		System.out.println("You're not connected to the right Server!");
				// 	}

				// }

			} else if (command.equals("u")) {
				// To do
				// File fUpload = new File("ClientShare/" + fileName);
				// long fileSize = fUpload.length();
				// String header = "upload "+ fileName + " " + fileSize + "\n";

				// headerWriter.write(header, 0, header.length());
				// headerWriter.flush();

				// FileInputStream fileIn = new FileInputStream(fUpload);

				// byte[] bytes = new byte[(int)fileSize];
				// fileIn.read(bytes);

				// fileIn.close();

				// dataOut.write(bytes, 0, (int)fileSize);


			} else if (command.equals("g")) {
				String header = "getList "+"\n"; 
				headerWriter.write(header, 0, header.length());
				headerWriter.flush();

                header = headerReader.readLine();
				// System.out.println("Received Response: " + header);
				if(header.equals("NO SHAREABLE FILES")){
					System.out.println("Sorry! There arew no shareable files in the shareable directory.");
				}else if (header.equals("OK")){
					String fName;
					int i = 1;
					while ((fName = headerReader.readLine()) != null) {	
						System.out.println("File " + i+": "+ fName);
						i++;
					}
                   
					
				}


			}else {
				System.out.println("Please follow the following instructions if you want to download or upload a file:\n"+
				"1. To download type: d FileName\n2.To Uplode type: u FileName");
			}
		}
		
	}
}
