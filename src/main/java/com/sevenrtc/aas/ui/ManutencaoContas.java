package com.sevenrtc.aas.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.sevenrtc.aas.entidades.CategoriaDRE;
import com.sevenrtc.aas.entidades.Conta;
import com.sevenrtc.aas.shared.Contas;
import com.sevenrtc.aas.shared.Contextos;
import com.sevenrtc.aas.ui.helper.JComboBoxAutoCompletion;
import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * UI para a manutenção (inclusão e alteração) de contas no plano de contas
 *
 * @author Anthony Accioly
 *
 */
public class ManutencaoContas extends JDialog {

    /**
     * Classe interna responsável por cuidar da ordem da transferência de foco
     * entre objetos internos da classe
     *
     * @author Anthony Accioly
     *
     */
    private class iFTP extends FocusTraversalPolicy {

        @Override
        public Component getComponentAfter(Container cont, Component comp) {
            if (comp.equals(fieldNomeConta)) {
                if (optionAtivo.isEnabled()) {
                    return optionAtivo;
                }
            }

            if (comp.equals(optionAtivo)) {
                return optionPassivo;
            }
            if (comp.equals(optionPassivo)) {
                return optionReceita;
            }
            if (comp.equals(optionReceita)) {
                return optionDespesa;
            }
            if (comp.equals(optionDespesa)) {
                if (comboDRE.isEnabled()) {
                    return comboDRE;
                } else if (botaoConfirmar.isEnabled()) {
                    return botaoConfirmar;
                }
                return botaoCancelar;
            }
            if (comp.equals(comboDRE.getEditor().getEditorComponent())) {
                if (botaoConfirmar.isEnabled()) {
                    return botaoConfirmar;
                }
                return botaoCancelar;
            }
            if (comp.equals(botaoConfirmar)) {
                return botaoCancelar;
            }

            return fieldNomeConta;
        }

        @Override
        public Component getComponentBefore(Container cont, Component comp) {
            if (comp.equals(optionAtivo)) {
                return fieldNomeConta;
            }
            if (comp.equals(optionPassivo)) {
                return optionAtivo;
            }
            if (comp.equals(optionReceita)) {
                return optionPassivo;
            }
            if (comp.equals(optionDespesa)) {
                return optionReceita;
            }
            if (comp.equals(comboDRE.getEditor().getEditorComponent())) {
                if (optionDespesa.isEnabled()) {
                    return optionDespesa;
                }
                return fieldNomeConta;
            }
            if (comp.equals(botaoConfirmar)) {
                if (comboDRE.isEnabled()) {
                    return comboDRE;
                } else if (optionDespesa.isEnabled()) {
                    return optionDespesa;
                }
                return fieldNomeConta;
            }
            if (comp.equals(botaoCancelar)) {
                if (botaoConfirmar.isEnabled()) {
                    return botaoConfirmar;
                } else if (comboDRE.isEnabled()) {
                    return comboDRE;
                } else if (optionDespesa.isEnabled()) {
                    return optionDespesa;
                }
                return fieldNomeConta;
            }

            return fieldNomeConta;
        }

        @Override
        public Component getDefaultComponent(Container con) {
            return fieldNomeConta;
        }

        @Override
        public Component getFirstComponent(Container con) {
            return fieldNomeConta;
        }

        @Override
        public Component getLastComponent(Container con) {
            return botaoCancelar;
        }
    }
    private static final long serialVersionUID = -7975413977558141485L;

    /**
     * Metodo principal da classe
     *
     * @param args argumentos passado como parametro
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
                    JDialog frame = new JDialog();
                    frame.setLocation(100, 100);
                    frame.getContentPane().add(
                            new ManutencaoContas().createPanel());
                    frame.pack();
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
    private JButton botaoCancelar = new JButton();
    private JButton botaoConfirmar = new JButton();
    private ButtonGroup buttonGroupTipos = new ButtonGroup();
    private int contaDisponivel;
    private JComboBox comboDRE = new JComboBox(Contas.getCategoriasDRE());
    private Conta contaAtual;
    private Conta contaInserida;
    private JFormattedTextField fieldConta = new JFormattedTextField();
    private JFormattedTextField fieldNomeConta = new JFormattedTextField();
    private boolean foiModificado = false;
    private JLabel labelConta = new JLabel();
    private JLabel labelContaNome = new JLabel();
    private JLabel labelDRE = new JLabel();
    private JLabel labelTipo = new JLabel();
    private Integer operacao; // 0-Inclusao;1-Alteração
    private JRadioButton optionAtivo = new JRadioButton();
    private JRadioButton optionDespesa = new JRadioButton();
    private JRadioButton optionPassivo = new JRadioButton();
    private JRadioButton optionReceita = new JRadioButton();

    // Seta configurações de foco e funções para autocompletar codigo
    {
        Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(
                forwardKeys);
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
                newForwardKeys);
        JComboBoxAutoCompletion.enable(comboDRE);
        this.setFocusTraversalPolicy(new iFTP());
    }

    /**
     * Construtor vazio
     */
    public ManutencaoContas() {
    }

