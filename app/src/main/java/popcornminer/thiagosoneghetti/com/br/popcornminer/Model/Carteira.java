package popcornminer.thiagosoneghetti.com.br.popcornminer.Model;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Requests.CarteiraRequests;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Carteira implements Serializable{


    private Long id;
    private String chave_publica;
    private String chave_privada;
    private String descricao;

    // Construtores
    public Carteira(Long id, String c_publica, String c_privada, String desc) {
        this.id = id;
        this.chave_publica = c_publica;
        this.chave_privada = c_privada;
        this.descricao = desc;
    }

    public Carteira(String c_publica, String c_privada, String desc) {
        this.chave_publica = c_publica;
        this.chave_privada = c_privada;
        this.descricao = desc;
    }

    // getters e setters
    public Long getId() {

        return id;
    }
    public void setId(Long id) {

        this.id = id;
    }

    public String getChave_publica() {
        return chave_publica;
    }

    public void setChave_publica(String chave_publica) {

        this.chave_publica = chave_publica;
    }

    public String getChave_privada() {

        return chave_privada;
    }
    public void setChave_privada(String chave_privada) {
        this.chave_privada = chave_privada;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public void saldoUC(Carteira carteira, final Context context){

        Retrofit rSaldo = new Retrofit.Builder().baseUrl("http://moeda.ucl.br/balance/").addConverterFactory(GsonConverterFactory.create()).build();

        CarteiraRequests service = rSaldo.create(CarteiraRequests.class);

        Call<Saldo> call = service.getSaldo(carteira.getChave_publica());

        call.enqueue(new Callback<Saldo>() {
            @Override
            public void onResponse(Call<Saldo> call, Response<Saldo> response) {
                if (response.isSuccessful()){
                    Saldo saldo = response.body();

                    Toast.makeText(context, "Saldo: UC "+saldo.getBalance(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Saldo da chave não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Saldo> call, Throwable t) {
                Toast.makeText(context, "Erro ao buscar saldo", Toast.LENGTH_SHORT).show();
            }
        });
    };


    public void transferenciaUC(Carteira carteira, String chave_publica_destino, Float valor, final Context context){
        //Instância Retrofit
        Retrofit rTransferencia = new Retrofit.Builder().baseUrl("https://moeda.ucl.br/transaction/").addConverterFactory(GsonConverterFactory.create()).build();

        CarteiraRequests service = rTransferencia.create(CarteiraRequests.class);

        Call<Transferencia> call = service.getTransferencia(carteira.getChave_privada(),chave_publica_destino, valor);

        call.enqueue(new Callback<Transferencia>() {
            @Override
            public void onResponse(Call<Transferencia> call, Response<Transferencia> response) {
                if (response.isSuccessful()){
                    Transferencia transferencia = response.body();

                    Toast.makeText(context, "Resposta: "+transferencia.getMensagem(), Toast.LENGTH_LONG).show();
                }else{
                    Transferencia transferencia = response.body();
                    Toast.makeText(context, "Transação: "+transferencia.getMensagem(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Transferencia> call, Throwable t) {

                Toast.makeText(context, "Erro ao fazer transferencia", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
