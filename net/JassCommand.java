package ch.epfl.javass.net;

/**
 * Enumeration of the different commands for the client-server communication.
 * @author Charles BEAUVILLE
 * @author Celia HOUSSIAUX
 *
 */
public enum JassCommand {
    PLRS("PLRS"),
    TRMP("TRMP"),
    HAND("HAND"),
    TRCK("TRCK"),
    CARD("CARD"),
    SCOR("SCOR"),
    WINR("WINR");
    
    private String name;
    
    private JassCommand(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
