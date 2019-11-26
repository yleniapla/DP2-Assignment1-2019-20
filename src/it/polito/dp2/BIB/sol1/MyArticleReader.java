package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;

public class MyArticleReader extends MyItemReader implements ArticleReader {
	
	private JournalReader journal;
	private IssueReader issue;
	
	public MyArticleReader(){
		super();
	}

	@Override
	public IssueReader getIssue() {
		return this.issue;
	}

	@Override
	public JournalReader getJournal() {
		return this.journal;
	}
	
	public void setJournal(JournalReader journal) {
		this.journal = journal;
	}

	public void setIssue(IssueReader issue) {
		this.issue = issue;
	}

}
