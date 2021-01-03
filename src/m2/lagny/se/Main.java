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

			ArrayList<Patient> reanimationPatients = new ArrayList<>();
			reanimationPatients.add(new Patient("Jules"));
			reanimationPatients.add(new Patient("Camille"));

			// Initialization of the Surgery Service's
			EmergencyCareService surgery = new EmergencyCareService("Surgery", provider);
			surgery.addNurses(3);

			ArrayList<Patient> surgeryPatients = new ArrayList<>();
			surgeryPatients.add(new Patient("Neymar"));
			surgeryPatients.add(new Patient("Verratti"));
			surgeryPatients.add(new Patient("Kimpembe"));

			// Initialize a Thread Array
			ArrayList<Thread> patientsThreads = new ArrayList<>();

			reanimationPatients.forEach((patient -> {
				patientsThreads.add(
					new Thread(() -> {
						try {
							patient.joinEmergencyCareService(reanimation);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					})
				);
			}));

			surgeryPatients.forEach((patient -> {
				patientsThreads.add(
					new Thread(() -> {
						try {
							patient.joinEmergencyCareService(surgery);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					})
				);
			}));

			patientsThreads.forEach(Thread::start);

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
