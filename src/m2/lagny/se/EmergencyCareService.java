package m2.lagny.se;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class EmergencyCareService {
	private final String serviceName;
	private final Provider provider;

	// We have here 3 semaphore to model our resources
	private final Semaphore semPhysician;
	private final Semaphore semRooms;
	private final Semaphore semNurses;

	// different states of the stacked patients through the hospital
	private final ArrayList<Patient> patients;
	private final ArrayList<Patient> patientsNoPaperInWR;
	private final ArrayList<Patient> patientsPaperInWR;
	private final ArrayList<Patient> patientsInRoom;

	public EmergencyCareService(String serviceName, Provider provider) throws InterruptedException {
		this.serviceName = serviceName;
		this.provider = provider;
		this.semPhysician = new Semaphore(0);
		this.semRooms = new Semaphore(0);
		this.semNurses = new Semaphore(0);
		this.patients = new ArrayList<>();
		this.patientsNoPaperInWR = new ArrayList<>();
		this.patientsPaperInWR = new ArrayList<>();
		this.patientsInRoom = new ArrayList<>();
		this.freePhysicianWhenNotNeeded();
		this.freeRoomsWhenNotNeeded();
	}

	public void addRooms(int amount) {
		for(int i = 0; i < amount; i++) {
			this.semRooms.release();
		}
	}

	public void addPhysicians(int amount) {
		for(int i = 0; i < amount; i++) {
			this.semPhysician.release();
		}
	}

	public void addNurses(int amount) {
		for(int i = 0; i < amount; i++) {
			this.semNurses.release();
		}
	}

	// Methods to model the patient care in the hospital
	public boolean addPatient(Patient patient) throws InterruptedException {
		System.out.println("(" + this.serviceName + ") | " + patient + " arrived in this service");
		this.patients.add(patient);
		Thread.sleep(1000);
		this.attemptToAcceptPatient(patient);
		Thread.sleep(1000);
		this.makePatientFillPaperwork(patient);
		Thread.sleep(1000);
		this.makeNurseProcessPatientPaperwork(patient);
		Thread.sleep(1000);
		this.sendPatientInRoom(patient);
		Thread.sleep(1000);
		this.makePhysicianExaminePatient(patient);
		Thread.sleep(1000);
		this.finishCuringAndCheckingOutPatient(patient);

		return true;
	}

	// Method to determine if the patient is admitted during the check in, and if yes send to the waiting room
	public boolean attemptToAcceptPatient(Patient patient) {
		if (patients.contains(patient)) {
			System.out.println("(" + this.serviceName + ") | " + patient + " entered in service and go to waiting-room");
			this.patientsNoPaperInWR.add(patient);
			this.patients.remove(patient);
			return true;
		}
		else {
			System.out.println("(" + this.serviceName + ") | " + patient + " canno't join the service");
			this.patients.remove(patient);
			return false;
		}
	}

	// Method where a patient will fill his paper
	public boolean makePatientFillPaperwork(Patient patient) {
		System.out.println("(" + this.serviceName + ") | " + patient + " is filling paperwork");
		return true;
	}

	// method where a nurse process papers of a patient
	public boolean makeNurseProcessPatientPaperwork(Patient patient) throws InterruptedException {
		while(!this.semNurses.tryAcquire()) {
			System.out.println("(" + this.serviceName + ") | No nurse available.. please wait");
			Thread.sleep(1000); // 1s to ask for a nurse again
		}
		System.out.println("(" + this.serviceName + ") | A nurse is here for " + patient + " papers");
		this.patientsNoPaperInWR.remove(patient);
		this.patientsPaperInWR.add(patient);
		return true;
	}

	// Method where a patient will be send in a room if there are some available
	public boolean sendPatientInRoom(Patient patient) throws InterruptedException {
		System.out.println("(" + this.serviceName + ") | " + patient + " waiting to join a room");
		while (!this.semRooms.tryAcquire()) {
			Thread askForARoom = new Thread(() -> {
				try {
					if(provider.giveRoomToServiceIfPossible(serviceName)) {
						semRooms.release();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			askForARoom.start();
			Thread.sleep(1000); // 1s to ask for a nurse again
		}
		this.patientsPaperInWR.remove(patient);
		this.patientsInRoom.add(patient);
		this.semNurses.release();
		return true;
	}

	// Method where a patient will be examine by a physician
	public boolean makePhysicianExaminePatient(Patient patient) throws InterruptedException {
		System.out.println("(" + this.serviceName + ") | " + patient + " waiting for a physician");
		while(!this.semPhysician.tryAcquire()) {
			Thread askForAPhysician = new Thread(() -> {
				try {
					if (provider.givePhysicianToServiceIfPossible(serviceName)) {
						semPhysician.release();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			askForAPhysician.start();
			Thread.sleep(1000);
		}
		System.out.println("(" + this.serviceName + ") | " + patient + " is examinating by a physician");
		Thread.sleep(3000);
		patient.cure();
		this.semPhysician.release();
		return true;
	}

	// Method where a patient is now cured, and gonna checking out
	public boolean finishCuringAndCheckingOutPatient(Patient patient) {
		System.out.println("(" + this.serviceName + ") | " + patient + " is checking out and leaving");
		this.patientsInRoom.remove(patient);
		this.semRooms.release();
		return true;
	}

	// Method with a infinite loop to send room if a service doesn't need it
	private void freeRoomsWhenNotNeeded() {
		Thread GivingRoomsToProvider = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1000);
					if(patients.size() == 0 && patientsInRoom.size() == 0 && patientsPaperInWR.size() == 0 && patientsNoPaperInWR.size() == 0) {
						// Line up check if the hospital is empty
						semRooms.acquire();
						System.out.println("(" + this.serviceName + ") | sending a room to provider");
						provider.getARoomForService(serviceName);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		GivingRoomsToProvider.start();
	}

	// Method with a infinite loop to send physician if a service doesn't need it
	private void freePhysicianWhenNotNeeded() {
		Thread GivePhysiciansToProvider = new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(1000);
					if(patients.size() == 0 && patientsInRoom.size() == 0 && patientsPaperInWR.size() == 0 && patientsNoPaperInWR.size() == 0) {
						// Line up check if the hospital is empty
						semPhysician.acquire();
						System.out.println("(" + this.serviceName + ") | sending a physician to provider");
						provider.getAPhysicianForService(serviceName);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		GivePhysiciansToProvider.start();
	}
}
