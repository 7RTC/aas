/**
 * 
 */
package com.sevenrtc.aas.entidades;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sevenrtc.aas.db.DAO;

/**
 * Classe de contrapartida as entidades "CategoriaDRE" no banco de dados
 * 
 * @author Anthony Accioly
 * 
 */
public class CategoriaDRE {

	/** Apaga uma entidade do banco */
	public static void delete(CategoriaDRE categoria) {
		String strSQL;

		strSQL = "DELETE FROM CAT_CategoriaDRE WHERE CAT_ID = " + categoria.id;
		DAO.update(strSQL);
	}

	/** Abre uma entidade do banco */
	public static CategoriaDRE load(ResultSet rs) {
		CategoriaDRE cat = new CategoriaDRE();

		try {
			cat.id = rs.getLong(1);
			cat.nome = rs.getString(2);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return cat;
	}

	/** Salva uma entidade no banco */
	public static void store(CategoriaDRE categoria) {
		String strSQL;

		strSQL = "INSERT INTO CAT_CategoriaDRE (CAT_Nome) VALUES ('"
				+ categoria.nome + "')";
		DAO.update(strSQL);
		ResultSet rs = DAO.query("SELECT IDENTITY() FROM CAT_CategoriaDRE");
		// Baixa o ID da Categoria DRE
		try {
			rs.next();
			categoria.id = rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/** Atualiza uma entidade do banco */
	public static void update(CategoriaDRE categoria) {
		String strSQL;

		strSQL = "UPDATE CAT_CategoriaDRE SET CAT_Nome = '" + categoria.nome
				+ "' WHERE CAT_ID = " + categoria.id;
		DAO.update(strSQL);
	}

	private long id; // Chave

	private String nome;

	/**
	 * Construtor padrão
	 */
	public CategoriaDRE() {

	}

	/**
	 * @param id
	 *            identificao da categoria
	 * @param nome
	 *            nome da categoria
	 */
	public CategoriaDRE(long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param nome
	 *            the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		if (id != 0)
			return id + " - " + nome;
		else
			return "(Nenhuma)";
	}

}
