package com.splitpdf.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.splitpdf.model.PdfModel;
import com.splitpdf.service.PdfService;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;

@Controller
public class PdfController {

	@Inject
	PdfService servive;
	
	@Post(uri="/split" , consumes = MediaType.MULTIPART_FORM_DATA , produces = MediaType.TEXT_PLAIN)
	public PdfModel splitpdf(@PathVariable("file") CompletedFileUpload file, PdfModel model) throws IOException,ClassNotFoundException, SQLException 
	{
		return this.servive.pdfsplit(file, model);
	}
}
