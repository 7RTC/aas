package com.sevenrtc.aas.shared;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Classe que tranforma o relatório de uma JTable em um relatório CSV
 * 
 * @author Anthony Accioly
 * 
 */
public class RelatoriosCSV {

	// Selecionador de arquivos
	private static JFileChooser painelArquivos = new JFileChooser();

	// Customiza o painel de arquivos para visualizar arquivos .csv como default
	static {
		painelArquivos.addChoosableFileFilter(new FileNameExtensionFilter(
				"Arquivo CSV", "csv"));
	}

	/**
	 * Metodo interno para a construção de relatórios CSV
	 * 
	 * @param tabelaResultados
	 *            tabela com a origem dos dados
	 * @return uma String contendo um balancete no formato CSV
	 */
	private static String balancete(JTable tabelaResultados) {
		int rows = tabelaResultados.getRowCount();
		int cols = tabelaResultados.getColumnCount();

		// Resultado de percorrer a tabela
		String resultado = "";

		// Pega o nome das colunas
		for (int i = 0; i < cols; i++) {
			resultado += "\"" + tabelaResultados.getColumnName(i) + "\";";
		}
		resultado += "\n\n";

		// pega os valores das tabelas

		// valor atual
		String atual = "";
		Object v;

		// Percorre linhas e colunas da tabela
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					// Isola o objeto atual
					v = tabelaResultados.getValueAt(i, j);
					// Formata-o conforme o tipo
					switch (j) {
					case 3:
					case 4:
						atual = Constantes.getFormatterValor().valueToString(v);
						break;
					default:
						if (tabelaResultados.getValueAt(i, j) != null)
							atual = v.toString();
						else
							atual = "";
						break;

					} // fim do switch

				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				resultado += "\"" + atual + "\";";

			} // fim do for de colunas

