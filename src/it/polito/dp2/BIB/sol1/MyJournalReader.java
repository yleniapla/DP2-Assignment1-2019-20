package it.polito.dp2.BIB.sol1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.JournalReader;

public class MyJournalReader implements JournalReader {
	
	private String ISSN, title, publisher;
	private Set<IssueReader> issues;
	
	public MyJournalReader(){
		this.issues = new HashSet<>();
	}

	@Override
	public String getISSN() {
		return this.ISSN;
	}

	@Override
	public IssueReader getIssue(int arg0, int arg1) {
		
		for(IssueReader i : this.issues){
			if(i.getYear()==arg0 && i.getNumber()==arg1)
			{
				return i;
			}
		}
		return null;
	}

	@Override
	public Set<IssueReader> getIssues(int arg0, int arg1) {
		Set<IssueReader> selected = new HashSet<>();
		
		for(IssueReader i : this.issues){
			if(i.getYear()>arg0 && i.getYear()<arg1)
			{
				selected.add(i);
			}
		}
		return selected;
	}

	@Override
	public String getPublisher() {
		return this.publisher;
	}

	@Override
	public String getTitle() {
		return this.title;
	}
	
	public void setISSN(String iSSN) {
		ISSN = iSSN;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setIssues(Set<IssueReader> issues) {
		this.issues.addAll(issues);
	}

}
