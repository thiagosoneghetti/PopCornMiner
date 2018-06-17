package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class AddCarteiraActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private EditText eChavePublica;
    private EditText eChavePrivada;
    private EditText eDescricao;
    private Button btSalvarCarteira;
    private Button btnScanAddCart;
    private CarteiraDao carteiraDao;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carteira);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = Firebase.getFirebaseAutenticacao();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

        context = this;
        // Recuperando os elementos da tela pelo ID
        eChavePublica = findViewById(R.id.editChavePublicaId);
        eChavePrivada = findViewById(R.id.editChavePrivadaId);
        eDescricao =  findViewById(R.id.editDescricaoId);
        btSalvarCarteira = findViewById(R.id.btSalvarCarteiraId);
        carteiraDao = new CarteiraDao(context);

        // Função do botão "Adicionar" que faz a verificação dos campos e salva a carteira
        btSalvarCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chavepublica = eChavePublica.getText().toString();
                String chaveprivada = eChavePrivada.getText().toString();
                String descricao = eDescricao.getText().toString();

                // Confirmando se algum campo está vazio antes de salvar
                if (chavepublica.equals("") || chaveprivada.equals("") || descricao.equals("")) {
                    // Verifiquando quais campos estão vazios e exibindo retorno
                    if (chavepublica.equals("") && chaveprivada.equals("") && descricao.equals("")) {
                        Toast.makeText(context, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                    } else if (descricao.equals("")) {
                        Toast.makeText(context, "Insira uma descrição.", Toast.LENGTH_SHORT).show();
                    } else if (chaveprivada.equals("")) {
                        Toast.makeText(context, "Insira uma chave privada.", Toast.LENGTH_SHORT).show();
                    } else if (chavepublica.equals("")) {
                        Toast.makeText(context, "Insira uma chave pública.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Verificando se há alguma chave inválida
                    if (chaveprivada.length() != 64 || chavepublica.length() != 66) {
                        // Verificando qual chave é inválida e dando retorno
                        if (chaveprivada.length() != 64) {
                            Toast.makeText(context, "Insira uma chave privada válida.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Salvando a carteira
                        // Método abaixo comentado é para salvar no SQLite, não utilizado mais
                        //salvar(chavepublica, chaveprivada, descricao);

                        // Método para salvar carteira no Firebase
                        salvarFB(chavepublica, chaveprivada, descricao);
                    }
                }
            }
        });

        // Código responsável pelo Scaner de QRcode
        btnScanAddCart = findViewById(R.id.btnScanAddCart);

        btnScanAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(AddCarteiraActivity.this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                intentIntegrator.setPrompt("Scanear QR Code da Carteira"); // Mensagem que aparece na parte inferior da tela
                intentIntegrator.setOrientationLocked(false);  // Habilitar rotação da tela de acordo com a orientação
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
                String resultado = result.getContents();
                // Se o resultado conter 160 caracteres, fará a separação das chaves (QR code padrão PopCornMiner)
                if(resultado.length() == 160){
                    eChavePublica.setText(resultado.substring(13,79));
                    eChavePrivada.setText(resultado.substring(94,158));
                // Se não, irá jogar para os dois campos o resultado do QR Code lido
                } else {
                    eChavePublica.setText(resultado);
                    eChavePrivada.setText(resultado);
                }

                Toast.makeText(context, "Leitura QR CODE realizada!", Toast.LENGTH_LONG).show();
                // Passando o dado scaneado para o EditText da chave publica destino
                //editCPublicaDest.setText(result.getContents());
            }else {
                Toast.makeText(context, "Leitura QR CODE cancelada!", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }


    // Salvar a carteira de forma Offiline, somente no dispositivo, não sendo utilizado no momento
/*    public void salvar(String chavepublica, String chaveprivada, String descricao){

        Carteira carteira = new Carteira(
            chavepublica,
            chaveprivada,
            descricao
        );

        carteiraDao.inserir(carteira);
        Toast.makeText(context, "Carteira "+ descricao +" adicionada.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, CarteiraActivity.class);
        startActivity(intent);
        finish();
     }*/

     public void salvarFB(String chavepublica, String chaveprivada, String descricao){

         final Carteira carteiraFB = new Carteira(chavepublica, chaveprivada, descricao);

         //Recuperar o usuário pelo ID Base 64
         Preferencias preferencias = new Preferencias(context);
         String identificador = preferencias.getIdentificador();

         //Recuperar instância Firebase no local informado : usuarios >> email em base64
         // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
         firebase = Firebase.getFirebaseDatabase().child("usuarios").child(identificador);

         firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Recuperar dados da conta vinculada ao identificador
                Usuario usuario = dataSnapshot.getValue( Usuario.class );

                // Verifica se o usuário existe
                if ( dataSnapshot.getValue() != null){

                    //Recuperar o usuário pelo ID Base 64
                    Preferencias preferencias = new Preferencias(context);
                    String identificador = preferencias.getIdentificador();

                    firebase = Firebase.getFirebaseDatabase();
                    // Adiciono um nó carteiras, um outro nó com o identificador (email base64) e um push para gerar uma key
                    firebase = firebase.child("carteiras")
                                       .child( identificador ).push();

                    // Adiciona a carteira para o Firebase
                    firebase.setValue( carteiraFB );

                    // Verificando se possui conexão com a internet, se não, informa para o usuário que a carteira foi salva localmente
                    Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                    if ( conexaoInternet == true ) {
                        Toast.makeText(AddCarteiraActivity.this, "Carteira "+ carteiraFB.getDescricao() +" adicionada.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,"SEM INTERNET: Carteira "+ carteiraFB.getDescricao() +" foi salva localmente, será salva no servidor após conexão ser restabelecida." , Toast.LENGTH_LONG).show();
                    }

                    // Abaixo método que inseria localmente a carteira no SQLite, não utilizado mais
                    //carteiraDao.inserir( carteiraFB );

                    // Após cadastrar vai para tela de Carteiras
                    Intent intent = new Intent(context, CarteiraActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    // Caso não encontre a conta criada, retorna uma mensagem para o usuário
                    Toast.makeText(context, "Usuário não encontrado!", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     };

    // Criação do Menu na action bar, onde é possivel fazer logout, ir para outras telas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carteira,menu);
        return super.onCreateOptionsMenu(menu);
    }
    // Opções que foram configuradas para aparecer no menu, são acões para irem para outras telas, e fazer logout
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(context,CarteiraActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(context,MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(context,TransferenciaActivity.class);
                startActivity(irTransferencia);
                finish();
                break;
            case R.id.bt_mcart_sair:
                // Desconecta o usuário atual do aplicativo
                usuarioFirebase  = Firebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(context,LoginActivity.class);
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
