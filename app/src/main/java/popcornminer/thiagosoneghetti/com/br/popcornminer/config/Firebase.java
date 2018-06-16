package popcornminer.thiagosoneghetti.com.br.popcornminer.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// final - Para a classe não ser extendida
public final class Firebase {

    // static - o valor do atributo é o mesmo, independente de quantas instancias forem criadas
    private static DatabaseReference referenciaDatabase;
    private static FirebaseAuth autenticacao;

    // Retornará a referencia do Firesabe
    // static - não precisará criar uma instância da classe, método poderá ser utilizado diretamente
    public static DatabaseReference getFirebaseDatabase(){
        // Verifica se o reference já não foi criado anteriormente
        if ( referenciaDatabase == null) {
            //Habilita persistência de dados offline
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            referenciaDatabase = FirebaseDatabase.getInstance().getReference();
        }

        return referenciaDatabase;
    };

    // Retornará o objeto do Firesabe que é responsável pela autenticação
    // static - não precisará criar uma instância da classe, método poderá ser utilizado diretamente
    public static FirebaseAuth getFirebaseAutenticacao(){
        // Verifica se a autenticação não foi criada anteriormente
        if ( autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    };

}
