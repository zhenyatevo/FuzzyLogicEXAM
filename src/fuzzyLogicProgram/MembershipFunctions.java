package fuzzyLogicProgram;

/*
  Класс, содержащий стандартные функции принадлежности для нечеткой логики.
  Все функции возвращают степень принадлежности значения x к нечеткому множеству.
 */
public final class MembershipFunctions {
    private MembershipFunctions() {}

    /*
     Треугольная функция принадлежности
     x - входное значение
     a - левая граница (где μ=0)
     b - вершина (где μ=1)
     c - правая граница (где μ=0)
     */
    public static double triangular(double x, double a, double b, double c) {
        if (x <= a || x >= c) return 0;
        return x <= b ? (x - a) / (b - a) : (c - x) / (c - b);
    }

    /*
     Трапециевидная функция принадлежности
     x - входное значение
     a - начало подъема (где μ=0)
     b - начало плато (где μ=1)
     c - конец плато (где μ=1)
     d - конец спада (где μ=0)
     */
    public static double trapezoidal(double x, double a, double b, double c, double d) {
        if (x <= a || x >= d) return 0;
        if (x >= b && x <= c) return 1;
        return x < b ? (x - a) / (b - a) : (d - x) / (d - c);
    }

    /*
     Убывающая линейная функция принадлежности
     x - входное значение
     a - начало спада (где μ=1)
     b - конец спада (где μ=0)
    */
    public static double decreasingLinear(double x, double a, double b) {
        if (x <= a) return 1;
        if (x >= b) return 0;
        return (b - x) / (b - a);
    }

    /*
     Возрастающая линейная функция принадлежности
     x - входное значение
     a - начало подъема (где μ=0)
     b - конец подъема (где μ=1)
    */
    public static double increasingLinear(double x, double a, double b) {
        if (x <= a) return 0;
        if (x >= b) return 1;
        return (x - a) / (b - a);
    }
}
