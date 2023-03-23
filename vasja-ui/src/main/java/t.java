import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;

public class t {
    public static void main(String[] args) {
        String tekst = "<p>Hola!</p>\n" +
                "\n" +
                "<p>Ha solicitado restablecer su contraseña.</p>\n" +
                "\n" +
                "<p>Haga clic en el siguiente enlace para cambiar su contraseña:</p>\n" +
                "\n" +
                "<p><a href=\"${LINK}\">Cambiar mi contraseña: ${LINK}</a></p>\n" +
                "\n" +
                "<p>Ignore este correo electrónico si recuerda su contraseña o no ha realizado la solicitud.</p>\n" +
                "\n" +
                "<p>Saludos!</p>\n" +
                "\n" +
                "<p>VASJA</p>";




        Map<String, String> toReplace = new HashMap<>();
        toReplace.put("LINK", "http://sayrwsyewrhy/shwsrhwh/");
            StringSubstitutor sub = new StringSubstitutor(toReplace);
        System.out.println(sub.replace(tekst));
    }
}
