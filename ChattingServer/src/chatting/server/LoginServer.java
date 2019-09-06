package chatting.server;

import chatting.domain.Account;
import chatting.domain.Room;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

public class LoginServer {

  /**
   * 로그인 확인해주고 클라이언트에게 이름을 반환하는 함수.
   * 
   * @param userId 유저 아이디
   * @param pwd 패스워드
   * @param clientSocket 클라이언트 소켓
   * @param writer 클라이언트 출력 소켓 버퍼
   */
  public static synchronized void findByUserIdAndPwd(String userId, String pwd, Socket clientSocket,
      PrintWriter writer) {

    try (BufferedReader br = new BufferedReader(new FileReader("account.txt"))) {

      // 아이디가 이미 존재할 경우
      if (MainServer.getUserInfo().containsKey(userId)) {
        writer.println("exist");
        writer.flush();
        return;
      }
      String line = "";

      String tempId;
      String tempPwd;
      String tempName;


      while ((line = br.readLine()) != null) {
        StringTokenizer st = new StringTokenizer(line);

        tempId = st.nextToken();
        tempPwd = st.nextToken();
        tempName = st.nextToken();

        // 아이디와 비번이 맞으면 처리
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
      writer.println("nothing");
      writer.flush();
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("로그인 데이터 읽는 도중 에러가 걸렸습니다. loginService");
      writer.println("nothing");
      writer.flush();
      e.printStackTrace();
    }
  }

  /**
   * 클라이언트 강제종료로 인해 소켓이 강제로 끊어졌을 경우 유저아이디의 모든 채팅방을 나가게 만드는 메소드 입니다.
   * 
   * @param userId 끊어진 유저 아이디.
   */
  public static void removeUserFromProgram(String userId) {

    // 채팅방에 남아있을 경우에 다 삭제해준다.
    if (MainServer.getUserInfo().containsKey(userId)) {
      List<Room> data = MainServer.getUserInfo().get(userId);
      System.out.println("remove data " + data.size());

      for (Room room : data) {
        // 계정접속 제거
        Account account = new Account(userId, room);
        MainServer.getUser().remove(account);

        // 방에서 퇴장, 방에 사람이 없을 경우 방도 삭제
        room.getAccounts().remove(userId);
        if (room.getAccounts().size() == 0) {
          MainServer.getRooms().remove(room.getRoomId());
        } else {
          room.setCount(room.getCount() - 1);
        }

        MainServer.getUserInfo().remove(userId);
      }
    }
  }

  /**
   * 회원가입 데이터 저장 메소드입니다.
   * 
   * @param userId 유저 아이디
   * @param pwd 패스워드
   * @param name 이름
   * @param writer 클라이언트 소켓 출력스트림
   */
  public static synchronized void signUp(String userId, String pwd, String name,
      PrintWriter writer) {

    System.out.println("signup()");

    // 파일 존재 확인
    checkFile();
    try (BufferedWriter bw = new BufferedWriter(new FileWriter("account.txt", true))) {
      if (alreadyExisted(userId)) {
        writer.println("exist");
        return;
      }
      bw.write(userId + " " + pwd + " " + name + "\n");
      writer.println("success");
      writer.flush();
    } catch (IOException e) {
      writer.println("fail");
      e.printStackTrace();
    }

  }

  private static void checkFile() {

    try {
      Path file = Paths.get("account.txt");
      if (!Files.exists(file)) {
        Files.createFile(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  private static boolean alreadyExisted(String userId) {

    try (BufferedReader br = new BufferedReader(new FileReader("account.txt"))) {
      String line = "";
      StringTokenizer lineSplit;
      while ((line = br.readLine()) != null) {
        lineSplit = new StringTokenizer(line);
        if (userId.equals(lineSplit.nextToken())) {
          return true;
        }
      }
      return false;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
