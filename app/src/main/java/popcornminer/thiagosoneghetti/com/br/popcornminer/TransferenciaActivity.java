package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Adapter.TransferenciaAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;


public class TransferenciaActivity extends AppCompatActivity {
    private CarteiraDao carteiraDao;
    private TransferenciaAdpter transferenciaAdpter;
    private ListView listaCarteiras;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        getSupportActionBar().setTitle("Transferência");

        carteiraDao = new CarteiraDao(this);
        listaCarteiras = (ListView) findViewById(R.id.listTransferenciaId);
        context = this;

        // Ao clicar em uma carteira da lista, e é passada a carteira selecionada para a outra view
        listaCarteiras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Carteira carteira = (Carteira) transferenciaAdpter.getItem(position);

            Intent intent = new Intent(TransferenciaActivity.this, NovaTransferenciaActivity.class);
            intent.putExtra("carteira", carteira);
            startActivity(intent);
            }
        });

    }

    private void atualizarListaTransferencia (){
        List<Carteira> carteiraLista = carteiraDao.recuperarCarteira();
        transferenciaAdpter = new TransferenciaAdpter(context, carteiraLista);
        listaCarteiras.setAdapter(transferenciaAdpter);
    };


    @Override
    protected void onStart() {
        try {
            super.onStart();
            atualizarListaTransferencia();
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
