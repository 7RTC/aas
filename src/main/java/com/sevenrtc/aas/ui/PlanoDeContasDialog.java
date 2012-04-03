package com.sevenrtc.aas.ui;

import com.sevenrtc.aas.ui.helper.ImageLoader;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Contas;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.entidades.Conta;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * Interface responsavel por exibir e dar ao usuário a opção de manipular o
 * plano de contas de determina empresa.
 * <p>
 * O plano de contas é apresentado como uma {@link javax.swing.JTree} construida
 * a partir dos dados do banco na classe {@link br.aas.comum.Contas}.
 * 
 * @author José Paulo Lima
 * @author Anthony Accioly
 * @see br.aas.comum.Contas
 * 
 */
public class PlanoDeContasDialog extends JDialog {
	/**
	 * Classe interna responsável por cuidar da ordem da transferência de foco
	 * entre objetos internos da classe
	 * 
	 * @author Anthony
	 * 
	 */
	private class iFTP extends FocusTraversalPolicy {

		@Override
		public Component getComponentAfter(Container cont, Component comp) {
			if (comp.equals(arvoreContas))
				return botaoIncluir;
			if (comp.equals(botaoIncluir))
				return botaoAlterar;
			if (comp.equals(botaoAlterar))
				return botaoExcluir;
			return arvoreContas;
		}

		@Override
		public Component getComponentBefore(Container cont, Component comp) {
			if (comp.equals(arvoreContas))
				return botaoExcluir;
			if (comp.equals(botaoExcluir))
				return botaoAlterar;
			if (comp.equals(botaoAlterar))
				return botaoExcluir;
			return arvoreContas;
		}

		@Override
		public Component getDefaultComponent(Container con) {
			return arvoreContas;
		}

		@Override
		public Component getFirstComponent(Container con) {
			return arvoreContas;
		}

		@Override
		public Component getLastComponent(Container con) {
			return arvoreContas;
		}

	}

	private static final long serialVersionUID = 1L;

