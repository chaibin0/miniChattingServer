package chatting.server;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import chatting.domain.Account;

public class ChatServer {

  public static void sendMessage(String userId, String roomNumber, String message) {

    try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream("roomin.bin"))) {
      Account account = null;
      while ((account = (Account) reader.readObject()) != null) {
        if (account.getNumber().equals(roomNumber)) {
          System.out.println(account.getAddress().getHostAddress());
          System.out.println(account.getPort());
          try (Socket socket = new Socket(account.getAddress(), account.getPort());
              BufferedWriter writer =
                  new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            writer.write("sendMessage " + userId + " " + roomNumber + " " + message);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
}
