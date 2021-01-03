package m2.lagny.se;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialization of a new Provider
            Provider provider = new Provider();

            // Initialization of the Reanimation Service's
            EmergencyCareService reanimation = new EmergencyCareService("Reanimation", provider);
            reanimation.addRooms(2);
            reanimation.addPhysicians(2);
            reanimation.addNurses(2);

            ArrayList<Patient> reanimationPatients = new ArrayList<Patient>();
            reanimationPatients.add(new Patient("Jules"));
            reanimationPatients.add(new Patient("Camille"));

            // Initialization of the Surgery Service's
            EmergencyCareService surgery = new EmergencyCareService("Surgery", provider);
            reanimation.addNurses(2);

            ArrayList<Patient> surgeryPatients = new ArrayList<Patient>();
            surgeryPatients.add(new Patient("Neymar"));
            surgeryPatients.add(new Patient("Verratti"));
            surgeryPatients.add(new Patient("Kimpembe"));

            // Initialize a Thread Array
            ArrayList<Thread> patientsThreads = new ArrayList<Thread>();

            reanimationPatients.forEach((patient -> {
                patientsThreads.add(
                        new Thread() {
                            public void run() {
                                try {
                                    patient.joinNewService(reanimation);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }));

            surgeryPatients.forEach((patient -> {
                patientsThreads.add(
                        new Thread() {
                            public void run() {
                                try {
                                    patient.joinNewService(surgery);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }));

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
