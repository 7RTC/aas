package com.sevenrtc.aas.entidades;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Constantes;
import java.sql.PreparedStatement;

/**
 * Classe de contrapartida as entidades "PartidaDiario" no banco de dados
 * 
 * @author Anthony Accioly
 * 
 */
public class PartidaDiario {

	/** Apaga uma entidade do banco */
	public static void delete(PartidaDiario partida) {
		String strSQL;

		strSQL = "DELETE FROM PDI_PartidaDiario WHERE PDI_ID = " + partida.id;
		DAO.update(strSQL);
	}

	/** Abre uma entidade do banco */
	public static PartidaDiario load(ResultSet rs) {
		PartidaDiario p = new PartidaDiario();

		try {
			p.id = rs.getLong(1);
			p.data = rs.getDate(2);
			p.historico = rs.getString(3);
			p.contexto = rs.getLong(4);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return p;
	}

    /**
     * Salva uma entidade no banco
     */
    public static void store(PartidaDiario partida) {
        final String strSQL =
                "INSERT INTO PDI_PartidaDiario (PDI_Data, PDI_Historico, CTX_ID) VALUES (?, ?, ?)";
        try {
            final PreparedStatement ps = DAO.getPreparedStatement(strSQL);
            ps.setDate(1, new java.sql.Date(partida.data.getTime()));
            ps.setString(2, partida.historico);
            ps.setLong(3, partida.contexto);
            DAO.update(ps);
            ResultSet rs = DAO.query("select identity() from pdi_partidadiario");
            rs.next();
            partida.id = rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	/** Atualiza uma entidade do banco */
	public static void update(PartidaDiario partida) {
		String strSQL;

		strSQL = "UPDATE PDI_PartidaDiario SET PDI_Data = '"
				+ Constantes.getFormatoDataSQL().format(partida.data) + "', ";
		strSQL = strSQL + "PDI_Historico = '" + partida.historico
				+ "', CTX_ID = " + partida.contexto + " WHERE pdi_id = "
				+ partida.id + "";
		DAO.update(strSQL);
	}

	private long id; // chave

	private Date data;

	private String historico;

	private long contexto; // foreign key para contexto

	/**
	 * 
	 */
	public PartidaDiario() {
	}

	/**
	 * @param id
	 *            codigo do movimento
	 * @param data
	 *            data do movimento
	 * @param historico
	 *            historico da operacao
	 * @param contexto
	 *            contexto da operação
	 */
	public PartidaDiario(long id, Date data, String historico, long contexto) {
		super();
		this.id = id;
		this.data = data;
		this.historico = historico;
		this.contexto = contexto;
	}

	/**
	 * @return the contexto
	 */
	public long getContexto() {
		return contexto;
	}

	public Date getData() {
		return data;
	}

	/**
	 * @return the historico
	 */
	public String getHistorico() {
		return historico;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param contexto
	 *            the contexto to set
	 */
	public void setContexto(long contexto) {
		this.contexto = contexto;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * @param historico
	 *            the historico to set
	 */
	public void setHistorico(String historico) {
		this.historico = historico;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

}
