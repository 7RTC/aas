package com.sevenrtc.aas.ui;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Constantes;
import com.sevenrtc.aas.shared.Contas;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.shared.Relatorios;
import com.sevenrtc.aas.entidades.Contexto;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.sevenrtc.aas.ui.helper.ImageLoader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

/**
 * Janela principal do programa com uma barra lateral e um menu que garantem
 * acesso a toda funcionalidade do programa
 * <p>
 * Janelas de relatorio foram implementadas como
 * {@link javax.swing.JInternalFrame} e sao chamadas dentro do desktop. Janelas
 * de funcionalidade como Plano de contas e lancamentos foram implementadas como
 * {@link javax.swing.JDialog}.
 * 
 * @author Anthony Accioly
 * 
 */
public class PrincipalFrame extends JFrame {

	private static JDesktopPane desktop = new JDesktopPane();

	private static final long serialVersionUID = 8190651983388786307L;

	/**
	 * @return the desktop
	 */
	public static Dimension getDesktopSize() {
		return desktop.getSize();
	}

	/**
	 * Metodo principal da classe
	 * 
	 * @param args
	 *            o conjunto de argumentos passados como parametro
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
					new PrincipalFrame().setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	private JMenuBar barraDeMenu = new JMenuBar();

	private BorderLayout borderLayout2 = new BorderLayout();

	private JButton botaoBalancete = new JButton();

	private JButton botaoBalanco = new JButton();

	private JButton botaoDiario = new JButton();

	private JButton botaoDRE = new JButton();

	private JButton botaoLancamento = new JButton();

	private JButton botaoPlanoDeContas = new JButton();

	private JButton botaoRazao = new JButton();

	private GridLayout gridLayout1 = new GridLayout();

	private ButtonGroup grupoMenuEmpresas = new ButtonGroup();

	private JMenu menuAbrirEmpresa = new JMenu();

	private JMenu menuAcoes = new JMenu();

	private JMenu menuAjuda = new JMenu();

	private JMenu menuArquivo = new JMenu();

	private JMenuItem menuBalancete = new JMenuItem();

	private JMenuItem menuBalanco = new JMenuItem();

	private JMenuItem menuDiario = new JMenuItem();

	private JMenuItem menuDRE = new JMenuItem();

	private JMenuItem menuItemSair = new JMenuItem();

	private JMenuItem menuItemSobre = new JMenuItem();

	private JMenuItem menuLancamento = new JMenuItem();

	private JMenuItem menuNovaEmpresa = new JMenuItem();

	private JMenuItem menuOrganizarEmpresas = new JMenuItem();

	private JMenuItem menuPlanoDeContas = new JMenuItem();

	private JMenuItem menuRazao = new JMenuItem();

	private JMenu menuRelatorios = new JMenu();

	private JPanel painelEsquerdo = new JPanel();

	private JScrollPane scrollMenu = new JScrollPane();

	/**
	 * Construtor principal da classe
	 * 
	 */
	public PrincipalFrame() {
		try {
			jbInit();
			this.setLocationRelativeTo(null);
			this.setIconImage(ImageLoader.abrirImagem("/images/aas.png", "Icone AAS")
					.getImage());
			this.setFuncoesDisponiveis(false);
			updateMenuAbrirEmpresas();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Abre a janela de about */
	private void abrirAbout() {
		new AboutDialog(this);
	}

	/** Abre o relatorio de balancete dentro de uma frame interna */
	private void abrirBalancete() {
		// Cria o titulo da janela de relatorio
		String titulo = "BALANCETE DE VERIFICAÇÃO EM ";
		try {
			titulo += Constantes.getFormatterData().valueToString(
					Constantes.getDataDeHoje());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// Cria a janela de relatorio com titulo e subtitulo
		RelatorioTableTituloDialog b = new RelatorioTableTituloDialog(titulo, Contextos
				.getNomeEmpresa(), Relatorios.BALANCETE);
		// Chama o relatorio de balancete
		b.getTabelaResultados().setModel(Relatorios.balancete());

		// Seta o tamanho e os formatadores (renderes) das colunas
		{
			TableColumnModel cm = b.getTabelaResultados().getColumnModel();
			cm.getColumn(0).setPreferredWidth(100);
			cm.getColumn(1).setPreferredWidth(250);
			cm.getColumn(2).setPreferredWidth(100);
			cm.getColumn(2).setCellRenderer(Constantes.getRendererValor());
			cm.getColumn(3).setPreferredWidth(100);
			cm.getColumn(3).setCellRenderer(Constantes.getRendererValor());
		}

		// Cria frame interna
		JInternalFrame jan = b.createInternalFrame("Balancete de Verificação ("
				+ Contextos.getNomeEmpresa() + ")");

		// Adiciona-a ao desktop
		desktop.add(jan);
		try {
			// Maxima e seleciona a frase atual
			jan.setMaximum(true);
			jan.setSelected(true);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
		}
	}

	/** Abre o relatorio de balanco dentro de uma frame interna */
	private void abrirBalanco() {
		// Cria o titulo da janela de relatorio
		String titulo = "BALANÇO PATRIMONIAL EM  ";
		try {
			titulo += Constantes.getFormatterData().valueToString(
					Constantes.getDataDeHoje());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// Cria a janela de relatorio com titulo e subtitulo
		RelatorioTableTituloDialog b = new RelatorioTableTituloDialog(titulo, Contextos
				.getNomeEmpresa(), Relatorios.BALANCO);
		// Chama o relatorio do balanco
		b.getTabelaResultados().setModel(Relatorios.balanco());

		// Seta o tamanho e os formatadores (renderes) das colunas
		{
			TableColumnModel cm = b.getTabelaResultados().getColumnModel();
			cm.getColumn(0).setPreferredWidth(350);
			cm.getColumn(1).setPreferredWidth(100);
			cm.getColumn(1).setCellRenderer(Constantes.getRendererValor());
		}
		JInternalFrame jan = b.createInternalFrame("Balanço Patrimonial ("
				+ Contextos.getNomeEmpresa() + ")");
		desktop.add(jan);
		try {
			jan.setMaximum(true);
			jan.setSelected(true);
		} catch (PropertyVetoException e1) {

		}
	}

	/** Abre o Livro Diario dentro de uma frame interna */
	private void abrirDiario() {
		RelatorioTableTituloDialog d = new RelatorioTableTituloDialog("LIVRO DIÁRIO", "",
				Relatorios.LIVRO_DIARIO);
		d.getTabelaResultados().setModel(Relatorios.livroDiario());
		// Seta o tamanho e os formatadores (renderes) das colunas
		{
			TableColumnModel cm = d.getTabelaResultados().getColumnModel();

			cm.getColumn(0).setCellRenderer(
					Constantes.getRendererCentralizado());
			cm.getColumn(1).setCellRenderer(Constantes.getRendererData());
			cm.getColumn(3).setCellRenderer(
					Constantes.getRendererCentralizado());
			cm.getColumn(4).setCellRenderer(Constantes.getRendererValor());
			cm.getColumn(5).setCellRenderer(Constantes.getRendererValor());

			cm.getColumn(0).setPreferredWidth(50);
			cm.getColumn(1).setPreferredWidth(80);
			cm.getColumn(2).setPreferredWidth(200);
			cm.getColumn(3).setPreferredWidth(85);
			cm.getColumn(4).setPreferredWidth(100);
			cm.getColumn(5).setPreferredWidth(100);

		}
		JInternalFrame jan = d.createInternalFrame("Livro Diário ("
				+ Contextos.getNomeEmpresa() + ")");
		desktop.add(jan);
		try {
			jan.setMaximum(true);
			jan.setSelected(true);
		} catch (PropertyVetoException e1) {

		}
	}

	/** Abre o DRE dentro de uma frame interna */
	private void abrirDRE() {
		// Cria o titulo da janela de relatório
		String titulo = "DEMONSTRAÇÃO DE RESULTADO DO EXERCÍCIO EM ";
		try {
			titulo += Constantes.getFormatterData().valueToString(
					Constantes.getDataDeHoje());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// Cria a janela de relatorio com titulo e subtitulo
		RelatorioTableTituloDialog d = new RelatorioTableTituloDialog(titulo, Contextos
				.getNomeEmpresa(), Relatorios.DRE);
		// Chama o relatório de balancete
		d.getTabelaResultados().setModel(Relatorios.DRE());

		// Seta o tamanho e os formatadores (renderes) das colunas
		{
			TableColumnModel cm = d.getTabelaResultados().getColumnModel();
			cm.getColumn(0).setPreferredWidth(300);
			cm.getColumn(1).setCellRenderer(Constantes.getRendererRightAlign());
			cm.getColumn(1).setPreferredWidth(100);
		}

		// Cria frame interna
		JInternalFrame jan = d.createInternalFrame("DRE ("
				+ Contextos.getNomeEmpresa() + ")");
		;
		// Adiciona-a ao desktop
		desktop.add(jan);
		try {
			// Maxima e seleciona a frase atual
			jan.setMaximum(true);
			jan.setSelected(true);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
		}
	}

	/** Abre a janela de lançamentos */
	private void abrirLancamento() {
		new LancamentosFrame(this);
	}

	/** Abre a janela do plano de contas */
	private void abrirPlanoDeContas() {
		new PlanoDeContasDialog(this);
	}

	/** Abre o livro razao dentro de uma frame interna */
	private void abrirRazao() {
		JInternalFrame jan = new RazaoDialog().createInternalFrame();
		desktop.add(jan);
		try {
			jan.setMaximum(true);
			jan.setSelected(true);
		} catch (PropertyVetoException e1) {

		}
	}

	/**
	 * Adiciona uma empresa ao menu abrirEmpresas
	 * 
	 * @param c
	 *            empresa a ser adicionada
	 * @param checked
	 *            se a empresa deve comecar selecionada ou nao
	 */
	private void adicionarMenu(final Contexto c, boolean checked) {
		final String nomeMenu = c.getId() + " - " + c.getEmpresa();
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(nomeMenu);
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Contextos.setContextoAtual(c.getId());
				Contextos.setNomeEmpresa(c.getEmpresa());
				Contas.updateContas();
				setFuncoesDisponiveis(true);
			}

		});
		grupoMenuEmpresas.add(menuItem);
		menuAbrirEmpresa.add(menuItem);
		menuItem.setSelected(checked);

	}

	/**
	 * Metodo que inicializa os componentes da janela principal
	 * 
	 * @throws Exception
	 *             Caso a GUI não possa ser inicializada
	 */
	private void jbInit() throws Exception {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				sair();
			}

		});

		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setSize((int) (screenSize.width * 0.8),
					(int) (screenSize.height * 0.8));

		}

		this.getContentPane().setLayout(borderLayout2);
		this.setTitle("Alliance Accounting System ");
		this.setJMenuBar(barraDeMenu);
		menuArquivo.setText("Arquivo");
		menuArquivo.setMnemonic('q');
		menuItemSair.setText("Sair");
		menuItemSair.setMnemonic('S');
		menuItemSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sair();
			}
		});
		painelEsquerdo.setLayout(gridLayout1);
		painelEsquerdo.setActionMap(new ActionMap());
		painelEsquerdo.setBounds(new Rectangle(0, 0, 91, 300));
		painelEsquerdo.setAlignmentX((float) 0.0);
		painelEsquerdo.setAlignmentY((float) 0.0);
		botaoLancamento.setText("Lançamentos");
		botaoLancamento.setMargin(new Insets(1, 10, 1, 10));
		botaoLancamento.setToolTipText("Abrir Janela de Lancamento");
		botaoLancamento.setMnemonic('L');
		botaoLancamento.setAlignmentY((float) 0.0);
		botaoLancamento.setHorizontalTextPosition(SwingConstants.CENTER);
		botaoLancamento.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoLancamento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirLancamento();
			}
		});
		botaoPlanoDeContas.setText("Plano de Contas");
		botaoPlanoDeContas.setToolTipText("Editar Plano de Contas");
		botaoPlanoDeContas.setMnemonic('c');
		botaoPlanoDeContas.setMargin(new Insets(1, 10, 1, 10));
		botaoPlanoDeContas.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoPlanoDeContas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirPlanoDeContas();
			}
		});
		desktop.setLayout(null);
		menuAjuda.setText("Ajuda");
		menuAjuda.setMnemonic('J');
		menuItemSobre.setText("Sobre...");

		menuItemSobre.setMnemonic('s');
		menuItemSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirAbout();
			}
		});

		menuOrganizarEmpresas.setText("Organizar Empresas...");
		menuOrganizarEmpresas.setMnemonic('o');
		menuOrganizarEmpresas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuOrganizarEmpresas_actionPerformed();
			}
		});
		menuBalancete.setText("Balancete de Verificação");
		menuBalancete.setMnemonic('v');
		menuBalancete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirBalancete();
			}
		});
		menuBalanco.setText("Balanço Patrimonial");
		menuBalanco.setMnemonic('b');
		menuBalanco.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirBalanco();
			}
		});
		menuDRE.setText("Demonstração de Resultado");
		menuDRE.setMnemonic('e');
		menuDRE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirDRE();
			}
		});
		menuDiario.setText("Livro Diário");
		menuDiario.setMnemonic('d');
		menuDiario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirDiario();
			}
		});
		menuRazao.setText("Livro Razão");
		menuRazao.setMnemonic('r');
		menuRazao.setDisplayedMnemonicIndex(6);
		menuRazao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirRazao();
			}
		});
		menuRelatorios.setText("Relatórios");
		menuRelatorios.setMnemonic('r');
		menuLancamento.setText("Efetuar Lancamento...");
		menuLancamento.setMnemonic('l');
		menuLancamento.setToolTipText("null");
		menuLancamento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirLancamento();
			}
		});
		menuPlanoDeContas.setText("Editar Plano de Contas...");
		menuPlanoDeContas.setMnemonic('c');
		menuPlanoDeContas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirPlanoDeContas();
			}
		});
		menuAcoes.setText("Ações");
		menuAcoes.setMnemonic('A');
		menuNovaEmpresa.setText("Nova Empresa...");
		menuNovaEmpresa.setMnemonic('n');
		menuNovaEmpresa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				novaEmpresa();
			}
		});
		menuAbrirEmpresa.setText("Abrir Empresa");
		menuAbrirEmpresa.setMnemonic('A');
		botaoDRE.setText("DRE");
		botaoDRE.setToolTipText("Abrir Demonstração de Resultado do Exercício");
		botaoDRE.setMnemonic('e');
		botaoDRE.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoDRE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirDRE();
			}
		});
		gridLayout1.setRows(7);
		gridLayout1.setColumns(1);
		botaoBalanco.setText("Balanço Patrimonial");
		botaoBalanco.setToolTipText("Abrir Balanço Patrimonial");
		botaoBalanco.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoBalanco.setMnemonic('p');
		botaoBalanco.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirBalanco();
			}
		});
		botaoDiario.setText("Livro Diário");
		botaoDiario.setToolTipText("Abrir o Livro Diário");
		botaoDiario.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoDiario.setMnemonic('D');
		botaoDiario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirDiario();
			}
		});
		botaoBalancete.setText("Balancete");
		botaoBalancete.setMargin(new Insets(1, 10, 1, 10));
		botaoBalancete.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoBalancete.setMnemonic('b');
		botaoBalancete.setToolTipText("Abrir o Balancete de Verificação");
		botaoBalancete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirBalancete();
			}
		});
		botaoRazao.setText("Livro Razão");
		botaoRazao.setToolTipText("Abrir o Livro Razão");
		botaoRazao.setVerticalTextPosition(SwingConstants.BOTTOM);
		botaoRazao.setMnemonic('z');
		botaoRazao.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				abrirRazao();
			}
		});

		menuArquivo.add(menuNovaEmpresa);
		menuArquivo.add(menuAbrirEmpresa);
		menuArquivo.add(menuOrganizarEmpresas);
		menuArquivo.addSeparator();
		menuArquivo.add(menuItemSair);

		barraDeMenu.add(menuArquivo);
		menuAjuda.add(menuItemSobre);
		menuAcoes.add(menuLancamento);
		menuAcoes.add(menuPlanoDeContas);
		barraDeMenu.add(menuAcoes);
		menuRelatorios.add(menuBalancete);
		menuRelatorios.add(menuBalanco);
		menuRelatorios.add(menuDRE);
		menuRelatorios.add(menuDiario);
		menuRelatorios.add(menuRazao);
		barraDeMenu.add(menuRelatorios);
		barraDeMenu.add(menuAjuda);
		painelEsquerdo.add(botaoLancamento, null);
		painelEsquerdo.add(botaoPlanoDeContas, null);
		painelEsquerdo.add(botaoBalancete, null);
		painelEsquerdo.add(botaoBalanco, null);
		painelEsquerdo.add(botaoDRE, null);
		painelEsquerdo.add(botaoDiario, null);
		painelEsquerdo.add(botaoRazao, null);
		scrollMenu.add(painelEsquerdo);
		scrollMenu.setViewportView(painelEsquerdo);
		this.getContentPane().add(scrollMenu, BorderLayout.WEST);
		this.getContentPane().add(desktop, BorderLayout.CENTER);

	}

	/** Metodo acionado quando o usuario deseja organizar as empresas do banco */
	private void menuOrganizarEmpresas_actionPerformed() {
		EdicaoContextosDialog cx = new EdicaoContextosDialog(this);
		if (cx.beenModified()) {
			// update nos menus
			this.updateMenuAbrirEmpresas();
			// Se contextos foram excluidos
			if (!cx.getExcluded().isEmpty()) {
				// Desativa funcoes de frames dinamicas
				for (JInternalFrame f : desktop.getAllFrames()) {
					String ti = f.getTitle();
					// Percorre os contextos excluidos
					for (Contexto c : cx.getExcluded()) {
						// E desativa funcoes das janelas dinamicas
						if (ti.equals("Livro Razão (" + c.getEmpresa() + ")")) {
							f.getContentPane().getComponent(5)
									.setVisible(false);
							f.getContentPane().getComponent(6)
									.setEnabled(false);

						} // fim do if
						// Seta o titulo das janelas
						if (ti.endsWith("(" + c.getEmpresa() + ")"))
							f.setTitle("[Excluído] " + ti);
					} // fim do for interno
				} // fim do for externo
			} // fim do if excluded

			// Se o contexto atual foi excluido
			if (cx.atualBeenExcluded()) {
				// Desabilita funcionalidades
				this.setFuncoesDisponiveis(false);
				Contextos.setContextoAtual(-1);
			}
			// Caso contrario
			else if (Contextos.getContextoAtual() != -1) {
				// Seta como selecionada a empresa aberta anteriormente
				for (Component c : menuAbrirEmpresa.getMenuComponents()) {
					JRadioButtonMenuItem r = (JRadioButtonMenuItem) c;
					if (r.getText().startsWith(
							"" + Contextos.getContextoAtual())) {
						r.setSelected(true);
						Contextos.setNomeEmpresa(r.getText().substring(
								r.getText().indexOf('-') + 2));

					} // fim do if interno
				} // fim do for
			} // fim do else
		} // fim do if modified

	} // fim do metodo

	/** Método acionado quando deseja-se criar uma nova empresa */
	private void novaEmpresa() {
		String nomeEmpresa = JOptionPane.showInputDialog(this,
				"Entre com o nome da empresa:", "Nova empresa",
				JOptionPane.PLAIN_MESSAGE);
		if (nomeEmpresa != null && !nomeEmpresa.equals("")) {
			Contexto empresa = new Contexto(0, nomeEmpresa);

			Contexto.store(empresa);
			Contextos.add(empresa);
			Contextos.setContextoAtual(empresa.getId());
			Contextos.setNomeEmpresa(empresa.getEmpresa());
			Contas.updateContas();
			adicionarMenu(empresa, true);
			this.setFuncoesDisponiveis(true);
		}
	}

	/** Método executado quando o usuário sai do programa * */
	private void sair() {
		DAO.shutdown();
		System.exit(0);
	}

	/**
	 * Dita se os relatórios, plano de contas e lançamentos podem ou não ser
	 * acessados (não podem ser acessados quando nenhuma empresa está aberta)
	 * 
	 * @param disponivel
	 *            verdadeiro para ativar as funções para o usuario
	 */
	private void setFuncoesDisponiveis(boolean disponivel) {
		botaoBalancete.setEnabled(disponivel);
		botaoRazao.setEnabled(disponivel);
		botaoDiario.setEnabled(disponivel);
		botaoBalanco.setEnabled(disponivel);
		botaoDRE.setEnabled(disponivel);
		botaoLancamento.setEnabled(disponivel);
		botaoPlanoDeContas.setEnabled(disponivel);
		menuAcoes.setEnabled(disponivel);
		menuRelatorios.setEnabled(disponivel);
	}

	/** Faz o update do menu de Empresas * */
	private void updateMenuAbrirEmpresas() {
		Contextos.update();
		menuAbrirEmpresa.removeAll();
		for (Contexto c : Contextos.getContextos())
			adicionarMenu(c, false);

	}

}
