package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import popcornminer.thiagosoneghetti.com.br.popcornminer.R;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Base64Custom;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Usuario;

public class CadastrarLoginActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button botaoCadastrar;
    private EditText editNome;
    private EditText editEmail;
    private EditText editSenha;
    private EditText editConfSenha;
    private Usuario usuario;
    private Context context;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_login);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayShowHomeEnabled(true); // Oculta o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Botão voltar

        context = this;

        botaoCadastrar = findViewById(R.id.btCadastrarLogin);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editConfSenha = findViewById(R.id.editConfSenha);


        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString();
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                String confsenha = editConfSenha.getText().toString();

                // Verificando se possui conexão com a internet
                  Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                  if ( conexaoInternet == true ) {

                      if ( nome.equals("") || email.equals("") || senha.equals("") || confsenha.equals("")) {
                          // Verificando se os campos estão vazios, caso estejam apresenta uma mensagem informando.
                          if ( nome.equals("") && email.equals("") && senha.equals("") && confsenha.equals("")) {
                              Toast.makeText(context, "Digite seu nome, e-mail e sua senha!", Toast.LENGTH_SHORT).show();
                          } else if ( nome.equals("") ) {
                              Toast.makeText(context, "Digite seu nome!", Toast.LENGTH_SHORT).show();
                          } else if ( email.equals("") ) {
                              Toast.makeText(context, "Digite seu e-mail!", Toast.LENGTH_SHORT).show();
                          } else if ( senha.equals("") ) {
                              Toast.makeText(context, "Digite sua senha!", Toast.LENGTH_SHORT).show();
                          } else {
                              Toast.makeText(context, "Digite a confirmação de senha!", Toast.LENGTH_SHORT).show();
                          }
                      } else {
                          // Se a senha for igual a confirmação de senha fará o cadastro
                          if (senha.equals(confsenha)) {

                              usuario = new Usuario();
                              usuario.setNome( nome );
                              usuario.setEmail( email );
                              usuario.setSenha( senha );

                              // Método responsável por salvar usuário no Firebase
                              cadastrarUsuario();

                          } else {
                              // Verificando se a senha e confirmação de senha conferem
                              Toast.makeText(context, "Senhas não conferem!", Toast.LENGTH_SHORT).show();
                              editSenha.setText("");
                              editConfSenha.setText("");
                          }
                      }
                  } else{
                      Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                  }
            }
        });
    }

    private void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Relização do Cadastro
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
            .addOnCompleteListener(CadastrarLoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                // verificando se o usuário foi criado com sucesso
                if (task.isSuccessful()){
                    //Toast.makeText(context, "Sucesso ao cadastrar usuário!", Toast.LENGTH_SHORT).show();

                    // Recuperando usuário criado no Firebase
                    // FirebaseUser usuarioFirebase = task.getResult().getUser();

                    // Convertendo o e-mail do usuário para Base 64 para gerar ID
                    String indentificadorUsuario = Base64Custom.codificarBase64( usuario.getEmail() );
                    usuario.setId( indentificadorUsuario );

                    // Salvando nao SharedPreferences o ID base 64 gerado pelo e-mail
                    // Convertendo e=mail para base 64, que será nosso ID único
                    Preferencias preferencias  = new Preferencias(context);
                    preferencias.salvarDados(indentificadorUsuario);

                    // Método responsável por salvar os dados do usuário no Firebase
                    usuario.salvar();

                    abrirTelaPrincipal();

                    }else{
                        // Tratamento de excessões
                        String erroExcessao;
                        try {
                            throw task.getException();
                        }catch (FirebaseAuthWeakPasswordException e){
                            erroExcessao = "Digite uma senha mais forte, contendo mais caracteres e com letras e números!";
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            erroExcessao = "E-mail digitado é inválido, digite um novo e-mail!";
                        }catch (FirebaseAuthUserCollisionException e){
                            erroExcessao = "E-mail já existente, digite um novo e-mail!";
                        }catch (Exception e){
                            erroExcessao = "Erro ao efeturar cadastro!";
                            e.printStackTrace();
                        }
                        Toast.makeText(context, ""+erroExcessao, Toast.LENGTH_SHORT).show();
                    }
                }
            });

    };
    // ir para tela de principal
    private void abrirTelaPrincipal (){
        mensagemBemVindo();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        //Encerrando Activity de cadastro
        finish();
    }

    public void mensagemBemVindo(){
        Preferencias preferencias = new Preferencias(CadastrarLoginActivity.this);
        String identificador = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificador);

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Listar carteiras

                Usuario usuario = dataSnapshot.getValue( Usuario.class );
                Toast.makeText(CadastrarLoginActivity.this, "Seja Bem-Vindo, "+usuario.getNome()+"! Usuário cadastrado com sucesso.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent btVoltar = new Intent(context, LoginActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            /*case R.id.bt_mhome_home:
                Intent irHome = new Intent(CadastrarLoginActivity.this,MainActivity.class);
                startActivity(irHome);
                finish();
                break;*/
            /*case R.id.bt_mhome_carteira:
                Intent irCarteira = new Intent(CadastrarLoginActivity.this,CarteiraActivity.class);
                startActivity(irCarteira);
                finish();
                break;
            case R.id.bt_mhome_transferencia:
                Intent irTransferencia = new Intent(CadastrarLoginActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                finish();
                break;
            case R.id.bt_mhome_sair:
                autenticacao  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(CadastrarLoginActivity.this, LoginActivity.class);
                startActivity(irLogin);
                finish();
                break; */
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

}
