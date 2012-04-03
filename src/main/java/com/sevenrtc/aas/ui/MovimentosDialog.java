package com.sevenrtc.aas.ui;

import javax.swing.JSeparator;

import com.sevenrtc.aas.shared.Constantes;
import com.sevenrtc.aas.shared.Contas;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.ui.helper.JComboBoxAutoCompletion;
import com.sevenrtc.aas.entidades.Movimento;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Classe responsavel pelos movimentos contabeis individuais
 * 
 * @author Anthony Accioly
 * 
 */
public class MovimentosDialog extends JDialog {
	/**
	 * Classe interna responsavel por cuidar da ordem da transferancia de foco
	 * entre objetos internos da classe
	 * 
	 * @author Anthony Accioly
	 * 
	 */
	class iFTP extends FocusTraversalPolicy {

		public Component getComponentAfter(Container focusCycleRoot,
				Component aComponent) {

			if (aComponent.equals(boxConta.getEditor().getEditorComponent()))
				return fieldValor;
			else if (aComponent.equals(fieldValor)) {
				if (botaoConfirmar.isEnabled())
					return botaoConfirmar;
				return botaoCancelar;
			} else if (aComponent.equals(botaoConfirmar))
				return botaoCancelar;
			else if (aComponent.equals(botaoCancelar)) {
				if (boxConta.isEnabled())
					return boxConta;
				return botaoCancelar;
			}
			return boxConta;
		}

		public Component getComponentBefore(Container focusCycleRoot,
				Component aComponent) {

			if (aComponent.equals(boxConta.getEditor().getEditorComponent()))
				return botaoCancelar;
			else if (aComponent.equals(fieldValor))
				return boxConta;
			else if (aComponent.equals(botaoConfirmar))
				return fieldValor;
			else if (aComponent.equals(botaoCancelar)) {
				if (botaoConfirmar.isEnabled())
					return botaoConfirmar;
				else if (fieldValor.isEnabled())
					return fieldValor;
				return botaoCancelar;
			}

			return boxConta;
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			if (boxConta.isEnabled())
				return boxConta;
			else
				return botaoCancelar;
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return boxConta;
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return botaoCancelar;
		}

	}

	private static final long serialVersionUID = -7975413977558141485L;

