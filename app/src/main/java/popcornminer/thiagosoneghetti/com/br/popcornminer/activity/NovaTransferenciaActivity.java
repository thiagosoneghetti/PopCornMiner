package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class NovaTransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
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

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        context = this;
        botaoTransferir = findViewById(R.id.btTransferirId);
        editCPublicaDest = findViewById(R.id.editCPublicaDestinoId);
        editValorTranf = findViewById(R.id.editValorTransfId);
        editDescricaoCarteira = findViewById(R.id.editDescricaoTransfId);



        // Recuperando os dados que foram passados na Activity anterior
        final Intent intent = getIntent();
        if(intent.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intent.getSerializableExtra("carteira");

            editDescricaoCarteira.setText(carteira.getDescricao());
        }

        botaoTransferir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificando se possui conexão com a internet
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if (conexaoInternet == true) {
                    // Verifica se algum dos campos está vazio, se não pula para verificação do tamanho da chave pública
                    if (editCPublicaDest.getText().toString().equals("") || editValorTranf.getText().toString().equals("")) {
                        // Verifica quais campos estão vazios
                        if (editCPublicaDest.getText().toString().equals("") && editValorTranf.getText().toString().equals("")) {
                            Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                        } else if (editCPublicaDest.getText().toString().equals("")) {
                            Toast.makeText(context, "Insira a chave pública de destino.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Insira um valor.", Toast.LENGTH_SHORT).show();
                        }
                        // Valida se a chave pública inserida possui 66 caracteres
                    } else if (editCPublicaDest.getText().toString().length() != 66) {
                        Toast.makeText(context, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Capturando os dados necessários para transação
                        String chave_publica_destino = editCPublicaDest.getText().toString();
                        Float valor = Float.parseFloat(editValorTranf.getText().toString());

                        confirmarTransferencia(carteira, chave_publica_destino, valor);
                    }
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
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
        msgBox.setCancelable(false); // Não deixar clicar fora da caixa para sair
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
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
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
