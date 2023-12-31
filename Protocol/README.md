 # xFx Protocol
The client opens a connection with the server and *informs* the server whether it wants to *download*, *upload*, *getlist*, or *resume* a file using a *header*.

## Download
If the client wants to download a file, It checks if it has a file with the same name; 
- If it does, then the client will display a message asking the end user to use the check command in order to check if the two files has the same content.
- If not, then the header will be as following:
  - **download[one space][file name][Line Feed]**   
Upon receiving this header, the server searches for the specified file.
- If the file is not found, then the server shall reply with a header as the following:
  - **NOT[one space]FOUND[Line Feed]**
- If the file is found, then the server shall reply
  - with a header as the following:
    - **OK[one space][file size][Line Feed]**
  - followed by the bytes of the file

## Upload
If the client wants to upload a file, then the header will be as the following:
- **upload[one space][file name][one space][file size][Line Feed]**

After sending the header, the client shall send the bytes of the file

## Get List
If the client wants to get list of files shareable by server, then the header will be as following:
- **getList[Line Feed]**
When the server receives this header, it searches for the list of shareable files.
- If there are no files shared files, then the server shall reply with the follwing header:
  - **NO[one space]SHAREABLE[one space]FILES[Line Feed]**
- If the client attempt to resume an already downloaded file, then the server shall reply with the following header:
  - **ALREADY_DOWNLOADED[Line Feed]**   
- If it exists, the server shall reply with the following header:
  - **OK[Line Feed]** 
  - then the server returns a list of these file names through the Socket API. Afterward, the client shall parse the received list from the server.
  
   
## Resume
If the client wants to resume the downloading of a file where it stops, in case there was a network connectivity issue, then the header will be as following:
-**resume[one space][file name][one space][downloded bytes][Line Feed]**

Upon receiving this header, then the server searches for the file specified file.
- If the file is not found, then the server shall reply with the following header:
  - **NOT[one space]FOUND[Line Feed]**
- If the file is found, then the server shall reply with a header as provided below:
  - **RESUME[one space][file size][Line Feed]**
  - followed by the remaning bytes needed to download the whole file.

## Check
If the client want to check if it is downloading the same file or not, it sends the following header to the server:
 - **check[one space][fileName][one space][clientChecksum][Line Feed]**
Upon receiving this header, the server searches for the specified file.
- If the file is not found, then the server shall reply with a header as the following:
  - **NOT[one space]FOUND[Line Feed]**
- If the file is found, the server shall reply with the following header if it has an equal checksum value as the client:
  - **DUPLICATED_FILE[Line Feed]**
- Else, it shall reply with download with a header as the following:
  - **NOT_SAME[one space][file size][Line Feed]**
  - followed by the bytes of the file
    
   
    
  

   
