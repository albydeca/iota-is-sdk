import java.io.IOException;

public class TestMain {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String args[]) throws IOException {
        LogCreator creator = new LogCreator();
        LogAuditor auditor = new LogAuditor();
        System.out.println("-------------------------- " + ANSI_GREEN +  "LogCreator" + ANSI_RESET + " --------------------------");
        creator.authorizedSubscriptions();
        creator.writeDataOnChannel();
        System.out.println("-------------------------- " + ANSI_GREEN +  "LogAuditor" + ANSI_RESET + " --------------------------");
        auditor.getDataFromChannel();
    }
}
