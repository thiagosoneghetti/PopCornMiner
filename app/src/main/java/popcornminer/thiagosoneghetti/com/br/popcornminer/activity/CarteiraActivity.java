package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Base64Custom;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class CarteiraActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference referenciaDatabase;
    private CarteiraDao carteiraDao;
    private CarteiraAdpter carteiraAdpter;
    private ListView listaCarteiras;
    private Context context;
    private FloatingActionButton botaoAddCarteira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();
        referenciaDatabase = ConfiguracaoFirebase.getFirebase();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        carteiraDao = new CarteiraDao(this);
        listaCarteiras = findViewById(R.id.listCarteirasId);
        context = this;

        //rSaldo = new Retrofit.Builder().baseUrl("http://moeda.ucl.br/balance/").addConverterFactory(GsonConverterFactory.create()).build();

        listaCarteiras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if ( conexaoInternet == true) {
                    Carteira carteira = (Carteira) carteiraAdpter.getItem(position);

                    carteira.saldoUC(carteira, context);
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        listaCarteiras.setLongClickable(true);
        listaCarteiras.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Carteira carteira = (Carteira) carteiraAdpter.getItem(position);

                confirmarExclusao(carteira.getId(),carteira.getDescricao());
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

    private void atualizarListaCarteira (){
        List<Carteira> carteiraLista = carteiraDao.recuperarCarteira();
        carteiraAdpter = new CarteiraAdpter(context, carteiraLista);
        listaCarteiras.setAdapter(carteiraAdpter);
    };

    private void confirmarExclusao (final long idExc, final String descricao){
        final long idCarteira = idExc;


        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Remover carteira:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Deseja excluir a carteira \""+ descricao +"\"?");
        msgBox.setCancelable(false); // Não deixar clicar fora da caixa para sair
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Exclusão cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Exclusao(idExc, descricao);
            }
        });
        msgBox.show();
    }

    private void Exclusao (final long idExc, String descricao){
        final long idCarteira = idExc;

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Confirmação de exclusão:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("AVISO: A carteira \""+ descricao +"\" será deletada permanentemente.");
        msgBox.setCancelable(false); // Não deixar clicar fora da caixa para sair
        msgBox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Exclusão cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        msgBox.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carteira,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(irHome);
                break;
            /*case R.id.bt_mcart_carteira:
                Intent irCarteira = new Intent(CarteiraActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                break; */
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(CarteiraActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;
            case R.id.bt_mcart_sair:
                usuarioFirebase  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(CarteiraActivity.this,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}