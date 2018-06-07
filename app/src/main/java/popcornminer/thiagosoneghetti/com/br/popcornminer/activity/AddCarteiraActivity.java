package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Base64Custom;
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
    private CarteiraDao carteiraDao;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_carteira);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //firebase = ConfiguracaoFirebase.getFirebase();
        //firebase.child("pontos").setValue("800");

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        context = this;
        eChavePublica = findViewById(R.id.editChavePublicaId);
        eChavePrivada = findViewById(R.id.editChavePrivadaId);
        eDescricao =  findViewById(R.id.editDescricaoId);
        btSalvarCarteira = findViewById(R.id.btSalvarCarteiraId);
        carteiraDao = new CarteiraDao(context);

        btSalvarCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chavepublica = eChavePublica.getText().toString();
                String chaveprivada = eChavePrivada.getText().toString();
                String descricao = eDescricao.getText().toString();

                // Confirmando se algum dos campos estão vazios antes de salvar
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
                    if(chaveprivada.length() != 64 || chavepublica.length() != 66) {
                        // Verificando qual chave é inválida e dando retorno
                        if (chaveprivada.length() != 64) {
                            Toast.makeText(context, "Insira uma chave privada válida.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Insira uma chave pública válida.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        // Salvando a carteira
                        //salvar(chavepublica, chaveprivada, descricao);
                        salvarFB(chavepublica, chaveprivada, descricao);
                    }
                }
            }
        });
    }

    public void salvar(String chavepublica, String chaveprivada, String descricao){

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
     }

     public void salvarFB(String chavepublica, String chaveprivada, String descricao){

         final Carteira carteiraFB = new Carteira(chavepublica, chaveprivada, descricao);

         //Recuperar o usuário pelo ID Base 64
         Preferencias preferencias = new Preferencias(context);
         String indentificador = preferencias.getIdentificador();
         Log.i("log - identificador",indentificador);

         //Recuperar instância Firebase
         // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
         firebase = ConfiguracaoFirebase.getFirebase().child("usuario").child(indentificador);

        //Era da parte 213 e 214 - verificando se havia e-mail cadastrado
         firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Recuperar dados da conta vinculada ao identificador
                Usuario usuario = dataSnapshot.getValue( Usuario.class );

                // Verifica se o usuário existe
                if ( dataSnapshot.getValue() == null){
                    // pode ser que de erro por conta do identificador do preferentes - VERIFICAr
                    //Recuperar o usuário pelo ID Base 64
                    Preferencias preferencias = new Preferencias(context);
                    String indentificador = preferencias.getIdentificador();
                    Log.i("log - identificador Sn",indentificador);

                    firebase = ConfiguracaoFirebase.getFirebase();
                    firebase = firebase.child("carteira")
                                       .child( indentificador ).push();

                    firebase.setValue( carteiraFB );

                    //carteiraDao.inserir( carteiraFB );
                    Toast.makeText(AddCarteiraActivity.this, "Carteira\""+ carteiraFB.getDescricao() +"\" adicionada.", Toast.LENGTH_SHORT).show();

                    finish();






                }else{
                    // Caso não encontre a conta criada
                    Toast.makeText(context, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                Intent btVoltar = new Intent(context,CarteiraActivity.class);
                startActivity(btVoltar);
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(context,MainActivity.class);
                startActivity(irHome);
                break;
            /*case R.id.bt_mcart_carteira:
                Intent irCarteira = new Intent(context,CarteiraActivity.class);
                startActivity(irCarteira);
                break; */
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(context,TransferenciaActivity.class);
                startActivity(irTransferencia);
                break;
            case R.id.bt_mcart_sair:
                usuarioFirebase  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(context,LoginActivity.class);
                startActivity(irLogin);
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }
}
