package com.sevenrtc.aas.shared;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.entidades.Contexto;

/**
 * Classe que lida com os Contextos do banco. O contexto aberto atualmente pode
 * ser recuperado e mudado a partir dos métodos getContextoAtual() e
 * setContextoAtual()
 * 
 * @author Anthony Accioly
 * 
 */
public class Contextos {

	private static long contextoAtual = -1;

	private static String nomeEmpresa = null;

	private static ArrayList<Contexto> contextos = new ArrayList<Contexto>();

	/**
	 * Adiciona um contexto a colecao
	 * 
	 * @param c
	 *            contexto a ser adiciona a colecao
	 */
	public static void add(Contexto c) {
		contextos.add(c);
	}

	/**
	 * Retorna o numero do contexto atual
	 * 
	 * @return o contexto atual
	 */
	public static long getContextoAtual() {
		return contextoAtual;
	}

	/**
	 * Retorna uma lista contendo os contextos do banco
	 * 
	 * @return a lista de contextos
	 */
	public static ArrayList<Contexto> getContextos() {
		return contextos;
	}

	/**
	 * Retorna o nome da empresa mais recentemente aberta pelo usuario
	 * 
	 * @return p mome da empresa aberta atualmente
	 */
	public static String getNomeEmpresa() {
		return nomeEmpresa;
	}

	/**
	 * Carrega os contextos do banco
	 */
	public static void update() {

		contextos.clear();

		ResultSet rs = DAO.query("SELECT * FROM CTX_CONTEXTO");

		try {
			// Enquanto existirem outros contextos
			while (rs.next())
				contextos.add(Contexto.load(rs)); // Abre-os
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param id
	 *            o contexto a ser setado
	 */
	public static void setContextoAtual(long id) {
		Contextos.contextoAtual = id;
	}

	/**
	 * @param nomeEmpresa
	 *            o nome da empresa a ser setado
	 */
	public static void setNomeEmpresa(String nomeEmpresa) {
		Contextos.nomeEmpresa = nomeEmpresa;
	}

	/** Impede construção da classe estatica */
	private Contextos() {
	}

}
