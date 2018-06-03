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

    public long inserir(Carteira carteira){
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUNA_CHAVE_PUBLICA, carteira.getChave_publica());
        values.put(COLUNA_CHAVE_PRIVADA, carteira.getChave_privada());
        values.put(COLUNA_DESCRICAO, carteira.getDescricao());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABELA, null, values);
        db.close();
        return newRowId;
    }


    public List<Carteira> recuperarCarteira() {

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
        while ( cursor.moveToNext() ) {
            ids.add( Long.parseLong( cursor.getString( indiceColunaId ) ));

            Long id = cursor.getLong(cursor.getColumnIndex(COLUNA_ID));
            String chave_publica = cursor.getString(cursor.getColumnIndex(COLUNA_CHAVE_PUBLICA));
            String chave_privada = cursor.getString(cursor.getColumnIndex(COLUNA_CHAVE_PRIVADA));
            String descricao = cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO));

            Carteira carteira = new Carteira(id,chave_publica, chave_privada, descricao);
            carteiras.add(carteira);
        }
        cursor.close();
        db.close();

        return carteiras;
    }


    public void removerCarteira (Long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "DELETE FROM "+ TABELA +" WHERE "+ COLUNA_ID +" = "+id;
        db.execSQL(sql);
    }

}
