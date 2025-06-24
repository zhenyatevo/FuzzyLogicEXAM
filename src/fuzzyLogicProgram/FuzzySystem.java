package fuzzyLogicProgram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 Нечеткая система принятия решений (система нечеткого вывода)
 */
public final class FuzzySystem {
    private final List<LinguisticVariable> inputVariables = new ArrayList<>();
    private final LinguisticVariable outputVariable;
    private final List<FuzzyRule> rules = new ArrayList<>();
    private final double outputMin;
    private final double outputMax;

    //Конструктор
    public FuzzySystem(LinguisticVariable outputVariable, double outputMin, double outputMax) {
        this.outputVariable = outputVariable;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
    }

    public void addInputVariable(LinguisticVariable variable) {

        inputVariables.add(variable);
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    /*
     Основной метод вычисления выхода системы
     inputValues - четкие входные значения
     return: четкое выходное значение
     */
    public double evaluate(Map<String, Double> inputValues) {
        Map<String, Map<String, Double>> fuzzifiedInputs = fuzzifyInputs(inputValues);
        Map<String, Double> outputActivations = activateRules(fuzzifiedInputs);
        return CentroidDefuzzifier.defuzzify(outputVariable, outputActivations, outputMin, outputMax);
    }

    /*
     Фаззификация входных данных
     return словарь: {переменная: {терм: степень принадлежности}}
     */
    public Map<String, Map<String, Double>> fuzzifyInputs(Map<String, Double> inputValues) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (LinguisticVariable var : inputVariables) {
            Map<String, Double> memberships = new HashMap<>();
            String varName = var.getName();
            double value = inputValues.get(varName);
            // Вычисляем принадлежности для всех термов переменной
            var.getTerms().forEach((termName, function) -> {
                memberships.put(termName, var.getMembership(termName, value));
            });

            result.put(varName, memberships);
        }

        return result;
    }

    // Версии активации правил с логированием и без
    public Map<String, Double> activateRules(Map<String, Map<String, Double>> fuzzifiedInputs) {
        return activateRulesWithLogging(fuzzifiedInputs, false);
    }

    public Map<String, Double> activateRulesWithLogging(Map<String, Map<String, Double>> fuzzifiedInputs) {
        return activateRulesWithLogging(fuzzifiedInputs, true);
    }

     /*
     Активация правил системы
     return словарь: {выходной терм: степень активации}
     */
    private Map<String, Double> activateRulesWithLogging(Map<String, Map<String, Double>> fuzzifiedInputs,
                                                         boolean logging) {
        Map<String, Double> outputActivations = new HashMap<>();

        if (logging) {
            System.out.println("\nАнализ базы правил:");
            System.out.println("--------------------------------------------------");
            System.out.println("№ | Условие                          | Активация");
            System.out.println("--------------------------------------------------");
        }

        int ruleNum = 1;
        for (FuzzyRule rule : rules) {
            double activation = rule.evaluate(fuzzifiedInputs);

            if (logging) {
                String condition = String.format("Если %s=%s и %s=%s",
                        rule.getInputVariables().get(0), rule.getInputTerms().get(0),
                        rule.getInputVariables().get(1), rule.getInputTerms().get(1));

                System.out.printf("%d | %-30s | %.2f\n",
                        ruleNum++, condition, activation);
            }

            if (activation > 0) {
                String outputTerm = rule.getOutputTerm();
                // Агрегируем через MAX, если правило активировало тот же терм
                outputActivations.merge(outputTerm, activation, Math::max);
            }
        }

        if (logging && !outputActivations.isEmpty()) {
            System.out.println("\nАктивированные выходные термины:");
            outputActivations.forEach((term, activation) ->
                    System.out.printf("- %s: степень активации = %.2f\n", term, activation));
        }

        return outputActivations;
    }
}