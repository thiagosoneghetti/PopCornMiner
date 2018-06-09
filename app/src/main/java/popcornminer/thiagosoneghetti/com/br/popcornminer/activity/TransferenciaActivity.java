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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;


public class TransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
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

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar


        //carteiraDao = new CarteiraDao(this);  // Não utilziado, somente no SQLite
        listaCarteiras = findViewById(R.id.listTransferenciaId);
        context = this;

    // Processos para recuperação das carteira no Firebase
        // Verificando se possui conexão com a internet, se sim busca lista de contatos, se não informa para o usuário que está sem internet
        Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
        if ( conexaoInternet == true ) {
            Preferencias preferencias = new Preferencias(TransferenciaActivity.this);
            String identificador = preferencias.getIdentificador();

            //Recuperar instância Firebase no local informado : carteiras >> email em base64
            // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
            firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child( identificador );

            // Listener que será notificado toda vez que houver mudança, para ser executado novamente
            valueEventListenerCarteira = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Lista para adicionar as carteiras
                    List<Carteira> carteiras = new ArrayList<>();

                    // Limpar lista de carteira antes de buscar no Firebase
                    carteiras.clear();

                    // Buscando as carteiras existentes no Firebase
                    for (DataSnapshot dados : dataSnapshot.getChildren()){
                        // Pegando os dados do Firebase para serem salvos na lista
                        Carteira carteiraFb = new Carteira();
                        carteiraFb.setIdentificador( dados.getKey());
                        carteiraFb.setDescricao((String) dados.child("descricao").getValue());
                        carteiraFb.setChave_publica((String) dados.child("chave_publica").getValue());
                        carteiraFb.setChave_privada((String) dados.child("chave_privada").getValue());
                        // Salvando carteira na lista
                        carteiras.add ( carteiraFb );
                    }
                    // Caso não haja nenhuma carteira cadastrada irá mostra mensagem para o usuário
                    if (carteiras.size() == 0){
                        Toast.makeText(context, "Nenhuma carteira cadastrada.", Toast.LENGTH_LONG).show();
                    }

                    // Passando a lista de carteiras para o adapter que mostrará as carteiras na tela
                    transferenciaAdpter = new TransferenciaAdpter(context, carteiras);
                    listaCarteiras.setAdapter(transferenciaAdpter);
                    // Notifica o adapter caso haja alguma alteração no firebase, para a lista ser atualizada
                    transferenciaAdpter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        } else{
            Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
        }


        // Ao clicar em uma carteira da lista, é passada a carteira selecionada para a outra view
        listaCarteiras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Carteira carteira = (Carteira) transferenciaAdpter.getItem(position);
            // Vai para outra tela levando as informações da carteira selecionada
            Intent intent = new Intent(TransferenciaActivity.this, NovaTransferenciaActivity.class);
            intent.putExtra("carteira", carteira);
            startActivity(intent);
            }
        });

    }

    // Atualizar a lista de carteiras do SQLite, não utitilizado mais
/*    private void atualizarListaTransferencia (){
        List<Carteira> carteiraLista = carteiraDao.recuperarCarteira();
        transferenciaAdpter = new TransferenciaAdpter(context, carteiraLista);
        listaCarteiras.setAdapter(transferenciaAdpter);
    };*/


    @Override
    protected void onStart() {
        super.onStart();
        // Era utilizado para atualizar a lista de carteira do SQLite
        //atualizarListaCarteira();

        // Método responsável para chama o listener, onde será feito a busca por carteiras no firebase
        firebase.addValueEventListener( valueEventListenerCarteira );
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Para o método listener que é responsável por ficar escutando modificações no firebase
        firebase.removeEventListener( valueEventListenerCarteira );
    }

    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transferencia,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
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
            case R.id.bt_mtransf_sair:
                // Desconecta o usuário atual do aplicativo
                usuarioFirebase  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
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
