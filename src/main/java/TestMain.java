import java.io.IOException;

public class TestMain {
    public static void main(String args[]) {
        Api test = new Api();
        try {
            test.createDID();
            test.createNonce();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
