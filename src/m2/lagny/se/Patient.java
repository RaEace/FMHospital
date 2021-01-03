package m2.lagny.se;

public class Patient {
  private String name;
  private boolean cured;

  public Patient(String name) {
    this.name = name;
    this.cured = false;
  }

  public boolean joinNewService(EmergencyCareService esc) throws InterruptedException {
    return esc.addPatient(this);
  }

  public boolean getCured() {
    return this.cured;
  }

  public void setCured() {
    this.cured = true;
  }

  @Override
  public String toString() {
    return name;
  }
}
