package chatting.domain;

import java.util.ArrayList;
import java.util.List;

public class Room {

  private static long increment = 1;

  private long roomId;

  private String name;

  private int count;

  private List<Account> users;

  private boolean isPrivate;

  public void addCount() {

    this.count++;
  }

  public void minusCount() {

    this.count--;
  }

  public static long getIncrement() {

    return increment++;
  }



  public static void setIncrement(long increment) {

    Room.increment = increment;
  }


  public Room() {

    this.users = new ArrayList<>();
  }


  public long getRoomId() {

    return roomId;
  }


  public int getCount() {

    return count;
  }


  public List<Account> getUsers() {

    return users;
  }


  public boolean isPrivate() {

    return isPrivate;
  }


  public void setRoomId(long roomId) {

    this.roomId = roomId;
  }


  public void setCount(int count) {

    this.count = count;
  }


  public void setPrivate(boolean isPrivate) {

    this.isPrivate = isPrivate;
  }


  public String getName() {

    return name;
  }


  public void setName(String name) {

    this.name = name;
  }


}
