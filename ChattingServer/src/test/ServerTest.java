package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.Test;


public class ServerTest {

  @Test
  public void test() throws IOException {

    ServerSocket server = new ServerSocket(5001);
    System.out.println("before accept()");
    Socket clientSock = server.accept();
    System.out.println("after accept()");
    BufferedReader br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
    String line = "";
    while ((line = br.readLine()) != null) {
      System.out.println(line);
    }
    PrintWriter writer = new PrintWriter(new OutputStreamWriter(clientSock.getOutputStream()));
    writer.println("보낸다");
    server.close();

  }

}
