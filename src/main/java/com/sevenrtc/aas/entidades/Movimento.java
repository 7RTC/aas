package com.sevenrtc.aas.entidades;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Constantes;
import com.sevenrtc.aas.shared.Contas;

/**
 * Classe de contrapartida as entidades "Movimento" no banco de dados
 * 
 * @author Anthony Accioly
 * 
 */
public class Movimento {

	/**
	 * Atualiza o saldo de uma conta de acordo com um movimento
	 * 
	 * @param codigo
	 *            codigo da conta a ser atualizada
	 * @param contexto
	 *            cotexto da conta
	 * @param valor
	 *            valor do movimento
	 * @param movTipo
	 *            tipo do movimento (debito ou credito)
	 * @return codigo do pai da conta alterada
	 */
	private static String atualizaSaldoConta(String codigo, long contexto,
			double valor, char movTipo) {

		// Seleciona o saldo, o tipo e o pai da conta atual
		double saldo = 0;
		char conTipo = 'z';
		String pai = null;

		try {
			ResultSet rs = DAO
					.query("SELECT con_saldo, con_tipo, con_pai FROM con_conta WHERE con_codigo = '"
							+ codigo + "' AND ctx_id = " + contexto);
			rs.next();
			saldo = rs.getDouble(1);
			conTipo = rs.getString(2).toUpperCase().charAt(0);
			pai = rs.getString(3);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Se a conta for de ativo ou despesa
		if (conTipo == 'A' || conTipo == 'D') {
			// Se o movimento for um debito
			if (movTipo == 'D')
				saldo += valor; // soma o valor
			// Se o movimento for um credito
			else if (movTipo == 'C')
				saldo -= valor; // subtrai o valor
		}

		// Se a conta for de passivo, patrimonio liquido ou receita
		else if (conTipo == 'P' || conTipo == 'L' || conTipo == 'R') {
			// Se o movimento for um credito
			if (movTipo == 'C')
				saldo += valor; // soma o valor
			// Se o movimento for um debito
			else if (movTipo == 'D')
				saldo -= valor; // subtrai o valor
		}

		// Persite a alteração no banco
		DAO.update("UPDATE con_conta SET con_saldo = " + saldo
				+ " WHERE con_codigo = '" + codigo + "' AND ctx_id = "
				+ contexto);

		return pai;
	}

	/** Apaga uma entidade do banco */
	public static void delete(Movimento mov) {

		// atualiza saldo da conta referenciada pelo movimento
		String pai = atualizaSaldoConta(mov.conta, mov.contexto, (-mov.valor
				.doubleValue()), mov.tipo);
		// e de todo pai da conta referida
		while (pai != null)
			pai = atualizaSaldoConta(pai, mov.contexto, (-mov.valor
					.doubleValue()), mov.tipo);

		// Apaga o movimento
		String strSQL = "DELETE FROM MOV_MOVIMENTO WHERE MOV_ID = " + mov.id;
		DAO.update(strSQL);

	}

	/** Abre uma entidade do banco */
	public static Movimento load(ResultSet rs) {
		Movimento mov = new Movimento();

		try {
			mov.id = rs.getLong(1);
			mov.valor = rs.getDouble(2);
			mov.saldoConta = rs.getDouble(3);
			mov.tipo = rs.getString(4).charAt(0);
			mov.partida = rs.getLong(5);
			mov.conta = rs.getString(6);
			mov.contexto = rs.getLong(7);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return mov;
	}

	/** Salva uma entidade no banco */
	public static void store(Movimento mov) {

		// atualiza saldo da conta referenciada pelo movimento
		String pai = atualizaSaldoConta(mov.conta, mov.contexto, mov.valor
				.doubleValue(), mov.tipo);
		// e de todo pai da conta referida
		while (pai != null)
			pai = atualizaSaldoConta(pai, mov.contexto,
					mov.valor.doubleValue(), mov.tipo);
		// copia o saldo da conta para a classe de movimento
		ResultSet rs = DAO
				.query("SELECT con_saldo FROM con_conta WHERE con_codigo = '"
						+ mov.conta + "' AND ctx_id = " + mov.contexto);
		try {
			rs.next();
			mov.saldoConta = rs.getDouble(1);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		// Insere movimento na tabela
		String strSQL;

		strSQL = "INSERT INTO MOV_Movimento (MOV_VALOR, MOV_SALDOCONTA, MOV_TIPO, "
				+ "PDI_ID, CON_CODIGO, CTX_ID) VALUES ("
				+ mov.valor
				+ ", "
				+ mov.saldoConta
				+ ", '"
				+ Character.toUpperCase(mov.tipo)
				+ "', "
				+ mov.partida
				+ ", '"
				+ mov.conta
				+ "', "
				+ mov.contexto + ") ";
		DAO.update(strSQL);

		rs = DAO.query("SELECT IDENTITY() FROM MOV_Movimento");
		// Baixa o ID do movimento
		try {
			rs.next();
			mov.id = rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/** Atualiza uma entidade do banco */
	public static void update(Movimento mov) {
		String strSQL;

		// Localiza o valor anterior do movimento e tira a diferença
		ResultSet rs = DAO
				.query("SELECT mov_valor, mov_tipo FROM mov_movimento WHERE mov_id = "
						+ mov.id);
		double difValor = 0;
		char movTipo = 'z';

		try {
			rs.next();
			difValor -= rs.getDouble(1);
			movTipo = rs.getString(2).charAt(0);
			/*
			 * Se o tipo do movimento anterior é diferente do atual inverte o
			 * sinal para validar a operação
			 */
			if (movTipo != mov.tipo)
				difValor = -difValor;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		difValor += mov.valor.doubleValue();

		// Se a diferenca não for igual a zero
		if (difValor != 0) {
			// atualiza saldo da conta referenciada pelo movimento
			String pai = atualizaSaldoConta(mov.conta, mov.contexto, difValor,
					mov.tipo);
			// e de todo pai da conta referida
			while (pai != null)
				pai = atualizaSaldoConta(pai, mov.contexto, difValor, mov.tipo);
			// copia o saldo da conta para a classe de movimento
			rs = DAO
					.query("SELECT con_saldo FROM con_conta WHERE con_codigo = '"
							+ mov.conta + "' AND ctx_id = " + mov.contexto);
			try {
				rs.next();
				mov.saldoConta = rs.getDouble(1);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		// atualiza o movimento
		strSQL = "UPDATE MOV_Movimento SET MOV_valor = " + mov.valor
				+ ", MOV_saldoconta = " + mov.saldoConta + ", MOV_tipo = '"
				+ Character.toUpperCase(mov.tipo) + "', PDI_ID = "
				+ mov.partida + ", CON_CODIGO = '" + mov.conta + "', CTX_ID = "
				+ mov.contexto + " WHERE MOV_ID = " + mov.id;

		DAO.update(strSQL);
	}

	private long id; // chave

	private Number valor;

	private Number saldoConta;

	private char tipo;

	private long partida; // foreign key para "P_Diario"

	private String conta; // foreign key para "Conta"

	private long contexto; // foreign key para "Conta"

	/**
	 * 
	 */
	public Movimento() {
	}

	/**
	 * @param id
	 *            identificacao do movimento
	 * @param valor
	 *            valor do movimento
	 * @param saldoConta
	 *            saldo da conta após o movimento
	 * @param tipo
	 *            tipo de movimento (D/C)
	 * @param partida
	 *            id da partida do movimento
	 * @param conta
	 *            conta a qual o movimento refere-se
	 * @param contexto
	 *            contexto ao qual o movimento refere-se
	 */
	public Movimento(long id, Number valor, Number saldoConta, char tipo,
			long partida, String conta, long contexto) {
		super();
		this.id = id;
		this.valor = valor;
		this.saldoConta = saldoConta;
		this.tipo = tipo;
		this.partida = partida;
		this.conta = conta;
		this.contexto = contexto;
	}

	/**
	 * @return the conta
	 */
	public String getConta() {
		return conta;
	}

	/**
	 * @return the contexto
	 */
	public long getContexto() {
		return contexto;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the partida
	 */
	public long getPartida() {
		return partida;
	}

	/**
	 * @return the saldoConta
	 */
	public Number getSaldoConta() {
		return saldoConta;
	}

	/**
	 * @return the tipo
	 */
	public char getTipo() {
		return tipo;
	}

	/**
	 * @return the valor
	 */
	public Number getValor() {
		return valor;
	}

	/**
	 * @param conta
	 *            the conta to set
	 */
	public void setConta(String conta) {
		this.conta = conta;
	}

	/**
	 * @param contexto
	 *            the contexto to set
	 */
	public void setContexto(long contexto) {
		this.contexto = contexto;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param partida
	 *            the partida to set
	 */
	public void setPartida(long partida) {
		this.partida = partida;
	}

	/**
	 * @param saldoConta
	 *            the saldoConta to set
	 */
	public void setSaldoConta(Number saldoConta) {
		this.saldoConta = saldoConta;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	/**
	 * @param valor
	 *            the valor to set
	 */
	public void setValor(Number valor) {
		this.valor = valor;
	}

	/** Retorna a representação String do objeto */
	public String toString() {
		String str = "";
		if (tipo == 'D')
			str += "Débito";
		else
			str += "Crédito";

		str += " em " + Contas.getNome(conta) + " [" + conta + "]";

		try {
			str += ". " + Constantes.getFormatterValor().valueToString(valor);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return str;
	}

}
