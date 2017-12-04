import java.util.ArrayList;
import java.util.Iterator;

public class Term extends GeneralObject {
    private String nameTerm;
    private ArrayList<Term> termParts = null;
    private TypeOperation typeInnerOperation = TypeOperation.None;
    private boolean isNegative = false;

    {
        typeObject = TypeObject.TERM;
    }

    public Term(String nameTerm) {
        this.nameTerm = nameTerm;
    }

    private Term(Term term) {
        this.nameTerm = term.nameTerm;
        this.isNegative = term.isNegative;
        this.typeInnerOperation = term.typeInnerOperation;
        if (term.termParts != null) {
            termParts = new ArrayList<>();
            for (Term t : term.termParts) termParts.add(new Term(t));
        }
    }

    private boolean isCompositeTerm() {
        return typeInnerOperation != TypeOperation.None && termParts != null;
    }

    private static Term unionTerms(Term termPart1, Term termPart2, TypeOperation typeInnerOperation) {

        ArrayList<Term> termParts = new ArrayList<>();
        if (termPart1.typeInnerOperation == typeInnerOperation)
            for (Term t : termPart1.termParts) termParts.add(new Term(t));
        else termParts.add(new Term(termPart1));

        if (termPart2.typeInnerOperation == typeInnerOperation)
            for (Term t : termPart2.termParts) termParts.add(new Term(t));
        else termParts.add(new Term(termPart2));

        termParts.sort((o1, o2) -> {
            return o1.nameTerm.compareTo(o2.nameTerm);
        });
        deleteDuplicateTerm(termParts);

        Term term = new Term(getNameCompositeTerm(termParts, typeInnerOperation));
        term.typeInnerOperation = typeInnerOperation;
        term.termParts = termParts;

        return term;
    }

    public Term executeBinaryOperation(Term term, TypeObject typeBinaryOperation) {
        if (typeBinaryOperation == TypeObject.AndOperation) {
            return executeAndOperation(term);
        } else if (typeBinaryOperation == TypeObject.OrOperation) {
            return executeOrOperation(term);
        } else return null;
    }

    public Term executeOrOperation(Term term) {
        if (this.nameTerm.equals("1") || term.nameTerm.equals("1")) return new Term("1");
        if (this.nameTerm.equals("0")) return new Term(term);
        if (term.nameTerm.equals("0")) return new Term(this);

        Term result = unionTerms(this, term, TypeOperation.OR);

        if (isExistsOppositeTerms(result.termParts)) {
            return new Term("1");
        }

        return result;
    }

    public Term executeAndOperation(Term term) {
        if (this.nameTerm.equals("0") || term.nameTerm.equals("0")) return new Term("0");
        if (this.nameTerm.equals("1")) return new Term(term);
        if (term.nameTerm.equals("1")) return new Term(this);

        Term result = unionTerms(this, term, TypeOperation.AND);

        if (isExistsOppositeTerms(result.termParts)) {
            return new Term("0");
        }

        return result;
    }

    public Term executeNegativeOperation() {
        Term term = new Term(this);
        if (!term.isCompositeTerm()) {
            if (term.nameTerm.equals("0")) term.nameTerm = "1";
            else if (term.nameTerm.equals("1")) term.nameTerm = "0";
            else term.isNegative = !this.isNegative;
        } else {
            if (term.typeInnerOperation == TypeOperation.AND)
                term.typeInnerOperation = TypeOperation.OR;
            else term.typeInnerOperation = TypeOperation.AND;
            for (int i = 0; i < term.termParts.size(); i++)
                term.termParts.set(i, term.termParts.get(i).executeNegativeOperation());
            term.nameTerm = term.toString();
        }
        return term;
    }

    @Override
    public String toString() {
        if (!isCompositeTerm()) {
            if (isNegative) return "-" + nameTerm;
            else return nameTerm;
        } else return getNameCompositeTerm(termParts, typeInnerOperation);
    }

    private static String getNameCompositeTerm(ArrayList<Term> termParts, TypeOperation typeInnerOperation) {
        String nameTerm = "";
        for (int i = 0; i < termParts.size(); i++) {
            if (termParts.get(i).isCompositeTerm())
                nameTerm += "(" + termParts.get(i) + ")";
            else nameTerm += termParts.get(i);
            if (i != termParts.size() - 1) nameTerm += typeInnerOperation;
        }
        return nameTerm;
    }

    private static void deleteDuplicateTerm(ArrayList<Term> termParts) {
        Iterator<Term> iteratorTerm = termParts.iterator();
        while (iteratorTerm.hasNext()) {
            Term currentTerm = iteratorTerm.next(), nextTerm = null;
            if (iteratorTerm.hasNext()) nextTerm = iteratorTerm.next();
            if (nextTerm != null && currentTerm.isNegative == nextTerm.isNegative &&
                    currentTerm.nameTerm.equals(nextTerm.nameTerm)) {
                iteratorTerm.remove();
            }
        }
    }

    private static boolean isExistsOppositeTerms(ArrayList<Term> termParts) {
        for (Term startTerm : termParts) {
            Term oppositeTerm = startTerm.executeNegativeOperation();
            for (Term otherTerm : termParts) {
                if (oppositeTerm.isNegative == otherTerm.isNegative &&
                        oppositeTerm.nameTerm.equals(otherTerm.nameTerm)) {
                    return true;
                }
            }
        }
        return false;
    }
}
