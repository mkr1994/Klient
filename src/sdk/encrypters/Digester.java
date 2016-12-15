package sdk.encrypters;

import java.security.MessageDigest;

/**
 * Digester used for hashing+salt passwords
 */

public class Digester {
    private final static String SALT = "82efbcc2cc33d33cdadf12806d75591a";
    private static MessageDigest digester;

    static {
        try {
            digester = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String hashWithSalt(String str) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Fejl");
        }

        str = str + Digester.SALT;

        return Digester.hash(str);
    }

    private static String hash(String str) {
        digester.update(str.getBytes());
        byte[] hash = digester.digest();
        StringBuffer hexString = new StringBuffer();
        for (byte aHash : hash) {
            if ((0xff & aHash) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & aHash)));
            } else {
                hexString.append(Integer.toHexString(0xFF & aHash));
            }
        }
        return hexString.toString();
    }
}