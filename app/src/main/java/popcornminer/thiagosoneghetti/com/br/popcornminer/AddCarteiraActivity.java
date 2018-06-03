package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;

public class AddCarteiraActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private EditText eChavePublica;
    private EditText eChavePrivada;
    private EditText eDescricao;
    private Button btSalvarCarteira;
    private CarteiraDao carteiraDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carteira);

        firebaseAuth  = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        eChavePublica = (EditText) findViewById(R.id.editChavePublicaId);
        eChavePrivada = (EditText) findViewById(R.id.editChavePrivadaId);
        eDescricao = (EditText) findViewById(R.id.editDescricaoId);
        btSalvarCarteira = (Button) findViewById(R.id.btSalvarCarteiraId);
        carteiraDao = new CarteiraDao(this);

        btSalvarCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        });

    }

    public void salvar(){
        Carteira carteira = new Carteira(
                eChavePublica.getText().toString(),
                eChavePrivada.getText().toString(),
                eDescricao.getText().toString()
        );

        carteiraDao.inserir(carteira);
        Toast.makeText(AddCarteiraActivity.this, "Adicionado com sucesso", Toast.LENGTH_SHORT).show();

        finish();
     };

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
                Intent btVoltar = new Intent(AddCarteiraActivity.this,CarteiraActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(AddCarteiraActivity.this,MainActivity.class);
                startActivity(irHome);
                break;
            /*case R.id.bt_mcart_carteira:
                Intent irCarteira = new Intent(AddCarteiraActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                break; */
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(AddCarteiraActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;
            case R.id.bt_mcart_sair:
                firebaseAuth.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(AddCarteiraActivity.this,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
