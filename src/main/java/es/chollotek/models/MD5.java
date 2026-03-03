package es.chollotek.models;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Alba
 */
public class MD5 {

    /**
     * Obtiene el valor de utilizar la función MD5 de una cadena
     *
     * @param input Cadena pasada para convertir a MD5
     * @return Cadena tras la conversión
     */
    public static String encriptar(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            // Rellenar con ceros a la izquierda si es necesario
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar en MD5", e);
        }
    }
}
