package popcornminer.thiagosoneghetti.com.br.popcornminer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Model.Transferencia;
import popcornminer.thiagosoneghetti.com.br.popcornminer.Requests.CarteiraRequests;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class NovaTransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Carteira carteira;
    private Button botaoTransferir;
    private TextView editDescricaoCarteira;
    private EditText editCPublicaDest;
    private EditText editValorTranf;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_transferencia);

        firebaseAuth  = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        botaoTransferir = findViewById(R.id.btTransferirId);
        editCPublicaDest = findViewById(R.id.editCPublicaDestinoId);
        editValorTranf = findViewById(R.id.editValorTransfId);
        editDescricaoCarteira = findViewById(R.id.editDescricaoTransfId);



        // Recuperando os dados que foram passados na Activity anterior
        Intent intent = getIntent();
        if(intent.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intent.getSerializableExtra("carteira");

            editDescricaoCarteira.setText(carteira.getDescricao());
        }

        botaoTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // verificando se os campos estão preenchidos
                if (editCPublicaDest.getText().toString().equals("") && editValorTranf.getText().toString().equals("") ) {
                    Toast.makeText(NovaTransferenciaActivity.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                }else if(editCPublicaDest.getText().toString().equals("")) {
                    Toast.makeText(NovaTransferenciaActivity.this, "Insira a chave pública de destino.", Toast.LENGTH_SHORT).show();
                }else if(editValorTranf.getText().toString().equals("")) {
                    Toast.makeText(NovaTransferenciaActivity.this, "Insira um valor.", Toast.LENGTH_SHORT).show();
                }else if(editCPublicaDest.getText().toString().length() != 66){
                    Toast.makeText(NovaTransferenciaActivity.this, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                }else {
                    // Carpturando os dados necessários para transação
                    String chave_publica_destino = editCPublicaDest.getText().toString();
                    Float valor = Float.parseFloat(editValorTranf.getText().toString());

                    confirmarTransferencia(carteira, chave_publica_destino, valor);
                }
            }
        });

    }

    private void confirmarTransferencia (Carteira carteira, String chave_publica_destino, Float valor ){
        final Carteira carteiraDestino = carteira;
        final String chavePublicaDestino = chave_publica_destino;
        final Float valorDestino = valor;

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Confirmação de Transação:");
        msgBox.setIcon(android.R.drawable.ic_menu_send);
        msgBox.setMessage("Transferir UC "+ valorDestino +" para \""+ chavePublicaDestino +"\"?");
        msgBox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NovaTransferenciaActivity.this, "Transação cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        msgBox.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                carteiraDestino.transferenciaUC(carteiraDestino, chavePublicaDestino, valorDestino, NovaTransferenciaActivity.this);

                Intent intent = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                startActivity(intent);
            }
        });
        msgBox.show();
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
                Intent btVoltar = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mtransf_home:
                Intent irHome = new Intent(NovaTransferenciaActivity.this,MainActivity.class);
                startActivity(irHome);
                break;
            case R.id.bt_mtransf_carteira:
                Intent irCarteira = new Intent(NovaTransferenciaActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                break;
            /*case R.id.bt_mtransf_transferencia:
                Intent irTransferencia = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;*/
            case R.id.bt_mtransf_sair:
                firebaseAuth.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(NovaTransferenciaActivity.this,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }


}