			resultado += "\n";
		} // fim do for de linhas

		return resultado;
	}

	/**
	 * Metodo interno para a construção de relatórios CSV
	 * 
	 * @param tabelaResultados
	 *            tabela com a origem dos dados
	 * @return uma String contendo um balanco no formato CSV
	 */
	private static String balanco(JTable tabelaResultados) {
		int rows = tabelaResultados.getRowCount();
		int cols = tabelaResultados.getColumnCount();

		// Resultado de percorrer a tabela
		String resultado = "";

		// Pega o nome das colunas
		for (int i = 0; i < cols; i++) {
			resultado += "\"" + tabelaResultados.getColumnName(i) + "\";";
		}
		resultado += "\n\n";

		// pega os valores das tabelas

		// valor atual
		String atual = "";
		Object v;

		// Percorre linhas e colunas da tabela
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					// Isola o objeto atual
					v = tabelaResultados.getValueAt(i, j);
					// Formata-o conforme o tipo
					switch (j) {
					case 1:
						atual = Constantes.getFormatterValor().valueToString(v);
						break;
					default:
						if (tabelaResultados.getValueAt(i, j) != null)
							atual = v.toString();
						else
							atual = "";
						break;

					} // fim do switch

				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				resultado += "\"" + atual + "\";";

			} // fim do for de colunas

			resultado += "\n";
		} // fim do for de linhas

		return resultado;
	}

	/**
	 * Metodo interno para a construção de relatórios CSV
	 * 
	 * @param tabelaResultados
	 *            tabela com a origem dos dados
	 * @return uma String contendo um DRE no formato CSV
	 */
	private static String DRE(JTable tabelaResultados) {
		int rows = tabelaResultados.getRowCount();
		int cols = tabelaResultados.getColumnCount();

		// Resultado de percorrer a tabela
		String resultado = "";

		// Pega o nome das colunas
		for (int i = 0; i < cols; i++) {
			resultado += "\"" + tabelaResultados.getColumnName(i) + "\";";
		}
		resultado += "\n\n";

		// pega os valores das tabelas

		// valor atual
		String atual = "";
		Object v;

		// Percorre linhas e colunas da tabela
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				// Isola o objeto atual
				v = tabelaResultados.getValueAt(i, j);
				atual = v.toString();
				// Remove tags html
				if (atual.startsWith("<html>"))
					atual = atual.substring(9, atual.length() - 4);

				resultado += "\"" + atual + "\";";

			} // fim do for de colunas

			resultado += "\n";
		} // fim do for de linhas

		return resultado;
	}

	/**
	 * Extrai um arquivo CSV de uma tabela de acordo com sua especificação
	 * 
	 * @param relatorio
	 *            tipo de relatorio a ser construido
	 * @param tabela
	 *            tabela com os dados de origem
	 */
	public static void extraiCSV(int relatorio, JTable tabela) {
		// Salva o arquivo
		int confirmou = painelArquivos.showSaveDialog(tabela);
		if (confirmou == JFileChooser.APPROVE_OPTION)
			try {
				File f = painelArquivos.getSelectedFile();

				// Completa a terminação se o usuário esqueceu
				if (!f.getName().contains("."))
					f = new File(painelArquivos.getSelectedFile()
							.getAbsolutePath()
							+ ".csv");

				// String de destino do relatório
				String rel = "";
				// Extrai relatório de acordo com o tipo
				switch (relatorio) {

				case Relatorios.BALANCETE:
					rel = balancete(tabela);
					break;
				case Relatorios.BALANCO:
					rel = balanco(tabela);
					break;
				case Relatorios.LIVRO_DIARIO:
					rel = livroDiario(tabela);
					break;
				case Relatorios.LIVRO_RAZAO:
					rel = livroRazao(tabela);
					break;
				case Relatorios.DRE:
					rel = DRE(tabela);
					break;
				}

				FileWriter wrt = new FileWriter(f);
				wrt.write(rel);
				wrt.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(tabela, "Erro ao salvar arquivo",
						"Erro", JOptionPane.ERROR_MESSAGE);
			}

	}

	/**
	 * Metodo interno para a construção de relatórios CSV
	 * 
	 * @param tabelaResultados
	 *            tabela com a origem dos dados
	 * @return uma String contendo um livro diario no formato CSV
	 */
	private static String livroDiario(JTable tabelaResultados) {
		int rows = tabelaResultados.getRowCount();
		int cols = tabelaResultados.getColumnCount();

		// Resultado de percorrer a tabela
		String resultado = "";

		// Pega o nome das colunas
		for (int i = 0; i < cols; i++) {
			resultado += "\"" + tabelaResultados.getColumnName(i) + "\";";
		}
		resultado += "\n\n";

		// pega os valores das tabelas

		// valor atual
		String atual = "";
		Object v;

		// Percorre linhas e colunas da tabela
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					// Isola o objeto atual
					v = tabelaResultados.getValueAt(i, j);
					// Formata-o conforme o tipo
					switch (j) {
					case 1:
						atual = Constantes.getFormatterData().valueToString(v);
						break;
					case 4:
					case 5:
						atual = Constantes.getFormatterValor().valueToString(v);
						break;
					default:
						if (tabelaResultados.getValueAt(i, j) != null)
							atual = v.toString();
						else
							atual = "";
						break;

					} // fim do switch

				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				resultado += "\"" + atual + "\";";

			} // fim do for de colunas

			resultado += "\n";
		} // fim do for de linhas

		return resultado;
	}

	/**
	 * Metodo interno para a construção de relatórios CSV
	 * 
	 * @param tabelaResultados
	 *            tabela com a origem dos dados
	 * @return uma String contendo um livro razão no formato CSV
	 */
	private static String livroRazao(JTable tabelaResultados) {
		int rows = tabelaResultados.getRowCount();
		int cols = tabelaResultados.getColumnCount();

		// Resultado de percorrer a tabela
		String resultado = "";

		// Pega o nome das colunas
		for (int i = 0; i < cols; i++) {
			resultado += "\"" + tabelaResultados.getColumnName(i) + "\";";
		}
		resultado += "\n\n";

		// pega os valores das tabelas

		// valor atual
		String atual = "";
		Object v;

		// Percorre linhas e colunas da tabela
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				try {
					// Isola o objeto atual
					v = tabelaResultados.getValueAt(i, j);
					// Formata-o conforme o tipo
					switch (j) {
					case 0:
						atual = Constantes.getFormatterData().valueToString(v);
						break;
					case 3:
					case 4:
					case 6:
						atual = Constantes.getFormatterValor().valueToString(v);
						break;
					default:
						if (tabelaResultados.getValueAt(i, j) != null)
							atual = v.toString();
						else
							atual = "";
						break;

					} // fim do switch

				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				resultado += "\"" + atual + "\";";

			} // fim do for de colunas

			resultado += "\n";
		} // fim do for de linhas

		return resultado;
	}

}
