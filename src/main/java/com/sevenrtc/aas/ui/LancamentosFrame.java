package com.sevenrtc.aas.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;

import java.awt.event.FocusEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import com.sevenrtc.aas.shared.Constantes;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.entidades.PartidaDiario;
import com.sevenrtc.aas.entidades.Movimento;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.NumberFormatter;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;

/**
 * Classe responsavel pelos lancamentos (conjunto de movimentos onde o crédito �
 * igual ao débito
 * 
 * @author Anthony Accioly
 * 
 */
public class LancamentosFrame extends JDialog {
	/**
	 * Classe interna responsável por cuidar da ordem da transferencia de foco
	 * entre objetos internos da classe
	 * 
	 * @author Anthony Accioly
	 * 
	 */
	private class iFTP extends FocusTraversalPolicy {

		public Component getComponentAfter(Container focusCycleRoot,
				Component aComponent) {

			if (aComponent.equals(fieldData))
				return listaDebitos;
			else if (aComponent.equals(listaDebitos))
				return listaCreditos;
			else if (aComponent.equals(listaCreditos)) {
				return areaHistorico;
			} else if (aComponent.equals(areaHistorico))
				return botaoConfirmar;
			else if (aComponent.equals(botaoConfirmar))
				return botaoCancelar;
			else if (aComponent.equals(botaoCancelar))
				return botaoDebitar;
			else if (aComponent.equals(botaoDebitar))
				return botaoCreditar;
			else if (aComponent.equals(botaoCreditar))
				return botaoApagar;
			else if (aComponent.equals(botaoApagar))
				return botaoModificar;
			else if (aComponent.equals(botaoModificar))
				return fieldData;

			return fieldData;
		}