	/**
	 * Metodo principal da classe
	 * 
	 * @param args
	 *            Strings passadas como parametro
	 */
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new MovimentosDialog('d');

			}
		});
	}

	private JButton botaoCancelar = new JButton();

	private JButton botaoConfirmar = new JButton();

	private JComboBox boxConta = new JComboBox(Contas.getCodigos());

	private boolean confirmou = false;

	/*
	 * Modificado manualmente, inicializa jFormattedTextFields com respectivos
	 * formatadores
	 */
	private JFormattedTextField fieldValor = new JFormattedTextField(Constantes
			.getFormatterValor());

	private JTextField fieldValorMovimento = new JTextField();

	private JSeparator jSeparator1 = new JSeparator();

	private JLabel labelConta = new JLabel();

	private JLabel labelValor = new JLabel();

	// Id do movimento
	private Movimento mv;

	// Seta configurações de foco e funções para autocompletar código
	{
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(
				forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newForwardKeys);
		this.setFocusTraversalPolicy(new iFTP());
		JComboBoxAutoCompletion.enable(boxConta);
		if (Contas.getCodigos().length == 0) {
			boxConta.setEnabled(false);
			fieldValor.setEnabled(false);
		}

	}

	/** Construtor para inicialização sem dependencia da interface principal */
	public MovimentosDialog(char tipo) {
		super();
		try {
			jbInit();
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Construtor para novos movimentos */
	public MovimentosDialog(JDialog parent, String title, char tipo) {
		super(parent, title, true);
		try {
			jbInit();
			this.mv = new Movimento(-1, 0, 0, tipo, -1, null, Contextos
					.getContextoAtual());
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(parent);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Construtor para movimentos existentes */
	public MovimentosDialog(JDialog parent, String title, Movimento m) {
		super(parent, title, true);
		try {
			jbInit();
			this.mv = m;
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setLocationRelativeTo(parent);
			boxConta.setSelectedItem(mv.getConta());
			fieldValor.setValue(mv.getValor());
			botaoConfirmar.setEnabled(true);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	/** Metodo ativado junto ao botao cancelar */
	private void botaoCancelar_actionPerformed() {
		this.dispose();
	}

	/** Metodo ativado junto ao botao confirmar */
	private void botaoConfirmar_actionPerformed() {

		mv.setConta((String) boxConta.getSelectedItem());
		mv.setValor((Number) fieldValor.getValue());
		confirmou = true;
		this.dispose();
	}

	/** Metodo ativado quando algum item da caixa de conta é selecionado */
	private void boxConta_itemStateChanged(ItemEvent e) {

		if (e.getStateChange() == ItemEvent.SELECTED)
			fieldValorMovimento.setText(Contas.getAssinatura((String) boxConta
					.getSelectedItem()));

	}

	/**
	 * Diz se o usuário confirmou a modificação
	 * 
	 * @return true caso o usuaário tenha confirmado a modificação
	 */
	public boolean getConfirmou() {
		return confirmou;
	}

	/**
	 * Retorna o ID do movimento modificado
	 * 
	 * @return long com o ID do movimento modificado
	 */
	public final long getMovimentoID() {
		return mv.getId();
	}

	/**
	 * Retorna o movimento modificado / recém inserido
	 * 
	 * @return movimento trabalhado na janela
	 */
	public Movimento getMv() {
		return mv;
	}

	/** Inicializa os componentes da janela
	 * 
	 * @throws Exception caso seja impossivel iniciar o GUI
	 */
	private void jbInit() throws Exception {
		JPanel painelPrincipal = new JPanel();
		FormLayout formlayout1 = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:70DLU:GROW(1.0),FILL:10DLU:NONE,FILL:55DLU:GROW(0.1),FILL:DEFAULT:NONE",
				"CENTER:DEFAULT:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:4DLU:NONE,CENTER:5DLU:NONE,CENTER:DEFAULT:NONE,CENTER:10DLU:NONE,CENTER:DEFAULT:NONE,CENTER:10DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");

		CellConstraints cc = new CellConstraints();
		painelPrincipal.setLayout(formlayout1);

		this.getContentPane().setLayout(new BorderLayout());
		labelValor.setText("Valor:");
		labelValor.setLabelFor(fieldValor);
		labelValor.setDisplayedMnemonic('v');
		labelConta.setText("Conta:");
		labelConta.setLabelFor(boxConta);
		labelConta.setDisplayedMnemonic('n');
		fieldValor.setToolTipText("Valor da Operação");

		/*
		 * Caso raro onde eu optei por um evento com logica interna para poder
		 * remove-lo em tempo de execucao usando o referenciador this... Por
		 * favor nao facam isso
		 */
		fieldValor.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				if (Character.isDigit(e.getKeyChar())) {
					botaoConfirmar.setEnabled(true);
					fieldValor.removeKeyListener(this);
				}

			}
		});
		boxConta.setEditable(true);
		boxConta.setToolTipText("Número da conta");
		boxConta.setMaximumRowCount(4);
		boxConta.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boxConta_itemStateChanged(e);
			}
		});
		botaoConfirmar.setText("Confirmar");
		botaoConfirmar.setMnemonic('o');
		botaoConfirmar.setToolTipText("Confirmar movimento");
		botaoConfirmar.setEnabled(false);
		botaoConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoConfirmar_actionPerformed();
			}
		});
		botaoCancelar.setText("Cancelar");
		botaoCancelar.setMnemonic('c');
		botaoCancelar.setToolTipText("Cancelar Operação");
		botaoCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoCancelar_actionPerformed();
			}
		});
		fieldValorMovimento.setEditable(false);
		// Modificado manualmente, atualiza o texto de field
		fieldValorMovimento.setText(Contas.getAssinatura((String) boxConta
				.getSelectedItem()));
		fieldValorMovimento.setToolTipText("Conta selecionada");
		fieldValorMovimento.setFocusable(false);

		painelPrincipal.add(labelConta, cc.xy(2, 3));
		painelPrincipal.add(labelValor, cc.xy(2, 6));
		painelPrincipal.add(jSeparator1, cc.xy(6, 8));
		painelPrincipal.add(botaoCancelar, cc.xywh(6, 5, 1, 2));
		painelPrincipal.add(botaoConfirmar, cc.xywh(6, 2, 1, 2));
		painelPrincipal.add(boxConta, cc.xy(4, 3));
		painelPrincipal.add(fieldValor, cc.xy(4, 6));
		painelPrincipal.add(fieldValorMovimento, cc.xywh(2, 10, 5, 1));
		addFillComponents(painelPrincipal, new int[] { 1, 2, 3, 4, 5, 6, 7 },
				new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
		this.add(painelPrincipal, BorderLayout.CENTER);
		this.pack();

	}

}
