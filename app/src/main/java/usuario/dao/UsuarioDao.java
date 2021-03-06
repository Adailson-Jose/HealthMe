package usuario.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import usuario.dominio.Pessoa;
import usuario.dominio.Usuario;
import usuario.negocio.UsuarioNegocio;

/**
 * <h1>UsuarioDao</h1>
 *Classe responsavel pelas chamadas e operacoes realizadas no banco de dados, mais especificamente
 * nas tabelas correspondetes a pessoa e usuario.
 */

public class UsuarioDao {
    private SQLiteDatabase db;
    private DbHelper dataBaseHelper;
    private UsuarioNegocio validacao;
    private Context context;
    private SqlScripts script;

    public UsuarioDao(Context context){
        this.context = context;
        dataBaseHelper = new DbHelper(context);
        script = new SqlScripts();
    }

    public void inserirRegistro(Pessoa pessoa){
        ContentValues valor;
        db = dataBaseHelper.getWritableDatabase();

        valor = new ContentValues();
        valor.put(DbHelper.USER, pessoa.getUsuario().getLogin());
        valor.put(DbHelper.PASSWORD, pessoa.getUsuario().getPassword());
        valor.put(DbHelper.ATIVO, pessoa.getUsuario().getAtivo());
        db.insert(DbHelper.TABELA_USUARIO, null, valor);

        valor = new ContentValues();
        valor.put(DbHelper.NOME, pessoa.getNome());
        valor.put(DbHelper.PESSOA_USER, pessoa.getUsuario().getLogin());
        valor.put(DbHelper.PLANO_SAUDE, pessoa.getPlanoSaude());

        db.insert(DbHelper.TABELA_PESSOA,null, valor);
        db.close();
    }

    public void atualizarRegistro(Pessoa pessoa){
        ContentValues valor;
        String where;
        validacao =  new UsuarioNegocio(this.context);

        db = dataBaseHelper.getWritableDatabase();
        where = DbHelper.ID + "=" + pessoa.getId();
        valor = new ContentValues();
        valor.put(DbHelper.NOME, pessoa.getNome());
        valor.put(DbHelper.PLANO_SAUDE, pessoa.getPlanoSaude());

        db.update(DbHelper.TABELA_PESSOA, valor, where, null);

        valor = new ContentValues();
        valor.put(DbHelper.USER, pessoa.getUsuario().getLogin());
        valor.put(DbHelper.PASSWORD, pessoa.getUsuario().getPassword());
        valor.put(DbHelper.ATIVO, pessoa.getUsuario().getAtivo());

        db.update(DbHelper.TABELA_USUARIO,valor, where, null);
        db.close();
    }

    /**
     * Metodo utilizado na verificacao do usuario no login da aplicacao.
     *
     * @param user String do nome do usuario.
     * @param password String da senha do usuario.
     * @return
     */
    public Usuario buscarUsuario(String user, String password) {
        db = dataBaseHelper.getReadableDatabase();

        String[] parametros = {user, password};

        Cursor cursor = db.rawQuery(script.cmdWhere(dataBaseHelper.TABELA_USUARIO,dataBaseHelper.USER,dataBaseHelper.PASSWORD),
                parametros);

        Usuario usuario = null ;

        if (cursor.moveToNext()) {
            usuario = criarUsuario(cursor);
        }
        cursor.close();
        db.close();
        return usuario;
    }

    /**
     * Metodo utilizado na verificacao do usuario no cadastro da aplicação. Tambem usado
     * na validacao da sessao do usuario.
     *
     * @param user String com os dados do usuario.
     * @return Retorna o usuario encontrado no banco.
     */

    public Usuario buscarUsuario(String user) {
        db = dataBaseHelper.getReadableDatabase();

        String[] parametros = {user};

        Cursor cursor = db.rawQuery(script.cmdWhere(dataBaseHelper.TABELA_USUARIO,dataBaseHelper.USER),
                parametros);

        Usuario usuario = null;

        if (cursor.moveToNext()) {
            usuario = criarUsuario(cursor);
        }
        cursor.close();
        db.close();
        return usuario;
    }

    /**
     * Metodo utilizado na verificacao da pessoa no cadastro da aplicação. Tambem usado
     * na validacao da sessao do usuario.
     *
     * @param nome String do nome: usado para comparacao no banco.
     * @return Retorna a pessoa encontrada no banco.
     */

    public Pessoa buscarPessoa(String nome) {
        db = dataBaseHelper.getReadableDatabase();

        String[] parametros = {nome};

        Cursor cursor = db.rawQuery(script.cmdWhere(dataBaseHelper.TABELA_PESSOA,dataBaseHelper.PESSOA_USER),
                parametros);

        Pessoa pessoa = null;

        if (cursor.moveToNext()) {
            pessoa = criarPessoa(cursor);
        }
        cursor.close();
        db.close();
        return pessoa;
    }

    private Usuario criarUsuario(Cursor cursor){
        Usuario usuario = new Usuario();
        usuario.setId(cursor.getInt(0));
        usuario.setLogin(cursor.getString(1));
        usuario.setPassword(cursor.getString(2));
        usuario.setState(cursor.getString(3));
        return usuario;
    }

    private Pessoa criarPessoa(Cursor cursor){

        Pessoa pessoa = new Pessoa();
        pessoa.setId(cursor.getShort(0));
        pessoa.setNome(cursor.getString(1));
        pessoa.setPlanoSaude(cursor.getString(5));
        return pessoa;
    }

}
