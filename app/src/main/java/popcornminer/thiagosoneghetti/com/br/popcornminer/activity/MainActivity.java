package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private Button botaoCarteira;
    private Button botaoTransferencia;
    private ValueEventListener valueEventListenerCarteira;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        // Pegando o contexto atual
        context = this;
        // Verificando se o usuário está logado, caso não, voltará para tela de inicio
        verificarSeUsuarioLogado();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        //actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

        // Verificando se possui conexão com a internet, se não, informa para o usuário que está sem internet
        Boolean conexaoInternet = ConexaoInternet.verificaConexao(this);
        if ( conexaoInternet == false ) {
            Toast.makeText(this, "NAVEGAÇÃO OFFLINE: sem conexão com a internet.", Toast.LENGTH_LONG).show();
        }

        // Recuperando os elementos da tela pelo ID
        botaoCarteira = findViewById(R.id.btCarteiraId);
        botaoTransferencia = findViewById(R.id.btTransferenciaId);

        // Botão de carteira, responsável para ir para tela de carteiras
        botaoCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarteiraActivity.class);
                startActivity(intent);
            }
        });
        // Botão de carteira, responsável para ir para tela de transferencias
        botaoTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TransferenciaActivity.class);
                startActivity(intent);
            }
        });


    // Processos para recuperação das carteira no Firebase
        Preferencias preferencias = new Preferencias(MainActivity.this);
        String identificador = preferencias.getIdentificador();

        //Recuperar instância Firebase no local informado : carteiras >> email em base64
        // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
        firebase = Firebase.getFirebaseDatabase().child("carteiras").child(identificador);

        // Listener que será notificado toda vez que houver mudança, para ser executado novamente
        valueEventListenerCarteira = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Lista para adicionar as carteiras
                List<Carteira> carteiras = new ArrayList<>();

                // Limpar lista de carteira antes de buscar no Firebase
                carteiras.clear();

                // Buscando as carteiras existentes no Firebase
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    // Pegando os dados do Firebase para serem salvos na lista
                    Carteira carteiraFb = new Carteira();
                    carteiraFb.setIdentificador(dados.getKey());
                    carteiraFb.setDescricao((String) dados.child("descricao").getValue());
                    carteiraFb.setChave_publica((String) dados.child("chave_publica").getValue());
                    carteiraFb.setChave_privada((String) dados.child("chave_privada").getValue());
                    // Salvando carteira na lista
                    carteiras.add(carteiraFb);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    private void verificarSeUsuarioLogado(){
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        //Verificar se usuário está logado, caso não, volta para tela de login
        if ( usuarioFirebase.getCurrentUser() == null){
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            // Fecha todas activitys que estavam na fila
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Método responsável para chama o listener, onde será feito a busca por carteiras no firebase
        firebase.addValueEventListener(valueEventListenerCarteira);
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
        inflater.inflate(R.menu.menu_home,menu);
        if(usuarioFirebase.getCurrentUser() != null) {
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            MenuItem menuItem = menu.findItem(R.id.bt_mhome_sair);
            menuItem.setTitle("Sair: " + usuarioFirebase.getCurrentUser().getDisplayName());
        }

        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_mhome_sair:
                // Desconecta o usuário atual do aplicativo
                Toast.makeText(context, "Usuário " + usuarioFirebase.getCurrentUser().getDisplayName() +" desconectado.", Toast.LENGTH_SHORT).show();
                usuarioFirebase.signOut();
                Intent irLogin = new Intent(context, LoginActivity.class);
                startActivity(irLogin);
                // Fecha todas activitys que estavam na fila
                finishAffinity();
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

}
