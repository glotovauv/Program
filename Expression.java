import java.util.ArrayDeque;
import java.util.ArrayList;

public class Expression {
    private ArrayList<GeneralObject> generalObjects = new ArrayList<>();
    private static ArrayList<Character> commandsSymbols = new ArrayList<Character>();

    static {
        commandsSymbols.add('(');
        commandsSymbols.add(')');
        commandsSymbols.add('*');
        commandsSymbols.add('+');
        commandsSymbols.add('-');
    }

    private Expression(String parseString){
        parseExpression(parseString);
    }

    public static Expression createExpression(String parseString){
        if(!isCorrect(parseString)) return null;
        else return new Expression(parseString);
    }

    public Term reduceExpression(){
        ArrayDeque<Integer> openBracketPositions = new ArrayDeque<>();
        for(int i = 0; i < generalObjects.size(); i++){
            if(generalObjects.get(i).typeObject == TypeObject.BRACKET){
                if(((Bracket) generalObjects.get(i)).isOpen) {
                    openBracketPositions.addLast(i);
                }
                else{
                    int openBracket = openBracketPositions.pollLast(), closeBracket = i;
                    generalObjects.remove(closeBracket);
                    generalObjects.remove(openBracket);
                    reduceSubexpression(openBracket, closeBracket - 2);
                    i = openBracket;
                }
            }
        }
        if(generalObjects.size() > 1) reduceSubexpression(0, generalObjects.size() - 1);
        return (Term) generalObjects.get(0);
    }

    private void parseExpression(String parseString) {
        String nameVariable = "";
        for (int i = 0; i < parseString.length(); i++) {
            switch (parseString.charAt(i)) {
                case '(':
                    generalObjects.add(new Bracket(true));
                    break;
                case ')':
                    generalObjects.add(new Bracket(false));
                    break;
                case '+':
                    generalObjects.add(new Operation(TypeOperation.OR));
                    break;
                case '-':
                    generalObjects.add(new Operation(TypeOperation.NOT));
                    break;
                case '*':
                    generalObjects.add(new Operation(TypeOperation.AND));
                    break;
                default:
                    nameVariable += parseString.charAt(i);
                    if (i + 1 == parseString.length() || commandsSymbols.contains(parseString.charAt(i + 1))) {
                        generalObjects.add(new Term(nameVariable));
                        nameVariable = "";
                    }
            }
        }
    }

    private static boolean isCorrect(String parseString){
        if(parseString == null || parseString.length() == 0) return false;
        int levelBrackets = 0;
        for(int i = 0; i < parseString.length(); i++){
            if(i == 0) {
                switch (parseString.charAt(i)) {
                    case ')':
                    case '*':
                    case '+':
                        return false;
                }
            }
            else if(i + 1 == parseString.length()){
                switch (parseString.charAt(i)) {
                    case '(':
                    case '-':
                    case '*':
                    case '+':
                        return false;
                }
            }
            switch (parseString.charAt(i)){
                case '(':
                    levelBrackets++;
                case '-':
                    if (i > 0 && (parseString.charAt(i - 1) == ')'
                            || !commandsSymbols.contains(parseString.charAt(i - 1))))
                        return false;
                    break;
                case ')':
                    levelBrackets--;
                    if(levelBrackets < 0) return false;
                case '+':
                case '*':
                    if(parseString.charAt(i - 1) != ')'
                            && commandsSymbols.contains(parseString.charAt(i - 1)))
                        return false;
                    break;
                default:
                    if(i > 0 && parseString.charAt(i - 1) == ')')
                        return false;
            }
        }
        return levelBrackets == 0;
    }

    private void reduceSubexpression(int startPosition, int endPosition){
        endPosition = executeAllNegativeOperations(startPosition, endPosition);
        endPosition = executeAllBinaryOperations(startPosition, endPosition, TypeObject.AndOperation);
        executeAllBinaryOperations(startPosition, endPosition, TypeObject.OrOperation);
    }

    private int executeAllNegativeOperations(int startPosition, int endPosition) {
        for(int i = startPosition; i <= endPosition; i++) {
            if (generalObjects.get(i).typeObject == TypeObject.NegativeOperation) {
                generalObjects.remove(i);
                endPosition--;
                if (generalObjects.get(i).typeObject == TypeObject.NegativeOperation) {
                    generalObjects.remove(i);
                    endPosition--;
                    i--;
                } else {
                    Term result = ((Term) (generalObjects.get(i))).executeNegativeOperation();
                    System.out.println("NotOperation: " + result);
                    generalObjects.set(i, result);
                }
            }
        }
        return endPosition;
    }

    private int executeAllBinaryOperations(int startPosition, int endPosition, TypeObject typeBinaryOperation){
        for(int i = startPosition; i <= endPosition; i++) {
            if (generalObjects.get(i).typeObject == typeBinaryOperation) {
                Term result = ((Term) (generalObjects.get(i - 1))).executeBinaryOperation((Term) generalObjects.get(i + 1), typeBinaryOperation);
                System.out.println(typeBinaryOperation + ": " + result);
                generalObjects.remove(i);
                generalObjects.remove(i);
                endPosition -= 2;
                i--;
                generalObjects.set(i, result);
            }
        }
        return endPosition;
    }

}
