
public enum TypeOperation {
    AND{
        public String toString() {
            return "*";
        }
    },
    OR{
        public String toString() {
            return "+";
        }
    },
    NOT{
        public String toString() {
            return "-";
        }
    },
    None;
}
