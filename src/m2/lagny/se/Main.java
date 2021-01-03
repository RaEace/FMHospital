package m2.lagny.se;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialization of a new Provider
            Provider provider = new Provider();

            //
            EmergencyCareService reanimation = new EmergencyCareService('Réanimation', provider);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
