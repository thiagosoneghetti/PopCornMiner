package popcornminer.thiagosoneghetti.com.br.popcornminer.Requests;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Saldo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface carteiraRequests {

    @POST("/transaction")
    @FormUrlEncoded
    Call<POST> getTransferencia(@Field("sender") String chaveprivada, @Field("destination") String chavepublica, @Field("amount") int valor);

    @GET("{chavepublica}")
    Call<Saldo> getSaldo(@Path("chavepublica") String chavepublica);
}
