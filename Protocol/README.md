 # xFx Protocol
The client opens a connection with the server and *informs* the server whether it wants to *download* or *upload* a file using a *header*.

## Download
If the client wants to download a file, then the header will be as the following:
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

## Get Shareable Files
If the client wants to get list of files shareable by server, then the header will be as following:
- **shareable[one space]files[Line Feed]**
When the server receives this header, it looks for the list of shareable files.
- If there are no files shared files, then the server shall reply with the follwing header:
  - **NO[one space]SHAREABLE[one space]FILES[Line Feed]**
- If there is at least one shareble file, then the server shall reply a header as the following:
  -**SHAREABLE[one space]FILES:[Line Feed]**

## Resume The Downloading
If the client wants to resume the downloading of a file where it stops, in case there was a network connectivity issue, then the header will be as following:
   