    /**
     * Construtor padrao para a classe de Manutenção de Contas
     *
     * @param owner janela Dona
     * @param conta Código da conta a ser manipulada
     * @param contaDisponivel primeira conta disponivel
     * @param mOperacao 0 para inclusão e 1 para alteracao
     */
    public ManutencaoContas(Dialog owner, String conta, int contaDisponivel,
            int mOperacao) {
        super(owner, true);
        this.contaDisponivel = contaDisponivel;
        operacao = mOperacao;
        carregarConta(conta);

        try {

            jbInit();
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setLocationRelativeTo(owner);
            checaBotaoConfirmar("");
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
     * @param cols an array of column indices in the first row where fill
     * components should be added.
     * @param rows an array of row indices in the first column where fill
     * components should be added.
     */
    void addFillComponents(Container panel, int[] cols, int[] rows) {
        Dimension filler = new Dimension(12, 12);

        boolean filled_cell_11 = false;
        CellConstraints cc = new CellConstraints();
        if (cols.length > 0 && rows.length > 0) {
            if (cols[0] == 1 && rows[0] == 1) {
                /**
                 * add a rigid area
                 */
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
     * Ativado junto ao botao cancelar
     */
    private void botaoCancelar_actionPerformed() {
        this.dispose();
    }

    /**
     * Ativado junto ao botao confirmar
     */
    private void botaoConfirmar_actionPerformed() {
        // Flag sinalizando erros
        boolean flagError = false;

        if (operacao == 0)// Inclusao
        {
            contaInserida = null;
            
            // Usa o número do último andar da conta como ordem
            int ordem = Integer.parseInt(this.fieldConta.getText().substring(
                    this.fieldConta.getText().lastIndexOf(".") + 1));

            /*
             * Cria conta a ser inserida, se possivel, com base nas
             * caracteristicas do pai
             */
            if (contaAtual != null) {
                contaInserida = new Conta(this.fieldConta.getText(), Contextos
                        .getContextoAtual(), this.fieldNomeConta.getText(), 0,
                        contaAtual.getTipo(), 'a', null, ordem, 0);
                if (!contaAtual.equals(Contextos.getNomeEmpresa())) {
                    contaInserida.setPai(contaAtual.getCodigo());
                }
            } else {
                contaInserida = new Conta(this.fieldConta.getText(), Contextos
                        .getContextoAtual(), this.fieldNomeConta.getText(), 0,
                        'z', 'a', null, ordem, 0);
            }

            // Seta o tipo de conta
            if (this.optionAtivo.isSelected()) {
                contaInserida.setTipo('A');
            } else if (this.optionPassivo.isSelected()) {
                contaInserida.setTipo('P');
            } else if (this.optionDespesa.isSelected()) {
                contaInserida.setTipo('D');
            } else if (this.optionReceita.isSelected()) {
                contaInserida.setTipo('R');
            }

            // Tenta armazenar a conta inserida
            try {
                Conta.store(contaInserida);
            } catch (Exception e1) {
                // Se ocorrer uma excecao avisa o usuario
                String mensagem = "Erro ao inserir nova conta\n";

                if (e1.getMessage().equals("Unique constraint violation: ")) {
                    mensagem += "O código de conta "
                            + contaInserida.getCodigo() + " já existe";
                }

                JOptionPane.showMessageDialog(this, mensagem, "Erro",
                        JOptionPane.ERROR_MESSAGE);
                // E sinaliza com uma flag de error
                flagError = true;
            }

            if (contaAtual != null) // Conta pai e analitica. Logo, deve virar sintetica
            {
                if (contaAtual.getFuncao() == 'A') {
                    contaAtual.setFuncao('S');
                    Conta.update(contaAtual);
                }
            }

        } else if (operacao == 1) { // Alteracao
            contaAtual.setNome(this.fieldNomeConta.getText());
            // Seta a categoria DRE
            contaAtual.setCategoria_dre(((CategoriaDRE) comboDRE
                    .getSelectedItem()).getId());
            Conta.update(contaAtual);

        }

        if (!flagError) {
            foiModificado = true;
        }

        this.dispose();
    }

    /**
     * Carrega uma conta do banco de dados se a mesma não for o nodo raiz (com o
     * nome da empresa aberta atualmente) da arvore
     *
     * @param conta código da conta a ser aberta
     */
    private void carregarConta(String conta) {
        if (!conta.equals(Contextos.getNomeEmpresa())) {
            contaAtual = Conta.load(conta);
        }

    }

    /**
     * Metodo auxiliar ao handler de teclas do fieldNomeConta. Verifica se, com
     * a tecla digitada e o que esta escrito na caixa de textos, o campo com o
     * nome da conta nao esta vazio
     *
     * @param ultimaTeclaDigitada conversão para string da ultima tecla digitada
     */
    private void checaBotaoConfirmar(String ultimaTeclaDigitada) {
        if (!(fieldNomeConta.getText() + ultimaTeclaDigitada).equals("")) {
            botaoConfirmar.setEnabled(true);
        } else {
            botaoConfirmar.setEnabled(false);
        }
    }

    /**
     * Retorna o painel da janela atual. <p> A janela em questão possui campos
     * para escolher o tipo da conta, nome e categoria DRE, além, é claro, de
     * botoes para efetivar sua funcionalidade
     *
     * @return o painel da janela atual
     */
    public JPanel createPanel() {
        JPanel painelPrincipal = new JPanel();
        FormLayout formlayout1 = new FormLayout(
                "FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:4DLU:NONE,FILL:70DLU:GROW(1.0),FILL:4DLU:NONE,FILL:DEFAULT:NONE,FILL:8DLU:NONE",
                "CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:2DLU:NONE,CENTER:DEFAULT:NONE,CENTER:4DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE,CENTER:4DLU:NONE,CENTER:DEFAULT:NONE,CENTER:DEFAULT:NONE");
        CellConstraints cc = new CellConstraints();
        painelPrincipal.setLayout(formlayout1);

        labelConta.setText("Conta:");
        labelConta.setLabelFor(fieldConta);
        labelConta.setDisplayedMnemonic('n');
        painelPrincipal.add(labelConta, new CellConstraints(2, 3, 1, 1,
                CellConstraints.DEFAULT, CellConstraints.BOTTOM));

        fieldConta.setName("fieldConta");
        fieldConta.setEditable(false);
        painelPrincipal.add(fieldConta, cc.xy(4, 3));

        labelContaNome.setName("labelContaNome");
        labelContaNome.setText("Descrição:");
        labelContaNome.setLabelFor(fieldNomeConta);
        labelContaNome.setDisplayedMnemonic('e');
        painelPrincipal.add(labelContaNome, new CellConstraints(2, 5, 1, 1,
                CellConstraints.DEFAULT, CellConstraints.BOTTOM));

        fieldNomeConta.setName("fieldNomeConta");
        painelPrincipal.add(fieldNomeConta, cc.xy(4, 5));

        labelTipo.setName("labelTipo");
        labelTipo.setText("Tipo:");
        painelPrincipal.add(labelTipo, cc.xy(2, 7));

        optionAtivo.setName("optionAtivo");
        optionAtivo.setSelected(true);
        optionAtivo.setText("Ativo");
        optionAtivo.setMnemonic('a');
        buttonGroupTipos.add(optionAtivo);
        painelPrincipal.add(optionAtivo, cc.xy(4, 7));

        optionPassivo.setName("optionPassivo");
        optionPassivo.setText("Passivo");
        optionPassivo.setMnemonic('p');
        buttonGroupTipos.add(optionPassivo);
        painelPrincipal.add(optionPassivo, cc.xy(4, 8));

        optionReceita.setName("optionReceita");
        optionReceita.setText("Receita");
        optionReceita.setMnemonic('r');
        buttonGroupTipos.add(optionReceita);
        painelPrincipal.add(optionReceita, cc.xy(4, 9));

        optionDespesa.setName("optionDespesa");
        optionDespesa.setText("Despesa");
        optionDespesa.setMnemonic('d');
        buttonGroupTipos.add(optionDespesa);
        painelPrincipal.add(optionDespesa, cc.xy(4, 10));

        labelDRE.setName("labelDRE");
        labelDRE.setText("Função DRE:");
        labelDRE.setLabelFor(comboDRE);
        labelDRE.setDisplayedMnemonic('f');
        painelPrincipal.add(labelDRE, cc.xy(2, 12));

        comboDRE.setName("comboDRE");
        painelPrincipal.add(comboDRE, cc.xywh(4, 12, 3, 1));

        painelPrincipal.add(createPanelBotoes(), cc.xywh(6, 2, 1, 9));
        addFillComponents(painelPrincipal, new int[]{1, 2, 3, 4, 5, 6, 7},
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13});

        return painelPrincipal;
    }

    /**
     * Cria uma painel com os botoes confirmar e cancelar
     *
     * @return o painel de botoes pertencente ao painel principal
     */
    public JPanel createPanelBotoes() {
        JPanel painelBotoes = new JPanel();
        FormLayout formlayout1 = new FormLayout("FILL:55DLU:GROW(1.0)",
                "FILL:18DLU:NONE,CENTER:2DLU:NONE,FILL:18DLU:NONE");
        CellConstraints cc = new CellConstraints();
        painelBotoes.setLayout(formlayout1);

        botaoConfirmar.setName("botaoConfirmar");
        botaoConfirmar.setText("Confirmar");
        botaoConfirmar.setMnemonic('o');
        botaoConfirmar.setToolTipText("Confirmar Atualização");
        painelBotoes.add(botaoConfirmar, cc.xy(1, 1));

        botaoCancelar.setName("botaoCancelar");
        botaoCancelar.setText("Cancelar");
        botaoCancelar.setMnemonic('c');
        botaoCancelar.setToolTipText("Cancelar Operação");
        painelBotoes.add(botaoCancelar, cc.xy(1, 3));

        addFillComponents(painelBotoes, new int[0], new int[]{2});
        return painelBotoes;
    }

    /**
     * Indica se ouve modificações no plano de contas
     *
     * @return true se o plano de contas foi modificado
     */
    public boolean foiModificado() {
        return foiModificado;
    }

    /**
     * Devolve a conta que foi modificada no caso da janela estar sendo
     * utilizada para fins de alteração
     *
     * @return conta modificada
     */
    public Conta getContaAtual() {
        return contaAtual;
    }

    /**
     * Devolve a conta que foi inserida no caso da janela estar sendo utilizada
     * para fins de inserção
     *
     * @return conta inserida
     */
    public Conta getContaInserida() {
        return contaInserida;
    }

    /**
     * Inicializa a {@link javax.swing.JDialog} atual. <p> Para tanto, insere o
     * painel principal na mesma, seta os handlers e executa o método pack
     *
     * @throws Exception caso seja impossivel criar o GUI
     */
    private void jbInit() throws Exception {
        JPanel painelPrincipal = createPanel();

        optionDespesa.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                comboDRE.setEnabled(true);

            }
        });

        optionReceita.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                comboDRE.setEnabled(true);

            }
        });

        optionPassivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                comboDRE.setEnabled(false);

            }
        });

        optionAtivo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                comboDRE.setEnabled(false);

            }
        });

        this.comboDRE.setEnabled(false);

        if (contaAtual != null) {
            if (contaAtual.getTipo() == 'A') {
                optionAtivo.setSelected(true);
            }
            if (contaAtual.getTipo() == 'D') {
                optionDespesa.setSelected(true);
                comboDRE.setEnabled(true);
            }
            if (contaAtual.getTipo() == 'R') {
                optionReceita.setSelected(true);
                comboDRE.setEnabled(true);
            }
            if (contaAtual.getTipo() == 'P') {
                this.optionPassivo.setSelected(true);
            }

        }

        fieldNomeConta.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String c = "";
                if (Character.isLetter(e.getKeyChar())) {
                    c = "" + e.getKeyChar();
                }
                checaBotaoConfirmar(c);
            }
        });

        if (operacao == 0) {
            this.setTitle("Adicionar Nova Conta");
        } else if (operacao == 1) {
            this.setTitle("Alterar Conta");
            fieldConta.setEditable(false);
            optionAtivo.setEnabled(false);
            optionPassivo.setEnabled(false);
            optionDespesa.setEnabled(false);
            optionReceita.setEnabled(false);
        }

        String conta = "";
        // Sugere dados de acordo com o tipo de operacao
        if (contaAtual != null) {
            if (operacao == 0) {
                conta = contaAtual.getCodigo() + ".";
            } else {
                this.setTitle("Alterar Conta");
                this.fieldConta.setText(contaAtual.getCodigo());
                fieldNomeConta.setText(contaAtual.getNome());
                comboDRE.setSelectedIndex((int) contaAtual.getCategoria_dre());
            }
        }
        if (operacao == 0) {
            this.fieldConta.setText(conta + (contaDisponivel));
        }

        botaoConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botaoConfirmar_actionPerformed();
            }
        });

        botaoCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                botaoCancelar_actionPerformed();
            }
        });

        this.getContentPane().setLayout(new BorderLayout());
        this.add(painelPrincipal, BorderLayout.CENTER);
        this.pack();

    }
}
