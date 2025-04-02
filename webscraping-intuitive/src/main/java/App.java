import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App {
	// Aqui vai a URL que será extraido os PDFs
	private static final String URL = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";

	// Aqui é o nome da pasta que será criada para armazenar os PDfs
	private static final String DOWNLOAD_DIR = "anexos";
	
	//Aqui é o programa principal
	public static void main(String[] args) throws IOException {
		
		//Criação da Pasta 
		Files.createDirectories(Paths.get(DOWNLOAD_DIR));
		
		// Instanciar o Documento 
		Document documento = Jsoup.connect(URL).get();

		// Usei HashSet e não list porque estava dando erro de repetição que foi tratado
		Set<String> linksPDF = new HashSet<>();
		
		// Filtrar as ancoras que possuem os hrefs referentes aos anexos que serão baixados
		for (Element link : documento.select("a[href$=.pdf]")) {
			
			String urlPDF = link.absUrl("href");
			
			if (urlPDF.toLowerCase().contains("anexo_i") || urlPDF.toLowerCase().contains("anexo_ii")) {
				linksPDF.add(urlPDF);
			}
		}
		
		
		Set<String> arquivosPDF = new HashSet<>();
		
		// Loop pra ir baixando os PDFs
		for (String linkPDF : linksPDF) {
			
			String arquivoNome = DOWNLOAD_DIR + "/" + linkPDF.substring(linkPDF.lastIndexOf("/") + 1);
			
			// Tratativa pra não repetir o download de arquivos que já existem
			if(!arquivosPDF.contains(arquivoNome)) {
				baixarArquivo(linkPDF, arquivoNome);
				arquivosPDF.add(arquivoNome);
				
			}
			
			// Baixar os arquivos
			baixarArquivo(linkPDF, arquivoNome);
			
			// Adicionar os arquivos baixados a uma coleção pra depois ser zipada
			arquivosPDF.add(arquivoNome);
		}
		
		// Compactar os arquivos
		compactarArquivos(arquivosPDF, "anexos.zip");
		
		System.out.println("Arquivos compactados em anexos.zip");

	}
	
	// Função que vai baixar os arquivos
	private static void baixarArquivo(String urlArquivo, String nomeArquivo) throws IOException {
		
		// Cria uma conexao entre o servidor local e o site
		HttpURLConnection conexao = (HttpURLConnection) new URL (urlArquivo).openConnection();
		
		// Pegou o arquivo, faz a leitura e baixa ele
		try (InputStream in = conexao.getInputStream(); FileOutputStream out = new FileOutputStream (nomeArquivo)) {
			
				byte[] buffer = new byte[1024];
				int leituraBytes;
				
				while ((leituraBytes = in.read(buffer)) != -1) {
					
					out.write(buffer, 0, leituraBytes);
				}
				
				System.out.println("Baixado: " + nomeArquivo);
		}
		
	}
	
	// Função que vai compactar os PDFs
	// Ela basicamente pega o arquivo, lê e o armazena em uma coleção que será zipada em anexos.zip
	private static void compactarArquivos (Set<String> arquivos, String compactoNomeArquivo) throws IOException {
		
		try (ZipOutputStream zipOut = new ZipOutputStream (new FileOutputStream(compactoNomeArquivo))) {
			
			Set<String> arquivosAdicionados = new HashSet<>();
			
			for (String caminhoArquivo : arquivos) {
				
				File arquivo = new File (caminhoArquivo);
				
				if(!arquivosAdicionados.contains(arquivo.getName())) {
					
					try (FileInputStream fis = new FileInputStream(arquivo)) {
						
						ZipEntry zipEntry = new ZipEntry (arquivo.getName());
						
						zipOut.putNextEntry(zipEntry);
						
						byte[] buffer = new byte[1024];
						int tamanho;
						
						while ((tamanho = fis.read(buffer)) > 0) {
							
							zipOut.write(buffer, 0, tamanho);
							
						}
						
						arquivosAdicionados.add(arquivo.getName());
						
					}
					
				}
				
				
				
			}
			
		}
		
	}
}
