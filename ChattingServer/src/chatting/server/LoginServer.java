package chatting.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

public class LoginServer {

  private static PrintWriter writer;

  public static synchronized void findByUserIdAndPwd(String userId, String pwd,
      Socket clientSocket) {

    try (BufferedReader br = new BufferedReader(new FileReader("account.txt"))) {
      writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

      String line = "";

      String tempId;
      String tempPwd;
      String tempName;

      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);

        tempId = st.nextToken();
        tempPwd = st.nextToken();
        tempName = st.nextToken();

        System.out.println(tempId + " " + tempPwd + " " + tempName);
        if (userId.equals(tempId) && pwd.equals(tempPwd)) {
          writer.println(tempName);
          System.out.println(tempName);
          writer.flush();
          return;
        }
      }

      writer.println("nothing");
      System.out.println("아이디가 없음");
      writer.flush();
    } catch (FileNotFoundException e) {
      System.out.println("로그인 파일이 없습니다 loginService");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("로그인 데이터 읽는 도중 에러가 걸렸습니다. loginService");
      e.printStackTrace();
    }
  }
}
