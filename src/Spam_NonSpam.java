/**
 * Enum to refer spam (1) and non spam (0)
 */
public enum Spam_NonSpam {
    NON_SPAM (0),
    SPAM (1);

    private int type;

    Spam_NonSpam(int type){
        this.type = type;
    }
}
