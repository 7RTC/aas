/**
 * 
 */
package com.sevenrtc.aas.ui.helper;

import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Classe responsável por carregar icones para a aplicação
 * 
 * @author Anthony Accioly
 * 
 */
public class ImageLoader {

	/**
	 * Abre uma imagem especificada pelo usuário
	 * 
	 * @param caminho
	 *            caminho completo ou relativo da imagem a ser aberta
	 * @param descricao
	 *            descrição da imagem para ajudar na tecnologia assistiva
	 * @return uma imagem especificada pelo usuário
	 */
	public static ImageIcon abrirImagem(String caminho, String descricao) {
		URL urlImagem = ImageLoader.class.getResource(caminho);
		if (urlImagem != null)
			return new ImageIcon(urlImagem, descricao);

		return null;
	};

	/** Impede construção da classe * */
	private ImageLoader() {
	}

}
