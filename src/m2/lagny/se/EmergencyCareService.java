package m2.lagny.se;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class EmergencyCareService {
  private String serviceName;
  private Provider provider;

  private Semaphore semPhysician;
  private Semaphore semRooms;
  private Semaphore semNurses;

  private ArrayList<Patient> patients;
  private ArrayList<Patient> patientsNoPaperInWR;
  private ArrayList<Patient> patientsPaperInWR;
  private ArrayList<Patient> patientsInRoom;

  public EmergencyCareService(String serviceName, Provider provider) {
    this.serviceName = serviceName;
    this.provider = provider;
    this.semPhysician = new Semaphore(0);
    this.semRooms = new Semaphore(0);
    this.semNurses = new Semaphore(0);
    this.patients = new ArrayList<Patient>();
    this.patientsNoPaperInWR = new ArrayList<Patient>();
    this.patientsPaperInWR = new ArrayList<Patient>();
    this.patientsInRoom = new ArrayList<Patient>();
  }

  public void PatientIsAccepted(Patient patient) {
    if (patients.contains(patient) && !patient.getCured()) {
      System.out.println("(" + this.serviceName + ") | " + patient + " entered in service and go to waiting-room");
      this.patientsNoPaperInWR.add(patient);
    }
    else System.out.println("(" + this.serviceName + ") | " + patient + " canno't join the service");
    this.patients.remove(patient);
  }

  public void PatientIsFillingPaper(Patient patient) {
    if(patientsNoPaperInWR.contains(patient)) {
      System.out.println("(" + this.serviceName + ") | " + patient + " is filling paper");
    } else System.out.println("(" + this.serviceName + ") | " + patient + " an error as occured");
  }

  public void NurseIsProcessingPaper(Patient patient) throws InterruptedException {
    if(patientsNoPaperInWR.contains(patient)) {
      while(!this.semNurses.tryAcquire()) {
        System.out.println("(" + this.serviceName + ") | No nurse available.. please wait");
        Thread.sleep(1000);
      }
      System.out.println("(" + this.serviceName + ") | A nurse is here for " + patient + " papers");
      this.patientsNoPaperInWR.remove(patient);
      this.patientsPaperInWR.add(patient);
    }
  }

  public void PatientIsInRoom(Patient patient) throws InterruptedException {
    if(patientsPaperInWR.contains(patient)) {
      System.out.println("(" + this.serviceName + ") | " + patient + " waiting to join a room");
      while (!this.semRooms.tryAcquire()) {
        Thread askForARoomToProvider = new Thread(() -> {
          try {
            if(provider.givingARoom()) {
              semRooms.release();
              System.out.println("(Provider) | send a room to service : " + serviceName);
            }
            else System.out.println("(Provider) | error : no room available");
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
        askForARoomToProvider.start();
        Thread.sleep(1000);
      }
      this.patientsPaperInWR.remove(patient);
      this.patientsInRoom.add(patient);
      this.semNurses.release();
    }
  }

  public void PhysicianIsExaminatingPatient(Patient patient) throws InterruptedException {
    if(patientsInRoom.contains(patient)) {
      System.out.println("(" + this.serviceName + ") | " + patient + " waiting for a physician");
      while (!this.semPhysician.tryAcquire()) {
        Thread askForAPhysicianToProvider = new Thread(() -> {
          try {
            if(provider.givingAPhysician()) {
              semPhysician.release();
              System.out.println("(Provider) | send a physician to service : " + serviceName);
            }
            else System.out.println("(Provider) | error : no physician available");
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
        askForAPhysicianToProvider.start();
        Thread.sleep(1000);
      }
      System.out.println("(" + this.serviceName + ") | " + patient + " is examinating by a physician");
      Thread.sleep(3000);
      patient.setCured();
      this.semPhysician.release();
    }
  }

  public void PatientIsNowCuredAndCheckOut() {

  }

  private void askForARoomToProvider() {

  }

  private void askForAPhysicianToProvider() {

  }

  private void sendARoomToProvider() {

  }

  private void sendAPhysicianToProvider() {

  }
}
