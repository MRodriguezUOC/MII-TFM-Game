package edu.uoc.mii.utils;

import com.badlogic.gdx.utils.Base64Coder;
import java.util.Random;

/**
 *
 * @author Marco Rodriguez
 */
public class SecurityUtils {
    private final String SECRET_KEY;
  
    /* Base iniciada con gemini. Prompt: Me puedes crear un algoritmo que genere 
       una contraseña a partir de un string?
    */

    // We define the allowed characters
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}";
    private static final String ALL = LOWER + UPPER + DIGITS + SPECIAL;

    /**
     * Genera una contraseña determinista basada en un texto semilla.
     * @param seedText The base text (in: "my User" or "my User+app")
     * @param length Desired password length (e.g., 12))
     * @return Password generated
     */
    public static String generateKey(String seedText, int length) {
        if (length < 4) length = 4; // Mínimo de seguridad

        // 1. CREATE A UNIQUE NUMERIC SEED
        // We convert the string into a long number to initialize the random 
        // number generator.
        // We use a manual algorithm to ensure it works the same on Android, 
        // HTML, and PC.
        long seed = 0;
        for (int i = 0; i < seedText.length(); i++) {
            seed = 31 * seed + seedText.charAt(i);
        }

        // 2. INITIALIZE THE GENERATOR WITH THE SEED
        // By using the same seed, the sequence of "random" numbers will always 
        // be identical.
        Random random = new Random(seed);

        StringBuilder sb = new StringBuilder(length);

        // 3. GUARANTEE SAFETY RULES
        // First, we force that there be at least one of each type
        sb.append(LOWER.charAt(random.nextInt(LOWER.length())));
        sb.append(UPPER.charAt(random.nextInt(UPPER.length())));
        sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        sb.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 4. FILL IN THE REST
        for (int i = 4; i < length; i++) {
            sb.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        // 5. SHUFFLE
        // Right now, the password always starts with a lowercase letter, 
        // uppercase letter...
        // We're going to deterministically disorder it so that it becomes 
        // unpredictable.
        char[] result = sb.toString().toCharArray();
        for (int i = result.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Intercambiar caracteres
            char temp = result[index];
            result[index] = result[i];
            result[i] = temp;
        }

        return new String(result);
    }    
    
    /* Base iniciada con gemini. Prompt: Me puedes crear una clase para cifrar 
       un texto en libGDX que soporte tambien HTML(GWT)?
    */
    
    public SecurityUtils(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }
        
    public String encrypt(String str) {
        if (str == null || str.isEmpty()) return "";
        try {
            // 1. We apply XOR (we mix the password with our key))
            String xor = xorOperation(str);
            // 2. We encoded them in Base64 so that they would be printable 
            // characterss
            return Base64Coder.encodeString(xor);
        } catch (Exception e) {
            return "";
        }
    }

    public String decrypt(String str) {
        if (str == null || str.isEmpty()) return "";
        try {
            // 1. We decode Base64
            String decoded = Base64Coder.decodeString(str);
            // 2. We apply XOR again (XOR is reversible using the same key)
            return xorOperation(decoded);
        } catch (Exception e) {
            return "";
        }
    }

    // The simple XOR algorithm: transforms the characters
    private String xorOperation(String input) {
        StringBuilder output = new StringBuilder();
        
        for (int i = 0; i < input.length(); i++) {
            // Bitwise operation between the input character and the key character
            char character = input.charAt(i);
            char keyChar = SECRET_KEY.charAt(i % SECRET_KEY.length());
            
            output.append((char) (character ^ keyChar));
        }
        
        return output.toString();
    }    
}
