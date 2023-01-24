package com.splitpdf.service;

import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.azure.ai.formrecognizer.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.models.AnalyzeResult;
import com.azure.ai.formrecognizer.models.DocumentOperationResult;
import com.azure.ai.formrecognizer.models.DocumentTable;
import com.azure.ai.formrecognizer.models.DocumentTableCell;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import com.splitpdf.model.PdfModel;

import io.micronaut.http.multipart.CompletedFileUpload;

import jakarta.inject.Singleton;

@Singleton
public class PdfServiceImpl implements PdfService {
	@Override
	public PdfModel pdfsplit(CompletedFileUpload file, PdfModel model)
			throws IOException, ClassNotFoundException, SQLException {

		String name = file.getFilename();
		byte[] filedata = file.getBytes();
		PDDocument document = PDDocument.load(filedata);
		ArrayList<Integer> pagesToExtract = new ArrayList<>();
		
		
		String option=model.getOption();
		String home = System.getProperty("user.home");
		String filename=null;
		if(option.equalsIgnoreCase("All"))
		{
			home = System.getProperty("user.home");
			filename="All";
			document.save(home + "/Downloads/" + name +filename+ ".pdf");
			 
		}
		else if(option.equalsIgnoreCase("Split"))
				{
		String input = model.getInputpage();
		

		String[] inputNumbers = input.split(",");

		for (String number : inputNumbers) {

			number = number.trim();

			try {
				pagesToExtract.add(Integer.parseInt(number));
			} catch (NumberFormatException e) {
				System.out.println(number + " is not a valid page number. Skipping...");
			}
		}
		
		
		PDDocument newDocument = new PDDocument();

		for (int pageNumber : pagesToExtract) {
			PDPage page = document.getPage(pageNumber - 1);
			newDocument.addPage(page);
		}
		
		
		
		 home = System.getProperty("user.home");
		newDocument.save(home + "/Downloads/" + name + ".pdf");

		document.close();
		newDocument.close();
		System.out.println("pdf save succesfully");
				}
		

		final String endpoint = "https://secondformrecognizer001.cognitiveservices.azure.com/";
		final String key = "40e2e4174044476596572ce562612d28";
		DocumentAnalysisClient client = new DocumentAnalysisClientBuilder().credential(new AzureKeyCredential(key))
				.endpoint(endpoint).buildClient();

		String modelId = "prebuilt-document";

		String FileName = home + "/Downloads/" + name + ".pdf";
		String extPattern = "(?<!^)[.]" + (".*");
		File document1 = new File(FileName);
		XSSFWorkbook workbook = new XSSFWorkbook();

		String replacedFileName = document1.getName().replaceAll(extPattern, "");
		System.out.println(replacedFileName + " replaced name");

		byte[] fileContent = Files.readAllBytes(document1.toPath());

		try (InputStream targetStream = new ByteArrayInputStream(fileContent)) {

			SyncPoller<DocumentOperationResult, AnalyzeResult> analyzeDocumentPoller = client
					.beginAnalyzeDocument(modelId, targetStream, document1.length());
			AnalyzeResult analyzeResult = analyzeDocumentPoller.getFinalResult();
//			String content = analyzeResult.getContent().toString();

			List<DocumentTable> tables = analyzeResult.getTables();
			for (int i = 0; i < tables.size(); i++) {

				DocumentTable documentTable = tables.get(i);

				System.out.printf("Table %d has %d rows and %d columns.%n", i, documentTable.getRowCount(),
						documentTable.getColumnCount());

				XSSFSheet spreadsheet = workbook.createSheet("Sheet no" + i);
				XSSFRow newrow = spreadsheet.createRow(0);

				int count = 0;
				int col = 0;

				for (DocumentTableCell dtc : documentTable.getCells()) {
					int rowIndex = dtc.getRowIndex();

					if (count < rowIndex) {
						count++;
						System.out.println(count);
						newrow = spreadsheet.createRow(count);
						col = 0;
					}
					XSSFCell newcell1 = newrow.createCell(col++);
//					String keywordContain = dtc.getContent().toString();
//					if (keywordContain.equals("EBITDA")) {
//						workbook.setSheetName(i, "Income Statement");
//					}
//					if (keywordContain.equals("Net worth")) {
//						workbook.setSheetName(i, "Balance Sheet");
//					}
//					if (keywordContain.equals("Free Cash Flow")) {
//						workbook.setSheetName(i, "Cash Flow");
//					}

					newcell1.setCellValue(dtc.getContent().toString());

				}

				System.out.println("=======================Created a new sheet===========================");

			}
			FileOutputStream out1 = new FileOutputStream(new File(home + "/Downloads/" + replacedFileName + ".xlsx"));
			workbook.write(out1);
			out1.close();

			System.out.printf("Successfully created %d sheets", tables.size());
		}

		return model;
	}

}
