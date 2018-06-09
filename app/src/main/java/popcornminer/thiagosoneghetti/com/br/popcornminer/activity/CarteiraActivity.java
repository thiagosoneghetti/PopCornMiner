package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.CarteiraFbAdapter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class CarteiraActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private CarteiraDao carteiraDao;
    private CarteiraAdpter carteiraAdpter;
    private ListView listaCarteiras;
    private Context context;
    private FloatingActionButton botaoAddCarteira;
    private CarteiraFbAdapter carteiraFbAdapter;
   // private ArrayList<Carteira> carteiras;
    private ValueEventListener valueEventListenerCarteira;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        carteiraDao = new CarteiraDao(this);
        listaCarteiras = findViewById(R.id.listCarteirasId);
        context = this;


        // Listagem das carteiras
        //carteiras = new ArrayList<>();

        // Recuperando contatos do Firebase
        Preferencias preferencias = new Preferencias(CarteiraActivity.this);
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
                    Log.i("carteirasfb",carteiraFb.getIdentificador());
                }

                if (carteiras.size() == 0){
                    Toast.makeText(context, "Nenhuma carteira cadastrada.", Toast.LENGTH_LONG).show();
                }

                carteiraAdpter = new CarteiraAdpter(context, carteiras);
                listaCarteiras.setAdapter(carteiraAdpter);
                // carteiraFbAdapter = new CarteiraFbAdapter(CarteiraActivity.this, carteiras);
                //carteiraFbAdapter.notifyDataSetChanged();
                carteiraAdpter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };


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
                Log.i("carteiraexclu",carteira.getIdentificador());
                // Exclusão Firebase
                confirmarExclusaoFB(carteira.getIdentificador(), carteira.getDescricao());


                // Exclusao pelo SQLite
                //confirmarExclusao(carteira.getId(),carteira.getDescricao());
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

/*
   private void recuperarCarteiraFB(){

        final ArrayList<Carteira> carteiras = null;

        // Recuperar identificador usuário logado
        Preferencias preferencias = new Preferencias(CarteiraActivity.this);
        String identificador = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child( identificador );

        // Listener para recuperar as carteiras a cada vez que forem alteradas
        valueEventListenerCarteira = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Instânciar objetos
                //carteiras = new ArrayList<>();
                //List<Carteira> carteiraLista  = new ArrayList<>();

                // Limpar a lista de carteiras, para evitar duplicidade
                carteiras.clear();

                // Listar carteiras
                // dados irá receber o filhos do dataSnapshot que passamos acima na firebase, no caso pegará os filhos que possuem aquele identificador
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    //
                    Carteira carteiraFB = dados.getValue( Carteira.class );
                    carteiras.add( carteiraFB );

                    Log.i("Log-carteiraFB", carteiraFB.toString());

                }

                // Notificar para o adapter que houve uma alteração
                carteiraFbAdapter.notifyDataSetChanged();

                //List<Carteira> carteiraLista = carteiras;
                carteiraFbAdapter = new CarteiraFbAdapter(CarteiraActivity.this, carteiras);
                Log.i("Log-Lista Carteira", carteiras.toString());
                listaCarteiras.setAdapter(carteiraFbAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        firebase.addValueEventListener( valueEventListenerCarteira );
    }
*/

    private void confirmarExclusaoFB (final String idCarteiraFb, final String descricao){



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
                ExclusaoFB(idCarteiraFb, descricao);
            }
        });
        msgBox.show();
    }

    private void ExclusaoFB (final String idCarteiraFb, String descricao){

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
                Preferencias preferencias = new Preferencias(CarteiraActivity.this);
                String identificador = preferencias.getIdentificador();

                firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child( identificador ).child(idCarteiraFb);
                firebase.removeValue();

            }
        });
        msgBox.show();
    }

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
                //atualizarListaCarteira();
                //recuperarCarteiraFB();
            }
        });
        msgBox.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //atualizarListaCarteira();

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
        inflater.inflate(R.menu.menu_carteira,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            /*case R.id.bt_mcart_carteira:
                Intent irCarteira = new Intent(CarteiraActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                finish();
                break; */
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(CarteiraActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                finish();
                break;
            case R.id.bt_mcart_sair:
                usuarioFirebase  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(CarteiraActivity.this,LoginActivity.class);
                startActivity(irLogin);
                finish();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}