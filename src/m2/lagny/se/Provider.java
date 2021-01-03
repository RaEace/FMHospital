package m2.lagny.se;

import java.util.concurrent.Semaphore;

public class Provider {
  private Semaphore semRooms;
  private Semaphore semPhysicians;
  private Semaphore semLock;

  public Provider() {
    this.semRooms = new Semaphore(0);
    this.semPhysicians = new Semaphore(0);
    this.semLock = new Semaphore(1);
  }

  public void getARoom(String service) throws InterruptedException {
    this.semLock.acquire();
    this.semRooms.release();
    System.out.println("(Provider) | get a room from service : " + service);
    this.semLock.release();
  }

  public boolean givingARoom(String service) throws InterruptedException {
    this.semLock.acquire();
    if(semRooms.tryAcquire()) {
      System.out.println("(Provider) | send a room to service : " + service);
      semLock.release();
      return true;
    } else {
      System.out.println("(Provider) | error : no room available");
      semLock.release();
      return false;
    }
  }

  public void getAPhysician(String service) throws InterruptedException {
    this.semLock.acquire();
    this.semPhysicians.release();
    System.out.println("(Provider) | get a physician from service : " + service);
    this.semLock.release();
  }

  public boolean givingAPhysician() throws InterruptedException {
    this.semLock.acquire();
    if(semRooms.tryAcquire()) {
      semLock.release();
      return true;
    } else {
      semLock.release();
      return false;
    }
  }
}
