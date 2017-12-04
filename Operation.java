
public class Operation extends GeneralObject {

    Operation(TypeOperation typeOperation) {
        if (typeOperation == TypeOperation.NOT)
            typeObject = TypeObject.NegativeOperation;

        else if (typeOperation == TypeOperation.AND)
            typeObject = TypeObject.AndOperation;

        else if (typeOperation == TypeOperation.OR)
            typeObject = TypeObject.OrOperation;
    }
}
