package ler_arquivo_excel_intuitive.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Procedimento {
	private String nomeProcedimento;
	private String rn;
	private String vigencia;
	private String odontologia;
	private String ambulatorial;
	private String HCO;
	private String HSQ;
	private String REF;
	private String PAC;
	private int DUT;
	private String subGrupo;
	private String grupo;
	private String capitulo;
}
