package com.sevenrtc.aas.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

import com.sevenrtc.aas.ui.helper.ImageLoader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * UI sobre informacoes do programa, contem, alem do icone e a mensagem de
 * copyright, um {@link javax.swing.JEditorPane} a partir do qual podem ser
 * disparados links para webpages
 * 
 * @author Anthony Accioly
 * 
 */
public class AboutDialog extends JDialog {
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
					new AboutDialog(null);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

			}
		});

	}

	private JButton botaoFechar = new JButton();

	private JEditorPane editorCreditos = new JEditorPane();

	private JLabel iconeAAS = new JLabel();

	private JLabel mensagemCopyright = new JLabel();

	/**
	 * Default constructor
	 */
	public AboutDialog(JFrame owner) {
		super(owner, "Sobre o AAS", true);
		initializePanel();
		this.setSize(500, 520);
		this.setLocationRelativeTo(owner);
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

	/** Executado junto ao botão fechar* */
	private void botaoFechar_actionPerformed() {
		this.dispose();
	}

	/**
	 * Cria o painel principal
	 * 
	 * @return painel principal da janela
	 */
	public JPanel createPanel() {
		JPanel painelPrincipal = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,FILL:80DLU:GROW(1.0),CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:4DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
		CellConstraints cc = new CellConstraints();
		painelPrincipal.setLayout(formlayout1);

		iconeAAS.setIcon(ImageLoader.abrirImagem("/images/lgo.png", "AAS"));
		iconeAAS.setName("iconeAAS");
		iconeAAS.setText("Versão 1.2");
		iconeAAS.setToolTipText("AAS Versão 1.2");
		iconeAAS.setVerticalTextPosition(JLabel.BOTTOM);
		painelPrincipal.add(iconeAAS, cc.xy(2, 2));

		editorCreditos.setEditable(false);
		editorCreditos.setName("editorCreditos");
		editorCreditos.setEditable(false);

		try {
			editorCreditos.addHyperlinkListener(new HyperlinkListener() {

				BrowserLauncher bL = new BrowserLauncher();

				public void hyperlinkUpdate(HyperlinkEvent e) {
					if (e.getEventType().equals(
							HyperlinkEvent.EventType.ACTIVATED))
						bL.openURLinBrowser(e.getURL().toString());

				}

			});
		} catch (BrowserLaunchingInitializingException e) {
			e.printStackTrace();
		} catch (UnsupportedOperatingSystemException e) {
			e.printStackTrace();
		}

		try {
			editorCreditos.setPage(AboutDialog.class.getResource("/about.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JScrollPane scrollCreditos = new JScrollPane();
		scrollCreditos.setViewportView(editorCreditos);
		scrollCreditos
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollCreditos
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		painelPrincipal.add(scrollCreditos, cc.xywh(2, 4, 3, 1));

		mensagemCopyright.setName("mensagemCopyright");
		mensagemCopyright
				.setText("Copyright © 2007-2020 AAS Contributors. All rights reserved.");
		mensagemCopyright.setHorizontalAlignment(JLabel.TRAILING);
		mensagemCopyright.setToolTipText("Copyright © 2007-2020 AAS Contributors");
		painelPrincipal.add(mensagemCopyright, cc.xywh(2, 6, 3, 1));

		botaoFechar.setActionCommand("Fechar");
		botaoFechar.setText("Fechar");
		botaoFechar.setToolTipText("Fechar a Janela");
		botaoFechar.setMnemonic('f');
		botaoFechar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoFechar_actionPerformed();
			}
		});
		painelPrincipal.add(botaoFechar, cc.xy(4, 8));

		addFillComponents(painelPrincipal, new int[] { 1, 2, 3, 4, 5 },
				new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
		return painelPrincipal;
	}

	/**
	 * Inicializa a JDialog, setando handlers e etc
	 */
	protected void initializePanel() {
		setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		add(createPanel(), BorderLayout.CENTER);
	}

}
