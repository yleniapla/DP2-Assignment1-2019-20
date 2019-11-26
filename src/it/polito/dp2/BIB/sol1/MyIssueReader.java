package it.polito.dp2.BIB.sol1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;

public class MyIssueReader implements IssueReader {
	
	private int year, number, id;
	private JournalReader journal;
	private Set<ArticleReader> articles;
	
	public MyIssueReader(){
		this.articles = new HashSet<>();
	}

	@Override
	public Set<ArticleReader> getArticles() {
		return this.articles;
	}

	@Override
	public JournalReader getJournal() {
		return this.journal;
	}

	@Override
	public int getNumber() {
		return this.number;
	}

	@Override
	public int getYear() {
		return this.year;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setJournal(JournalReader journal) {
		this.journal = journal;
	}

	public void setArticles(Set<ArticleReader> articles) {
		this.articles.addAll(articles);
	}
	
	

}
