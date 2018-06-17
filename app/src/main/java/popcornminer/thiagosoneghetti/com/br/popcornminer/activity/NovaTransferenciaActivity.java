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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
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
    private Button btnScanQR;
    private Button btnGerarQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_transferencia);

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

        context = this;
        // Recuperando os elementos da tela pelo ID
        botaoTransferir = findViewById(R.id.btTransferirId);
        editCPublicaDest = findViewById(R.id.editCPublicaDestinoId);
        editValorTranf = findViewById(R.id.editValorTransfId);
        editDescricaoCarteira = findViewById(R.id.editDescricaoTransfId);

        // Recuperando os dados que foram passados na Activity anterior
        final Intent intent = getIntent();
        if(intent.getSerializableExtra("carteira") != null) {
            carteira = (Carteira) intent.getSerializableExtra("carteira");
            // Passando o nome da carteira selecionada para a tela, mostrando o nome da carteira na tela de transferencia
            editDescricaoCarteira.setText(carteira.getDescricao());
        }

        // Botão responsável por acionar a transferencia
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
                        // Método responsável pela confirmação da transferência, se selecionado sim, será realizada, se não, cancelada.
                        confirmarTransferencia(carteira, chave_publica_destino, valor);
                    }
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Código responsável por ir para Activity que gera o QR Code
        btnGerarQR = findViewById(R.id.btnGerarQR);

        btnGerarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Passando os dados da carteira para outra Activity para geração do QR Code
                Intent intentQR = new Intent(NovaTransferenciaActivity.this, GeradorQrCode.class);
                intentQR.putExtra("carteira",carteira);
                startActivity(intentQR);
            }
        });


        // Código responsável pelo Scaner de QRcode
        btnScanQR = findViewById(R.id.btnScanQR);

        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            IntentIntegrator intentIntegrator = new IntentIntegrator(NovaTransferenciaActivity.this);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES); // Formato do código
            intentIntegrator.setPrompt("Scanear QR Code do destinatário"); // Mensagem que aparece na parte inferior da tela
            intentIntegrator.setOrientationLocked(false); //Habilitar rotação da tela de acordo com a orientação
            intentIntegrator.setCameraId(0); // Camera transeira
            intentIntegrator.initiateScan(); // Inicializa o Scan
            }
        });


    }

    // Método responsável por pegar os dados scaneado no QR code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(result != null){
            if(result.getContents() != null){
                Toast.makeText(context, "Leitura QR CODE realizada!", Toast.LENGTH_LONG).show();
                // Passando o dado scaneado para o EditText da chave publica destino
                editCPublicaDest.setText(result.getContents());
            }else {
                Toast.makeText(context, "Leitura QR CODE cancelada!", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    // Pede para usuário confirmar a transferencia, caso sim realiza a transferencia, se não, é abortada a transferencia
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
            // Chama o método que é responsável por realizar a transferencia
            carteiraDestino.transferenciaUC(carteiraDestino, chavePublicaDestino, valorDestino, NovaTransferenciaActivity.this);
            // Volta para a tela de transferencias
            Intent intent = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
            startActivity(intent);
            }
        });
        msgBox.show();
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
            case R.id.bt_mtransf_sair:
                // Desconecta o usuário atual do aplicativo
                autenticacao  = Firebase.getFirebaseAutenticacao();
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
