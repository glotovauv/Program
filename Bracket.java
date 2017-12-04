
public class Bracket extends GeneralObject {

    boolean isOpen;

    Bracket(boolean isOpen){
        typeObject = TypeObject.BRACKET;
        this.isOpen = isOpen;
    }
}
