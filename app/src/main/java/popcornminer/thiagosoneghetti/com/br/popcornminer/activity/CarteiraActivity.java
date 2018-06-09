package popcornminer.thiagosoneghetti.com.br.popcornminer.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.config.ConfiguracaoFirebase;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.ConexaoInternet;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.Preferencias;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.Carteira;
import popcornminer.thiagosoneghetti.com.br.popcornminer.model.CarteiraDao;
import popcornminer.thiagosoneghetti.com.br.popcornminer.R;

public class CarteiraActivity extends AppCompatActivity {
    private FirebaseAuth usuarioFirebase;
    private DatabaseReference firebase;
    private CarteiraDao carteiraDao;
    private CarteiraAdpter carteiraAdpter;
    private ListView listaCarteiras;
    private Context context;
    private FloatingActionButton botaoAddCarteira;
    private ValueEventListener valueEventListenerCarteira;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carteira);

        // Chamando o objeto do Firebase que é responsável pela autenticação
        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Configurações menu superior (ActionBar)
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setIcon(R.mipmap.ic_launcher_foreground); // Atribuir um ícone na actionbar
        actionBar.setDisplayShowHomeEnabled(true); // Habilitar o título da barra de ação
        actionBar.setDisplayHomeAsUpEnabled(true); // Habilitar botão voltar

        //carteiraDao = new CarteiraDao(this);  // Não utilziado, somente no SQLite
        listaCarteiras = findViewById(R.id.listCarteirasId);
        context = this;

    // Processos para recuperação das carteira no Firebase
        // Verificando se possui conexão com a internet, se sim busca lista de contatos, se não informa para o usuário que está sem internet
        Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
        if ( conexaoInternet == true ) {
            Preferencias preferencias = new Preferencias(CarteiraActivity.this);
            String identificador = preferencias.getIdentificador();

            //Recuperar instância Firebase no local informado : carteiras >> email em base64
            // O que caminho que for configurado aqui, será armazenado no DataSnapshot abaixo
            firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child(identificador);

            // Listener que será notificado toda vez que houver mudança, para ser executado novamente
            valueEventListenerCarteira = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Lista para adicionar as carteiras
                    List<Carteira> carteiras = new ArrayList<>();

                    // Limpar lista de carteira antes de buscar no Firebase
                    carteiras.clear();

                    // Buscando as carteiras existentes no Firebase
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        // Pegando os dados do Firebase para serem salvos na lista
                        Carteira carteiraFb = new Carteira();
                        carteiraFb.setIdentificador(dados.getKey());
                        carteiraFb.setDescricao((String) dados.child("descricao").getValue());
                        carteiraFb.setChave_publica((String) dados.child("chave_publica").getValue());
                        carteiraFb.setChave_privada((String) dados.child("chave_privada").getValue());
                        // Salvando carteira na lista
                        carteiras.add(carteiraFb);
                    }
                    // Caso não haja nenhuma carteira cadastrada irá mostra mensagem para o usuário
                    if (carteiras.size() == 0) {
                        Toast.makeText(context, "Nenhuma carteira cadastrada.", Toast.LENGTH_LONG).show();
                    }

                    // Passando a lista de carteiras para o adapter que mostrará as carteiras na tela
                    carteiraAdpter = new CarteiraAdpter(context, carteiras);
                    listaCarteiras.setAdapter(carteiraAdpter);
                    // Notifica o adapter caso haja alguma alteração no firebase, para a lista ser atualizada
                    carteiraAdpter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        } else{
            Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
        }

        // Consulta o saldo quando clicado na carteira
        listaCarteiras.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Verifica se há conexão com a internet
                Boolean conexaoInternet = ConexaoInternet.verificaConexao(context);
                if ( conexaoInternet == true) {
                    Carteira carteira = (Carteira) carteiraAdpter.getItem(position);
                    // Método que faz a consulta no servidor
                    carteira.saldoUC(carteira, context);
                } else {
                    Toast.makeText(context, "Sem conexão com a internet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Quando há um logo clica na carteira, chama essa função de exclusão
        listaCarteiras.setLongClickable(true);
        listaCarteiras.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Carteira carteira = (Carteira) carteiraAdpter.getItem(position);
                // Exclusão da carteira no Firebase
                confirmarExclusaoFB(carteira.getIdentificador(), carteira.getDescricao());

                // Exclusao pelo SQLite - não utilizado mais
                //confirmarExclusao(carteira.getId(),carteira.getDescricao());
                return true;
            }
        });


        // Botão de direciona para tela de cadastro de nova carteira
        botaoAddCarteira = findViewById(R.id.btAddCarteiraId);
        botaoAddCarteira.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteiraActivity.this, AddCarteiraActivity.class);
                startActivity(intent);
            }
        });

    }

    // Atualizar a lista de carteiras do SQLite, não utitilizado mais
