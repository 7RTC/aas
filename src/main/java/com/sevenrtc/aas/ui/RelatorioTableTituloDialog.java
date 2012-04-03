/**
 * 
 */
package com.sevenrtc.aas.ui;

import com.sevenrtc.aas.shared.RelatoriosCSV;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * Classe de interface para relatórios diversos
 * 
 * @author Anthony Accioly
 * 
 */
public class RelatorioTableTituloDialog extends JPanel {
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
					frame.getContentPane().add(new RelatorioTableTituloDialog());
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

	private int tipoRelatorio;

	private String titulo;

	private String subtitulo;

	private JTable tabelaResultados = new JTable();

	private JSeparator linhaSeparatoria = new JSeparator();

	private JButton botaoExportar = new JButton();

	private JLabel labelTituloRelatorio = new JLabel();

	private JLabel labelSubtitulo = new JLabel();

	/**
	 * Construtor padrão da classe
	 */
	public RelatorioTableTituloDialog() {
		initializePanel();
	}

	/**
	 * Construtor da classe para ser utilizado com parametros de configuração
	 * 
	 * @param titulo
	 *            titulo da janela
	 * @param subtitulo
	 *            subtitulo da janela
	 * @param tipoRelatorio
	 *            inteiro representando o tipo de relatorio
	 */
	public RelatorioTableTituloDialog(String titulo, String subtitulo,
			int tipoRelatorio) {
		this.titulo = titulo;
		this.subtitulo = subtitulo;
		this.tipoRelatorio = tipoRelatorio;
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
	private void addFillComponents(Container panel, int[] cols, int[] rows) {
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

	/** Metodo ativado juntamente ao botao exportar */
	private void botaoExportar_actionPerformed() {
		RelatoriosCSV.extraiCSV(tipoRelatorio, tabelaResultados);
	}

	/**
	 * Retorna a Frame interna deste Relatório
	 * 
	 * @param titulo
	 *            String de titulo da conta atual
	 */
	public JInternalFrame createInternalFrame(String titulo) {

		JInternalFrame temp = new JInternalFrame(titulo, true, true, true, true);
		Dimension dskSize = PrincipalFrame.getDesktopSize();
		dskSize.setSize(dskSize.getWidth() * 0.9, dskSize.getHeight() * 0.9);
		temp.setLocation(5, 5);
		temp.setSize(dskSize);

		temp.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
		temp.getContentPane().add(createPanel());
		temp.setVisible(true);

		return temp;
	}

	/**
	 * Cria o painel interno pertencente à Frame
	 * 
	 * @return painel interno linkado à todos os componentes
	 */
	public JPanel createPanel() {
		JPanel jpanel1 = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:80DLU:NONE,FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:DEFAULT:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:20DLU:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		jpanel1.setLayout(formlayout1);

		tabelaResultados.setName("tabelaResultados");
		tabelaResultados.setCellSelectionEnabled(true);
		tabelaResultados.setFillsViewportHeight(true);
		JScrollPane jscrollpane1 = new JScrollPane();
		jscrollpane1.setViewportView(tabelaResultados);
		jpanel1.add(jscrollpane1, cc.xywh(2, 6, 2, 1));

		linhaSeparatoria.setName("linhaSeparatoria");
		jpanel1.add(linhaSeparatoria, cc.xywh(2, 8, 2, 1));

		botaoExportar.setMnemonic('x');
		botaoExportar.setText("Exportar CSV");
		botaoExportar.setToolTipText("Exporta um arquivo CSV da tabela atual");
		botaoExportar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				botaoExportar_actionPerformed();
			}
		});
		jpanel1.add(botaoExportar, cc.xy(3, 10));

		labelTituloRelatorio.setFont(new Font("Arial", Font.BOLD, 16));
		labelTituloRelatorio.setName("labelTituloRelatorio");
		labelTituloRelatorio.setText(titulo);
		labelTituloRelatorio.setHorizontalAlignment(JLabel.CENTER);
		jpanel1.add(labelTituloRelatorio, cc.xywh(2, 2, 2, 1));

		labelSubtitulo.setFont(new Font("Arial", Font.BOLD, 16));
		labelSubtitulo.setName("labelSubtitulo");
		labelSubtitulo.setText(subtitulo);
		labelSubtitulo.setHorizontalAlignment(JLabel.CENTER);
		jpanel1.add(labelSubtitulo, cc.xywh(2, 4, 2, 1));

		addFillComponents(jpanel1, new int[] { 1, 2, 3, 4 }, new int[] { 1, 2,
				3, 4, 5, 6, 7, 8, 9, 10, 11 });
		return jpanel1;
	}

	/**
	 * @return o subtitulo da janela
	 */
	public final String getSubtitulo() {
		return subtitulo;
	}

	/**
	 * @return a tabela de resultados do relatório
	 */
	public JTable getTabelaResultados() {
		return tabelaResultados;
	}

	/**
	 * @return um inteiro representando o tipo de relatório
	 */
	public final int getTipoRelatorio() {
		return tipoRelatorio;
	}

	/**
	 * @return o titulo da janela
	 */
	public final String getTitulo() {
		return titulo;
	}

	/**
	 * Inicializador da janela para o construtor padrão
	 */
	protected void initializePanel() {
		setLayout(new BorderLayout());
		add(createPanel(), BorderLayout.CENTER);
	}

	/**
	 * @param subtitulo
	 *            subtitulo da janela
	 */
	public final void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	/**
	 * @param tabelaResultados
	 *            tabela de resultados do relatório
	 */
	public void setTabelaResultados(JTable tabelaResultados) {
		this.tabelaResultados = tabelaResultados;
	}

	/**
	 * @param tipoRelatorio
	 *            o tipo atual do relatório
	 */
	public final void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	/**
	 * @param titulo
	 *            o titulo da janela
	 */
	public final void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}