package org.sanjose.helper;

/**
 * SORCER class
 * User: prubach
 * Date: 17.09.16
 */
public class Rot10 {

        public static String rot10(String input) {
            String output = "";
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if       (c >= 33 && c <= 116) c += 10;
                else c -= 84;
                output += c;
            }
            return output;
        }
}
