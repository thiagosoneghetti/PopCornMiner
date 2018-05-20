package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;

public class AddCarteiraActivity extends AppCompatActivity {
    private EditText eChavePublica;
    private EditText eChavePrivada;
    private EditText eDescricao;
    private Button btSalvarCarteira;
    private CarteiraDao carteiraDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carteira);
        getSupportActionBar().setTitle("Adicionar Carteira");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
