package sdk.encrypters;
import java.util.Random;

/**
 * Class used for encryption and decryption. https://github.com/KyleBanks/XOREncryption/blob/master/Java%20(Android%20compatible)/XOREncryption.java
 */
public class Crypter {


    public static String encryptDecryptXOR(String input) {
        char[] key = {'M', 'K', 'R'}; //The same key as on the server.
        StringBuilder output = new StringBuilder();

        //For loop der scrambler den String, der bliver indtastet
        for (int i = 0; i < input.length(); i++) {
            output.append((char) (input.charAt(i) ^ key[i % key.length]));
        }


        return output.toString();
    }
}
