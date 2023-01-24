package com.splitpdf.model;

public class PdfModel {
	private String Option;
	private String inputpage;
	public PdfModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PdfModel(String option, String inputpage) {
		super();
		Option = option;
		this.inputpage = inputpage;
	}
	@Override
	public String toString() {
		return "PdfModel [Option=" + Option + ", inputpage=" + inputpage + "]";
	}
	public String getOption() {
		return Option;
	}
	public void setOption(String option) {
		Option = option;
	}
	public String getInputpage() {
		return inputpage;
	}
	public void setInputpage(String inputpage) {
		this.inputpage = inputpage;
	}

}