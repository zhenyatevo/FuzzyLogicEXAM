package fuzzyLogicProgram;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/*
 Класс лингвистической переменной нечеткой логики.
 Содержит термы (нечеткие множества) с их функциями принадлежности.
 */

public final class LinguisticVariable {
    private final String name;
    private final Map<String, Function<Double, Double>> terms;
    // Термы и их функции принадлежности

    public LinguisticVariable(String name) { //Конструктор
        this.name = Objects.requireNonNull(name);
        this.terms = new HashMap<>();
    }

    /*
     Добавление терма к переменной
     termName - название терма
     membershipFunction - функция принадлежности для терма
     */
    public void addTerm(String termName, Function<Double, Double> membershipFunction) {
        terms.put(termName, membershipFunction);
    }

    //Вычисление степени принадлежности значения к терму
    public double getMembership(String termName, double value) {
        Function<Double, Double> function = terms.get(termName);
        if (function == null) {
            throw new IllegalArgumentException("Unknown term: " + termName);
        }
        return function.apply(value);
    }

    public String getName() {
        return name;
    }

    public Map<String, Function<Double, Double>> getTerms() {
        return Collections.unmodifiableMap(terms);
    }
}