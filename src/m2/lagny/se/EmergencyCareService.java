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
      System.out.println("(" + serviceName + ") | " + patient + " entered in service and go to waiting-room");
      this.patientsNoPaperInWR.add(patient);
    }
    else {
      System.out.println("(" + serviceName + ") | " + patient + " canno't join the service");
    }
    this.patients.remove(patient);
  }

  public void PatientIsWaitingWithoutPaper() {

  }

  public void PatientIsFillingPaper() {

  }

  public void NurseIsProcessingPaper() {

  }

  public void PatientIsInRoom() {

  }

  public void PhysicianIsExaminatingPatient() {

  }

  public void PatientIsNowCuredAndCheckOut() {

  }
}
