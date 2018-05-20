package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Requests.carteiraRequests;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Saldo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarteiraActivity extends AppCompatActivity {

    private CarteiraDao carteiraDao;
    private CarteiraAdpter carteiraAdpter;
    private ListView listaCarteiras;
    private Context context;
    private Retrofit rSaldo;
    private FloatingActionButton botaoAddCarteira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Minhas Carteiras");

        carteiraDao = new CarteiraDao(this);
        listaCarteiras = (ListView) findViewById(R.id.listCarteirasId);
        context = this;

        rSaldo = new Retrofit.Builder().baseUrl("http://moeda.ucl.br/balance/").addConverterFactory(GsonConverterFactory.create()).build();

        listaCarteiras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Carteira carteira = (Carteira) carteiraAdpter.getItem(position);

                saldoUC(carteira);
            }
        });



        listaCarteiras.setLongClickable(true);
        listaCarteiras.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Carteira carteira = (Carteira) carteiraAdpter.getItem(position);

                confirmarExclusao(carteira.getId());
                return true;
            }
        });




        // Botão de direciona para tela de cadastro de nova carteira
        botaoAddCarteira = findViewById(R.id.btAddCarteiraId);

        botaoAddCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteiraActivity.this, AddCarteiraActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saldoUC( Carteira carteira){

        carteiraRequests service = rSaldo.create(carteiraRequests.class);

        Call<Saldo> call = service.getSaldo(carteira.getChave_publica());

        call.enqueue(new Callback<Saldo>() {
            @Override
            public void onResponse(Call<Saldo> call, Response<Saldo> response) {
                if (response.isSuccessful()){
                    Saldo saldo = response.body();

                    Toast.makeText(CarteiraActivity.this, "Saldo: UC"+saldo.getBalance(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CarteiraActivity.this, "Saldo da chave não encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Saldo> call, Throwable t) {
                Toast.makeText(CarteiraActivity.this, "Erro ao buscar saldo", Toast.LENGTH_SHORT).show();
            }
        });
    };

    private void atualizarListaCarteira (){
        List<Carteira> carteiraLista = carteiraDao.recuperarCarteira();
        carteiraAdpter = new CarteiraAdpter(context, carteiraLista);
        listaCarteiras.setAdapter(carteiraAdpter);
    };

    public void confirmarExclusao (final long idExc){
        final long idCarteira = idExc;

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Excluindo...");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Tem certeza que deseja excluir a carteira selecionada?");
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Exclusão cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Carteira deletada com sucesso!", Toast.LENGTH_SHORT).show();
                carteiraDao.removerCarteira(idCarteira);
                atualizarListaCarteira();
            }
        });
        msgBox.show();
    }


    @Override
    protected void onStart() {
        try {
            super.onStart();
            atualizarListaCarteira();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}