import org.apache.logging.log4j.LogManager;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        System.out.println("Print");
        System.out.println("Root Logger Level: " + LogManager.getRootLogger().getLevel());
    }
}

