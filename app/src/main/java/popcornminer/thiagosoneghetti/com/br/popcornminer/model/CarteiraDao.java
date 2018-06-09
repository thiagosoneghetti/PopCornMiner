package popcornminer.thiagosoneghetti.com.br.popcornminer.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.CarteiraAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.adapter.TransferenciaAdpter;
import popcornminer.thiagosoneghetti.com.br.popcornminer.helper.CarteiraOpenHelper;

public class CarteiraDao {
    private CarteiraOpenHelper dbHelper;
    private CarteiraAdpter carteiraAdpter;
    private TransferenciaAdpter transferenciaAdpter;

    //https://developer.android.com/training/data-storage/sqlite?hl=pt-Br#kotlin

    public static final String TABELA = "carteira";
    public static final String COLUNA_ID = "ID";
    public static final String COLUNA_CHAVE_PUBLICA = "CHAVE_PUBLICA";
    public static final String COLUNA_CHAVE_PRIVADA = "CHAVE_PRIVADA";
    public static final String COLUNA_DESCRICAO = "DESCRICAO";

    public CarteiraDao (Context context) {
        dbHelper = new CarteiraOpenHelper(context);
    }

    // responsável por inserir a carteira no banco SQLite
    public long inserir(Carteira carteira){
        // Obtém o repositório de dados no modo de gravação
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Cria um novo mapa de valores, onde os nomes das colunas são as chaves e recebe os valores
        ContentValues values = new ContentValues();
        values.put(COLUNA_CHAVE_PUBLICA, carteira.getChave_publica());
        values.put(COLUNA_CHAVE_PRIVADA, carteira.getChave_privada());
        values.put(COLUNA_DESCRICAO, carteira.getDescricao());

        // Insere a nova linha, retornando o valor da chave primária da nova linha
        long newRowId = db.insert(TABELA, null, values);
        db.close();
        return newRowId;
    }

    // Recuperar/mostrar a lista de carteiras cadastradas
    public List<Carteira> recuperarCarteira() {
        // Obtém o repositório de dados no modo de leitura
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Recuperar as carteiras
        String sql = "SELECT * FROM "+ TABELA +" ORDER BY "+ COLUNA_ID +" DESC" ;
        Cursor cursor = db.rawQuery(sql, null);

        // Recuperar os ids das colunas
        int indiceColunaId = cursor.getColumnIndex(COLUNA_ID);

        // Listas
        List<Carteira> carteiras = new ArrayList<>();
        ArrayList<Long> ids;
        ids = new ArrayList<>();

        // Listar as carteiras
        // Buscará no banco até não encontrar mais nenhuma carteira e vai inserindo na "List carteiras"
        while ( cursor.moveToNext() ) {
            ids.add( Long.parseLong( cursor.getString( indiceColunaId ) ));

            Long id = cursor.getLong(cursor.getColumnIndex(COLUNA_ID));
            String chave_publica = cursor.getString(cursor.getColumnIndex(COLUNA_CHAVE_PUBLICA));
            String chave_privada = cursor.getString(cursor.getColumnIndex(COLUNA_CHAVE_PRIVADA));
            String descricao = cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO));
            //  Instancia uma nova carteira e adiciona na List carteiras
            Carteira carteira = new Carteira(id,chave_publica, chave_privada, descricao);
            carteiras.add(carteira);
        }
        cursor.close();
        db.close();

        return carteiras;
    }

    // Responsável por remover do banco a carteira pelo ID
    public void removerCarteira (Long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "DELETE FROM "+ TABELA +" WHERE "+ COLUNA_ID +" = "+id;
        db.execSQL(sql);
    }

}
