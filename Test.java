public class Test {
    public static void main(String[] args) {
        // Coordonnées du vecteur
        double x = -109.0;
        double y = -114.0;

        // Calcul de l'angle en radians
        double angleRadians = Math.atan2(y, x);

        // Conversion de radians en degrés
        double angleDegrees = Math.toDegrees(angleRadians);

        System.out.println("Angle en radians: " + angleRadians / Math.PI);
        System.out.println("Angle en degrés: " + angleDegrees);
    }
}
