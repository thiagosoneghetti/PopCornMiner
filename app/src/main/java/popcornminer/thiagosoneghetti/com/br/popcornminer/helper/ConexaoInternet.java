package popcornminer.thiagosoneghetti.com.br.popcornminer.helper;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConexaoInternet {
    // Função para verificar existência de conexão com a internet
    public final static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }
}
