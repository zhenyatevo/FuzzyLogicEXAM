package fuzzyLogicProgram;

import java.util.HashMap;
import java.util.Map;

//Контроллер управления вентилятором на основе нечеткой логики
public final class FanController {
    private final FuzzySystem fuzzySystem;
    private final LinguisticVariable temperature;
    private final LinguisticVariable humidity;
    private final LinguisticVariable fanSpeed;

    public FanController() {
        // Создаем лингвистические переменные
        this.temperature = createTemperatureVariable();
        this.humidity = createHumidityVariable();
        this.fanSpeed = createFanSpeedVariable();

        // Инициализируем нечеткую систему
        this.fuzzySystem = new FuzzySystem(fanSpeed, 0, 100);
        this.fuzzySystem.addInputVariable(temperature);
        this.fuzzySystem.addInputVariable(humidity);

        // Добавляем правила
        createRules();
    }

    // Создание лингвистической переменной для температуры
    private LinguisticVariable createTemperatureVariable() {
        LinguisticVariable temp = new LinguisticVariable("temperature");
        // Термы с их функциями принадлежности:
        temp.addTerm("low", x -> MembershipFunctions.decreasingLinear(x, 15, 22));
        temp.addTerm("medium", x -> MembershipFunctions.triangular(x, 20, 25, 30));
        temp.addTerm("high", x -> MembershipFunctions.increasingLinear(x, 28, 35));
        return temp;
    }

    // Создание лингвистической переменной для влажности
    private LinguisticVariable createHumidityVariable() {
        LinguisticVariable hum = new LinguisticVariable("humidity");
        hum.addTerm("low", x -> MembershipFunctions.decreasingLinear(x, 30, 50));
        hum.addTerm("medium", x -> MembershipFunctions.triangular(x, 40, 55, 70));
        hum.addTerm("high", x -> MembershipFunctions.increasingLinear(x, 60, 90));
        return hum;
    }

    // Создание лингвистической переменной для скорости вентилятора
    private LinguisticVariable createFanSpeedVariable() {
        LinguisticVariable speed = new LinguisticVariable("fan_speed");
        speed.addTerm("off", x -> MembershipFunctions.decreasingLinear(x, 0, 30));
        speed.addTerm("medium", x -> MembershipFunctions.triangular(x, 20, 50, 80));
        speed.addTerm("fast", x -> MembershipFunctions.triangular(x, 60, 75, 90));
        speed.addTerm("max", x -> MembershipFunctions.increasingLinear(x, 70, 100));
        return speed;
    }

    // Создание базы правил нечеткой системы
    private void createRules() {
        // Правило 1: Если температура низкая и влажность высокая → выключен
        fuzzySystem.addRule(new FuzzyRule("temperature", "low", "humidity", "high", "off"));

        // Правило 2: Если температура средняя и влажность средняя → средняя скорость
        fuzzySystem.addRule(new FuzzyRule("temperature", "medium", "humidity", "medium", "medium"));

        // Правило 3: Если температура высокая и влажность низкая → максимальная скорость
        fuzzySystem.addRule(new FuzzyRule("temperature", "high", "humidity", "low", "max"));

        // Правило 4: Если температура высокая и влажность средняя → быстрая скорость
        fuzzySystem.addRule(new FuzzyRule("temperature", "high", "humidity", "medium", "fast"));

        // Правило 5: Если температура средняя и влажность низкая → средняя скорость
        fuzzySystem.addRule(new FuzzyRule("temperature", "medium", "humidity", "low", "medium"));
    }

    /*
     Основной метод расчета скорости вентилятора
     temperature - текущая температура
     humidity - текущая влажность
     return: рекомендуемая скорость вентилятора [0..100%]
     */
    public double calculateFanSpeed(double temperature, double humidity) {
        Map<String, Double> inputs = new HashMap<>();
        inputs.put("temperature", temperature);
        inputs.put("humidity", humidity);

        System.out.println("\n=== Начало процесса нечеткого вывода ===");

        // 1. Фаззификация входных данных
        System.out.println("\n1. Фаззификация входных данных:");
        printFuzzyStatus(temperature, humidity);

        // 2. Активация правил
        Map<String, Map<String, Double>> fuzzifiedInputs = fuzzySystem.fuzzifyInputs(inputs);
        Map<String, Double> outputActivations = fuzzySystem.activateRulesWithLogging(fuzzifiedInputs);

        // 3. Дефаззификация
        System.out.println("\n3. Дефаззификация:");
        double speed = CentroidDefuzzifier.defuzzifyWithLogging(fanSpeed, outputActivations, 0, 100);

        System.out.println("\n=== Результат нечеткого вывода ===");
        System.out.printf("Рекомендуемая скорость вентилятора: %.1f%%\n", speed);

        return speed;
    }

    //Печать текущих степеней принадлежности для входных значений
    public void printFuzzyStatus(double temp, double hum) {
        System.out.printf("Температура %.1f°C: low=%.2f, medium=%.2f, high=%.2f%n",
                temp,
                temperature.getMembership("low", temp),
                temperature.getMembership("medium", temp),
                temperature.getMembership("high", temp));

        System.out.printf("Влажность %.1f%%: low=%.2f, medium=%.2f, high=%.2f%n",
                hum,
                humidity.getMembership("low", hum),
                humidity.getMembership("medium", hum),
                humidity.getMembership("high", hum));
    }
}