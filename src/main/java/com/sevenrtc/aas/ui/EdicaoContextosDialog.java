/**
 * 
 */
package com.sevenrtc.aas.ui;

/**
 * Representa a lista de empresas presentes no banco e da ao usuario a opcaoo de
 * manipula-las
 * 
 * @author Anthony Accioly
 * @author Denise
 * 
 */
import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.entidades.Contexto;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class EdicaoContextosDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Main method for panel
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
					JFrame frame = new JFrame();
					frame.setLocation(100, 100);
					frame.add(new EdicaoContextosDialog(null).createPanel());

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	private boolean atualExcluido = false;

	private JButton botaoExcluir = new JButton();

	private JButton botaoFechar = new JButton();

	private JButton botaoModificar = new JButton();

	// Lista dinamica para a JList
	private DefaultListModel emp = new DefaultListModel();

	private ArrayList<Contexto> excluded = new ArrayList<Contexto>();

	private JLabel labelEmpresas = new JLabel();

	private JList listaEmpresas = new JList(emp);

	private boolean modified = false;

	/**
	 * Default constructor
	 */
	public EdicaoContextosDialog(Frame parent) {
		super(parent, "Organizar Contextos", true);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initializePanel();
		carregaLista();
		this.pack();
		this.setLocationRelativeTo(parent);
		listaEmpresas.setSelectedIndex(0);
		this.setVisible(true);

	}

	/**
	 * Adds fill components to empty cells in the first row and first column of
	 * the grid. This ensures that the grid spacing will be the same as shown in
	 * the designer.
	 * 
	 * @param cols
	 *            an array of column indices in the first row where fill
	 *            components should be added.
	 * @param rows
	 *            an array of row indices in the first column where fill
	 *            components should be added.
	 */
	void addFillComponents(Container panel, int[] cols, int[] rows) {
		Dimension filler = new Dimension(10, 10);

		boolean filled_cell_11 = false;
		CellConstraints cc = new CellConstraints();
		if (cols.length > 0 && rows.length > 0) {
			if (cols[0] == 1 && rows[0] == 1) {
				/** add a rigid area */
				panel.add(Box.createRigidArea(filler), cc.xy(1, 1));
				filled_cell_11 = true;
			}
		}

		for (int index = 0; index < cols.length; index++) {
			if (cols[index] == 1 && filled_cell_11) {
				continue;
			}
			panel.add(Box.createRigidArea(filler), cc.xy(cols[index], 1));
		}

		for (int index = 0; index < rows.length; index++) {
			if (rows[index] == 1 && filled_cell_11) {
				continue;
			}
			panel.add(Box.createRigidArea(filler), cc.xy(1, rows[index]));
		}

	}

	/**
	 * Diz se o contexto em uso foi exlcluido
	 * 
	 * @return verdadeiro se o contexto atual for excluido
	 */
	public final boolean atualBeenExcluded() {
		return atualExcluido;
	}

	/**
	 * Diz se algum contexto foi modificado
	 * 
	 * @return verdadeiro se algum contexto foi modificado
	 */
	public boolean beenModified() {
		return modified;
	}

	/**
	 * Metodo acionado quanto o botao excluir e ativado
	 */
	private void botaoExcluir_actionPerformed() {
		String mensagem = "ATENÇÃO: Excluir uma empresa implicará na perda de\n"
				+ "todos os dados associados a ela de forma permanente\n"
				+ "e irreversível.\n" + "Deseja continuar com a operação?";
		int ret = JOptionPane.showOptionDialog(this, mensagem,
				"Excluir Empresa", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null,
				new Object[] { "Sim", "Não" }, "Sim");
		// Se o usuário confirmar
		if (ret == 0) {
			// Isola os items selecionados
			Object[] excluidas = listaEmpresas.getSelectedValues();
			for (Object o : excluidas) {
				// Isola o objeto atual
				Contexto c = (Contexto) o;
				// isola o id do objeto atual
				long id = c.getId();
				DAO.update(" DELETE FROM mov_movimento WHERE ctx_id = " + id);
				DAO
						.update("DELETE FROM pdi_partidadiario WHERE ctx_id = "
								+ id);
				DAO.update("DELETE FROM con_conta WHERE ctx_id = " + id);
				DAO.update("DELETE FROM ctx_contexto WHERE ctx_id = " + id);
				// adiciona-o a colecao de excluidos
				excluded.add(c);
				// Remove o elemento selecionado da lista
				emp.removeElement(c);
				// Se o contexto atual foi excluido
				if (c.getId() == Contextos.getContextoAtual())
					// indica o fato
					atualExcluido = true;
			}
			// Checa funcionalidade dos botoes
			checaBotoes();
			// indica modificacao
			modified = true;
		}

	}

	/**
	 * Metodo acionado quanto o botao fechar e ativado
	 */
	private void botaoFechar_actionPerformed() {
		this.dispose();
	}

	/**
	 * Metodo acionado quanto o botao modificar e ativado
	 */
	private void botaoModificar_actionPerformed() {
		Contexto empresa = (Contexto) listaEmpresas.getSelectedValue();
		String nomeEmpresa = JOptionPane.showInputDialog(this,
				"Entre com o novo nome da empresa:", "Atualizar Empresa",
				JOptionPane.PLAIN_MESSAGE);
		if (nomeEmpresa != null && !nomeEmpresa.equals("")) {
			empresa.setEmpresa(nomeEmpresa);
			Contexto.update(empresa);
			listaEmpresas.repaint();
			// indica modificacao
			modified = true;
		}

	}

	/**
	 * Carrega a lista inicialmente de acordo com os contextos da classe
	 * Contexto
	 */
	private void carregaLista() {
		for (Contexto c : Contextos.getContextos())
			emp.addElement(c);
		checaBotoes();
	}

	/**
	 * Seta ou apaga os botoes Modificar e excluir de acordo com as empresas do
	 * modelo
	 */
	private void checaBotoes() {
		boolean set = !emp.isEmpty();
		botaoModificar.setEnabled(set);
		botaoExcluir.setEnabled(set);
	}

	/**
	 * Cria o painel principal
	 * 
	 * @return painel principal da janela
	 */
	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:PREF:GROW(1.0),FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:115PX:GROW(1.0),CENTER:4DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		listaEmpresas.setName("listaEmpresas");
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(listaEmpresas);
		jscrollpane1
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jscrollpane1
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpanel1.add(jscrollpane1, new CellConstraints(2, 5, 1, 1,
				CellConstraints.FILL, CellConstraints.FILL));

		labelEmpresas.setName("labelEmpresas");
		labelEmpresas.setText("Lista de Empresas Cadastradas:");
		labelEmpresas.setLabelFor(listaEmpresas);
		labelEmpresas.setDisplayedMnemonic('l');
		jpanel1.add(labelEmpresas, cc.xy(2, 2));

		jpanel1.add(createPanelBotoes(), new CellConstraints(2, 7, 1, 1,
				CellConstraints.CENTER, CellConstraints.CENTER));
		addFillComponents(jpanel1, new int[] { 1, 2, 3 }, new int[] { 1, 2, 3,
				4, 5, 6, 7, 8 });
		return jpanel1;
	}

	/**
	 * Cria o painel de botoes
	 * 
	 * @return painel de botoes dentro do painel principal
	 */
	public JPanel createPanelBotoes() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:50DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:50DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:50DLU:GROW(1.0)",
				"CENTER:17DLU:NONE");
		jpanel1.setLayout(formlayout1);

		botaoModificar.setText("Modificar");
		botaoModificar.setMnemonic('m');
		botaoModificar.setToolTipText("Modificar Empresa");
		botaoModificar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				botaoModificar_actionPerformed();
			}

		});
		jpanel1.add(botaoModificar, new CellConstraints(1, 1, 1, 1,
				CellConstraints.DEFAULT, CellConstraints.FILL));

		botaoExcluir.setText("Excluir");
		botaoExcluir.setMnemonic('e');
		botaoExcluir.setToolTipText("Excluir Empresa");
		botaoExcluir.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				botaoExcluir_actionPerformed();
			}

		});
		jpanel1.add(botaoExcluir, new CellConstraints(3, 1, 1, 1,
				CellConstraints.DEFAULT, CellConstraints.FILL));

		botaoFechar.setText("Fechar");
		botaoFechar.setMnemonic('f');
		botaoFechar.setToolTipText("Cancelar Operação");
		botaoFechar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				botaoFechar_actionPerformed();
			}

		});
		jpanel1.add(botaoFechar, new CellConstraints(5, 1, 1, 1,
				CellConstraints.DEFAULT, CellConstraints.FILL));

		addFillComponents(jpanel1, new int[] { 2, 4 }, new int[0]);
		return jpanel1;
	}

	/**
	 * Devolve uma ArrayList dos Contextos (empresas) excluidos
	 * 
	 * @return Retorna a lista de contextos excluidos
	 */
	public final ArrayList<Contexto> getExcluded() {
		return excluded;
	}

	/**
	 * Metodo que inicializa a Dialog
	 */
	private void initializePanel() {
		setLayout(new BorderLayout());
		add(createPanel(), BorderLayout.CENTER);
	}

}
