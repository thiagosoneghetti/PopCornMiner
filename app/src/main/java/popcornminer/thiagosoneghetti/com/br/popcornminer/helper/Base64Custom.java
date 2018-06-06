package popcornminer.thiagosoneghetti.com.br.popcornminer.helper;

import android.util.Base64;

public class Base64Custom {

    // Converter uma string para base 64
    public static String codificarBase64(String texto){
        /* Onde tiver os caracteres //n = quebra de linha e //r = caracter de escape será substituido por ""(vazio)  */
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","" );
    }
    // Converter de base 64 para String
    public static String decodificarBase64(String textoCodificado){
        // new String() utilizado para converter de Bytes para String
        return new String( Base64.decode(textoCodificado, Base64.DEFAULT) );
    }

}