		public Component getComponentBefore(Container focusCycleRoot,
				Component aComponent) {

			if (aComponent.equals(listaDebitos))
				return fieldData;
			else if (aComponent.equals(listaCreditos))
				return listaDebitos;
			else if (aComponent.equals(areaHistorico)) {
				return listaCreditos;
			} else if (aComponent.equals(botaoConfirmar))
				return areaHistorico;
			else if (aComponent.equals(botaoCancelar))
				return botaoConfirmar;
			else if (aComponent.equals(botaoDebitar))
				return botaoCancelar;
			else if (aComponent.equals(botaoCreditar))
				return botaoDebitar;
			else if (aComponent.equals(botaoApagar))
				return botaoCreditar;
			else if (aComponent.equals(botaoModificar))
				return botaoApagar;
			else if (aComponent.equals(fieldData))
				return botaoModificar;

			return fieldData;
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			return fieldData;
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return fieldData;
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return botaoCancelar;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2934588875281726473L;

	/**
	 * Método principal da classe
	 * 
	 * @param args
	 *            the command line arguments
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

	// Partida de Diario atual
	private PartidaDiario pD;

	private Double saldo = 0.0;

	// Listas de debito e credito das operações
	private ArrayList<Movimento> debitos;

	private ArrayList<Movimento> creditos;

	private NumberFormatter formatterSaldo;

	// Modelos para lista, permitem inserção dinamica de elementos
	private DefaultListModel d1 = new DefaultListModel();

	private DefaultListModel d2 = new DefaultListModel();

	private JLabel labelData = new JLabel();

	// Modificado manualmente, aceita datas no formato local
	private JFormattedTextField fieldData = new JFormattedTextField(Constantes
			.getFormatterData());

	private JSeparator jSeparator1 = new JSeparator();

	private JButton botaoConfirmar = new JButton();

	private JButton botaoCancelar = new JButton();

	private JButton botaoDebitar = new JButton();

	private JButton botaoCreditar = new JButton();

	private JButton botaoApagar = new JButton();

	private JButton botaoModificar = new JButton();

	private JSeparator jSeparator2 = new JSeparator();

	private JLabel labelDebitos = new JLabel();

	private JLabel labelCreditos = new JLabel();

	private JScrollPane scrollDebitos = new JScrollPane();

	private JScrollPane scrollCreditos = new JScrollPane();

	private JLabel labelSaldo = new JLabel();

	private JLabel labelValorSaldo = new JLabel();

	private JSeparator jSeparator3 = new JSeparator();

	private JList listaDebitos = new JList(d1);

	private JList listaCreditos = new JList(d2);

	private JLabel labelDescricao = new JLabel();

	private JLabel labelValorDescricao = new JLabel();

	private JLabel labelHistorico = new JLabel();

	private JScrollPane scrollHistorico = new JScrollPane();

	private JTextArea areaHistorico = new JTextArea();

	/*
	 * Inicializa formatador do campo saldo, seu diferencial em relação ao
	 * Constantes.formatterValor é que numeros negativos nao possuem sufixo ou
	 * prefixo
	 */
	{
		NumberFormat v = NumberFormat.getCurrencyInstance();
		if (v instanceof DecimalFormat) {
			// Elimina prefixos e sufixos de moedas locais (ex R$, U$, etc)
			((DecimalFormat) v).setPositivePrefix("");
			((DecimalFormat) v).setPositiveSuffix("");
			// Elimina sinais negativos
			((DecimalFormat) v).setNegativePrefix("");
			((DecimalFormat) v).setNegativeSuffix("");

		}
		formatterSaldo = Constantes.getFormatterValor();
	}

	/** Construtor padrão * */
	public LancamentosFrame() {
		try {
			jbInit();
			this.pD = new PartidaDiario(-1, null, null, -1);
			this.debitos = new ArrayList<Movimento>();
			this.creditos = new ArrayList<Movimento>();
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setFocusTraversalPolicy(new iFTP());
			areaHistorico.setFocusTraversalKeysEnabled(true);
			this.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Construtor que associa a Dialog à frame principal
	 * 
	 * @param parent
	 *            frame owner da atual
	 */
	public LancamentosFrame(JFrame parent) {
		super(parent, true);
		try {
			jbInit();
			this.pD = new PartidaDiario(-1, null, null, -1);
			this.debitos = new ArrayList<Movimento>();
			this.creditos = new ArrayList<Movimento>();
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			this.setFocusTraversalPolicy(new iFTP());
			areaHistorico.setFocusTraversalKeysEnabled(true);
			this.setLocationRelativeTo(parent);
			this.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Ativa o botao confirmar nos devidos caso */
	private void atualizaBotaoConfirmar() {
		if (saldo == 0 && !debitos.isEmpty() && !creditos.isEmpty())
			botaoConfirmar.setEnabled(true);
		else
			botaoConfirmar.setEnabled(false);
	}

	/** Ativa os botoes apagar e modificar nos devidos casos */
	private void atualizaBotoesApagModf() {
		if (listaDebitos.getSelectedIndex() != -1
				|| listaCreditos.getSelectedIndex() != -1) {
			botaoApagar.setEnabled(true);
			botaoModificar.setEnabled(true);
		} else {
			botaoApagar.setEnabled(false);
			botaoModificar.setEnabled(false);
		}
	}

	/** Atualiza o label de saldo */
	private void atualizaLabelValorSaldo() {
		// Se o saldo atual for zero, seta a cor para azul
		if (saldo == 0)
			labelValorSaldo.setForeground(Color.BLUE);
		// Se o saldo atual for positivo, seta a cor para verde escuro
		else if (saldo > 0)
			labelValorSaldo.setForeground(Color.GREEN.darker().darker());
		// Caso contrario seta o saldo para vermelho
		else
			labelValorSaldo.setForeground(Color.RED);

		// Atualiza o label
		try {
			labelValorSaldo.setText(formatterSaldo.valueToString(saldo));
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	/** Ativado juntamente ao botao apagar * */
	private void botaoApagar_actionPerformed() {
		// Se houverem valores selecionados na lista de Debitos
		if (listaDebitos.getSelectedIndex() != -1) {
			// Recebe os indices selecionados da lista
			int[] items = listaDebitos.getSelectedIndices();
			// Variavel auxiliar
			int item;
			/*
			 * Percorre os items selecionados da lista de tras para frente com a
			 * intencao de manter os indices equiparados entre a lista e a
			 * colecao durante o processo de exclusao
			 */
			for (int i = items.length - 1; i >= 0; i--) {
				// Isola a posicao do item atual
				item = items[i];
				// Remove o elemento da lista
				d1.removeElementAt(item);
				// Extorna o saldo
				saldo += debitos.get(item).getValor().doubleValue();
				// Remove o elemento da colecao
				debitos.remove(item);

			}
		}
		// Se houverem valores selecionados na lista de creditos
		else if (listaCreditos.getSelectedIndex() != -1) {
			// Recebe os indices selecionados da lista
			int[] items = listaCreditos.getSelectedIndices();
			// Variavel auxiliar
			int item;
			/*
			 * Percorre os items selecionados da lista de tras para frente com a
			 * intenca de manter os indices equiparados entre a lista e a
			 * colecao durante o processo de exclusao
			 */
			for (int i = items.length - 1; i >= 0; i--) {
				// Isola a posicao do item atual
				item = items[i];
				// Remove o elemento da lista
				d2.removeElementAt(item);
				// Extorna o saldo
				saldo -= creditos.get(item).getValor().doubleValue();
				// Remove o elemento da colecao
				creditos.remove(item);
			}
		}

		// Faz atualizacoes na GUI
		atualizaBotaoConfirmar();
		atualizaBotoesApagModf();
		atualizaLabelValorSaldo();
		labelValorDescricao.setText("");

	}

	/** Ativado juntamente ao botao cancelar * */
	private void botaoCancelar_actionPerformed() {
		this.dispose();
	} // fim do metodo

	/** Ativado juntamente ao botao confirmar * */
	private void botaoConfirmar_actionPerformed() {
		// Extrai dados referentes a partida de Diario
		pD.setContexto(Contextos.getContextoAtual());
		pD.setData((Date) fieldData.getValue());
		pD.setHistorico(areaHistorico.getText());
		// Joga-a para o banco
		PartidaDiario.store(pD);

		// Joga os debitos e creditos para o banco
		for (Movimento m : debitos) {
			m.setPartida(pD.getId());
			Movimento.store(m);
		}

		for (Movimento m : creditos) {
			m.setPartida(pD.getId());
			Movimento.store(m);
		}

		this.dispose();

	}

	/** Ativado juntamente ao botao creditar * */
	private void botaoCreditar_actionPerformed() {
		MovimentosDialog m = new MovimentosDialog(this, "Crédito", 'C');
		// Se o usuario confirmou a operacao
		if (m.getConfirmou()) {
			// adiciona o credito a colecao de creditos
			creditos.add(m.getMv());
			// Tenta adicionar o credito a lista de creditos
			try {
				d2.addElement(Constantes.getFormatterValor().valueToString(
						(m.getMv().getValor()))
						+ " [" + m.getMv().getConta() + "] ");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			// adiciona o movimento atual do saldo
			saldo += m.getMv().getValor().doubleValue();
			// atualiza o campo saldo
			atualizaLabelValorSaldo();
			// atualzia o botao confirmar
			atualizaBotaoConfirmar();
		}

	}

	/** Ativado juntamente ao botao debitar * */
	private void botaoDebitar_actionPerformed() {
		// Invoca janela de movimentos
		MovimentosDialog m = new MovimentosDialog(this, "Débito", 'D');
		// Se o usuario confirmou a operacao
		if (m.getConfirmou()) {
			// adiciona o debito a colecao de debitos
			debitos.add(m.getMv());
			// Tenta adicionar o debito a lista de debitos
			try {
				d1.addElement(Constantes.getFormatterValor().valueToString(
						(m.getMv().getValor()))
						+ " [" + m.getMv().getConta() + "] ");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			// subtrai o movimento atual do saldo
			saldo -= m.getMv().getValor().doubleValue();
			// atualiza o campo saldo
			atualizaLabelValorSaldo();
			// atualzia o botao confirmar
			atualizaBotaoConfirmar();

		}

	}

	/** Ativado juntamente ao botao modificar * */
	private void botaoModificar_actionPerformed() {
		// Se ha um indice selecionadO na lista de debitos
		if (listaDebitos.getSelectedIndex() != -1) {
			// Seleciona o indice atual
			int index = listaDebitos.getSelectedIndex();
			// Seleciona o valor antigo
			double oldValor = debitos.get(index).getValor().doubleValue();
			// Chama a janela de movimentos
			MovimentosDialog m = new MovimentosDialog(this, "Débito", debitos.get(index));
			// Se o usuario confirmou a operacao
			if (m.getConfirmou()) {
				// Tenta modificar o debito na lista de ed�bitos
				try {
					d1.add(index, Constantes.getFormatterValor().valueToString(
							(m.getMv().getValor()))
							+ " [" + m.getMv().getConta() + "] ");
					d1.remove(index + 1);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				// Atualiza o valor de saldo
				saldo += oldValor;
				saldo -= m.getMv().getValor().doubleValue();
				// atualiza o campo saldo
				atualizaLabelValorSaldo();
				// atualzia o botao confirmar
				atualizaBotaoConfirmar();
			} // fim do if interno
		} // fim do if externo

		// Se ha um indice selecionada na lista de creditos
		else if (listaCreditos.getSelectedIndex() != -1) {
			// Seleciona o indice atual
			int index = listaCreditos.getSelectedIndex();
			// Seleciona o valor antigo
			double oldValor = creditos.get(index).getValor().doubleValue();
			// Chama a janela de movimentos
			MovimentosDialog m = new MovimentosDialog(this, "Crédito", creditos.get(index));
			// Se o usuario confirmou a operacao
			if (m.getConfirmou()) {
				// Tenta modificar o credito na lista de creditos
				try {
					d2.add(index, Constantes.getFormatterValor().valueToString(
							(m.getMv().getValor()))
							+ " [" + m.getMv().getConta() + "] ");
					d2.remove(index + 1);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				// Atualiza o valor de saldo
				saldo -= oldValor;
				saldo += m.getMv().getValor().doubleValue();
				// atualiza o campo saldo
				atualizaLabelValorSaldo();
				// atualzia o botao confirmar
				atualizaBotaoConfirmar();
			} // fim do if interno
		} // fim do if externo
	}

	/**
	 * Inicializa os componentes da janela
	 * 
	 * @throws Exception
	 *             Caso seja impossivel iniciar a GUI
	 */
	private void jbInit() throws Exception {
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(440, 320));
		this.setTitle("Lançamento");
		this.setResizable(false);
		labelData.setText("Data:");
		labelData.setBounds(new Rectangle(10, 15, 45, 20));
		labelData.setDisplayedMnemonic('t');
		labelData.setLabelFor(fieldData);
		fieldData.setBounds(new Rectangle(50, 15, 105, 20));
		// Modificado manualmente, começa a Janela com a data de hoje
		fieldData.setValue(Constantes.getDataDeHoje());
		/*
		 * Modificado manualmente,seta o tool tip de acordo com a pattern de
		 * data
		 */
		fieldData.setToolTipText("Formato " + Constantes.getPatternData());
		jSeparator1.setBounds(new Rectangle(10, 40, 265, 5));
		botaoConfirmar.setText("Confirmar");
		botaoConfirmar.setBounds(new Rectangle(300, 10, 115, 30));
		botaoConfirmar.setMnemonic('o');
		botaoConfirmar.setToolTipText("Confirmar Partida");
		botaoConfirmar.setEnabled(false);
		botaoConfirmar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoConfirmar_actionPerformed();
			}
		});
		botaoCancelar.setText("Cancelar");
		botaoCancelar.setBounds(new Rectangle(300, 45, 115, 30));
		botaoCancelar.setToolTipText("Cancelar Operação");
		botaoCancelar.setMnemonic('c');
		botaoCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoCancelar_actionPerformed();
			}
		});
		botaoDebitar.setText("Debitar...");
		botaoDebitar.setBounds(new Rectangle(300, 100, 115, 30));
		botaoDebitar.setMnemonic('d');
		botaoDebitar.setToolTipText("Debitar conta");
		botaoDebitar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoDebitar_actionPerformed();
			}
		});
		botaoCreditar.setText("Creditar...");
		botaoCreditar.setBounds(new Rectangle(300, 137, 115, 30));
		botaoCreditar.setToolTipText("Creditar Conta");
		botaoCreditar.setMnemonic('r');
		botaoCreditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoCreditar_actionPerformed();
			}
		});
		botaoApagar.setText("Apagar...");
		botaoApagar.setBounds(new Rectangle(300, 173, 115, 30));
		botaoApagar
				.setToolTipText("Apagar Movimento (Permite múltipla seleção)");
		botaoApagar.setMnemonic('a');
		botaoApagar.setEnabled(false);
		botaoApagar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoApagar_actionPerformed();
			}
		});
		botaoModificar.setText("Modificar...");
		botaoModificar.setBounds(new Rectangle(300, 210, 115, 30));
		botaoModificar.setMnemonic('M');
		botaoModificar.setToolTipText("Modificar Movimento ");
		botaoModificar.setEnabled(false);
		botaoModificar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				botaoModificar_actionPerformed();
			}
		});
		jSeparator2.setBounds(new Rectangle(300, 85, 115, 15));
		labelDebitos.setText("Débitos:");
		labelDebitos.setBounds(new Rectangle(10, 50, 70, 15));
		labelDebitos.setDisplayedMnemonic('b');
		labelDebitos.setLabelFor(listaDebitos);
		labelCreditos.setText("Créditos:");
		labelCreditos.setBounds(new Rectangle(155, 50, 100, 15));
		labelCreditos.setDisplayedMnemonic('i');
		labelCreditos.setDisplayedMnemonicIndex(2);
		labelCreditos.setLabelFor(listaCreditos);
		scrollDebitos.setBounds(new Rectangle(10, 70, 125, 80));
		scrollDebitos
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollCreditos.setBounds(new Rectangle(155, 70, 125, 80));
		scrollCreditos
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		labelSaldo.setBounds(new Rectangle(10, 225, 50, 20));
		labelSaldo
				.setToolTipText("Saldo da operação (azul e neutro, vermelho é devedor e verde é credor)");
		labelSaldo.setText("Saldo:");
		labelValorSaldo.setBounds(new Rectangle(55, 225, 235, 20));
		labelValorSaldo
				.setToolTipText("Saldo da operação (azul e neutro, vermelho é devedor e verde é credor)");
		atualizaLabelValorSaldo();
		jSeparator3.setBounds(new Rectangle(10, 250, 405, 10));
		listaCreditos.setToolTipText("Lista de Créditos");
		listaCreditos.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				listaCreditos_focusGained();
			}
		});
		listaCreditos.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listaCreditos_valueChanged();
			}
		});
		listaDebitos.setToolTipText("Lista de Débitos");
		listaDebitos.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				listaDebitos_focusGained();
			}
		});
		listaDebitos.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				listaDebitos_valueChanged();
			}
		});
		labelDescricao.setText("Descrição:");
		labelDescricao.setBounds(new Rectangle(10, 260, 75, 20));
		labelDescricao.setToolTipText("Tipo de movimento, conta  e valor");
		labelValorDescricao.setBounds(new Rectangle(85, 260, 325, 20));
		labelValorDescricao.setToolTipText("Tipo de movimento, conta  e valor");
		labelHistorico.setText("Histórico:");
		labelHistorico.setBounds(new Rectangle(10, 155, 90, 15));
		labelHistorico.setDisplayedMnemonic('h');
		labelHistorico.setLabelFor(areaHistorico);
		scrollHistorico.setBounds(new Rectangle(10, 175, 270, 45));
		scrollHistorico
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollHistorico.setToolTipText("Descrição da operação");
		areaHistorico.setToolTipText("Descrição da operação");
		scrollHistorico.getViewport().add(areaHistorico, null);
		this.getContentPane().add(scrollHistorico, null);
		this.getContentPane().add(labelHistorico, null);
		this.getContentPane().add(labelValorDescricao, null);
		this.getContentPane().add(labelDescricao, null);
		this.getContentPane().add(jSeparator3, null);
		this.getContentPane().add(labelValorSaldo, null);
		this.getContentPane().add(labelSaldo, null);
		scrollCreditos.getViewport().add(listaCreditos, null);
		this.getContentPane().add(scrollCreditos, null);
		scrollDebitos.getViewport().add(listaDebitos, null);
		this.getContentPane().add(scrollDebitos, null);
		this.getContentPane().add(labelCreditos, null);
		this.getContentPane().add(labelDebitos, null);
		this.getContentPane().add(jSeparator2, null);
		this.getContentPane().add(botaoModificar, null);
		this.getContentPane().add(botaoApagar, null);
		this.getContentPane().add(botaoCreditar, null);
		this.getContentPane().add(botaoDebitar, null);
		this.getContentPane().add(botaoCancelar, null);
		this.getContentPane().add(botaoConfirmar, null);
		this.getContentPane().add(jSeparator1, null);
		this.getContentPane().add(fieldData, null);
		this.getContentPane().add(labelData, null);

	}

	/** Evento ativado quando a lista de creditos ganha foco */
	private void listaCreditos_focusGained() {
		// Limpa a seleca da lista de debitos
		listaDebitos.getSelectionModel().clearSelection();
	}

	/** Evento ativado quando a lista de creditos tem um item selecionado */
	private void listaCreditos_valueChanged() {
		// Mostra o resumo da operacao em questao
		if (listaCreditos.getSelectedIndex() != -1)
			labelValorDescricao.setText(creditos.get(
					listaCreditos.getSelectedIndex()).toString());
		// Atualiza os botoes de apagar e modificar
		atualizaBotoesApagModf();
	}

	/** Evento ativado quando a lista de debitos ganha foco */
	private void listaDebitos_focusGained() {
		// Limpa a selecao da lista de creditos
		listaCreditos.getSelectionModel().clearSelection();
	}

	/** Evento ativado quando a lista de debitos tem um item selecionado */
	private void listaDebitos_valueChanged() {
		// Mostra o resumo da operacao em questao
		if (listaDebitos.getSelectedIndex() != -1)
			labelValorDescricao.setText(debitos.get(
					listaDebitos.getSelectedIndex()).toString());
		// Atualiza os botoes de apagar e modificar
		atualizaBotoesApagModf();
	}

}