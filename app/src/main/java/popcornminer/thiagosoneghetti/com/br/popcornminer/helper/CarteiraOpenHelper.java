package popcornminer.thiagosoneghetti.com.br.popcornminer.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CarteiraOpenHelper extends SQLiteOpenHelper{
    //https://developer.android.com/guide/topics/data/data-storage?hl=pt-Br#db



    private static final int VERSAO_DB = 1;
    private static final String NOME_DB = "DBCARTEIRA";
    private static final String NOME_TABELA = "carteira";
//    private static final String NOME_TABELA2 = "favoritos";
    private static final String CRIAR_TABELA =
            "CREATE TABLE IF NOT EXISTS " + NOME_TABELA + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CHAVE_PUBLICA TEXT NOT NULL," +
                "CHAVE_PRIVADA TEXT NOT NULL," +
                "DESCRICAO TEXT NOT NULL );";

/*
    private static final String CRIAR_TABELA2 =
            "CREATE TABLE IF NOT EXISTS " + NOME_TABELA2 + " (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "CHAVE_PUBLICA TEXT NOT NULL," +
                    "DESCRICAO TEXT NOT NULL );";
*/

    public CarteiraOpenHelper(Context context) {
        super(context, NOME_DB  , null, VERSAO_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRIAR_TABELA);
//        db.execSQL(CRIAR_TABELA2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
