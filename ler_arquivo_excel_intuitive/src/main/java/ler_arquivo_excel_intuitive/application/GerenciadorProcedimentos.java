package ler_arquivo_excel_intuitive.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Cleanup;

public class GerenciadorProcedimentos {
	
	// Aqui cria o CSV, baseado no documento xlsx
	public void criaCSV(List<Procedimento> procedimentos, String arquivoCSV) throws IOException {
		
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(arquivoCSV),"UTF-8")) {
			
			writer.append("PROCEDIMENTO;RN(alteração);VIGENCIA;ODONTOLOGIA;AMBULATORIAL;HCO;HSQ;REF;PAC;DUT;SUBGRUPO;GRUPO;CAPÍTULO\n");

			for (Procedimento procedimento : procedimentos) {
				
				writer.append(procedimento.getNomeProcedimento() != null ? procedimento.getNomeProcedimento() : "").append(";")
				.append(procedimento.getRn() != null ? procedimento.getRn() : "").append(";")
				.append(procedimento.getVigencia() != null ? procedimento.getVigencia().toString() : "").append(";")
				.append(procedimento.getOdontologia() != null ? procedimento.getOdontologia() : "").append(";")
                .append(procedimento.getAmbulatorial() != null ? procedimento.getAmbulatorial() : "").append(";")
                .append(procedimento.getHCO() != null ? procedimento.getHCO() : "").append(";")
                .append(procedimento.getHSQ() != null ? procedimento.getHSQ() : "").append(";")
                .append(procedimento.getREF() != null ? procedimento.getREF() : "").append(";")
                .append(procedimento.getPAC() != null ? procedimento.getPAC() : "").append(";")
                .append(String.valueOf(procedimento.getDUT())).append(";")
                .append(procedimento.getSubGrupo() != null ? procedimento.getSubGrupo() : "").append(";")
                .append(procedimento.getGrupo() != null ? procedimento.getGrupo() : "").append(";")
                .append(procedimento.getCapitulo() != null ? procedimento.getCapitulo() : "").append("\n");
				
			}
			
			System.out.println("Arquivo csv criado com sucesso!");
		}
		
	}
	
	public <T> List<T> criarLista(Iterator<T> iterator){
		
		return IteratorUtils.toList(iterator);
		
	}
	
	// Imprime na tela os valores
	public void imprimir(List<Procedimento> procedimentos) {
		
		procedimentos.forEach(System.out::println);
		
	}
	
	// Pega valor da celula se tiver vazio, para não dar erros
	public String pegarValorCelula(Cell celula) {
		if (celula == null) return "vazio";
		DataFormatter formatter = new DataFormatter();
		return formatter.formatCellValue(celula);
	}
	
	// Pega valores inteiros, para novamente não dar erro
	public int pegarValorInteiro(Cell celula) {
		
		String valorCelula = pegarValorCelula(celula).replaceAll("\\D","");
		
		if(valorCelula.isEmpty()) {
			return 0;
		}
		
		return Integer.parseInt(valorCelula);
		
	}
	

	public List<Procedimento> criar() throws IOException {
			
			// Criando uma lista de procedimentos
		List<Procedimento> procedimentos = new ArrayList<>();
			
			//Pegar o Arquivo
		@Cleanup FileInputStream fis = new FileInputStream("dados.xlsx");
			
			// Instanciando um arquivo XLSX com Apache POI
		Workbook workbook = new XSSFWorkbook(fis);
			
			// Instnaciando uma planilha pegando a planilha desejada dentro do arquivo
		Sheet planilha = workbook.getSheetAt(2);
			
			//Pegar todas as linhas da planilha
		List<Row> rows = criarLista(planilha.iterator());
			
		int linhasParaRemover = Math.min(5, rows.size());
		
		for(int i = 0; i < linhasParaRemover; i++) {
			rows.remove(0);
		}
		
		rows.forEach(row ->{
				
				//Pegando as celulas das linhas
				List<Cell> celulas = criarLista(row.cellIterator());
				
				//Atribuindo valor a classe procedimento
				Procedimento procedimento = Procedimento.builder()
						.nomeProcedimento(pegarValorCelula(celulas.get(0)))
						.rn(pegarValorCelula(celulas.get(1)))
						.vigencia(pegarValorCelula(celulas.get(2)))
						.odontologia(pegarValorCelula(celulas.get(3)))
						.ambulatorial(pegarValorCelula(celulas.get(4)))
						.HCO(pegarValorCelula(celulas.get(5)))
						.HSQ(pegarValorCelula(celulas.get(6)))
						.REF(pegarValorCelula(celulas.get(7)))
						.PAC(pegarValorCelula(celulas.get(8)))
						.DUT(pegarValorInteiro(celulas.get(9)))
						.subGrupo(pegarValorCelula(celulas.get(10)))
						.grupo(pegarValorCelula(celulas.get(11)))
						.capitulo(pegarValorCelula(celulas.get(12)))
						.build();
						
				
				// Adiciona o objeto a lista de procedimentos
				procedimentos.add(procedimento);
				
		});
		
		return procedimentos;
	}
	
	//A função compacta o csv depois de gerado
	public void compactarArquivos (String compactoNomeArquivo, String nomeArquivo) throws IOException {
		
		try (FileOutputStream fos = new FileOutputStream(compactoNomeArquivo);
	             ZipOutputStream zos = new ZipOutputStream(fos);
	             FileInputStream fis = new FileInputStream(nomeArquivo)) {
	            
	            ZipEntry zipEntry = new ZipEntry(new File(nomeArquivo).getName());
	            zos.putNextEntry(zipEntry);
	            
	            byte[] buffer = new byte[1024];
	            int tamanho;
	            while ((tamanho = fis.read(buffer)) >= 0) {
	                zos.write(buffer, 0, tamanho);
	            }
	            zos.closeEntry();
	            
	            System.out.println("Arquivo compactado com sucesso!");
	        } catch (IOException e) {
	            System.err.println("Erro ao compactar o arquivo: " + e.getMessage());
	        }
		
	}
}
