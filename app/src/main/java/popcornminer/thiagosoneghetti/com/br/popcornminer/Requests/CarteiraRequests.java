package popcornminer.thiagosoneghetti.com.br.popcornminer.Requests;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Saldo;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Transferencia;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarteiraRequests {
    // http://www.mobimais.com.br/blog/retrofit-2-consumir-json-no-android/

    @POST("{sua_chave_privada}/{chave_publica_destino}/{valor}")
    Call<Transferencia> getTransferencia(@Path("sua_chave_privada") String sua_chave_privada,
                                         @Path("chave_publica_destino") String chave_publica_destino,
                                         @Path("valor") float valor);

    @GET("{chavepublica}")
    Call<Saldo> getSaldo(@Path("chavepublica") String chavepublica);
}
