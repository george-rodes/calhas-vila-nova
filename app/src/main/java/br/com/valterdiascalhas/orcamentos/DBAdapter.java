package br.com.valterdiascalhas.orcamentos;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.sqlite.SQLiteQueryBuilder;
import android.os.CancellationSignal;
import android.os.Environment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CRUD Create, Retrieve, Update and Delete
 **/
public class DBAdapter {
    DBHelper mydbHelper;
    private HashMap<String, String> mAliasMap;

    public DBAdapter(Context context) {
        mydbHelper = DBHelper.getInstance(context);
        /******************IMPORTANT FOR SEARCH ********************/
        // This HashMap is used to map table fields to Custom Suggestion fields
        mAliasMap = new HashMap<String, String>();
        // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put("_ID", "_id as _id");
        // Text for Suggestions ( Mandatory )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, "apelido as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        // Icon for Suggestions ( Optional )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_ICON_1, "1  as " + SearchManager.SUGGEST_COLUMN_ICON_1);
        // This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "_id as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    /************************
     * SEARCH
     ************************/
    Cursor getClientes(String[] selectionArgs) {
        String selection = DBHelper.APELIDO + " like ? ";
        if (selectionArgs != null) {
            selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(mAliasMap);
        queryBuilder.setTables(DBHelper.TN_CLIENTE);
        Cursor c = queryBuilder.query(mydbHelper.getReadableDatabase(),
                new String[]{"_ID",
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_ICON_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID},
                selection,
                selectionArgs,
                null,
                null,
                DBHelper.APELIDO + " asc ", "10"
        );
        return c;
    }

    Cursor getCliente(String id) {
        /** Return Publisher corresponding to the id, not used  */
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.TN_CLIENTE);
        Cursor c = queryBuilder.query(mydbHelper.getReadableDatabase(),
                new String[]{"_id", "apelido", "nome", "fone1"},
                "_id = ?", new String[]{id}, null, null, null, "1"
        );
        return c;
    }

    Cursor getOneClient(String id) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DBHelper.TN_CLIENTE);
        Cursor c = queryBuilder.query(mydbHelper.getReadableDatabase(),
                new String[]{"apelido"},
                "_id = ?", new String[]{id}, null, null, null, "1"
        );
        return c;
    }

    /** My Name is C.R.U.D **/

    /**
     * Create
     **/

    boolean insertCliente(Cliente c) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.APELIDO, c.getApelido());
        cv.put(DBHelper.NOME, c.getNome());
        cv.put(DBHelper.FONE, c.getFone1());
        cv.put(DBHelper.EMAIL, c.getFone2());
        cv.put(DBHelper.RUA, c.getRua());
        cv.put(DBHelper.BAIRRO, c.getBairro());
        cv.put(DBHelper.CIDADE, c.getCidade());
        cv.put(DBHelper.OBS, c.getObs());
        long id = db.insert(DBHelper.TN_CLIENTE, null, cv);
        db.close();
        return id > 0;
    }

    boolean insertOrcamento(Orcamento o) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.ORCAMENTO, o.getOrcamento());
        cv.put(DBHelper.NOME, o.getNome());
        cv.put(DBHelper.DATA, o.getData());
        cv.put(DBHelper.ANO, o.getAno());
        cv.put(DBHelper.MES, o.getMes());
        cv.put(DBHelper.DIA, o.getDia());
        cv.put(DBHelper.RUA, o.getRua());
        cv.put(DBHelper.BAIRRO, o.getBairro());
        cv.put(DBHelper.CIDADE, o.getCidade());
        cv.put(DBHelper.TOTAL, o.getTotal());
        cv.put(DBHelper.OBS, o.getObservacao());
        long id = db.insert(DBHelper.TN_ORCAMENTO, null, cv);
        db.close();
        return id > 0;
    }

    boolean insertItemOrcamento(ItemOrcamento o) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.ORCAMENTO, o.getOrcamento());
        cv.put(DBHelper.MATERIAL, o.getMaterial());
        cv.put(DBHelper.METROS, o.getMetros());
        cv.put(DBHelper.PRECO_METRO, o.getPrecometro());
        cv.put(DBHelper.OBS, o.getObservacao());
        long id = db.insert(DBHelper.TN_ITEM_ORCAMENTO, null, cv);
        db.close();
        return id > 0;
    }

    boolean insertMaterial(Material m) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        try {
            cv.put(DBHelper.CODIGO, m.getCodigo()); /** UNIQUE **/
            cv.put(DBHelper.NOME, m.getNome());
            cv.put(DBHelper.MATERIAPRIMA, m.getMateriaprima());
            cv.put(DBHelper.IMAGEM, m.getImagem());
            cv.put(DBHelper.OBS, m.getObs());
            db.insert(DBHelper.TN_MATERIAL, null, cv);
            db.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean insertRua(String r) {
        if (!r.isEmpty()) {
            SQLiteDatabase db = mydbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            try {
                cv.put(DBHelper.RUA, r); /** UNIQUE **/
                long id = db.insert(DBHelper.TN_RUAS, null, cv);
                db.close();
                return id > 0;
            } catch (Exception e) {
                return false;
            }
        } else return false;
    }

    boolean insertBairro(String b) {
        if (!b.isEmpty()) {
            SQLiteDatabase db = mydbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            try {
                cv.put(DBHelper.BAIRRO, b); /** UNIQUE **/
                long id = db.insert(DBHelper.TN_BAIRROS, null, cv);
                db.close();
                return id > 0;
            } catch (Exception e) {
                return false;
            }
        } else return false;

    }


    /** Retrieve **/

    /**
     * Cliente
     **/

    Cursor retrieveClientes() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        return db.query(DBHelper.TN_CLIENTE, null, null, null, null, null, DBHelper.APELIDO);
    }

    Cursor retrieveCliente(String apelido) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {apelido};
        return db.query(DBHelper.TN_CLIENTE, null, DBHelper.APELIDO + " = ? ", selectionArgs, null, null, null);
    }

    List<String> retrieveAllNomeDeClientes() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.NOME};
        Cursor c = db.query(DBHelper.TN_CLIENTE, columns, null, null, null, null, DBHelper.NOME);
        List<String> nomes = new ArrayList<>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                nomes.add(c.getString(c.getColumnIndex(DBHelper.NOME)));
            }
        } else nomes.add("Sem Nomes");
        c.close();
        return nomes;
    }

    List<String> retrieveAllApelidoDeClientes() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.APELIDO};
        Cursor c = db.query(DBHelper.TN_CLIENTE, columns, null, null, null, null, DBHelper.APELIDO);
        List<String> nomes = new ArrayList<>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                nomes.add(c.getString(c.getColumnIndex(DBHelper.APELIDO)));
            }
        } else nomes.add("Sem Nomes");
        c.close();
        return nomes;
    }

    Cursor checkIfClienteApelidoMatches(String apelido) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        apelido = "%" + apelido.trim() + "%";
        String[] selectionArgs = {apelido};
        return db.query(DBHelper.TN_CLIENTE, null, DBHelper.APELIDO + " LIKE ? ", selectionArgs, null, null, null, "1");
    }

    Cursor checkIfClienteApelidoExists(String apelido) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {apelido};
        return db.query(DBHelper.TN_CLIENTE, null, DBHelper.APELIDO + " = ? ", selectionArgs, null, null, null, "1");
    }

    boolean booleanClienteApelidoExists(String apelido) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {apelido};
        Cursor c = db.query(DBHelper.TN_CLIENTE, null, DBHelper.APELIDO + " = ? ", selectionArgs, null, null, null, "1");
        return c.moveToNext();
    }

    boolean findFirstCliente(String apelido) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {apelido};
        return (db.query(DBHelper.TN_CLIENTE, null, DBHelper.APELIDO + " = ? ", selectionArgs, null, null, null, "1")).moveToNext();
    }

    Cursor checkIfClienteNomeExists(String nome) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        nome = "%" + nome.trim() + "%";
        String[] selectionArgs = {nome};
        return db.query(DBHelper.TN_CLIENTE, null, DBHelper.NOME + " LIKE ? ", selectionArgs, null, null, null, "1");
    }

    /**
     * orcamento
     **/
    Cursor buscaOrcamentosDoMaterial(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String MY_QUERY = "select orcamento.nome, orcamento.data  FROM orcamento JOIN itemorcamento ON orcamento.orcamento = itemorcamento.orcamento JOIN material ON itemorcamento.material = material.codigo\n" + "WHERE material.codigo = ?";
        //MY_QUERY = "select orcamento.nome, orcamento.data  FROM orcamento JOIN itemorcamento ON orcamento.orcamento = itemorcamento.orcamento JOIN material ON itemorcamento.material = material.codigo WHERE material.codigo = \"Rufo de Encosto C20\"";
        String[] selectionArgs = {codigo};

        return db.rawQuery(MY_QUERY, selectionArgs);
    }

    Cursor myQueryBuilder(String query) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        return db.rawQuery(query, null);
    }

    Cursor retrieveOrcamentos(String nome) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {nome};
        String orderBy = " ano desc, mes desc, dia desc ";
        return db.query(DBHelper.TN_ORCAMENTO, null, DBHelper.NOME + " = ? ", selectionArgs, null, null, orderBy);
    }

    Cursor retrieveOrcamento(String id) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {id};
        return db.query(DBHelper.TN_ORCAMENTO, null, DBHelper.ORCAMENTO + " = ? ", selectionArgs, null, null, null);
    }

    boolean booleanOrcamentoExists(String id) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {id};
        Cursor c = db.query(DBHelper.TN_ORCAMENTO, null, DBHelper.ORCAMENTO + " = ? ", selectionArgs, null, null, null);
        return c.moveToNext();
    }

    boolean findFirstOrcamento(String nome) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {nome};
        return (db.query(DBHelper.TN_ORCAMENTO, null, DBHelper.NOME + " = ? ", selectionArgs, null, null, null, "1")).moveToNext();
    }

    int giveMeNextOrcamento() {
        int increment;
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + DBHelper.ORCAMENTO + ") from " + DBHelper.TN_ORCAMENTO, null);
        c.moveToNext();
        increment = c.getInt(0) + 1;
        c.close();
        return increment;
    }

    /**
     * itemorcamento
     **/
    Cursor retrieveItemOrcamento(String orcamento) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {orcamento};
        return db.query(DBHelper.TN_ITEM_ORCAMENTO, null, DBHelper.ORCAMENTO + " = ? ", selectionArgs, null, null, DBHelper.MATERIAL);
    }

    Cursor checkIfItemOrcamentoExists(String orcamento_id, String material) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String selection = DBHelper.ORCAMENTO + " = ? AND " + DBHelper.MATERIAL + " = ? ";
        String[] selectionArgs = {orcamento_id, material};
        return db.query(DBHelper.TN_ITEM_ORCAMENTO, null, selection, selectionArgs, null, null, null, "1");
    }

    boolean booleanCheckIfItemOrcamentoExists(String orcamento_id, String material) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String selection = DBHelper.ORCAMENTO + " = ? AND " + DBHelper.MATERIAL + " = ? ";
        String[] selectionArgs = {orcamento_id, material};
        return db.query(DBHelper.TN_ITEM_ORCAMENTO, null, selection, selectionArgs, null, null, null, "1").moveToNext();
    }

    boolean booleanFindFirstItemOrcamento(String material) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String selection = DBHelper.MATERIAL + " = ? ";
        String[] selectionArgs = {material};
        return db.query(DBHelper.TN_ITEM_ORCAMENTO, null, selection, selectionArgs, null, null, null, "1").moveToNext();
    }

    Cursor findFirstItemOrcamento(String material) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String selection = DBHelper.MATERIAL + " = ? ";
        String[] selectionArgs = {material};
        return db.query(DBHelper.TN_ITEM_ORCAMENTO, null, selection, selectionArgs, null, null, null, "1");
    }

    int giveMeNextItemOrcamento() {
        int increment;
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(" + DBHelper.UID + ") from " + DBHelper.TN_ITEM_ORCAMENTO, null);
        c.moveToNext();
        increment = c.getInt(0) + 1;
        c.close();
        return increment;
    }

    /**
     * material
     **/
    Cursor retrieveMateriais() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.UID, DBHelper.CODIGO, DBHelper.NOME, DBHelper.MATERIAPRIMA, DBHelper.IMAGEM, DBHelper.OBS};
        return db.query(DBHelper.TN_MATERIAL, columns, null, null, null, null, DBHelper.CODIGO);
    }

    Cursor retrieveMaterial(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.UID, DBHelper.CODIGO, DBHelper.NOME, DBHelper.IMAGEM};
        String[] selectionArgs = {codigo};
        return db.query(DBHelper.TN_MATERIAL, columns, DBHelper.CODIGO + " = ? ", selectionArgs, null, null, DBHelper.CODIGO);
    }

    Cursor checkIfMaterialExists(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {codigo};
        return db.query(DBHelper.TN_MATERIAL, null, DBHelper.CODIGO + " = ? ", selectionArgs, null, null, null, "1");
    }

    boolean booleanCheckIfMaterialExists(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] selectionArgs = {codigo};
        return db.query(DBHelper.TN_MATERIAL, null, DBHelper.CODIGO + " = ? ", selectionArgs, null, null, null, "1").moveToNext();
    }

    Cursor checkIfMaterialMatches(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        codigo = "%" + codigo.trim() + "%";
        String[] selectionArgs = {codigo};
        return db.query(DBHelper.TN_MATERIAL, null, DBHelper.CODIGO + " LIKE ? ", selectionArgs, null, null, null, "1");
    }

    boolean booleanCheckIfMaterialMatches(String codigo) {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        codigo = "%" + codigo.trim() + "%";
        String[] selectionArgs = {codigo};
        return db.query(DBHelper.TN_MATERIAL, null, DBHelper.CODIGO + " LIKE ? ", selectionArgs, null, null, null, "1").moveToNext();
    }

    List<String> retrieveAllMaterial() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.CODIGO};
        Cursor c = db.query(DBHelper.TN_MATERIAL, columns, null, null, null, null, DBHelper.CODIGO);
        List<String> nomes = new ArrayList<>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                nomes.add(c.getString(c.getColumnIndex(DBHelper.CODIGO)));
            }
        } else nomes.add("Sem Material");
        c.close();
        return nomes;
    }

    List<String> retrieveADistinctNomeDeMaterial() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.NOME};
        Cursor c = db.query(true, DBHelper.TN_MATERIAL, columns, null, null, null, null, DBHelper.NOME, null);

        /** Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
         **/

        List<String> nomes = new ArrayList<>();
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                nomes.add(c.getString(c.getColumnIndex(DBHelper.NOME)));
            }
        } else nomes.add("Sem Nome");
        c.close();
        return nomes;
    }


    /**
     * rua
     **/
    String[] retrieveAllRuas() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.RUA};
        Cursor c = db.query(DBHelper.TN_RUAS, columns, null, null, null, null, DBHelper.RUA);
        if (c.getCount() > 0) {
            String[] nomes = new String[c.getCount()];
            while (c.moveToNext()) {
                nomes[c.getPosition()] = (c.getString(c.getColumnIndex(DBHelper.RUA)));
            }
            c.close();
            return nomes;
        }
        return null;
    }

    /**
     * bairro
     **/
    String[] retrieveAllBairros() {
        SQLiteDatabase db = mydbHelper.getReadableDatabase();
        String[] columns = {DBHelper.BAIRRO};
        Cursor c = db.query(DBHelper.TN_BAIRROS, columns, null, null, null, null, DBHelper.BAIRRO);
        if (c.getCount() > 0) {
            String[] nomes = new String[c.getCount()];
            while (c.moveToNext()) {
                nomes[c.getPosition()] = (c.getString(c.getColumnIndex(DBHelper.BAIRRO)));
            }
            c.close();
            return nomes;
        }
        return null;
    }


    /******************
     * *****************
     * **** Update *****
     * *****************
     ******************/
    boolean updateCliente(Cliente c) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.NOME, c.getNome());
        cv.put(DBHelper.FONE, c.getFone1());
        cv.put(DBHelper.EMAIL, c.getFone2());
        cv.put(DBHelper.RUA, c.getRua());
        cv.put(DBHelper.BAIRRO, c.getBairro());
        cv.put(DBHelper.CIDADE, c.getCidade());
        String selection = DBHelper.APELIDO + " = ?";
        String[] selectionArgs = {c.getApelido()};
        int count = db.update(DBHelper.TN_CLIENTE, cv, selection, selectionArgs);
        return count > 0;
    }

    boolean updateOrcamento(Orcamento o) {
        String orcamento = String.valueOf(o.getOrcamento());
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = DBHelper.ORCAMENTO + " = ?";
        String[] selectionArgs = {orcamento};
        cv.put(DBHelper.NOME, o.getNome());
        cv.put(DBHelper.DATA, o.getData());
        cv.put(DBHelper.ANO, o.getAno());
        cv.put(DBHelper.MES, o.getMes());
        cv.put(DBHelper.DIA, o.getDia());
        cv.put(DBHelper.RUA, o.getRua());
        cv.put(DBHelper.BAIRRO, o.getBairro());
        cv.put(DBHelper.CIDADE, o.getCidade());
        cv.put(DBHelper.TOTAL, o.getTotal());
        cv.put(DBHelper.OBS, o.getObservacao());
        int count = db.update(DBHelper.TN_ORCAMENTO, cv, selection, selectionArgs);
        return count > 0;

    }

    boolean updateItemOrcamento(String id, long metros, long preco) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.METROS, metros);
        cv.put(DBHelper.PRECO_METRO, preco);
        String selection = DBHelper.UID + " = ?";
        String[] selectionArgs = {id};
        int count = db.update(DBHelper.TN_ITEM_ORCAMENTO, cv, selection, selectionArgs);
        return count > 0;
    }

    /**
     * Material(String codigo, String nome, String materiaprima, String imagem, String obs)
     **/
    boolean updateMaterial(Material c) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.NOME, c.getNome());
        cv.put(DBHelper.MATERIAPRIMA, c.getMateriaprima());
        cv.put(DBHelper.IMAGEM, c.getImagem());
        cv.put(DBHelper.OBS, c.getObs());
        String selection = DBHelper.CODIGO + " = ?";
        String[] selectionArgs = {c.getCodigo()};
        int count = db.update(DBHelper.TN_MATERIAL, cv, selection, selectionArgs);
        return count > 0;
    }


    /**
     * Delete
     */

    int deleteCliente(String str) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        String selection = DBHelper.APELIDO + " = ?";
        String[] selectionArgs = {str};
        return db.delete(DBHelper.TN_CLIENTE, selection, selectionArgs);
    }

    int deleteOrcamento(String orcamento_id) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        String selection = DBHelper.ORCAMENTO + " = ? ";
        String[] selectionArgs = {orcamento_id};
        return db.delete(DBHelper.TN_ORCAMENTO, selection, selectionArgs);
    }

    int deleteItemOrcamento(String orcamento_id, String material) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        String selection = DBHelper.ORCAMENTO + " = ? AND " + DBHelper.MATERIAL + " = ? ";
        String[] selectionArgs = {orcamento_id, material};
        return db.delete(DBHelper.TN_ITEM_ORCAMENTO, selection, selectionArgs);
    }

    boolean deleteItemOrcamentobyId(int id) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        String selection = DBHelper.UID + " = ? ";
        String[] selectionArgs = {"" + id};
        return db.delete(DBHelper.TN_ITEM_ORCAMENTO, selection, selectionArgs) > 0;
    }

    int deleteMaterial(String str) {
        SQLiteDatabase db = mydbHelper.getWritableDatabase();
        String selection = DBHelper.CODIGO + " = ?";
        String[] selectionArgs = {str};
        return db.delete(DBHelper.TN_MATERIAL, selection, selectionArgs);
    }

    static class DBHelper extends SQLiteOpenHelper {
        private static DBHelper sInstance;

        static synchronized DBHelper getInstance(Context context) {
            if (sInstance == null) {
                sInstance = new DBHelper(context.getApplicationContext());
            }
            return sInstance;
        }

        private DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        static final String DB_NAME = "appledore";
        static final int DB_VERSION = 1;

        static final String UID = "_id";
        static final String NOME = "nome";
        static final String OBS = "observacao";

        private static final String CT_VERSAO = "CREATE TABLE IF NOT EXISTS versao ( data_ultima_atualizacao TEXT UNIQUE );";
        private static final String DT_VERSAO = "DROP TABLE IF EXISTS versao";

        static final String TN_CLIENTE = "cliente";
        static final String APELIDO = "apelido";
        static final String FONE = "fone1";
        static final String EMAIL = "fone2";
        static final String RUA = "rua";
        static final String BAIRRO = "bairro";
        static final String CIDADE = "cidade";
        static final String CT_CLIENTE = "CREATE TABLE IF NOT EXISTS "
                + TN_CLIENTE + " ( "
                + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + APELIDO + " TEXT UNIQUE, "
                + NOME + " TEXT, "
                + FONE + " TEXT, "
                + EMAIL + " TEXT, "
                + RUA + " TEXT, "
                + BAIRRO + " TEXT, "
                + CIDADE + " TEXT, "
                + OBS + " TEXT );";
        static final String DT_CLIENTE = "DROP TABLE IF EXISTS " + TN_CLIENTE;
        static final String TN_MATERIAL = "material";
        static final String CODIGO = "codigo";
        static final String MATERIAPRIMA = "materiaprima";
        static final String IMAGEM = "imagem";

        private static final String CT_MATERIAL = "CREATE TABLE IF NOT EXISTS "
                + TN_MATERIAL + " ( "
                + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CODIGO + " TEXT UNIQUE, "
                + NOME + " TEXT, "
                + MATERIAPRIMA + " TEXT,"
                + IMAGEM + " TEXT, "
                + OBS + " TEXT );";

        private static final String DT_MATERIAL = "DROP TABLE IF EXISTS " + TN_MATERIAL;

        /**
         * usar data string para visualização, extra campos para ano, mes, dia para classificação do cursor
         * money in centavos
         **/
        static final String TN_ORCAMENTO = "orcamento";
        static final String DATA = "data";
        static final String ANO = "ano";
        static final String MES = "mes";
        static final String DIA = "dia";
        static final String ORCAMENTO = "orcamento";
        static final String TOTAL = "total";
        private static final String CT_ORCAMENTO = "CREATE TABLE IF NOT EXISTS "
                + TN_ORCAMENTO + " ( "
                + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ORCAMENTO + " INTEGER UNIQUE, "
                + NOME + " TEXT, "  //=apelido
                + DATA + " TEXT, "
                + ANO + " INTEGER, "
                + MES + " INTEGER, "
                + DIA + " INTEGER, "
                + RUA + " TEXT, "
                + BAIRRO + " TEXT, "
                + CIDADE + " TEXT, "
                + TOTAL + " INTEGER, "
                + OBS + " TEXT );";
        private static final String DT_ORCAMENTO = "DROP TABLE IF EXISTS " + TN_ORCAMENTO;

        static final String TN_ITEM_ORCAMENTO = "itemorcamento";
        static final String MATERIAL = "material";
        static final String METROS = "metros";
        static final String PRECO_METRO = "precometro";
        private static final String CT_ITEM_ORCAMENTO = "CREATE TABLE IF NOT EXISTS "
                + TN_ITEM_ORCAMENTO + " ( "
                + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORCAMENTO + " INTEGER, "
                + MATERIAL + " TEXT, "
                + METROS + " INTEGER, "
                + PRECO_METRO + " INTEGER, "
                + OBS + " TEXT, UNIQUE ( " + ORCAMENTO + ", " + MATERIAL + "));";
        private static final String DT_ITEM_ORCAMENTO = "DROP TABLE IF EXISTS " + TN_ITEM_ORCAMENTO;

        static final String TN_RUAS = "ruas";
        private static final String CT_RUAS = "CREATE TABLE IF NOT EXISTS ruas ( _id INTEGER PRIMARY KEY AUTOINCREMENT, rua TEXT UNIQUE )";
        private static final String DT_RUAS = "DROP TABLE IF EXISTS " + TN_RUAS;

        static final String TN_BAIRROS = "bairros";
        private static final String CT_BAIRROS = "CREATE TABLE IF NOT EXISTS bairros ( _id INTEGER PRIMARY KEY AUTOINCREMENT, bairro TEXT UNIQUE )";
        private static final String DT_BAIRROS = "DROP TABLE IF EXISTS " + TN_BAIRROS;

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CT_MATERIAL);
            db.execSQL(CT_ITEM_ORCAMENTO);
            db.execSQL(CT_ORCAMENTO);
            db.execSQL(CT_CLIENTE);
            db.execSQL(CT_VERSAO);
            db.execSQL(CT_RUAS);
            db.execSQL(CT_BAIRROS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DT_CLIENTE);
            db.execSQL(DT_ORCAMENTO);
            db.execSQL(DT_ITEM_ORCAMENTO);
            db.execSQL(DT_MATERIAL);
            db.execSQL(DT_VERSAO);
            db.execSQL(DT_RUAS);
            db.execSQL(DT_BAIRROS);
            onCreate(db);
        }
    }
}