/*    private void atualizarListaCarteira (){
        List<Carteira> carteiraLista = carteiraDao.recuperarCarteira();
        carteiraAdpter = new CarteiraAdpter(context, carteiraLista);
        listaCarteiras.setAdapter(carteiraAdpter);
    };*/

    // Primeira mensagem perguntando se deseja excluir a carteita, caso sim, passará os dados para o método ExclusaoFB
    private void confirmarExclusaoFB (final String idCarteiraFb, final String descricao){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
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

    // Mostra uma nova mensagem de confirmação, e se confirmado, deletará a carteira
    private void ExclusaoFB (final String idCarteiraFb, String descricao){

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Confirmação de exclusão:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("AVISO: A carteira \""+ descricao +"\" será deletada permanentemente.");
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
                Toast.makeText(context, "Carteira deletada com sucesso!", Toast.LENGTH_SHORT).show();
                Preferencias preferencias = new Preferencias(CarteiraActivity.this);
                String identificador = preferencias.getIdentificador();

                firebase = ConfiguracaoFirebase.getFirebase().child("carteiras").child( identificador ).child(idCarteiraFb);
                firebase.removeValue();

            }
        });
        msgBox.show();
    }

/* Os dois métodos abaixos são os mesmo que o acima, porém são para o SQLite, não utilizado mais
    private void confirmarExclusao (final long idExc, final String descricao){
        final long idCarteira = idExc;


        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
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
                Exclusao(idExc, descricao);
            }
        });
        msgBox.show();
    }

    private void Exclusao (final long idExc, String descricao){
        final long idCarteira = idExc;

        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Confirmação de exclusão:");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("AVISO: A carteira \""+ descricao +"\" será deletada permanentemente.");
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
                Toast.makeText(context, "Carteira deletada com sucesso!", Toast.LENGTH_SHORT).show();
                carteiraDao.removerCarteira(idCarteira);
                atualizarListaCarteira();
            }
        });
        msgBox.show();
    }
*/

    @Override
    protected void onStart() {
        super.onStart();
        // Era utilizado para atualizar a lista de carteira do SQLite
        //atualizarListaCarteira();

        // Método responsável para chama o listener, onde será feito a busca por carteiras no firebase
        firebase.addValueEventListener( valueEventListenerCarteira );
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Para o método listener que é responsável por ficar escutando modificações no firebase
        firebase.removeEventListener( valueEventListenerCarteira );
    }

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
                Intent btVoltar = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(btVoltar);
                finish();
                break;
            case R.id.bt_mcart_home:
                Intent irHome = new Intent(CarteiraActivity.this,MainActivity.class);
                startActivity(irHome);
                finish();
                break;
            case R.id.bt_mcart_transferencia:
                Intent irTransferencia = new Intent(CarteiraActivity.this,TransferenciaActivity.class);
                startActivity(irTransferencia);
                finish();
                break;
            case R.id.bt_mcart_sair:
                // Desconecta o usuário atual do aplicativo
                usuarioFirebase  = ConfiguracaoFirebase.getFirebaseAutenticacao();
                usuarioFirebase.signOut();
                Toast.makeText(this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
                Intent irLogin = new Intent(CarteiraActivity.this,LoginActivity.class);
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