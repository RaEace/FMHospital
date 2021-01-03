package m2.lagny.se;

public class Patient {
  private String name;
  private boolean cured;

  public Patient(String name, boolean cured) {
    this.name = name;
    this.cured = cured;
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
