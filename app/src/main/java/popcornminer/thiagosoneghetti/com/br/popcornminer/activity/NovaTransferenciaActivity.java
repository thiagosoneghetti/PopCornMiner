package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class NovaTransferenciaActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private Carteira carteira;
    private Button botaoTransferir;
    private TextView editDescricaoCarteira;
    private EditText editCPublicaDest;
    private EditText editValorTranf;
    private Context context;
    private ImageView btnScanQR;
    private Button btnGerarQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_transferencia);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = Firebase.getFirebaseAutenticacao();
        // Pegando o contexto atual
        context = this;
        // Verificando se o usuário está logado, caso não, voltará para tela de inicio
        verificarSeUsuarioLogado();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

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
/*
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
*/

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

    // Pede para usuário confirmar a transferencia, caso sim realiza a transferencia, se não, é abortada a transferencia
    private void confirmarTransferencia (Carteira carteira, String chave_publica_destino, Float valor ){
        final Carteira carteiraDestino = carteira;
        final String chavePublicaDestino = chave_publica_destino;
        final Float valorDestino = valor;

        AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
        msgBox.setTitle("Confirmação de Transação:");
        msgBox.setIcon(android.R.drawable.ic_menu_send);
        msgBox.setMessage("Transferir UC "+ valorDestino +" para \""+ chavePublicaDestino +"\"?");
        final EditText confirmarSenha = new EditText(context);
        confirmarSenha.setGravity(Gravity.CENTER);
        confirmarSenha.setHint("Digite sua senha");
        confirmarSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmarSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
        msgBox.setView(confirmarSenha);
        confirmarSenha.setWidth(60);
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
                usuarioFirebase = Firebase.getFirebaseAutenticacao();
                FirebaseUser usuario = usuarioFirebase.getCurrentUser();

                if(usuarioFirebase.getCurrentUser() != null) {
                    if (confirmarSenha.getText().toString().equals("")) {
                        Toast.makeText(context, "Informe sua senha.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Passando email e senha para validação
                        AuthCredential credencial = EmailAuthProvider
                                .getCredential(usuario.getEmail(), confirmarSenha.getText().toString());

                        usuario.reauthenticate(credencial)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            // Chama o método que é responsável por realizar a transferencia
                                            carteiraDestino.transferenciaUC(carteiraDestino, chavePublicaDestino, valorDestino, NovaTransferenciaActivity.this);
                                            // Volta para a tela de transferencias
                                            Intent intent = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(context, "Senha Incorreta.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }else{
                    Toast.makeText(context, "Nenhum usuário autenticado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        msgBox.show();
    }





    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_transferencia,menu);
        if(usuarioFirebase.getCurrentUser() != null) {
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            // Mudando o texto do botão sair para mostar Sair: Nome do usuário
            MenuItem menuItem = menu.findItem(R.id.bt_mtransf_sair);
            menuItem.setTitle("Sair: " + usuarioFirebase.getCurrentUser().getDisplayName());
        }

        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(NovaTransferenciaActivity.this,TransferenciaActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mtransf_home:
                Intent irHome = new Intent(NovaTransferenciaActivity.this,MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            case R.id.bt_mtransf_carteira:
                Intent irCarteira = new Intent(NovaTransferenciaActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                finish();
                break;
            case R.id.bt_mtransf_sair:
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
