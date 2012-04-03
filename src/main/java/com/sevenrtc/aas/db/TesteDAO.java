/**
 *
 */
package com.sevenrtc.aas.db;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.entidades.CategoriaDRE;
import com.sevenrtc.aas.entidades.Conta;
import com.sevenrtc.aas.entidades.Contexto;
import com.sevenrtc.aas.entidades.Movimento;
import com.sevenrtc.aas.entidades.PartidaDiario;
import com.sevenrtc.aas.shared.Constantes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Classe que testa as funcionalidades da classe DAO e os métodos store, load,
 * delete e update das classes entidades. Ilustra como manipular o banco através
 * de comandos sql e através dos métodos preparados de cada classe de entidade
 *
 * @author Anthony Accioly
 *
 */
public class TesteDAO {

    /**
     * Método principal da classe.
     *
     * @param args parametros passados por linha de comando
     * @throws ParseException caso o usuário tente converter um valor inválido
     */
    public static void main(String[] args) throws ParseException {
        
        Locale.setDefault(new Locale("pt", "BR"));

        // Testa as funcionalidades da classe Contexto
        {
            Contexto cxt = new Contexto(-1, "USP");
            Contexto.store(cxt);
            ResultSet rs = DAO.query("select * from ctx_contexto where ctx_empresa = 'USP'");
            try {
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Contexto cxt2 = Contexto.load(rs);
            Contexto.delete(cxt);
            Contexto.store(cxt2);
            cxt2.setEmpresa("AAS");
            Contexto.update(cxt2);
        }
        // Testa as funcionalidades da classe P_Diario
        {
            PartidaDiario pd = new PartidaDiario(-1, new Date(), "teste", 2);
            PartidaDiario.store(pd);
            ResultSet rs = DAO.query("select * from pdi_partidadiario where pdi_historico = 'teste'");
            try {
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            PartidaDiario pd2 = PartidaDiario.load(rs);
            PartidaDiario.delete(pd);
            PartidaDiario.store(pd2);
            pd2.setHistorico("teste 2");
            PartidaDiario.update(pd2);

        }
        // Testa as funcionalidades da classe Categoria_DRE
        {
            CategoriaDRE cat = new CategoriaDRE((long) -1, "teste");
            CategoriaDRE.store(cat);
            ResultSet rs = DAO.query("select * from cat_categoriadre where cat_nome = 'teste'");
            try {
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            CategoriaDRE cat2 = CategoriaDRE.load(rs);
            CategoriaDRE.delete(cat);
            CategoriaDRE.store(cat2);
            cat2.setNome("teste 2");
            CategoriaDRE.update(cat2);
        }
        // Testa as funcionalidades da classe Conta
        {
            Conta con = new Conta("33.33.1", 2, "teste", (Number) Constantes.getFormatterValor().stringToValue("999,99"), 'z', 'z',
                    null, 1);
            try {
                Conta.store(con);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Conta con2 = null;
            ResultSet rs = DAO.query("select * from con_conta where con_nome = 'teste'");
            try {
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con2 = Conta.load(rs);
            Conta.delete(con);
            try {
                Conta.store(con);
            } catch (Exception e) {
                e.printStackTrace();
            }
            con2.setNome("teste 2");
            Conta.update(con2);
        }

        // Testa as funcionalidades da classe Movimento
        {
            Movimento mov = new Movimento(-1, (Number) Constantes.getFormatterValor().stringToValue("250,00"),
                    (Number) Constantes.getFormatterValor().stringToValue(
                    "(1.000.000,00)"), 'c', 2, "33.33.1", 2);
            Movimento.store(mov);
            ResultSet rs = DAO.query("select * from mov_movimento");
            try {
                rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Movimento mov2 = Movimento.load(rs);
            Movimento.delete(mov);
            Movimento.store(mov2);
            mov2.setTipo('d');
            Movimento.update(mov2);

        }

        // Limpa o banco

        {
            DAO.update("delete from mov_movimento where mov_valor = 250.00");
            DAO.update("delete from pdi_partidadiario where pdi_historico = 'teste' or pdi_historico = 'teste 2'");
            DAO.update("delete from con_conta where con_nome = 'teste' or con_nome = 'teste 2'");
            DAO.update("delete from ctx_contexto where ctx_empresa = 'USP' or ctx_empresa = 'AAS'");
            DAO.update("delete from cat_categoriadre where cat_nome = 'teste' or cat_nome = 'teste 2'");
        }

        System.out.println("Fim de execução");
        DAO.shutdown();
    }
}
