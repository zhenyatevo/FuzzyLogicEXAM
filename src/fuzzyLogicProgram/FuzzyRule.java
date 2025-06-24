package fuzzyLogicProgram;

import java.util.List;
import java.util.Map;
//Класс нечеткого правила вида "ЕСЛИ условие ТО заключение"
public final class FuzzyRule {
    private final List<String> inputVariables;
    private final List<String> inputTerms;
    private final String outputTerm;

    //Конструктор правила для двух условий
    public FuzzyRule(String inputVar1, String inputTerm1,
                     String inputVar2, String inputTerm2,
                     String outputTerm) {
        this.inputVariables = List.of(inputVar1, inputVar2);
        this.inputTerms = List.of(inputTerm1, inputTerm2);
        this.outputTerm = outputTerm;
    }

    //Вычисление степени активации правила
    public double evaluate(Map<String, Map<String, Double>> inputs) {
        double activation = 1.0;

        for (int i = 0; i < inputVariables.size(); i++) {
            String varName = inputVariables.get(i);
            String termName = inputTerms.get(i);

            Map<String, Double> varMemberships = inputs.get(varName);
            if (varMemberships == null) {
                return 0.0; // Нет данных по терму
            }

            Double membership = varMemberships.get(termName);
            if (membership == null) {
                return 0.0; // Нет данных по терму
            }
            // Логическое И через минимум
            activation = Math.min(activation, membership);
        }

        return activation;
    }

    public String getOutputTerm() {
        return outputTerm;
    }

    public List<String> getInputVariables() {
        return inputVariables;
    }

    public List<String> getInputTerms() {
        return inputTerms;
    }
}
