package com.sevenrtc.aas.entidades;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sevenrtc.aas.db.DAO;

/**
 * Classe de contrapartida as entidades "Contexto" no banco de dados
 * 
 * @author Anthony Accioly
 * 
 */
public class Contexto {

	/** Apaga uma entidade do banco */
	public static void delete(Contexto contexto) {
		String strSQL;

		strSQL = "DELETE FROM CTX_Contexto WHERE CTX_ID = " + contexto.id + "";
		DAO.update(strSQL);

	}

	/** Abre uma entidade do banco */
	public static Contexto load(ResultSet rs) {
		Contexto c = new Contexto();

		try {
			c.id = rs.getLong(1);
			c.empresa = rs.getString(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return c;
	}

	/** Salva uma entidade no banco */
	public static void store(Contexto contexto) {
		String strSQL;

		strSQL = "INSERT INTO CTX_Contexto (CTX_Empresa) VALUES ('"
				+ contexto.empresa + "')";
		DAO.update(strSQL);
		ResultSet rs = DAO.query("select identity() from ctx_contexto");
		try {
			rs.next();
			contexto.id = rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Atualiza uma entidade do banco */
	public static void update(Contexto contexto) {
		String strSQL;

		strSQL = "UPDATE CTX_Contexto SET CTX_Empresa = '" + contexto.empresa
				+ "' WHERE CTX_ID = " + contexto.id;
		DAO.update(strSQL);

	}

	private long id; // chave

	private String empresa;

	/**
	 * 
	 */
	public Contexto() {
	}

	/**
	 * @param id
	 *            identificacao do contexto
	 * @param empresa
	 *            nome da empresa associada ao contexto
	 */
	public Contexto(long id, String empresa) {
		super();
		this.id = id;
		this.empresa = empresa;
	}

	/**
	 * @return the empresa
	 */
	public String getEmpresa() {
		return empresa;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param empresa
	 *            the empresa to set
	 */
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/** Sobrecarga do metodo toString() */
	public String toString() {
		return id + " - " + empresa;
	}

}
