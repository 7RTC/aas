package com.sevenrtc.aas.db;

/**
 *
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe de acesso ao banco de dados. Possui metodos estaticos para fazer
 * querys e updates.
 *
 * @author Anthony Accioly
 *
 */
public class DAO {

    private static Connection conn;
    private static Statement st;

    // Carrega o banco estaticamente
    static {

        load();

    }

    /**
     * Extrai resultados de um ResultSet
     *
     * @param rs ResultSet da consulta
     * @throws SQLException
     */
    public static void imprime(ResultSet rs) {

        // Extrai metaDados e o tamanhodas colunas
        ResultSetMetaData meta;
        try {
            meta = rs.getMetaData();
            int colmax = meta.getColumnCount();
            int i;
            Object o = null;

            // Imprime o nome das colunas
            for (i = 0; i < colmax; i++) {
                System.out.printf("%-10s ", meta.getColumnName(i + 1));
            }
            System.out.println("\n");

            // Trabalha com o cursor de resultados
            while (rs.next()) {
                // Opera sobre as colunas
                for (i = 0; i < colmax; ++i) {
                    // Imprime o resultado
                    o = rs.getObject(i + 1);
                    System.out.printf("%-10s ", o.toString());
                }

                System.out.println(" "); // Quebra a linha atual
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Carrega o objeto de banco de dados
     */
    public static void load() {
        // Se o banco estiver aberto, fecha-o
        if (conn != null || st != null) {
            shutdown();
        }

        // Abre o Driver de conexão
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Problemas ao carregar o Driver de conexão");
        }
        // Abre o banco
        try {
            conn = DriverManager.getConnection("jdbc:hsqldb:"
                    + ".AAS/arquivos/banco", "sa", "");
            st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); // Objeto de conversacao
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cria tabelas se estas ja nao tiverem sido criadas
        ResultSet rs = query("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES WHERE TABLE_NAME = 'CTX_CONTEXTO'");

        try {
            if (!rs.next()) {
                String sqlCTContexto, sqlCTContas, sqlCTDRE, sqlCTPartidas, sqlCTMovimentos;
                String sqlATContas1, sqlATContas2, sqlATContas3, sqlATMovimentos1, sqlATMovimentos2, sqlALTPartidas;
                String idxContas;

                sqlCTContexto = "CREATE TABLE CTX_Contexto "
                        + "(CTX_ID  INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) "
                        + "NOT NULL PRIMARY KEY, "
                        + "CTX_Empresa  VARCHAR(255) NOT NULL)";

                sqlCTPartidas = "CREATE TABLE PDI_PartidaDiario "
                        + "(PDI_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) "
                        + "NOT NULL PRIMARY KEY, "
                        + "PDI_Data  DATE NOT NULL, "
                        + "PDI_Historico  LONGVARCHAR, "
                        + "CTX_ID INTEGER NOT NULL)";

                sqlCTDRE = "CREATE TABLE CAT_CategoriaDRE "
                        + "(CAT_ID  INTEGER IDENTITY NOT NULL PRIMARY KEY, "
                        + " CAT_Nome VARCHAR(255) NOT NULL)";

                sqlCTMovimentos = "CREATE TABLE MOV_Movimento "
                        + "(MOV_ID   INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1) "
                        + "NOT NULL PRIMARY KEY, "
                        + "MOV_Valor  DECIMAL NOT NULL, "
                        + "MOV_SaldoConta DECIMAL NOT NULL, "
                        + "MOV_Tipo CHAR(1) NOT NULL, "
                        + "PDI_ID  INTEGER NOT NULL, "
                        + "CON_Codigo VARCHAR(255) NOT NULL, "
                        + "CTX_ID INTEGER NOT NULL)";

                sqlCTContas = "CREATE TABLE CON_Conta "
                        + "(CON_Codigo  VARCHAR(255) NOT NULL, "
                        + "CTX_ID  INTEGER NOT NULL, "
                        + "CON_Nome  VARCHAR(255) NOT NULL, "
                        + "CON_Saldo  DECIMAL NOT NULL, "
                        + "CON_Tipo  CHAR(1) NOT NULL, "
                        + "CON_Funcao  CHAR(1) NOT NULL, "
                        + "CON_Pai  VARCHAR(255), " 
                        + "CON_Ordem INTEGER NOT NULL, "
                        + "CAT_ID  INTEGER NOT NULL, "
                        + "PRIMARY KEY (CON_Codigo, CTX_ID))";

                sqlATContas1 = "ALTER TABLE CON_CONTA "
                        + "ADD CONSTRAINT FK_CTX_ID FOREIGN KEY (CTX_ID) REFERENCES CTX_CONTEXTO (CTX_ID)";

                sqlATContas2 = "ALTER TABLE CON_CONTA "
                        + "ADD CONSTRAINT FK_CAT_ID FOREIGN KEY (CAT_ID) REFERENCES CAT_CATEGORIADRE (CAT_ID)";

                sqlATContas3 = "ALTER TABLE CON_CONTA "
                        + "ADD CONSTRAINT FK_CON_ID FOREIGN KEY (CON_Codigo, CTX_ID) REFERENCES CON_CONTA (CON_Codigo, CTX_ID)";

                sqlATMovimentos1 = "ALTER TABLE MOV_MOVIMENTO "
                        + "ADD CONSTRAINT FK_PDI_ID FOREIGN KEY (PDI_ID) REFERENCES PDI_PARTIDADIARIO (PDI_ID)";

                sqlATMovimentos2 = "ALTER TABLE MOV_MOVIMENTO "
                        + "ADD CONSTRAINT FK_CON_ID_MOV FOREIGN KEY (CON_Codigo, CTX_ID) REFERENCES CON_CONTA (CON_Codigo, CTX_ID)";

                sqlALTPartidas = "ALTER TABLE PDI_PARTIDADIARIO "
                        + "ADD CONSTRAINT FK_CTX_ID_PDI FOREIGN KEY (CTX_ID) REFERENCES CTX_CONTEXTO (CTX_ID)";
                
                idxContas = "CREATE INDEX IDX_CON_CONTA_ORDEM ON CON_CONTA (CON_ORDEM)";

                DAO.update(sqlCTContexto);
                DAO.update(sqlCTDRE);
                DAO.update(sqlCTContas);
                DAO.update(sqlCTPartidas);
                DAO.update(sqlCTMovimentos);

                DAO.update(sqlATContas1);
                DAO.update(sqlATContas2);
                DAO.update(sqlATContas3);
                DAO.update(sqlATMovimentos1);
                DAO.update(sqlATMovimentos2);
                DAO.update(sqlALTPartidas);
                
                DAO.update(idxContas);
            }
        } catch (SQLException e) {
            System.err.println("Erro de acesso ao banco");
            System.exit(1);
        }

    }

    /**
     * Retorna o resultado de um Select
     *
     * @param expression Expressao SQL
     */
    public static synchronized ResultSet query(String expression) {

        ResultSet rs = null;
        try {
            rs = st.executeQuery(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;

    }

    /**
     * Fecha o Banco e persiste os Dados
     */
    public static void shutdown() {

        try {
            st.execute("SHUTDOWN");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Para comandos como CREATE, DROP, INSERT and UPDATE
     *
     * @param expression Expressao SQL
     */
    public static synchronized void update(String expression) {

        int i = 0;
        try {
            i = st.executeUpdate(expression);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (i == -1) {
            System.err.println("Erro ao executar expressão : " + expression);
        }

    }

    public static PreparedStatement getPreparedStatement(String expression) throws SQLException {
        return conn.prepareStatement(expression,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * Para comandos como CREATE, DROP, INSERT and UPDATE, relança exceções
     *
     * @param expression Expressão SQL
     * @throws Exception
     */
    public static synchronized void update2(String expression) throws Exception {

        int i = 0;
        i = st.executeUpdate(expression);

        if (i == -1) {
            System.err.println("Erro ao executar expressão : " + expression);
        }

    }
    
    public static synchronized void update(PreparedStatement ps) throws SQLException {
       ps.execute();     
    } 

    /**
     * Construtor privado impede que instancias DAO sejam criadas
     */
    private DAO() {
    }
}
