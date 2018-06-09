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

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar
        //actionBar.setTitle("Navegacao PopCornMiner");

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
    }

    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bt_mhome_sair:
                // Desconecta o usuário atual do aplicativo
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(MainActivity.this,LoginActivity.class);
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
