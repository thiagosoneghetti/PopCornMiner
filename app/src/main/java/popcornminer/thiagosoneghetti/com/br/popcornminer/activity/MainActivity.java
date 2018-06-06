package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;


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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Base64Custom;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebase;
    private Button botaoCarteira;
    private Button botaoTransferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mensagemBemVindo();

        ActionBar actionBar = getSupportActionBar();
//        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(false); // Botão voltar

        //getSupportActionBar().setTitle("Navegacao PopCornMiner");

        botaoCarteira = findViewById(R.id.btCarteiraId);
        botaoTransferencia = findViewById(R.id.btTransferenciaId);

        botaoCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarteiraActivity.class);
                startActivity(intent);
            }
        });

        botaoTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TransferenciaActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case android.R.id.home:
                Intent btVoltar = new Intent(MainActivity.this,MainActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mhome_home:
                Intent irHome = new Intent(MainActivity.this,MainActivity.class);
                startActivity(irHome);
                break;*/
            /*case R.id.bt_mhome_carteira:
                Intent irCarteira = new Intent(MainActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                break;
            case R.id.bt_mhome_transferencia:
                Intent irTransferencia = new Intent(MainActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;*/
            case R.id.bt_mhome_sair:
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    public void mensagemBemVindo(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser usuarioFirebase = autenticacao.getCurrentUser();
        // Buscando e-mail do usuário no Firebase
        String email = usuarioFirebase.getEmail();

        /*String identificador = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        firebase = ConfiguracaoFirebase.getFirebase().child("usuario").child(identificador);
        String nome = new (firebase.child("nome"));
        */
        Toast.makeText(MainActivity.this, "Seja Bem-Vindo! Logado com "+ email +"!", Toast.LENGTH_LONG).show();

    }
}
