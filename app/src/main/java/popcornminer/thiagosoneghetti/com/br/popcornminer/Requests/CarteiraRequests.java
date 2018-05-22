package popcornminer.thiagosoneghetti.com.br.popcornminer.Requests;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Saldo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarteiraRequests {
    // http://www.mobimais.com.br/blog/retrofit-2-consumir-json-no-android/

    @POST("transaction")
    @FormUrlEncoded
    Call<POST> getTransferencia(@Field("sender") String sua_chave_privada,
                                @Field("destination") String chave_publica_destino,
                                @Field("amount") Integer valor);

    @GET("{chavepublica}")
    Call<Saldo> getSaldo(@Path("chavepublica") String chavepublica);
}
