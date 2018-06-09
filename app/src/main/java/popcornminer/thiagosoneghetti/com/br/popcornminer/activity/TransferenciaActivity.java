package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.TransferenciaAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;


public class TransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private CarteiraDao carteiraDao;
    private TransferenciaAdpter transferenciaAdpter;
    private ListView listaCarteiras;
    private Context context;
    private ValueEventListener valueEventListenerCarteira;

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

        // Listagem das carteiras
        //carteiras = new ArrayList<>();

        // Recuperando contatos do Firebase
        Preferencias preferencias = new Preferencias(TransferenciaActivity.this);
        String identificador = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child( identificador );

        // Listener que será notificado toda vez que houver mudança
        valueEventListenerCarteira = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Listas
                List<Carteira> carteiras = new ArrayList<>();

                // Limpar lista de carteira antes de buscar no Firebase
                carteiras.clear();

                // Listar carteiras
                for (DataSnapshot dados : dataSnapshot.getChildren()){
                    Carteira carteiraFb = new Carteira();
                    carteiraFb.setIdentificador( dados.getKey());
                    carteiraFb.setDescricao((String) dados.child("descricao").getValue());
                    carteiraFb.setChave_publica((String) dados.child("chave_publica").getValue());
                    carteiraFb.setChave_privada((String) dados.child("chave_privada").getValue());
                    carteiras.add ( carteiraFb );
                    /*
                    Carteira carteira = dados.getValue( Carteira.class );
                    carteiras.add ( carteira );
                    */
                }

                if (carteiras.size() == 0){
                    Toast.makeText(context, "Nenhuma carteira cadastrada.", Toast.LENGTH_LONG).show();
                }

                transferenciaAdpter = new TransferenciaAdpter(context, carteiras);
                listaCarteiras.setAdapter(transferenciaAdpter);
                // carteiraFbAdapter = new CarteiraFbAdapter(CarteiraActivity.this, carteiras);
                //carteiraFbAdapter.notifyDataSetChanged();
                transferenciaAdpter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };


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
        super.onStart();
       //     atualizarListaTransferencia();
        // método para iniciar a lista
        firebase.addValueEventListener( valueEventListenerCarteira );
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener( valueEventListenerCarteira );
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
