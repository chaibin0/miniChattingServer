package chatting.domain;

import java.util.HashSet;
import java.util.Set;

public class Room {

  private static long increment = 1;

  private long roomId;

  private String name;

  private int count;

  private Set<String> accounts = new HashSet<>();

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


  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (roomId ^ (roomId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Room)) {
      return false;
    }
    Room other = (Room) obj;
    if (roomId != other.roomId) {
      return false;
    }
    return true;
  }

  public long getRoomId() {

    return roomId;
  }


  public int getCount() {

    return count;
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

  public Set<String> getAccounts() {

    return accounts;
  }



}
