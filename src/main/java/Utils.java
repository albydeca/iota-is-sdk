import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public static String hex(byte[] bytes) {
        String result =  Hex.encodeHexString(bytes);
        return result;
    }

    public static String asHex (byte buf[]) {

        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;

        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");

            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }

        return strbuf.toString();
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
    }

}
