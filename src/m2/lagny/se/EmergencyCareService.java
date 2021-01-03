package m2.lagny.se;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class EmergencyCareService {
  private String ServiceName;
  private Provider provider;

  private Semaphore semPhysician;
  private Semaphore semRooms;
  private Semaphore semNurses;

  private ArrayList<Patient> patients;
  private ArrayList<Patient> patientsNoPaperInWR;
  private ArrayList<Patient> patientSPaperInWR;
  private ArrayList<Patient> patientsInRoom;

  public void PatientIsAccepted() {

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
