package chatting.domain;

import java.net.InetAddress;

public class Account implements java.io.Serializable {

  /**
   * uid임.
   */
  private static final long serialVersionUID = -3760842113195021932L;

  String userId;

  String number;

  InetAddress address;

  int port;

  public Account(String userId) {

    this.userId = userId;
  }

  public Account(String userId, String number, InetAddress address, int port) {

    this.userId = userId;
    this.number = number;
    this.address = address;
    this.port = port;
  }



  public String getNumber() {

    return number;
  }


  public void setNumber(String number) {

    this.number = number;
  }


  public String getUserId() {

    return userId;
  }


  public void setUserId(String userId) {

    this.userId = userId;
  }



  public InetAddress getAddress() {

    return address;
  }



  public int getPort() {

    return port;
  }



  public void setAddress(InetAddress address) {

    this.address = address;
  }



  public void setPort(int port) {

    this.port = port;
  }

}
