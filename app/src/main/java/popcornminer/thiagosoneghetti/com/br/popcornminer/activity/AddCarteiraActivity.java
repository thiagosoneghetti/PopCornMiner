package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

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
import com.google.firebase.database.DatabaseReference;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class AddCarteiraActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private DatabaseReference referenciaFirebase;
    private EditText eChavePublica;
    private EditText eChavePrivada;
    private EditText eDescricao;
    private Button btSalvarCarteira;
    private CarteiraDao carteiraDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carteira);

        //referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        //referenciaFirebase.child("pontos").setValue("800");

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
                // Confirmando se os campos estão vazios antes de salvar
                if (eChavePublica.getText().toString().equals("") && eChavePrivada.getText().toString().equals("") && eDescricao.getText().toString().equals("")) {
                    Toast.makeText(AddCarteiraActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }else if(eDescricao.getText().toString().equals("")){
                    Toast.makeText(AddCarteiraActivity.this, "Insira uma descrição.", Toast.LENGTH_SHORT).show();
                }else if(eChavePrivada.getText().toString().equals("")){
                    Toast.makeText(AddCarteiraActivity.this, "Insira uma chave privada.", Toast.LENGTH_SHORT).show();
                }else if(eChavePublica.getText().toString().equals("")){
                    Toast.makeText(AddCarteiraActivity.this, "Insira uma chave pública.", Toast.LENGTH_SHORT).show();
                }else{
                    if(eChavePrivada.getText().toString().length() != 64 ) {
                        Toast.makeText(AddCarteiraActivity.this, "Insira uma chave privada válida.", Toast.LENGTH_SHORT).show();
                    }else if(eChavePublica.getText().toString().length() != 66 ) {
                        Toast.makeText(AddCarteiraActivity.this, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                    }else{
                        salvar();
                    }
                }
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
        Toast.makeText(AddCarteiraActivity.this, "Carteira\"" +eDescricao.getText().toString()+ "\" adicionada.", Toast.LENGTH_SHORT).show();

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
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
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
