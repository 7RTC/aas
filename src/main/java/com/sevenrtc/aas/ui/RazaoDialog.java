/**
 * 
 */
package com.sevenrtc.aas.ui;

import com.sevenrtc.aas.shared.Constantes;
import com.sevenrtc.aas.shared.Contas;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.ui.helper.JComboBoxAutoCompletion;
import com.sevenrtc.aas.shared.Relatorios;
import com.sevenrtc.aas.shared.RelatoriosCSV;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableColumnModel;

/**
 * Classe de interface para relatórios do Livro Razão
 * 
 * @author Anthony Accioly
 * 
 */
public class RazaoDialog extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6479814357522126962L;

	/**
	 * Main method for panel
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
					JFrame frame = new JFrame();
					frame.setSize(600, 400);
					frame.setLocation(100, 100);
					frame.getContentPane().add(new RazaoDialog());
					frame.setVisible(true);

					frame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent evt) {
							System.exit(0);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	private boolean justUsed = false;

	private long contexto;

	private JLabel labelTitulo = new JLabel();

	private JButton botaoExportar = new JButton();

	private JLabel labelNomeConta = new JLabel();

	private JTable tabelaResultados = new JTable();

	private JSeparator linhaSeparatoria = new JSeparator();

	private JButton botaoExibir = new JButton();

	private JComboBox comboCodigoConta = new JComboBox(Contas.getCodigos());

	private JLabel labelCodigo = new JLabel();

	private JLabel labelValorConta = new JLabel();

	// Seta funções para autocompletar código
	{
		JComboBoxAutoCompletion.enable(comboCodigoConta);
		comboCodigoConta.setEditable(true);
		if (Contas.getCodigos().length == 0) {
			comboCodigoConta.setEnabled(false);
			botaoExibir.setEnabled(false);
		} else
			labelValorConta.setText(Contas.getNome((String) comboCodigoConta
					.getSelectedItem()));

	}

	/**
	 * Default constructor
	 */
	public RazaoDialog() {
		contexto = Contextos.getContextoAtual();
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

	/** Metodo ativado junto ao botao exibir */
	private void botaoExibir_actionPerformed() {
		// Contra bug do java que dispara dois eventos por clique
		justUsed = !justUsed;
		if (!justUsed)
			return;
		// Abre o relatorio para a conta atual
		tabelaResultados.setModel(Relatorios.fichaRazao(
				(String) comboCodigoConta.getSelectedItem(), contexto));
		// Se a tabela nao foi formatada ate entao

		// Seta os renderes e tamanhos
		TableColumnModel cm = tabelaResultados.getColumnModel();
		cm.getColumn(0).setCellRenderer(Constantes.getRendererData());
		cm.getColumn(1).setCellRenderer(Constantes.getRendererCentralizado());
		cm.getColumn(3).setCellRenderer(Constantes.getRendererValor());
		cm.getColumn(4).setCellRenderer(Constantes.getRendererValor());
		cm.getColumn(5).setCellRenderer(Constantes.getRendererCentralizado());
		cm.getColumn(6).setCellRenderer(Constantes.getRendererValor());

		cm.getColumn(0).setPreferredWidth(80);
		cm.getColumn(1).setPreferredWidth(50);
		cm.getColumn(2).setPreferredWidth(200);
		cm.getColumn(3).setPreferredWidth(100);
		cm.getColumn(4).setPreferredWidth(100);
		cm.getColumn(5).setPreferredWidth(25);
		cm.getColumn(6).setPreferredWidth(100);

	}

	/** Metodo ativado junto ao botao exportar */
	private void botaoExportar_actionPerformed() {
		// Contra bug da aplicacao que dispara dois eventos por clique
		justUsed = !justUsed;
		if (!justUsed)
			return;

		RelatoriosCSV.extraiCSV(Relatorios.LIVRO_RAZAO, tabelaResultados);

	}

	/** Metodo ativado junto ao botao exibir */
	private void comboCodigoConta_itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED)
			labelValorConta.setText(Contas.getNome((String) comboCodigoConta
					.getSelectedItem()));
	}

	/** Retorna a Frame interna deste Relatório */
	public JInternalFrame createInternalFrame() {

		JInternalFrame temp = new JInternalFrame("Livro Razão ("
				+ Contextos.getNomeEmpresa() + ")", true, true, true, true);
		Dimension dskSize = PrincipalFrame.getDesktopSize();
		temp.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		dskSize.setSize(dskSize.getWidth() * 0.9, dskSize.getHeight() * 0.9);
		temp.setLocation(5, 5);
		temp.setSize(dskSize);

		temp.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		temp.setContentPane(createPanel());
		temp.setVisible(true);

		return temp;
	}

	/**
	 * Cria o painel principal da janela atual
	 * 
	 * @return o painel principal da janela
	 */
	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:DEFAULT:GROW(1.0),FILL:4DLU:NONE,FILL:80DLU:NONE,FILL:4DLU:NONE,FILL:80DLU:NONE,FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:10DLU:NONE,FILL:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:20DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		botaoExportar.setText("Exportar CVS");
		botaoExportar.setMnemonic('x');
		botaoExportar.setToolTipText("Exporta um arquivo CVS da tabela atual");
		botaoExportar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoExportar_actionPerformed();
			}
		});
		jpanel1.add(botaoExportar, cc.xy(8, 11));

		labelNomeConta.setFocusable(false);
		labelNomeConta.setFont(new Font("Arial", Font.BOLD, 14));
		labelNomeConta.setName("labelNomeConta");
		labelNomeConta.setToolTipText("Conta selecionada");
		labelNomeConta.setText("Conta: ");
		labelNomeConta.setHorizontalAlignment(JLabel.CENTER);
		labelNomeConta.setVerticalAlignment(JLabel.BOTTOM);
		labelNomeConta.setVerticalTextPosition(JLabel.BOTTOM);
		jpanel1.add(labelNomeConta, new CellConstraints(2, 4, 1, 1,
				CellConstraints.DEFAULT, CellConstraints.BOTTOM));

		tabelaResultados.setCellSelectionEnabled(true);
		tabelaResultados.setColumnSelectionAllowed(true);
		tabelaResultados.setName("tabelaResultados");
		tabelaResultados.setFillsViewportHeight(true);
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(tabelaResultados);
		jpanel1.add(jscrollpane1, cc.xywh(2, 7, 7, 1));

		linhaSeparatoria.setName("linhaSeparatoria");
		jpanel1.add(linhaSeparatoria, cc.xywh(2, 9, 7, 1));

		labelTitulo.setFont(new Font("Dialog", Font.BOLD, 16));
		labelTitulo.setName("labelTitulo");
		labelTitulo.setText("LIVRO RAZÃO");
		labelTitulo.setHorizontalAlignment(JLabel.CENTER);
		labelTitulo.setHorizontalTextPosition(JLabel.CENTER);
		jpanel1.add(labelTitulo, cc.xywh(2, 2, 7, 1));

		botaoExibir.setName("botaoExibir");
		botaoExibir.setText("Exibir");
		botaoExibir
				.setToolTipText("Exibe a Ficha Razão para a conta selecionada");
		botaoExibir.setMnemonic('e');
		botaoExibir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoExibir_actionPerformed();
			}
		});
		jpanel1.add(botaoExibir, cc.xy(6, 11));

		comboCodigoConta.setName("comboCodigoConta");
		comboCodigoConta.setToolTipText("Número da conta");
		comboCodigoConta.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				comboCodigoConta_itemStateChanged(e);
			}
		});
		jpanel1.add(comboCodigoConta, cc.xy(8, 4));

		labelCodigo.setFont(new Font("Arial", Font.BOLD, 14));
		labelCodigo.setName("labelCodigo");
		labelCodigo.setText("Código:");
		labelCodigo.setLabelFor(comboCodigoConta);
		labelCodigo.setDisplayedMnemonic('o');
		labelCodigo.setHorizontalAlignment(JLabel.RIGHT);
		labelCodigo.setHorizontalTextPosition(JLabel.RIGHT);
		labelCodigo.setVerticalAlignment(JLabel.BOTTOM);
		jpanel1.add(labelCodigo, cc.xy(6, 4));

		labelValorConta.setFont(new Font("Arial", Font.BOLD, 14));
		labelValorConta.setName("labelValorConta");
		labelValorConta.setVerticalAlignment(JLabel.BOTTOM);
		jpanel1.add(labelValorConta, cc.xy(4, 4));

		addFillComponents(jpanel1, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 },
				new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		return jpanel1;
	}

	/**
	 * Devolve a tabela do relatório atual
	 * 
	 * @return a tabela sendo exibida na janela
	 */
	public JTable getTabelaResultados() {
		return tabelaResultados;
	}

	/**
	 * Permite a sobreposição da tabela exibida na janela
	 * 
	 * @param tabelaResultados
	 *            tabela de resultados a ser setada
	 */
	public void setTabelaResultados(JTable tabelaResultados) {
		this.tabelaResultados = tabelaResultados;
	}

}