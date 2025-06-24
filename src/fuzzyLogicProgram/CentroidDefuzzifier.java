package fuzzyLogicProgram;

import java.util.Map;
//Класс для дефаззификации методом центра площади
public final class CentroidDefuzzifier {
    private static final int PRECISION = 1000;// Количество шагов интегрирования

    private CentroidDefuzzifier() {}// Запрещаем создание экземпляров

    /*
     Дефаззификация методом центра площади
     outputVar - выходная лингвистическая переменная
     activatedTerms - активированные термы с их степенями
     min - минимальное значение выходной переменной
     max - максимальное значение выходной переменной
     return: четкое значение выходной переменной
     */
    public static double defuzzify(LinguisticVariable outputVar,
                                   Map<String, Double> activatedTerms,
                                   double min, double max) {
        return defuzzifyWithLogging(outputVar, activatedTerms, min, max, false);
    }

    // Версия с логированием
    public static double defuzzifyWithLogging(LinguisticVariable outputVar,
                                              Map<String, Double> activatedTerms,
                                              double min, double max) {
        return defuzzifyWithLogging(outputVar, activatedTerms, min, max, true);
    }

    private static double defuzzifyWithLogging(LinguisticVariable outputVar,
                                               Map<String, Double> activatedTerms,
                                               double min, double max,
                                               boolean logging) {
        double step = (max - min) / PRECISION;
        double numerator = 0.0;
        double denominator = 0.0;

        if (logging) {
            System.out.println("\nПроцесс дефаззификации методом центра площади:");
            System.out.println("x\tμ(x)\tx*μ(x)");
            System.out.println("---------------------");
        }

        for (double x = min; x <= max; x += step) {
            double mu = aggregatedMembership(outputVar, activatedTerms, x);

            if (logging && x % 10 == 0) { // Выводим каждые 10 единиц для краткости
                System.out.printf("%.1f\t%.3f\t%.3f\n", x, mu, x * mu);
            }

            numerator += x * mu * step;
            denominator += mu * step;
        }

        if (logging) {
            System.out.println("... (пропущены промежуточные значения)");
            System.out.println("---------------------");
            System.out.printf("Итого: ∫x*μ(x)dx = %.3f, ∫μ(x)dx = %.3f\n", numerator, denominator);
            System.out.printf("Центр площади = %.3f / %.3f = %.1f%%\n",
                    numerator, denominator, denominator == 0 ? 0 : numerator / denominator);
        }

        return denominator == 0 ? 0 : numerator / denominator;
    }

    /*
     Вычисление агрегированной функции принадлежности
     return: максимальное значение из всех активированных термов (логическое ИЛИ через MAX)
     */
    private static double aggregatedMembership(LinguisticVariable outputVar,
                                               Map<String, Double> activatedTerms,
                                               double x) {
        double maxMu = 0.0;
        for (Map.Entry<String, Double> entry : activatedTerms.entrySet()) {
            double termMu = outputVar.getMembership(entry.getKey(), x);
            maxMu = Math.max(maxMu, Math.min(entry.getValue(), termMu));
            // MIN для активации, MAX для агрегации
        }
        return maxMu;
    }
}
