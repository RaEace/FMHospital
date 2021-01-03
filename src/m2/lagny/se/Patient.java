package m2.lagny.se;

public class Patient {
  private final String name;
  private boolean cured;

  public Patient(String name) {
    this.name = name;
    this.cured = false;
  }

  public boolean joinEmergencyCareService(EmergencyCareService emergencyCareService) throws InterruptedException {
    return emergencyCareService.addPatient(this);
  }

  public void cure() {
    this.cured = true;
  }

  @Override
  public String toString() {
    return name;
  }
}
