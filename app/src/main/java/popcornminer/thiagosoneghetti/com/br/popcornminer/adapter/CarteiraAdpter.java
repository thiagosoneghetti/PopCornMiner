package popcornminer.thiagosoneghetti.com.br.popcornminer.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.activity.EditarCarteiraActivity;
import popcornminer.thiagosoneghetti.com.br.popcornminer.activity.NovaTransferenciaActivity;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.Firebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class CarteiraAdpter extends BaseAdapter {
    //http://blog.alura.com.br/personalizando-uma-listview-no-android/
    private DatabaseReference firebase;
    private FirebaseAuth usuarioFirebase;
    List<Carteira> carteiras;
    Context context;

    public CarteiraAdpter(Context context, List<Carteira> carteiras){
        this.context = context;
        this.carteiras = carteiras;
    }

    // Informando o total de itens da lista
    @Override
    public int getCount() {
        return carteiras.size();
    }

    // Devolvendo o item da lista pela posição
    @Override
    public Object getItem(int position) {
        return carteiras.get(position) ;
    }

    // Devolvendo o id do item da lista
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.adapter_lista_carteira,null);
        final Carteira carteira = carteiras.get(position);

        // Pegando elementos da View pelo ID
        TextView txtDescricao = view.findViewById(R.id.itListaTransferenciaDescricao);
        TextView txtChavePublica = view.findViewById(R.id.itListaCarteiraChavePublica);
        ImageView btnSaldo = view.findViewById(R.id.btSaldoLC);
        ImageView btnDeletar = view.findViewById(R.id.btDeletarLC);
        ImageView btnEditar = view.findViewById(R.id.btEditarLC);

        // Inserindo os dados do elemento na view
        txtDescricao.setText(carteira.getDescricao());
        txtChavePublica.setText(carteira.getChave_publica());

        // Ao clicar no botão chama a chama a função de consulta de saldo
        btnSaldo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifica se há conexão com a internet
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if ( conexaoInternet == true) {
                    // Método que faz a consulta no servidor
                    carteira.saldoUC(carteira, context);
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Ao clicar no botão chama a função de exclusão
        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Carteira carteira = carteiras.get(position);
                // Exclusão da carteira no Firebase
                ExclusaoFB(carteira.getIdentificador(), carteira.getDescricao());

                // Exclusao pelo SQLite - não utilizado mais
                //confirmarExclusao(carteira.getId(),carteira.getDescricao());
            }
        });

        // Ao clicar no botão é passado a carteira para a activity de edição de carteira
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtualizarFB(carteira, view);
            }
        });

        return view;
    }

    // Primeira mensagem perguntando se deseja excluir a carteita, caso sim, passará os dados para o método ExclusaoFB
    private void confirmarExclusaoFB (final String idCarteiraFb, final String descricao){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
        msgBox.setTitle("Remover carteira:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Deseja excluir a carteira \""+ descricao +"\"?");
        msgBox.setCancelable(false); // Não deixar clicar fora da caixa para sair
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Exclusão cancelada!", Toast.LENGTH_SHORT).show();
            }
        });

        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ExclusaoFB(idCarteiraFb, descricao);
            }
        });
        msgBox.show();
    }

    // Mostra uma nova mensagem de confirmação com senha, e se confirmado, deletará a carteira
    private void ExclusaoFB (final String idCarteiraFb, String descricao){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
        msgBox.setTitle("Confirmação de exclusão:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("AVISO: A carteira \""+ descricao +"\" será deletada permanentemente.");
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
                Toast.makeText(context, "Exclusão cancelada!", Toast.LENGTH_SHORT).show();
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
                                        Preferencias preferencias = new Preferencias(context);
                                        String identificador = preferencias.getIdentificador();

                                        // Realizando exclusão e informando
                                        firebase = Firebase.getFirebaseDatabase().child("carteiras").child(identificador).child(idCarteiraFb);
                                        firebase.removeValue();
                                        Toast.makeText(context, "Carteira deletada com sucesso!", Toast.LENGTH_SHORT).show();
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

    // Mostra uma nova mensagem de confirmação com senha, e se confirmado, poderá acessar a carteira para edição
    private void AtualizarFB (final Carteira carteira, final View view){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
        msgBox.setTitle("Confirmação de atualização:");
        msgBox.setIcon(android.R.drawable.ic_menu_edit);
        msgBox.setMessage("AVISO: A carteira "+ carteira.getDescricao() +" poderá ser atualizada permanentemente. Digite sua senha para acessar a área de edição.");
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
                Toast.makeText(context, "Edição cancelada!", Toast.LENGTH_SHORT).show();
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
                                        Preferencias preferencias = new Preferencias(context);
                                        String identificador = preferencias.getIdentificador();

                                        Intent intent = new Intent(view.getContext(), EditarCarteiraActivity.class);
                                        intent.putExtra("carteira", carteira);
                                        view.getContext().startActivity(intent);
                                        // Realizando exclusão e informando

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

}
