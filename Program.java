import java.util.Scanner;

public class Program {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введите выражение: ");
        String line = scan.nextLine();

        Expression expression = Expression.createExpression(line);
        if(expression != null) {
            Term term = expression.reduceExpression();
            System.out.println("Результат: " + term);
        }
        else System.out.println("Введено некорректное выражение!");
    }
}
