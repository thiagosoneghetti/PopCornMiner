package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.TransferenciaAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;


public class TransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private CarteiraDao carteiraDao;
    private TransferenciaAdpter transferenciaAdpter;
    private ListView listaCarteiras;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar


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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transferencia,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(TransferenciaActivity.this,MainActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mtransf_home:
                Intent irHome = new Intent(TransferenciaActivity.this,MainActivity.class);
                startActivity(irHome);
                break;
            case R.id.bt_mtransf_carteira:
                Intent irCarteira = new Intent(TransferenciaActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                break;
            /*case R.id.bt_mtransf_transferencia:
                Intent irTransferencia = new Intent(TransferenciaActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;*/
            case R.id.bt_mtransf_sair:
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(TransferenciaActivity.this,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
