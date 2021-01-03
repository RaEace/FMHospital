package m2.lagny.se;

import java.util.concurrent.Semaphore;

public class Provider {
	// semaphore who represent the states of the resource of the provider
	private final Semaphore semRooms;
	private final Semaphore semPhysicians;
	private final Semaphore semLock;

	// initialize resource of the provider
	public Provider() {
		this.semRooms = new Semaphore(0);
		this.semPhysicians = new Semaphore(0);
		this.semLock = new Semaphore(1);
	}

	// Method where the provider will receive a room from a service
	public void getARoomForService(String service) throws InterruptedException {
		this.semLock.acquire();
		this.semRooms.release();
		System.out.println("(Provider) | get a room from service : " + service);
		this.semLock.release();
	}

	// Method who will send a room to a service
	// if possible => decrease semRooms by one and return true
	// if none => return false
	public boolean giveRoomToServiceIfPossible(String service) throws InterruptedException {
		this.semLock.acquire();
		if(semRooms.tryAcquire()) {
			System.out.println("(provider) give a room to service : " + service);
			semLock.release();
			return true;
		} else {
			semLock.release();
			return false;
		}
	}

	// Method where the provider will receive a physician from a service
	public void getAPhysicianForService(String service) throws InterruptedException {
		this.semLock.acquire();
		this.semPhysicians.release();
		System.out.println("(Provider) | get a physician from service : " + service);
		this.semLock.release();
	}

	// Method who will send a physician to a service
	// if possible => decrease semPhysicians by one and return true
	// if none => return false
	public boolean givePhysicianToServiceIfPossible(String service) throws InterruptedException {
		this.semLock.acquire();
		if(semPhysicians.tryAcquire()) {
			System.out.println("(provider) give a physician to service : " + service);
			semLock.release();
			return true;
		} else {
			semLock.release();
			return false;
		}
	}
}
