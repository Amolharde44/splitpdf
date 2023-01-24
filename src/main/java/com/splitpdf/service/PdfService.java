package com.splitpdf.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.splitpdf.model.PdfModel;

import io.micronaut.http.multipart.CompletedFileUpload;

public interface PdfService {
		
	public PdfModel pdfsplit(CompletedFileUpload file , PdfModel model) throws IOException, ClassNotFoundException, SQLException;
}
