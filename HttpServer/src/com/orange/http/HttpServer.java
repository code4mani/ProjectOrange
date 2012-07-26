package com.orange.http;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServer {
    public static void main(String [] args)
    {
         int i=1;
         System.out.println("********************************************************************************");
         System.out.println("******************************  HTTP SERVER ************************************");
         System.out.println("********************************************************************************");
         System.out.println("Server Started...");
         System.out.println("Waiting for connections...");
         try
         {

              ServerSocket s = new ServerSocket(80);
              for(;;)
              {
                   Socket incoming = s.accept();
                   System.out.println("New Client Connected with id " + i +" from "+incoming.getInetAddress().getHostName() );
                   System.out.println("");
                   System.out.println("REQUEST HEADER                                    ");
                   Thread t = new ThreadedServer(incoming,i);
                   i++;
                   t.start();
              }
         }
         catch(Exception e)
         {
              System.out.println("Error: " + e);
         }
    }

}


class ThreadedServer extends Thread {
	final static String CRLF = "";
	Socket incoming;
	int counter;
	
	public ThreadedServer (Socket s, int c) {
		incoming = s;
		counter = c;
	}
	
	public void run() {
		String statusLine = null;
		String contentTypeLine = null;
		String contentLength = null;
		String venderLine = "Server: Executer 1.1";
		String entityBody = null;
		BufferedReader in = null;
		PrintWriter out = null;
		OutputStream output = null;
		try {
			in = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			out = new PrintWriter(incoming.getOutputStream(), true);
			output=incoming.getOutputStream();
		}catch (UnknownHostException e) {
            System.err.println("Don't know about host.");
            System.exit(1);
        } catch (IOException e) {
        	System.err.println("Couldn't get I/O for " + "the connection to: host");
        	System.exit(1);
		}
		
		String headerLine = null;
		try {
			headerLine = in.readLine();
		} catch (IOException e) {
			System.err.println("IO Exception : Could not able to read from the socket" + e.getMessage());
		}
		
		System.out.println(headerLine);
		/*String reqh = null;

        boolean done=false;
        while(!done)
        {
             try {
				reqh=in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             if(reqh == null)
                  done = true;
             else
             {
                  //out.println("Server>>> " + headerline);
                  System.out.println(reqh);
             }
        }*/
		
		StringTokenizer s = new StringTokenizer(headerLine);
        String meth = s.nextToken();
        if(meth.equals("GET")||meth.equals("POST"))
        {
        	int dot1,dot2,fslash;
        	String fname,ext,FileName;
        	String url = s.nextToken();

        	dot1=url.indexOf('.');
        	dot2=url.lastIndexOf('.');
        	fslash=url.lastIndexOf('/');
        	fname=url.substring(dot1+1,dot2);
        	ext=url.substring(dot2,fslash);
        	FileName=fname+ext;
        	//System.out.println("FNAME:"+FileName);
        	if(ext.equals(".html")||ext.equals(".htm"))
        	{
        		FileInputStream fis=null;
        		boolean filexists=true;
        		try
        		{
        			fis=new FileInputStream(FileName);
        		}
        		catch(FileNotFoundException e)
        		{
        			System.out.println("Exception: "+e.getMessage());
        			filexists=false;
        		}
        		if(filexists) {
        			statusLine=" HTTP/1.1 200 Ok"+CRLF;
        			contentTypeLine="Content-Type: text/html "+CRLF;
        			try {
						contentLength="Content-Length:"+(new Integer(fis.available())).toString() + CRLF;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		else {
	        		 statusLine = "HTTP/1.0 404 Not Found" + CRLF ;
	        		 contentTypeLine = "Content-Type: text/html"+CRLF ;
	        		 entityBody = "<HTML>" +
	                    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
	                    "<BODY><H1>404 File Not Found</H1></BODY></HTML>" ;
	        		 }
        		/*System.out.println(statusLine);
                System.out.println(venderLine);
                System.out.println(contentLength);
                System.out.println(contentTypeLine);*/

                 try {
					output.write(statusLine.getBytes());
					output.write(venderLine.getBytes());
	                 output.write(contentLength.getBytes());
	                 output.write(contentTypeLine.getBytes());
	                 output.write(CRLF.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                 
                 
                 if (filexists) {
                	 send_Bytes(fis, output) ;
                     try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 }
                 try {
					incoming.close();
					 in.close();
	                 out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	 }
        }
	}

	private void send_Bytes(FileInputStream fis1, OutputStream os) {
		 byte[] buffer = new byte[1024];
      	 int bytes = 0 ;
      	 try {
			while ((bytes = fis1.read(buffer)) != -1 ) {
				 os.write(buffer, 0, bytes);
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
