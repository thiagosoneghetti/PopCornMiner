package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Transferencia;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Requests.CarteiraRequests;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class NovaTransferenciaActivity extends AppCompatActivity {
    private Carteira carteira;
    private Button botaoTransferir;
    private TextView editDescricaoCarteira;
    private EditText editCPublicaDest;
    private EditText editValorTranf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_transferencia);
        getSupportActionBar().setTitle("Transferência");

        botaoTransferir = findViewById(R.id.btTransferirId);
        editCPublicaDest = findViewById(R.id.editCPublicaDestinoId);
        editValorTranf = findViewById(R.id.editValorTransfId);
        editDescricaoCarteira = findViewById(R.id.editDescricaoTransfId);



        // Recuperando os dados que foram passados na Activity anterior
        Intent intent = getIntent();
        if(intent.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intent.getSerializableExtra("carteira");

            editDescricaoCarteira.setText(carteira.getDescricao());
        }

        botaoTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editCPublicaDest.getText().toString().equals("") || editValorTranf.getText().toString().equals("") ) {
                    Toast.makeText(NovaTransferenciaActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();

                }else {
                    String chave_publica_destino = editCPublicaDest.getText().toString();
                    Float valor = Float.parseFloat(editValorTranf.getText().toString());

                    carteira.transferenciaUC(carteira, chave_publica_destino, valor, NovaTransferenciaActivity.this);

                    Intent intent = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                    startActivity(intent);
                }
            }
        });

    }



/*    private void transferencia(){

       //Instância Retrofit
        Retrofit rTransferencia = new Retrofit.Builder()
                .baseUrl("http://moeda.ucl.br/transaction/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        CarteiraRequests service = rTransferencia.create(CarteiraRequests.class);

        Call<Transferencia> call = service.getTransferencia(sua_chave_privada,chave_publica_destino, valor);
        call.enqueue(new Callback<Transferencia>() {
            @Override
            public void onResponse(Call<Transferencia> call, Response<Transferencia> response) {
                if (response.isSuccessful()){
                    Transferencia transferencia = response.body();

                    Toast.makeText(NovaTransferenciaActivity.this, "Transferido: " + transferencia.getMensagem(), Toast.LENGTH_SHORT).show();
                }else{
                    Transferencia transferencia = response.body();
                    Toast.makeText(NovaTransferenciaActivity.this, "Transferência não completada: "+transferencia.getMensagem(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Transferencia> call, Throwable t) {
                Toast.makeText(NovaTransferenciaActivity.this, "Saldo não encontrado", Toast.LENGTH_SHORT).show();
            }
        });
        finish();

    }
*/

}
