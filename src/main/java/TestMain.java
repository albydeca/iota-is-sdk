import org.bouncycastle.crypto.CryptoException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class TestMain {
    public static void main(String args[]) {
        Api test = new Api();
        try {
            test.createDID();
            test.createNonce();
            test.sigantureNonce();
            test.createChannel();
            test.writeDataOnChannel();
            test.getDataFromChannel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CryptoException e) {
            e.printStackTrace();
        }
    }
}
