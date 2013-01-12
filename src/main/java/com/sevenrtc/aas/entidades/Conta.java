package com.sevenrtc.aas.entidades;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Contextos;

/**
 * Classe de contrapartida as entidades "Conta" no banco de dados
 *
 * @author Anthony Accioly
 *
 */
public class Conta {

    /**
     * Apaga uma entidade do banco
     */
    public static void delete(Conta conta) {
        String strSQL;

        strSQL = "DELETE FROM CON_Conta WHERE CON_Codigo = '" + conta.codigo
                + "' AND CTX_ID = " + conta.contexto;

        DAO.update(strSQL);
    }

    /**
     * Abre uma entidade do banco
     */
    public static Conta load(ResultSet rs) {
        Conta c = new Conta();

        try {
            c.codigo = rs.getString("CON_Codigo");
            c.contexto = rs.getLong("CTX_ID");
            c.nome = rs.getString("CON_Nome");
            c.saldo = rs.getDouble("CON_Saldo");
            c.tipo = rs.getString("CON_Tipo").charAt(0);
            c.funcao = rs.getString("CON_Funcao").charAt(0);
            c.pai = rs.getString("CON_Pai");
            c.ordem = rs.getInt("CON_Ordem");
            c.categoria_dre = rs.getLong("CAT_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return c;
    }

    /**
     * Abre uma entidade do banco
     */
    public static Conta load(String conta) {
        String x;
        x = "SELECT * FROM CON_CONTA WHERE CON_codigo = '" + conta
                + "' AND CTX_ID = " + Contextos.getContextoAtual();
        ResultSet rs = DAO.query(x);
        try {
            rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Conta c = new Conta();

        try {
            c.codigo = rs.getString("CON_Codigo");
            c.contexto = rs.getLong("CTX_ID");
            c.nome = rs.getString("CON_Nome");
            c.saldo = rs.getDouble("CON_Saldo");
            c.tipo = rs.getString("CON_Tipo").charAt(0);
            c.funcao = rs.getString("CON_Funcao").charAt(0);
            c.pai = rs.getString("CON_Pai");
            c.ordem = rs.getInt("CON_Ordem");
            c.categoria_dre = rs.getLong("CAT_ID");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return c;
    }

    /**
     * Salva uma entidade no banco
     */
    public static void store(Conta conta) throws Exception {
        String strSQL;

        strSQL = "INSERT INTO CON_Conta (CON_Codigo, CTX_ID, CON_Nome, CON_Saldo, CON_Tipo, CON_Funcao, CON_Pai, CON_Ordem, CAT_ID) "
                + "VALUES ('"
                + conta.codigo
                + "', "
                + conta.contexto
                + ", '"
                + conta.nome
                + "', "
                + conta.saldo
                + ", '"
                + Character.toUpperCase(conta.tipo)
                + "', "
                + "'"
                + Character.toUpperCase(conta.funcao) + "', ";

        // Trata valores nulos para o campo pai
        if (conta.pai != null) {
            strSQL += "'" + conta.pai + "', ";
        } else {
            strSQL +=  "null, ";
        }

        strSQL += conta.ordem + ", ";
        strSQL += conta.categoria_dre + ")";

        DAO.update2(strSQL);
    }

    /**
     * Atualiza uma entidade do banco
     */
    public static void update(Conta conta) {
        String strSQL;

        strSQL = "UPDATE CON_Conta SET CON_Nome = '" + conta.nome
                + "', CON_Saldo = " + conta.saldo + ", CON_Tipo = '"
                + Character.toUpperCase(conta.tipo) + "', CON_Funcao = '"
                + Character.toUpperCase(conta.funcao) + "'"; 
        // Adiciona pai, se não null, à string de atualização
        if (conta.pai != null) {
            strSQL += ", CON_Pai = '" + conta.pai + "'";
        }
        strSQL += ", CON_Ordem = " + conta.ordem ;
        strSQL += ", CAT_ID = " + conta.categoria_dre + " "
                + "WHERE CON_Codigo = '" + conta.codigo + "'"
                + " AND CTX_ID = " + conta.contexto + "";

        DAO.update(strSQL);
    }
    private long categoria_dre; // foreign key para categoria_dre, pode ser null
    private String codigo; // chave
    private long contexto; // chave e foreign key para Contexto
    private char funcao;
    private String nome;
    private String pai; // foreign key para Conta, pode ser null
    private Number saldo;
    private char tipo;
    private int ordem;

    /**
     *
     */
    public Conta() {
    }

    /**
     * @param codigo codigo da conta no formato #(#)*(.#(#)*)
     *
     * @param contexto contexto da conta
     * @param nome nome da conta
     * @param saldo saldo inicial da conta
     * @param tipo (A)tivo, (P)assivo, Patrimonio (L)iquido, (R)eceita ou
     * (D)espesa
     * @param funcao (S)intetica ou (A)nalitica
     * @param pai conta pai
     * @param ordem ordem da conta entre seus irmãos
     * @param categoria_dre Categoria na demonstração de resultados para contas
     * do tipo despesa ou receita
     */
    public Conta(String codigo, long contexto, String nome, Number saldo,
            char tipo, char funcao, String pai, int ordem, long categoria_dre) {
        super();
        this.codigo = codigo;
        this.contexto = contexto;
        this.nome = nome;
        this.saldo = saldo;
        this.tipo = tipo;
        this.funcao = funcao;
        this.pai = pai;
        this.ordem = ordem;
        this.categoria_dre = categoria_dre;
    }

    /**
     * @return A categoria DRE da conta
     */
    public long getCategoria_dre() {
        return categoria_dre;
    }

    /**
     * @return o código da conta
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @return o numero do contexto ao qual ela pertence
     */
    public long getContexto() {
        return contexto;
    }

    /**
     * @return a função (analitica ou sintética) da conta
     */
    public char getFuncao() {
        return funcao;
    }

    /**
     * @return o nome da conta
     */
    public String getNome() {
        return nome;
    }
    
        
    /**
     * @return a ordem da conta
     */
    public int getOrdem() {
        return ordem;
    }

    /**
     * @return a conta pai da atual
     */
    public String getPai() {
        return pai;
    }

    /**
     * @return o saldo da conta
     */
    public Number getSaldo() {
        return saldo;
    }

    /**
     * @return o tipo da conta
     */
    public char getTipo() {
        return tipo;
    }

    /**
     * @param categoria_dre A categoria DRE da conta
     */
    public void setCategoria_dre(long categoria_dre) {
        this.categoria_dre = categoria_dre;
    }

    /**
     * @param codigo o código da conta
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @param contexto o numero do contexto ao qual ela pertence
     */
    public void setContexto(long contexto) {
        this.contexto = contexto;
    }

    /**
     * @param funcao a função da conta (analitica ou sintética)
     */
    public void setFuncao(char funcao) {
        this.funcao = funcao;
    }

    /**
     * @param nome o nome da conta
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param ordem a ordem da conta entre as contas irmãs
     */
    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    /**
     * @param pai o pai da conta (null para nenhum)
     */
    public void setPai(String pai) {
        this.pai = pai;
    }

    /**
     * @param saldo o saldo da conta
     */
    public void setSaldo(Number saldo) {
        this.saldo = saldo;
    }

    /**
     * @param tipo o tipo da conta
     */
    public void setTipo(char tipo) {
        this.tipo = tipo;
    }    

    @Override
    public String toString() {
        return codigo + "-" + nome;
    }
}
