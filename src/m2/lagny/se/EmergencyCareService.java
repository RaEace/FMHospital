package m2.lagny.se;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class EmergencyCareService {
  private String serviceName;
  private Provider provider;

  // We have here 3 semaphore to modelize our ressources
  private Semaphore semPhysician;
  private Semaphore semRooms;
  private Semaphore semNurses;

  // differents states of the stacked patients through the hospital
  private ArrayList<Patient> patients;
  private ArrayList<Patient> patientsNoPaperInWR;
  private ArrayList<Patient> patientsPaperInWR;
  private ArrayList<Patient> patientsInRoom;

  public EmergencyCareService(String serviceName, Provider provider) throws InterruptedException {
    this.serviceName = serviceName;
    this.provider = provider;
    this.semPhysician = new Semaphore(0);
    this.semRooms = new Semaphore(0);
    this.semNurses = new Semaphore(0);
    this.patients = new ArrayList<Patient>();
    this.patientsNoPaperInWR = new ArrayList<Patient>();
    this.patientsPaperInWR = new ArrayList<Patient>();
    this.patientsInRoom = new ArrayList<Patient>();
    this.givePhysiciansToProvider();
    this.givingRoomsToProvider();
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

  // Methods to modelize the patient care in the hospital
  public boolean addPatient(Patient patient) throws InterruptedException {
    System.out.println("(" + this.serviceName + ") | " + patient + " arrived in this service");
    this.patients.add(patient);
    Thread.sleep(1000);
    this.patientIsAccepted(patient);
    Thread.sleep(1000);
    this.patientIsFillingPaper(patient);
    Thread.sleep(1000);
    this.nurseIsProcessingPaper(patient);
    Thread.sleep(1000);
    this.patientIsInRoom(patient);
    Thread.sleep(1000);
    this.physicianIsExaminatingPatient(patient);
    Thread.sleep(1000);
    this.patientIsNowCuredAndCheckOut(patient);

    return true;
  }

  // Method to determine if the patient is admitted during the check in, and if yes send to the waiting room
  public boolean patientIsAccepted(Patient patient) {
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
  public boolean patientIsFillingPaper(Patient patient) {
      System.out.println("(" + this.serviceName + ") | " + patient + " is filling paper");
      return true;
  }

  // method where a nurse process papers of a patient
  public boolean nurseIsProcessingPaper(Patient patient) throws InterruptedException {
      while(!this.semNurses.tryAcquire()) {
        System.out.println("(" + this.serviceName + ") | No nurse available.. please wait");
        Thread.sleep(1000); // 1s to ask for a nurse again
      }
      System.out.println("(" + this.serviceName + ") | A nurse is here for " + patient + " papers");
      this.patientsNoPaperInWR.remove(patient);
      this.patientsPaperInWR.add(patient);
      return true;
  }

  // Method where a client will be send in a room if there are some availables
  public boolean patientIsInRoom(Patient patient) throws InterruptedException {
      System.out.println("(" + this.serviceName + ") | " + patient + " waiting to join a room");
      while (!this.semRooms.tryAcquire()) {
        Thread askForARoom = new Thread(() -> {
          try {
            if(provider.givingARoom(serviceName)) {
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

  // Method where a patient will be examinate by a physician
  public boolean physicianIsExaminatingPatient(Patient patient) throws InterruptedException {
      System.out.println("(" + this.serviceName + ") | " + patient + " waiting for a physician");
      while(!this.semPhysician.tryAcquire()) {
        Thread askForAPhysician = new Thread(() -> {
          try {
            if (provider.givingAPhysician(serviceName)) {
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
  public boolean patientIsNowCuredAndCheckOut(Patient patient) {
    System.out.println("(" + this.serviceName + ") | " + patient + " is checking out and leaving");
    this.patientsInRoom.remove(patient);
    this.semRooms.release();
    return true;
  }

  // Method with a infinite loop to send room if a service doesn't need it
  private void givingRoomsToProvider() throws InterruptedException {
    Thread GivingRoomsToProvider = new Thread(() -> {
      while(true) {
        try {
          Thread.sleep(1000);
          if(patients.size() == 0 && patientsInRoom.size() == 0 && patientsPaperInWR.size() == 0 && patientsNoPaperInWR.size() == 0) {
            // Line up check if the hospital is empty
            semRooms.acquire();
            System.out.println("(" + this.serviceName + ") | sending a room to provider");
            provider.getARoom(serviceName);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    GivingRoomsToProvider.start();
  }

  // Method with a infinite loop to send physician if a service doesn't need it
  private void givePhysiciansToProvider() throws InterruptedException{
    Thread GivePhysiciansToProvider = new Thread(() -> {
      while(true) {
        try {
          Thread.sleep(1000);
          if(patients.size() == 0 && patientsInRoom.size() == 0 && patientsPaperInWR.size() == 0 && patientsNoPaperInWR.size() == 0) {
            // Line up check if the hospital is empty
            semPhysician.acquire();
            System.out.println("(" + this.serviceName + ") | sending a physician to provider");
            provider.getAPhysician(serviceName);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    GivePhysiciansToProvider.start();
  }
}
