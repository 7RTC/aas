package com.sevenrtc.aas.shared;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.entidades.CategoriaDRE;
import com.sevenrtc.aas.entidades.Conta;

/**
 * Classe que mapeia codigo de conta a sua respectiva conta
 *
 * @author Anthony Accioly
 *
 */
public class Contas {

    // Mapa dos codigos das contas para seus nomes
    private static HashMap<String, String> contas = new HashMap<String, String>();
    // Codigos das contas em ordem
    private static Object[] cods;
    // Assinatura das contas em ordem
    private static Object[] assinaturasContas;
    // Categorias DRE em ordem
    private static Object[] categoriasDRE;
    // Nodo pai da estrutura de arvore do plano de contas
    private static DefaultMutableTreeNode noPai;

    /**
     * Adiciona uma conta a colecao
     *
     * @param c Conta a ser adicionada
     */
    public static void add(Conta c) {
        contas.put(c.getCodigo(), c.getNome());
    }

    ;

	/**
	 * Procura e adiciona os nodos descendentes do atual (recursivamente)
	 * 
	 * @param noPai
	 *            nodo pai
	 * @param mConta
	 *            conta do nodo pai
	 * @return nodo pai com com seus nodos descendentes adicionados
	 */
	public static DefaultMutableTreeNode adicionarNode(
            DefaultMutableTreeNode noPai, String mConta) {
        DefaultMutableTreeNode mAux;
        ResultSet x = contasFilhas(mConta);
        try {
            while (x.next()) {
                mAux = new DefaultMutableTreeNode(x.getString(1) + "-"
                        + x.getString(3));
                adicionarNode(mAux, x.getString(1));
                noPai.add(mAux);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return noPai;
    }

    ;

	/** Consulta o banco por contas filhas de determinado pai */
	public static ResultSet contasFilhas(String contaPai) {
        ResultSet mCursor = DAO.query("SELECT * FROM CON_CONTA WHERE CON_Pai = + '" + contaPai
                + "' AND CTX_ID = " + Contextos.getContextoAtual());

        return mCursor;
    }

    /**
     * Constroi e retorna uma estrutura de arvore contendo o plano de conta da
     * empresa atual
     *
     */
    private static void estruturaContas() {
        // Le cada conta analitica do banco e adiciona à colecao
        DefaultMutableTreeNode mAux;
        // Chama todas as contas no nivel superior da arvore
        ResultSet rs = DAO.query("SELECT * FROM CON_CONTA WHERE CON_PAI is null AND CTX_ID = "
                + Contextos.getContextoAtual());
        // Cria o nodo pai da colecao
        noPai = new DefaultMutableTreeNode(Contextos.getNomeEmpresa());

        try {
            // Para cada conta do nivel superior da arvore (sem pai)
            while (rs.next()) {
                // Cria um nodo para ela
                mAux = new DefaultMutableTreeNode(rs.getString(1) + "-"
                        + rs.getString(3));
                noPai.add(mAux);
                // E procura por todos seus descendentes
                adicionarNode(mAux, rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Procedimento interno para extrair os códigos e nomes das coleções em
     * ordem
     */
    private static void extractInfo() {
        cods = contas.keySet().toArray();
        Arrays.sort(cods);
        assinaturasContas = new Object[cods.length];
        /*
         * Itera sobre os elements do array de códigos e constrói suas
         * assinaturas
         */
        for (int i = 0; i < assinaturasContas.length; i++) {
            assinaturasContas[i] = getAssinatura((String) cods[i]);
        }

    }

    /**
     * Volta a assinatura de determinada conta
     *
     * @param codigo Codigo da conta
     * @return assinatura da conta
     */
    public static String getAssinatura(String codigo) {
        if (codigo == null) {
            return null;
        }
        return codigo + " - " + contas.get(codigo);
    }

    /**
     * @return array com as assinaturas das contas
     */
    public static Object[] getAssinaturasContas() {
        return assinaturasContas;
    }

    /**
     * Retorna os codigos das contas em ordem
     *
     * @return codigos da coleção
     */
    public static Object[] getCodigos() {
        return cods;
    }

    /**
     * Acha o nome de uma conta referente a um código
     *
     * @param codigo código da conta
     * @return nome da conta
     */
    public static String getNome(String codigo) {
        return contas.get(codigo);
    }

    /**
     * Devolve o nodo raiz de uma estrutura de arvore do plano de contas da
     * empresa atual. <p> O nodo pode ser usado diretamente no construtor de uma
     * {@link javax.swing.JTree} para exibir a estrutura do plano de contas. A
     * arvore é montada em tempo de execução para a empresa, porém, atualizações
     * nas contas são refletidas dinamicamente na mesma, não sendo necessario a
     * reconstrução dos dados a partir do nodo pai. <p> A estrutura do plano de
     * contas de uma empresa é mantida em memoria até que o usuario carregue o
     * plano de contas para outra empresa. Logo, acessos consecutivos ao plano
     * de contas da mesma empresa não requerem reestruturação da arvore
     *
     * @return nodo pai de uma estrutura de plano de contas para a empresa atual
     */
    public static DefaultMutableTreeNode getNoPai() {
        // Se o nodo aberto for nulo ou percentente a outra empresa
        if (noPai == null || !noPai.equals(Contextos.getNomeEmpresa())) // Estrutura as contas da empresa atual
        {
            estruturaContas();
        }
        // Retorna o nodo pai
        return noPai;
    }

    /**
     *
     * @return as categorias DRE em ordem
     */
    public static Object[] getCategoriasDRE() {
        return categoriasDRE;
    }

    /**
     * Remove uma conta da coleção
     *
     * @param c Conta a ser removida
     */
    public static void remove(Conta c) {
        contas.remove(c.getCodigo());
    }

    /**
     * Carrega as contas do banco de dados
     */
    public static void updateContas() {

        // Le cada conta analitica do banco e adiciona à colecao
        ResultSet rs = DAO.query("SELECT * FROM CON_CONTA WHERE CON_FUNCAO = 'A' AND CTX_ID = "
                + Contextos.getContextoAtual());
        contas = new HashMap<String, String>();

        int count = 0;
        try {
            Conta c;
            while (rs.next()) {
                c = Conta.load(rs);
                add(c);
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se não há contas e há uma empresa aberta atualmente
        if (count == 0 && Contextos.getContextoAtual() != 0) {
            try {
                // ** Ativo
                Conta.store(new Conta("1", Contextos.getContextoAtual(),
                        "Ativo", 0, 'a', 's', null, 0));
                // Circulante
                Conta.store(new Conta("1.1", Contextos.getContextoAtual(),
                        "Circulante", 0, 'a', 's', "1", 0));
                Conta.store(new Conta("1.1.1", Contextos.getContextoAtual(),
                        "Caixa", 0, 'a', 'a', "1.1", 0));
                Conta.store(new Conta("1.1.2", Contextos.getContextoAtual(),
                        "Banco", 0, 'a', 'a', "1.1", 0));
                Conta.store(new Conta("1.1.3", Contextos.getContextoAtual(),
                        "Duplicatas a receber", 0, 'a', 'a', "1.1", 0));
                Conta.store(new Conta("1.1.4", Contextos.getContextoAtual(),
                        "Estoques", 0, 'a', 'a', "1.1", 0));
                Conta.store((new Conta("1.1.5",
                        Contextos.getContextoAtual(),
                        "Despesas pagas antecipadamente", 0, 'a', 'a',
                        "1.1", 0)));
                // Realizável a longo prazo
                Conta.store(new Conta("1.2", Contextos.getContextoAtual(),
                        "Realizável a Longo Prazo", 0, 'a', 's', "1", 0));
                Conta.store(new Conta("1.2.1", Contextos.getContextoAtual(),
                        "Contas a receber", 0, 'a', 'a', "1.2", 0));
                // Permanente
                Conta.store(new Conta("1.3", Contextos.getContextoAtual(),
                        "Permanente", 0, 'a', 's', "1", 0));
                Conta.store(new Conta("1.3.1", Contextos.getContextoAtual(),
                        "Equipamentos", 0, 'a', 'a', "1.3", 0));
                Conta.store(new Conta("1.3.2", Contextos.getContextoAtual(),
                        "Terrenos", 0, 'a', 'a', "1.3", 0));
                Conta.store(new Conta("1.3.3", Contextos.getContextoAtual(),
                        "Imóveis", 0, 'a', 'a', "1.3", 0));
                Conta.store(new Conta("1.3.4", Contextos.getContextoAtual(),
                        "Veículos", 0, 'a', 'a', "1.3", 0));
                Conta.store(new Conta("1.3.5", Contextos.getContextoAtual(),
                        "Móveis e Utensílios", 0, 'a', 'a', "1.3", 0));

                // ** Passivo
                Conta.store(new Conta("2", Contextos.getContextoAtual(),
                        "Passivo", 0, 'p', 's', null, 0));
                // Circulante
                Conta.store(new Conta("2.1", Contextos.getContextoAtual(),
                        "Circulante", 0, 'p', 's', "2", 0));
                Conta.store(new Conta("2.1.1", Contextos.getContextoAtual(),
                        "Fornecedores", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.2", Contextos.getContextoAtual(),
                        "Impostos a recolher", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.3", Contextos.getContextoAtual(),
                        "Salários a Pagar", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.4", Contextos.getContextoAtual(),
                        "Encargos Sociais a Recolher", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.5", Contextos.getContextoAtual(),
                        "Empréstimos a Pagar", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.6", Contextos.getContextoAtual(),
                        "Contas a Pagar", 0, 'p', 'a', "2.1", 0));
                Conta.store(new Conta("2.1.7", Contextos.getContextoAtual(),
                        "Títulos a Pagar", 0, 'p', 'a', "2.1", 0));
                // Exigível a Longo Prazo
                Conta.store(new Conta("2.2", Contextos.getContextoAtual(),
                        "Exigível a longo prazo", 0, 'p', 's', "2", 0));
                Conta.store(new Conta("2.2.1", Contextos.getContextoAtual(),
                        "Financiamentos", 0, 'p', 'a', "2.2", 0));

                // Patrimônio Líquido
                Conta.store(new Conta("3", Contextos.getContextoAtual(),
                        "Patrimônio Líquido", 0, 'l', 's', null, 0));
                Conta.store(new Conta("3.1", Contextos.getContextoAtual(),
                        "Capital", 0, 'l', 'a', "3", 0));
                Conta.store(new Conta("3.2", Contextos.getContextoAtual(),
                        "Lucros Acumulados", 0, 'l', 'a', "3", 0));
                Conta.store(new Conta("3.3", Contextos.getContextoAtual(),
                        "Reservas", 0, 'l', 'a', "3", 0));

                // ** Vendas e Deduções
                Conta.store(new Conta("4", Contextos.getContextoAtual(),
                        "Vendas", 0, 'r', 's', null, 0));
                // Vendas Brutas
                Conta.store(new Conta("4.1", Contextos.getContextoAtual(),
                        "Vendas Brutas", 0, 'r', 's', "4", 0));
                Conta.store(new Conta("4.1.1", Contextos.getContextoAtual(),
                        "Mercado Interno", 0, 'r', 'a', "4.1", 1));
                Conta.store(new Conta("4.1.2", Contextos.getContextoAtual(),
                        "Mercado Externo", 0, 'r', 'a', "4.1", 1));
                // Deduções
                Conta.store(new Conta("4.2", Contextos.getContextoAtual(),
                        "Deduções", 0, 'd', 's', "4", 0));
                Conta.store(new Conta("4.2.1", Contextos.getContextoAtual(),
                        "IPI", 0, 'd', 'a', "4.2", 2));
                Conta.store(new Conta("4.2.2", Contextos.getContextoAtual(),
                        "ICMS", 0, 'd', 'a', "4.2", 2));
                Conta.store(new Conta("4.2.3", Contextos.getContextoAtual(),
                        "ISS", 0, 'd', 'a', "4.2", 2));
                Conta.store(new Conta("4.2.4", Contextos.getContextoAtual(),
                        "Impostos Diversos", 0, 'd', 'a', "4.2", 2));
                Conta.store(new Conta("4.2.5", Contextos.getContextoAtual(),
                        "Devoluções", 0, 'd', 'a', "4.2", 2));
                Conta.store(new Conta("4.2.6", Contextos.getContextoAtual(),
                        "Abatimentos", 0, 'd', 'a', "4.2", 2));

                // ** Despesas
                Conta.store(new Conta("5", Contextos.getContextoAtual(),
                        "Despesas", 0, 'd', 's', null, 0));
                // Custos dos Produtos Vendidos
                Conta.store(new Conta("5.1", Contextos.getContextoAtual(),
                        "Custos dos Produtos Vendidos", 0, 'd', 's', "5", 3));
                Conta.store(new Conta("5.1.1", Contextos.getContextoAtual(),
                        "Matéria Prima", 0, 'd', 'a', "5.1", 0));
                Conta.store(new Conta("5.1.2", Contextos.getContextoAtual(),
                        "Mão-de-obra Direta", 0, 'd', 'a', "5.1", 0));
                Conta.store(new Conta("5.1.3", Contextos.getContextoAtual(),
                        "Aluguel da Fábrica", 0, 'd', 'a', "5.1", 0));
                Conta.store(new Conta("5.1.4", Contextos.getContextoAtual(),
                        "Energia Elétrica", 0, 'd', 'a', "5.1", 0));
                Conta.store(new Conta("5.1.5", Contextos.getContextoAtual(),
                        "Depreciação de Equipamentos", 0, 'd', 'a', "5.1", 0));
                // Despesas de Vendas
                Conta.store(new Conta("5.2", Contextos.getContextoAtual(),
                        "Despesas de Vendas", 0, 'd', 's', "5", 4));
                Conta.store(new Conta("5.2.1", Contextos.getContextoAtual(),
                        "Comissão de Vendedores", 0, 'd', 'a', "5.2", 0));
                Conta.store(new Conta("5.2.2", Contextos.getContextoAtual(),
                        "Propaganda", 0, 'd', 'a', "5.2", 0));
                Conta.store(new Conta("5.2.3", Contextos.getContextoAtual(),
                        "Salários do Pessoal de Vendas", 0, 'd', 'a',
                        "5.2", 0));
                Conta.store(new Conta("5.2.4", Contextos.getContextoAtual(),
                        "Devedores Duvidosos", 0, 'd', 'a', "5.2", 0));
                // Despesas Administrativas
                Conta.store(new Conta("5.3", Contextos.getContextoAtual(),
                        "Despesas Administrativas", 0, 'd', 's', "5", 4));
                Conta.store(new Conta("5.3.1", Contextos.getContextoAtual(),
                        "Aluguel do Escritório", 0, 'd', 'a', "5.3", 0));
                Conta.store(new Conta("5.3.2", Contextos.getContextoAtual(),
                        "Material de Escritório", 0, 'd', 'a', "5.3", 0));
                Conta.store(new Conta("5.3.3", Contextos.getContextoAtual(),
                        "Salário do Pessoal Administrativo", 0, 'd', 'a',
                        "5.3", 0));
                Conta.store(new Conta("5.3.4", Contextos.getContextoAtual(),
                        "Encargos Sociais", 0, 'd', 'a', "5.3", 0));
                // Honorários dos administradores
                Conta.store(new Conta("5.4", Contextos.getContextoAtual(),
                        "Honorários da administração", 0, 'd', 's', "5", 4));
                Conta.store(new Conta("5.4.1", Contextos.getContextoAtual(),
                        "Honorários da diretoria", 0, 'd', 'a', "5.4", 0));
                // Resultados financeiros
                Conta.store(new Conta("5.5", Contextos.getContextoAtual(),
                        "Resultados Financeiros", 0, 'r', 's', "5", 0));
                Conta.store(new Conta("5.5.1", Contextos.getContextoAtual(),
                        "Juros", 0, 'd', 'a', "5.5", 5));
                Conta.store(new Conta("5.5.2", Contextos.getContextoAtual(),
                        "Correção Monetária da Dívida", 0, 'd', 'a', "5.5", 5));
                Conta.store(new Conta("5.5.3", Contextos.getContextoAtual(),
                        "Variação Cambial", 0, 'd', 'a', "5.5", 5));
                Conta.store(new Conta("5.5.4", Contextos.getContextoAtual(),
                        "Receita Financeira", 0, 'r', 'a', "5.5", 5));
                Conta.store(new Conta("5.5.5", Contextos.getContextoAtual(),
                        "Ganhos Extraordinários", 0, 'r', 'a', "5.5", 5));
                Conta.store(new Conta("5.5.6", Contextos.getContextoAtual(),
                        "Provisão para imposto de renda", 0, 'd', 'a',
                        "5.5", 5));
                // Resultados não operacionais
                Conta.store(new Conta("5.6", Contextos.getContextoAtual(),
                        "Resultados não operacionais", 0, 'r', 's', "5", 0));
                Conta.store(new Conta("5.6.1", Contextos.getContextoAtual(),
                        "Receitas não operacionais", 0, 'r', 'a', "5.6", 6));
                Conta.store(new Conta("5.6.2", Contextos.getContextoAtual(),
                        "Despesas não operacionais", 0, 'd', 'a', "5.6", 6));
                // Imposto de Renda e Contribuições Sociais
                Conta.store(new Conta("5.7", Contextos.getContextoAtual(),
                        "Imposto de Renda e Contribuições Sociais", 0, 'd',
                        's', "5", 0));
                Conta.store(new Conta("5.7.1", Contextos.getContextoAtual(),
                        "Corrente", 0, 'd', 'a', "5.7", 7));
                Conta.store(new Conta("5.7.2", Contextos.getContextoAtual(),
                        "Diferido", 0, 'r', 'a', "5.7", 7));
                // Imposto de Renda e Contribuições Sociais
                Conta.store(new Conta("5.8", Contextos.getContextoAtual(),
                        "Participações", 0, 'd', 's', "5", 0));
                Conta.store(new Conta("5.8.1", Contextos.getContextoAtual(),
                        "Debêntures", 0, 'd', 'a', "5.8", 8));
                Conta.store(new Conta("5.8.2", Contextos.getContextoAtual(),
                        "Doações", 0, 'd', 'a', "5.8", 8));
                Conta.store(new Conta("5.8.3", Contextos.getContextoAtual(),
                        "Participação dos administradores", 0, 'd', 'a', "5.8",
                        8));
                Conta.store(new Conta("5.8.4", Contextos.getContextoAtual(),
                        "Participação minoritária", 0, 'd', 'a', "5.8", 8));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chama o procedimento recursivamente
            updateContas();
            // sai do método para evitar repetição do extractInfo()
            return;
        }
        // Extrai os nomes e códigos da coleção em ordem alfabética
        extractInfo();

    }

    /**
     * Le cada Categoria DRE e adiciona a coleção
     *
     */
    public static void updateCategorias() {
        // Lista temporaria de categorias
        ArrayList<CategoriaDRE> categorias = new ArrayList<CategoriaDRE>();

        // Cria as categorias
        categorias.add(new CategoriaDRE(0, "(Nenhuma)"));
        categorias.add(new CategoriaDRE(1, "Receita Bruta"));
        categorias.add(new CategoriaDRE(2, "Deduções"));
        categorias.add(new CategoriaDRE(3, "Custos do Período"));
        categorias.add(new CategoriaDRE(4, "Resultado Operacional"));
        categorias.add(new CategoriaDRE(5, "Resultado Financeiro"));
        categorias.add(new CategoriaDRE(6, "Resultado Não Operacional"));
        categorias.add(new CategoriaDRE(7, "Imposto de Renda e C.S."));
        categorias.add(new CategoriaDRE(8, "Participações"));
        // Converte-as para um vetor
        categoriasDRE = categorias.toArray();

        try {
            // Se as categorias ainda não foram persistidas no banco
            if (!DAO.query("SELECT * FROM cat_categoriadre").next()) // Adiciona-as ao banco
            {
                for (CategoriaDRE c : categorias) {
                    CategoriaDRE.store(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Impede a construção da classe
     */
    private Contas() {
    }
}