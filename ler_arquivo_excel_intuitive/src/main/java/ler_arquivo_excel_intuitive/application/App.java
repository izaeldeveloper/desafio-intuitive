package ler_arquivo_excel_intuitive.application;

import java.io.IOException;
import java.util.List;

public class App {
	public static void main(String[] args) throws IOException {
		
		//Criei uma classe para Gerenciar todo Teste
		GerenciadorProcedimentos app = new GerenciadorProcedimentos();
		
		//Criei a lista do tipo da classe que criei dos objetos
		List<Procedimento> procedimentos = app.criar();
		
		//Imprimir no console para depuração
		app.imprimir(procedimentos);
		
		//Converte o xlsx em csv
		app.criaCSV(procedimentos, "procedimentos.csv");
		
		//Compacta ele
		app.compactarArquivos("procedimentos.zip", "procedimentos.csv");
		
	}
	
	
	
}
