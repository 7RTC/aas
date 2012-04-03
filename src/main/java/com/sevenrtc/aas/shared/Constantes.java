/**
 * 
 */
package com.sevenrtc.aas.shared;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

/**
 * Classe que possui constantes e Formaters estaticos para uso integrado com
 * {@link javax.swing.JFormattedTextField}, {@link javax.swing.JTable} e afins
 * 
 * @author Anthony Accioly
 * 
 */
public class Constantes {

	/** Data atual do sistema */
	private static Date dataDeHoje = new Date();

	/** Formato ao estilo DATETIME de SQL */
	private static DateFormat formatoDataSQL = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** Formatter para Datas */
	private static DateFormatter formatterData = new DateFormatter(DateFormat
			.getDateInstance());

	/** Formatter para valores derivado da moeda local */
	private static NumberFormatter formatterValor;

	/** Pattern Data */
	private static String patternData = ((SimpleDateFormat) DateFormat
			.getDateInstance()).toPattern().toLowerCase();

	/** Renderer para celulas com valores centralizados */
	private static DefaultTableCellRenderer rendererCentralizado = new DefaultTableCellRenderer() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
			setHorizontalAlignment(CENTER);
			if (value == null)
				setText("");
			else
				setText(value.toString());
		}
	};

	/** Renderer para celulas com valores alinhado a esquerda */
	private static DefaultTableCellRenderer rendererRightAlign = new DefaultTableCellRenderer() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
			setHorizontalAlignment(RIGHT);
			if (value == null)
				setText("");
			else
				setText(value.toString());
		}
	};

	/** Renderer para celulas com datas */
	private static DefaultTableCellRenderer rendererData = new DefaultTableCellRenderer() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {

			try {
				setText(formatterData.valueToString(value));
				setHorizontalAlignment(CENTER);
			} catch (ParseException e) {
				setText("");
			}
		}
	};

	/** Renderer para celulas com valores */
	private static DefaultTableCellRenderer rendererValor = new DefaultTableCellRenderer() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {

			try {
				setText(formatterValor.valueToString(value));
				setHorizontalAlignment(RIGHT);
			} catch (ParseException e) {
				setText("");
			}
		}
	};

	// Seta parametros dos Formatters
	static {
		// Datas
		formatterData.setOverwriteMode(true);

		// Valores
		NumberFormat v = NumberFormat.getCurrencyInstance();
		if (v instanceof DecimalFormat) {
			// Elimina prefixos e sufixos de moedas locais (ex R$, U$, etc)
			((DecimalFormat) v).setPositivePrefix("");
			((DecimalFormat) v).setPositiveSuffix("");
			// Elimina sinais negativos substituindo-os por parenteses
			((DecimalFormat) v).setNegativePrefix("(");
			((DecimalFormat) v).setNegativeSuffix(")");

		}
		formatterValor = new NumberFormatter(v);
		formatterValor.setAllowsInvalid(false);

	}

	/** Retorna a data do sistema * */
	public static final Date getDataDeHoje() {
		return dataDeHoje;
	}

	/**
	 * Retorna o formatador de datas padrão DATETIME sql (yyyy-mm-dd), util para
	 * converter valores da classe Date sql em DATETIME para o banco e
	 * vice-versa
	 */
	public static final DateFormat getFormatoDataSQL() {
		return formatoDataSQL;
	}

	/**
	 * Retorna o formatador de datas para uso integrado com JFormattedTextFields
	 * ou para ser parser de strings
	 */
	public static final DateFormatter getFormatterData() {
		return formatterData;
	}

	/**
	 * Retorna o formatador de valores númericos para uso integrado com
	 * JFormattedTextFields ou para ser parser de strings
	 */
	public static final NumberFormatter getFormatterValor() {
		return formatterValor;
	}

	/**
	 * Retorna uma String contendo o formatado das datas do sistema ex:
	 * dd/mm/yyyy. Objeto útil de ser usado com tool tip texts
	 */
	public static final String getPatternData() {
		return patternData;
	}

	/**
	 * Retorna um renderer para {@link javax.swing.JTable} que permite a
	 * centralização do conteudo das células
	 */
	public static final DefaultTableCellRenderer getRendererCentralizado() {
		return rendererCentralizado;
	}

	/**
	 * Retorna um renderer para formatar datas em {@link javax.swing.JTable}
	 */
	public static final DefaultTableCellRenderer getRendererData() {
		return rendererData;
	}

	/**
	 * Retorna um renderer para {@link javax.swing.JTable} que permite a
	 * o alinhamento a direita do conteudo das células
	 */
	public static final DefaultTableCellRenderer getRendererRightAlign() {
		return rendererRightAlign;
	}

	/**
	 * Retorna um renderer para formatar valores em JTables
	 */
	public static final DefaultTableCellRenderer getRendererValor() {
		return rendererValor;
	}

	/** Construtor privado não permite a construção dessa classe */
	private Constantes() {
	}

}
