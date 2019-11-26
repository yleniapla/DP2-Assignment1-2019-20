package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.BookReader;


public class MyBookReader extends MyItemReader implements BookReader {
	
	private String ISBN, publisher;
	private int year;

	public MyBookReader(){
		super();
	}

	@Override
	public String getISBN() {
		return this.ISBN;
	}

	@Override
	public String getPublisher() {
		return this.publisher;
	}

	@Override
	public int getYear() {
		return this.year;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
