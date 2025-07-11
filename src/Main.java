import fuzzyLogicProgram.FanController;

public class Main {
    public static void main(String[] args) {
        FanController controller = new FanController();


        // Пример 1: температура 27°C, влажность 45%
        System.out.println("=== Пример 1: температура 27°C, влажность 45% ===");
        double temp1 = 27.0;
        double hum1 = 45.0;
        double speed1 = controller.calculateFanSpeed(temp1, hum1);


        // Пример 2: температура 32°C, влажность 60%
        //System.out.println("\n\n=== Пример 2: температура 32°C, влажность 60% ===");
        //double temp2 = 17.0;
        //double hum2 = 60.0;
        //double speed2 = controller.calculateFanSpeed(temp2, hum2);

    }
}