	/**
	 * Método principal da classe
	 * 
	 * @param args
	 *            argumentos passados como parametro
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new LancamentosFrame();
			}
		});
	}

	// Modelo da arvore
	private DefaultTreeModel modeloArvore = new DefaultTreeModel(Contas
			.getNoPai());

	private JTree arvoreContas = new JTree(modeloArvore);

	private JButton botaoAlterar = new JButton();

	private JButton botaoExcluir = new JButton();

	private JButton botaoIncluir = new JButton();

	private JPanel contentPane;

	private ImageIcon imagemIncluir = ImageLoader.abrirImagem("/images/incluir.gif","Incluir nova conta");

	private ImageIcon imagemAlterar = ImageLoader.abrirImagem("/images/alterar.gif","Alterar conta atual");

	private ImageIcon imagemExcluir = ImageLoader.abrirImagem("/images/delete.gif","Apagar conta atual");

	private JToolBar jToolBar = new JToolBar();

	private boolean precisaDeUpdate = false;

	private JScrollPane scrollArvores = new JScrollPane();

	/**
	 * Construtor da interface de plano de contas
	 * 
	 * @param parent
	 *            frame dona da atual
	 */
	public PlanoDeContasDialog(JFrame parent) {
		super(parent, true);
		try {
			jbInit();
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(parent);
			this.setFocusTraversalPolicy(new iFTP());

			this.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Método ativado quando a seleção da arvore de contas muda */
	private void arvoreContas_valueChanged() {
		String x = null;
		if (arvoreContas.getLastSelectedPathComponent() != null)
			x = arvoreContas.getLastSelectedPathComponent().toString();
		if (x != null && !x.equals(Contextos.getNomeEmpresa())) {
			/*
			 * Ativa o botao alterar se nao estivermos tratando do nodo raiz
			 */
			botaoAlterar.setEnabled(true);
			// E o botao excluir se estivermos tratando de um nodo folha
			if (((DefaultMutableTreeNode) arvoreContas
					.getLastSelectedPathComponent()).isLeaf())
				botaoExcluir.setEnabled(true);
			// Desabilita o botao excluir se nao for folha
			else
				botaoExcluir.setEnabled(false);

		}
		// Desabilita ambos se for raiz
		else {
			botaoAlterar.setEnabled(false);
			botaoExcluir.setEnabled(false);
		}
	}

	/** Método ativado junto ao botao alterar */
	private void botaoAlterar_actionPerformed() {
		// Isola o nodo atual
		DefaultMutableTreeNode nodoAtual = (DefaultMutableTreeNode) arvoreContas
				.getLastSelectedPathComponent();

		String numeroConta = nodoAtual.toString();
		// Isola a conta do nodo atual
		numeroConta = numeroConta.substring(0, numeroConta
				.indexOf((String) "-"));
		// Chama a janela de manutencao
		ManutencaoContas mc = new ManutencaoContas(this, numeroConta, 0, 1);

		boolean modified = mc.foiModificado();
		// Se houve modificacoes
		if (modified) {
			Conta conta = mc.getContaAtual();
			// Muda o nodo atual
			nodoAtual.setUserObject(conta.getCodigo() + "-" + conta.getNome());
			modeloArvore.nodeChanged(nodoAtual);
		}

		this.precisaDeUpdate |= modified;
	}

	/** Método ativado junto ao botao excluir */
	private void botaoExcluir_actionPerformed() {
		// Seleciona o nodo atual
		DefaultMutableTreeNode nodoAtual = (DefaultMutableTreeNode) arvoreContas
				.getLastSelectedPathComponent();
		// Extrai o numero da conta a partir dele
		String numeroConta = nodoAtual.toString();
		numeroConta = numeroConta.substring(0, numeroConta
				.indexOf((String) "-"));
		// Exclusao
		try {
			// Tenta excluir o movimento atual
			DAO.update2("DELETE FROM CON_Conta WHERE CON_Codigo = '"
					+ numeroConta + "' AND CTX_ID = "
					+ Contextos.getContextoAtual());

			/*
			 * Se o pai deste nodo não possui outros filhos e não é o nodo
			 * principal
			 */
			if (nodoAtual.getParent().getChildCount() == 1
					&& nodoAtual.getParent().toString() != Contextos
							.getNomeEmpresa()) {
				// isola o nodo pai
				String numeroContaPai = nodoAtual.getParent().toString();
				numeroContaPai = numeroContaPai.substring(0, numeroContaPai
						.indexOf((String) "-"));
				// atualiza a conta pai
				DAO.update("UPDATE con_conta SET con_funcao = 'A' "
						+ "WHERE con_codigo = '" + numeroContaPai + "' "
						+ "AND ctx_id = " + Contextos.getContextoAtual());
			}

			// Muda a seleção da arvore para o nodo anterior
			arvoreContas.setSelectionPath(new TreePath(nodoAtual
					.getPreviousNode().getPath()));

			// Exclui de forma dinamica o nodo atual da arvore
			modeloArvore.removeNodeFromParent(nodoAtual);
			// Verifica os botoes de incluir, excluir, etc
			arvoreContas_valueChanged();

			// Seta que precisa-se de um update no plano de contas
			precisaDeUpdate = true;
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Impossivel excluir uma conta que possui movimentos",
					"Erro", JOptionPane.ERROR_MESSAGE);
		}

	}

	/** Método ativado junto ao botao incluir */
	private void botaoIncluir_actionPerformed() {
		// Nodo pai selecionado
		DefaultMutableTreeNode nodoPai = (DefaultMutableTreeNode) arvoreContas
				.getLastSelectedPathComponent();
		// Conta do nodo selecionado
		String numeroContaPai = nodoPai.toString();
		if (!numeroContaPai.equals(Contextos.getNomeEmpresa()))
			numeroContaPai = numeroContaPai.substring(0, numeroContaPai
					.indexOf((String) "-"));
		// Invoca janela de manutencao
		ManutencaoContas m = new ManutencaoContas(this, numeroContaPai, nodoPai
				.getChildCount(), 0);
		// Verifica se houve modificacoes
		boolean modified = m.foiModificado();
		// Se houve modificacoes
		if (modified) {
			// Isola a conta inserida
			Conta contaInserida = m.getContaInserida();
			// Cria um nodo a partir da conta inserida
			DefaultMutableTreeNode nodoInserido = new DefaultMutableTreeNode(
					contaInserida.getCodigo() + "-" + contaInserida.getNome());
			// Insere o nodo na arvore
			modeloArvore.insertNodeInto(nodoInserido, nodoPai, nodoPai
					.getChildCount());
			// Abre o nodo inserido
			arvoreContas.scrollPathToVisible(new TreePath(nodoInserido
					.getPath()));
			arvoreContas_valueChanged();
		}

		this.precisaDeUpdate |= modified;
	}

	/**
	 * Inicializa a {@link javax.swing.JDialog} do plano de contas
	 * <p>
	 * A janela é inicializada no centro da janela pai ocupando uma area de 60%
	 * do tamanho da ultima
	 * 
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BorderLayout());
		// Seta o tamanho da janela
		{
			Dimension r = (super.getOwner()).getSize();
			int height = (int) (r.getHeight() * 0.6);
			int width = (int) (r.getWidth() * 0.6);
			this.setSize(width, height);
		}
		setTitle("Plano de Contas");
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				// Faz um update das contas ao final da operacao se necessario
				if (precisaDeUpdate)
					Contas.updateContas();
			}

		});

		arvoreContas.setAutoscrolls(true);
		arvoreContas.setSelectionRow(0);
		arvoreContas.setBorder(BorderFactory.createEtchedBorder());
		arvoreContas.setDoubleBuffered(true);
		arvoreContas.setEditable(false);
		arvoreContas.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				arvoreContas_valueChanged();
			}

		});
		arvoreContas.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		scrollArvores.setBounds(new Rectangle(7, 33, 381, 242));
		scrollArvores.setViewportView(arvoreContas);
		botaoIncluir.setIcon(imagemIncluir);
		botaoIncluir.setToolTipText("Incluir Conta");
		botaoIncluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoIncluir_actionPerformed();
			}
		});
		botaoAlterar.setIcon(imagemAlterar);
		botaoAlterar.setToolTipText("Alterar Conta");
		botaoAlterar.setEnabled(false);
		botaoAlterar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoAlterar_actionPerformed();
			}
		});
		botaoExcluir.setIcon(imagemExcluir);
		botaoExcluir.setToolTipText("Excluir Conta");
		botaoExcluir.setEnabled(false);
		botaoExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoExcluir_actionPerformed();
			}
		});
		jToolBar.add(botaoIncluir);
		jToolBar.add(botaoAlterar);
		jToolBar.add(botaoExcluir);
		contentPane.add(scrollArvores, BorderLayout.CENTER);
		contentPane.add(jToolBar, BorderLayout.NORTH);

	}

}
