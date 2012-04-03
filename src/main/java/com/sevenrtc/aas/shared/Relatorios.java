/**
 * 
 */
package com.sevenrtc.aas.shared;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.NumberFormatter;

import com.sevenrtc.aas.db.DAO;

/**
 * Classe com metodo estaticos que retornam tabelas de relatórios
 * 
 * @author Anthony Accioly
 * 
 */
public class Relatorios {

	/** Constante que representa relatorios do tipo balancete */
	public static final int BALANCETE = 0;

	/** Constante que representa relatorios do tipo balanco patrimonial */
	public static final int BALANCO = 1;

	/** Constante que representa relatorios do tipo DRE */
	public static final int DRE = 2;

	/** Constante que representa relatorios do tipo livro diario */
	public static final int LIVRO_DIARIO = 3;

	/** Constante que representa relatorios do tipo livro razao */
	public static final int LIVRO_RAZAO = 4;

	/** Retorna o relatório BALANCETE */
	public static AbstractTableModel balancete() {

		// Modelo de data que será retornado
		AbstractTableModel modelo = new AbstractTableModel() {

			private static final long serialVersionUID = 0L;

			// Nome das colunas do relatório
			String[] colunas = { "Código", "Conta", "Saldo Devedor",
					"Saldo Credor" };

			// Data da tabela
			ArrayList<Object[]> data = new ArrayList<Object[]>();

			// Chama o médoto de processamento da consulta
			{
				processaConsulta();
			}

			// A partir daqui métodos obrigatórios do modelo, não alterar

			public int getColumnCount() {
				return colunas.length;
			}

			public String getColumnName(int col) {
				return colunas[col];
			}

			public int getRowCount() {
				return data.size();
			}

			public Object getValueAt(int row, int col) {
				return data.get(row)[col];
			}

			/** Método de processamento da consulta */
			void processaConsulta() {
				// Consulta SQL
				ResultSet rs = DAO
						.query("SELECT con_codigo, con_nome, con_saldo,"
								+ " con_tipo" + " FROM con_conta"
								+ " WHERE ctx_id = "
								+ Contextos.getContextoAtual()
								+ " AND  con_funcao = 'A'");

				// Processamento da consulta
				try {
					// Variaveis auxiliares
					double saldoDevedorT = 0, saldoCredorT = 0;

					while (rs.next()) {
						// Cria o objeto de leitura atual
						Object[] item = new Object[colunas.length];
						// Le dados
						item[0] = rs.getObject(1);
						item[1] = rs.getObject(2);
						double saldo = rs.getDouble(3);
						char tipo = rs.getString(4).charAt(0);
						// De a cordo com o tipo de coluna posiciona o saldo
						if (tipo == 'A' || tipo == 'D') {
							item[2] = saldo;
							saldoDevedorT += saldo;

						} else {
							item[3] = saldo;
							saldoCredorT += saldo;
						}

						// Adiciona item à tabela
						data.add(item);
					}

					// Adiciona uma quebra a tabela
					Object[] quebra = { null, null, null, null };
					data.add(quebra);
					// Adiciona o valor total a tabela
					Object[] total = { "#", "Total", saldoDevedorT,
							saldoCredorT };

					data.add(total);
					// Fecha cursor
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};

		return modelo;

	};

	/** Retorna o relatório BALANCO */
	public static AbstractTableModel balanco() {

		// Modelo de data que será retornado
		AbstractTableModel modelo = new AbstractTableModel() {

			private static final long serialVersionUID = 0L;

			// Nome das colunas do relatório
			String[] colunas = { "Conta", "Saldo" };

			// Data da tabela
			ArrayList<Object[]> data = new ArrayList<Object[]>();

			// Chama o médoto de processamento da consulta
			{
				processaConsulta();
			}

			// A partir daqui m[etodos obrigatórios do modelo, não alterar

			public int getColumnCount() {
				return colunas.length;
			}

			public String getColumnName(int col) {
				return colunas[col];
			}

			public int getRowCount() {
				return data.size();
			}

			public Object getValueAt(int row, int col) {
				return data.get(row)[col];
			}

			/** Método de processamento da consulta */
			void processaConsulta() {
				processaSubInstanciaBP('A');
				processaSubInstanciaBP('P');
				processaSubInstanciaBP('L');
			}

			/**
			 * Processa em separado o ativo, passivo e patrimonio liquido do
			 * balanço
			 * 
			 * @param tipo
			 *            (A)tivo (P)assivo ou Patrimonio (L)iquido
			 */
			private void processaSubInstanciaBP(char tipo) {

				ResultSet rs = DAO
						.query("SELECT con_codigo, con_nome, con_saldo"
								+ " FROM con_conta " + "WHERE ctx_id = "
								+ Contextos.getContextoAtual()
								+ " AND con_tipo='" + tipo + "'");
				// Processamento da consulta
				try {
					while (rs.next()) {
						// Cria o objeto de leitura atual
						Object[] item = new Object[colunas.length];
						// Le dados

						// Calcula o pre espacamento correto das contas
						String espacamento = "";
						{
							String conta = rs.getString(1);
							/*
							 * Adiciona espacamento de acordo com o número de
							 * pontos da conta
							 */
							for (int index = conta.indexOf('.'); index >= 0; index = conta
									.indexOf('.', index + 1))
								espacamento += "       ";
						}
						item[0] = espacamento + rs.getString(2);
						item[1] = rs.getDouble(3);

						// Adiciona item à tabela
						data.add(item);
					}

					// adiciona linha em branco ao termino
					data.add(new Object[] { null, null });

					// Fecha cursor
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};

		return modelo;

	};

	/** Retorna o relatório DRE */
	public static AbstractTableModel DRE() {

		// Modelo de data que será retornado
		AbstractTableModel modelo = new AbstractTableModel() {

			private static final long serialVersionUID = 0L;

			// Nome das colunas do relatório
			String[] colunas = { "Receita/Despesa", "Valor" };

			// Variavel auxiliar
			double totalAcumulado;

			// formatter para valores
			NumberFormatter fv = Constantes.getFormatterValor();

			// Consulta
			ResultSet rs = DAO
					.query("SELECT con_nome, con_saldo, con_tipo, cat_id, cat_nome "
							+ "FROM con_conta JOIN cat_categoriadre "
							+ "ON con_conta.cat_id = cat_categoriadre.cat_id "
							+ "WHERE con_conta.cat_id != 0 "
							+ "AND cat_categoriadre.cat_id != 0 "
							+ "AND con_conta.ctx_id = "
							+ Contextos.getContextoAtual()
							+ " ORDER BY cat_id, con_saldo DESC");

			// Data da tabela
			ArrayList<Object[]> data = new ArrayList<Object[]>();

			// Chama o médoto de processamento da consulta
			{
				processaConsulta();
			}

			// A partir daqui métodos obrigatórios do modelo, não alterar

			/**
			 * Process a soma do DRE até o momento e inclui no relatorio
			 * 
			 * @param titulo
			 *            titulo do campo de soma a ser incluido no relatorio
			 */
			private void efetuaSoma(String titulo) {
				// String de formatacao
				final String form = "<html><b>";

				try {
					// somatoria
					data.add(new Object[] { form + titulo + "</b>",
							form + fv.valueToString(totalAcumulado) + "</b>" });
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}

			public int getColumnCount() {
				return colunas.length;
			}

			public String getColumnName(int col) {
				return colunas[col];
			}

			public int getRowCount() {
				return data.size();
			}

			public Object getValueAt(int row, int col) {
				return data.get(row)[col];
			}

			/** Método de processamento da consulta */
			void processaConsulta() {

				totalAcumulado = 0;
				// Processa receita Bruta
				processaInstancia(1, null, true);
				// Processa deduções
				processaInstancia(2, null, true);
				// Receita Líquida
				efetuaSoma("RECEITA OPERACIONAL LÍQUIDA");
				// Processa CMV
				processaInstancia(3, null, false);
				// Lucro bruto
				efetuaSoma("LUCRO BRUTO");
				// Processa Resultado Operacional
				processaInstancia(4, "RECEITAS (DESPESAS) OPERACIONAIS", true);
				// Lucro Operacional operacional non f
				efetuaSoma("LUCRO OPERACIONAL ANTES DO RESULTADO FINANCEIRO");
				// Processa resultado Financeiro
				processaEDivideInstancia(5, null, "financeiras", true);
				// Lucro Operacional
				efetuaSoma("LUCRO OPERACIONAL");
				// Processa Resultado não operacional
				processaEDivideInstancia(6, null, "não operacionais", true);
				// Lucro pre IR
				efetuaSoma("LUCRO ANTES DO IMPOSTO DE RENDA E DA CONTRIBUIÇÃO SOCIAL");
				// Processa Imposto de renda
				processaInstancia(7, "IMPOSTO DE RENDA E CONTRIBUIÇÃO SOCIAL",
						true);
				// Lucro pre participações
				efetuaSoma("LUCRO ANTES DAS PARTICIPAÇÕES");
				// Processa participações
				processaInstancia(8, null, false);
				// Lucro Liquido
				efetuaSoma("LUCRO LÍQUIDO DO EXERCÍCIO");
			}

			/**
			 * Processa uma instância em separado do DRE dividindo-a em receitas
			 * e despesas
			 * 
			 * @param codCat
			 *            codigo da categoria procurada
			 * @param nomeCat
			 *            nome da categoria DRE atual, null para inferir do
			 *            banco
			 * @param comp
			 *            o complemento que deve ser posto depois de Despesas e
			 *            Receitas. Exemplo Receitas [Financeiras]
			 * @param exibeTitulo
			 *            define se deve ou não ser exibido o titulo da
			 *            categoria DRE
			 */
			private void processaEDivideInstancia(long codCat, String nomeCat,
					String comp, boolean exibeTitulo) {
				// categoria atual
				long catAtual = -1;
				// saldo da categoria
				double saldoCat = 0;
				// Controla saldos de despesa e receita
				double saldoDespesa = 0;
				double saldoReceita = 0;

				// Verifica a existencia da categoria atual
				try {
					if (rs.next()) {
						catAtual = rs.getLong(4);
						// Se a categoria atual não é a procurada pelo usuario
						if (catAtual != codCat) {
							// Volta o cursor
							rs.previous();
							// e sai do método
							return;
						}
						// Se a categoria atual for nula, pega o nome do banco
						if (nomeCat == null)
							nomeCat = rs.getString(5);
						// retorna para o primeiro registro da categoria
						rs.previous();
					}
					// Sai do metodo se não existir uma nova categoria
					else
						return;
				} catch (SQLException e) {
					e.printStackTrace();
				}

				/*
				 * Adiciona o titulo da categoria ao DRE, usa html para promover
				 * o negrito
				 */
				Object[] tituloCategoria = new Object[] {
						"<html><b>" + nomeCat.toUpperCase() + "</b>", null };

				// Controal se o titulo deve ser adicionado ou não à conta
				if (exibeTitulo)
					data.add(tituloCategoria);

				try {

					double saldoConta;
					char tipoConta = 'z';

					// Percorre os dados do select
					while (rs.next()) {

						// Se a conta nao pertencer a categoria atual
						if (rs.getLong(4) != catAtual) {
							// volta à posição anterior nos resultados
							rs.previous();
							// e sai do laço
							break;
						}

						// Caso contrario, isola o saldo e o tipo da conta
						saldoConta = rs.getDouble(2);
						tipoConta = rs.getString(3).charAt(0);

						// se a conta for de despesa, inverte o sinal
						if (tipoConta == 'D' && saldoConta != 0)
							saldoConta = -saldoConta;

						// Se o saldo for positivo, adiciona as receitas
						if (saldoConta >= 0)
							saldoReceita += saldoConta;
						// Caso contrario, adiciona as despesas
						else
							saldoDespesa += saldoConta;
					} // fim do while

				} catch (SQLException e) {
					e.printStackTrace();
				}

				// Para o saldo da categoria
				{
					// Calcula o saldo da categoria
					saldoCat = saldoDespesa + saldoReceita;

					String saldoCatFormatado = "<html><b>";
					try {
						// Formata o saldo da conta em negrito
						saldoCatFormatado += fv.valueToString(saldoCat);
					} catch (ParseException e) {

					}
					saldoCatFormatado += "</b>";

					/*
					 * adiciona o saldo da categoria à tabela na coluna do
					 * titulo
					 */
					tituloCategoria[1] = saldoCatFormatado;
				}

				// Para os saldos de despesa e receita
				{
					String saldoDespesaFormatado = null;
					String saldoReceitaFormatado = null;
					// Tenta formatar os saldos
					try {
						saldoDespesaFormatado = fv.valueToString(saldoDespesa);
						saldoReceitaFormatado = fv.valueToString(saldoReceita);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// Cria objetos de despesa e receita
					Object[] despesa = { "Despesas " + comp,
							saldoDespesaFormatado };
					Object[] receita = { "Receitas " + comp,
							saldoReceitaFormatado };

					// Adiciona-os objetos na ordem de seu valor absoluto
					if (saldoReceita >= -saldoDespesa) {
						data.add(receita);
						data.add(despesa);
					} else {
						data.add(despesa);
						data.add(receita);
					}
				}

				// Soma o saldo da categoria atual ao total do DRE
				totalAcumulado += saldoCat;

			} // fim do metodo

			/**
			 * Processa uma instância em separado do DRE, por exemplo Receita
			 * Bruta ou Resultados Operacionais
			 * 
			 * @param codCat
			 *            codigo da categoria procurada
			 * @param nomeCat
			 *            nome da categoria DRE atual, null para inferir do
			 *            banco
			 * @param exibeTitulo
			 *            indica se um titulo em negrito com a somatoria dos
			 *            valores deve ser exibido
			 * 
			 */
			private void processaInstancia(long codCat, String nomeCat,
					boolean exibeTitulo) {
				// categoria atual
				long catAtual = -1;
				// saldo da categoria
				double saldoCat = 0;

				// Verifica a existencia da categoria atual
				try {
					if (rs.next()) {
						catAtual = rs.getLong(4);
						// Se a categoria atual não é a procurada pelo usuario
						if (catAtual != codCat) {
							// Volta o cursor
							rs.previous();
							// e sai do método
							return;
						}
						// Se a categoria atual for nula, pega o nome do banco
						if (nomeCat == null)
							nomeCat = rs.getString(5);
						// retorna para o primeiro registro da categoria
						rs.previous();
					}
					// Sai do metodo se não existir uma nova categoria
					else
						return;
				} catch (SQLException e) {
					e.printStackTrace();
				}

				/*
				 * Adiciona o titulo da categoria ao DRE, usa html para promover
				 * o negrito
				 */
				Object[] tituloCategoria = new Object[] {
						"<html><b>" + nomeCat.toUpperCase() + "</b>", null };

				// Controal se o titulo deve ser adicionado ou não à conta
				if (exibeTitulo)
					data.add(tituloCategoria);

				try {
					// Conta atual
					String nomeConta;
					double saldoConta;
					String saldoContaFormatado;
					char tipoConta = 'z';

					// Percorre os dados do select
					while (rs.next()) {
						// Se a conta nao pertencer a categoria atual
						if (rs.getLong(4) != catAtual) {
							// volta à posição anterior nos resultados
							rs.previous();
							// e sai do laço
							break;
						}

						/*
						 * Caso contrario, isola o nome o saldo e o tipo da
						 * conta
						 */
						nomeConta = rs.getString(1);
						saldoConta = rs.getDouble(2);
						tipoConta = rs.getString(3).charAt(0);
						// se a conta for de despesa, inverte o sinal
						if (tipoConta == 'D' && saldoConta != 0)
							saldoConta = -saldoConta;
						// formata saldo
						saldoContaFormatado = fv.valueToString(saldoConta);
						// Adiciona a conta atual à tabela
						data
								.add(new Object[] { nomeConta,
										saldoContaFormatado });
						// Adiciona o saldo da conta ao saldo da categoria
						saldoCat += saldoConta;

					} // fim do while
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				// Formata o saldo da conta em negrito
				String saldoFormatado = "<html><b>";
				try {
					saldoFormatado += fv.valueToString(saldoCat);
				} catch (ParseException e) {

				}
				saldoFormatado += "</b>";
				// adiciona o saldo da categoria à tabela na coluna do titulo
				tituloCategoria[1] = saldoFormatado;

				// Soma o saldo da categoria atual ao total do DRE
				totalAcumulado += saldoCat;

			} // fim do metodo

		};

		return modelo;

	};

	/**
	 * Retorna a FICHA RAZAO de determina conta
	 * 
	 * @param conta
	 *            conta analisada
	 * @param contexto
	 *            contexto a qual a conta se refere
	 */
	public static AbstractTableModel fichaRazao(final String conta,
			final long contexto) {

		// Modelo de data que será retornado
		AbstractTableModel modelo = new AbstractTableModel() {

			private static final long serialVersionUID = 0L;

			// Nome das colunas do relat�rio
			String[] colunas = { "Data", "L. Nr.", "Histórico", "Débito",
					"Crédito", "D/C", "Saldo" };

			// Data da tabela
			ArrayList<Object[]> data = new ArrayList<Object[]>();

			// Chama o médoto de processamento da consulta
			{
				processaConsulta();
			}

			// A partir daqui métodos obrigatórios do modelo, não alterar

			public int getColumnCount() {
				return colunas.length;
			}

			public String getColumnName(int col) {
				return colunas[col];
			}

			public int getRowCount() {
				return data.size();
			}

			public Object getValueAt(int row, int col) {
				return data.get(row)[col];
			}

			/** Método de processamento da consulta */
			void processaConsulta() {
				// Consulta SQL
				ResultSet rs = DAO
						.query("SELECT pdi_data, pdi_id, pdi_historico, "
								+ "mov_valor, mov_tipo, mov_saldoconta "
								+ "FROM mov_movimento JOIN pdi_partidadiario "
								+ "ON mov_movimento.pdi_id = pdi_partidadiario.pdi_id "
								+ "WHERE ctx_id = " + contexto
								+ " AND con_codigo = '" + conta + "'");

				// Processamento da consulta
				try {

					while (rs.next()) {
						// Cria o objeto de leitura atual
						Object[] item = new Object[colunas.length];
						// Le dados
						item[0] = rs.getObject(1);
						item[1] = rs.getObject(2);
						item[2] = rs.getObject(3);
						char tipo = rs.getString(5).charAt(0);
						double saldo = rs.getDouble(4);
						// De a cordo com o tipo de coluna posiciona o saldo
						if (tipo == 'D')
							item[3] = saldo;
						else
							item[4] = saldo;
						item[5] = tipo;
						item[6] = rs.getDouble(6);

						// Adiciona item à tabela
						data.add(item);
					}

					// Fecha cursor
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};

		return modelo;

	};

	/** Retorna o relatório LIVRO DIÁRIO */
	public static AbstractTableModel livroDiario() {

		// Modelo de data que será retornado
		AbstractTableModel modelo = new AbstractTableModel() {

			private static final long serialVersionUID = 0L;

			// Nome das colunas do relatório
			String[] colunas = { "L. Nr.", "Data",
					"Títulos das Contas e Histórico", "Código Conta", "Débito",
					"Crédito" };

			// Data da tabela
			ArrayList<Object[]> data = new ArrayList<Object[]>();

			// Chama o médoto de processamento da consulta
			{
				processaConsulta();
			}

			// A partir daqui métodos obrigatórios do modelo, não alterar

			public int getColumnCount() {
				return colunas.length;
			}

			public String getColumnName(int col) {
				return colunas[col];
			}

			public int getRowCount() {
				return data.size();
			}

			public Object getValueAt(int row, int col) {
				return data.get(row)[col];
			}

			/** Método de processamento da consulta */
			void processaConsulta() {
				// Consulta SQL
				ResultSet rs = DAO
						.query("SELECT pdi_id, pdi_data, mov_tipo,"
								+ " con_codigo, pdi_historico, mov_valor"
								+ " FROM mov_movimento mov"
								+ " JOIN pdi_partidadiario part"
								+ " ON mov.pdi_id = part.pdi_id"
								+ " WHERE mov.ctx_id = "
								+ Contextos.getContextoAtual()
								+ " AND part.ctx_id = "
								+ Contextos.getContextoAtual()
								+ "ORDER BY pdi_data, pdi_id, mov_tipo DESC, mov_valor");

				// Processamento da consulta
				try {

					while (rs.next()) {
						/*
						 * Le o lancamento e o primeiro movimento da partida
						 * atual
						 */
						Object[] lanc = new Object[colunas.length];

						// Le dados
						int lancNumero = rs.getInt(1); // numero do lancamento
						lanc[0] = lancNumero;
						lanc[1] = rs.getObject(2);
						char tipo = rs.getString(3).charAt(0);
						// Recebe o numero da conta
						String nrConta = rs.getString(4);
						// Recebe o historico da operação
						String historico = rs.getString(5);
						// Acha o nome da conta a partir de seu numero
						String nomeConta = Contas.getNome(nrConta);
						lanc[2] = nomeConta;
						lanc[3] = nrConta;
						double saldo = rs.getDouble(6);
						// De a cordo com o tipo de coluna posiciona o saldo
						if (tipo == 'D')
							lanc[4] = saldo;
						else
							lanc[5] = saldo;
						// Adiciona o lancamento a tabela
						data.add(lanc);

						// Enquanto existirem novos dados no relatorio
						while (rs.next()) {
							// Se o movimento pertence a partida atual
							if (rs.getInt(1) == lancNumero) {
								// Cria um objeto para ele
								Object[] mov = new Object[colunas.length];
								// Le o tipo de movimento, conta e saldo
								tipo = rs.getString(3).charAt(0);
								nrConta = rs.getString(4);
								saldo = rs.getDouble(6);
								// Acha o nome da conta a partir de seu numero
								nomeConta = Contas.getNome(nrConta);
								mov[3] = nrConta;
								/*
								 * De a cordo com o tipo de coluna posiciona o
								 * saldo e acrescenta ou nao o "a"
								 */
								if (tipo == 'D') {
									mov[2] = nomeConta;
									mov[4] = saldo;
								} else {
									mov[2] = "a " + nomeConta;
									mov[5] = saldo;
								}
								// adiciona o movimento
								data.add(mov);
							}
							// Caso contrario
							else {
								// Retorna ao dado anterior do relatório
								rs.previous();
								// Sai do laço interno
								break;
							} // fim do if
						} // fim do laço interno

						// Adiciona o histórico ao relatório
						data.add(new Object[] { null, null, historico, null,
								null, null });
						// quebra uma linha
						data.add(new Object[colunas.length]);
					} // fim do laço externo

					// Fecha cursor
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		};

		return modelo;

	};

	/** Impede a construção da classe */
	private Relatorios() {
	}
}
