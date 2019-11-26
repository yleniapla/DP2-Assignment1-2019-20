package it.polito.dp2.BIB.sol1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.BIB.ArticleReader;
import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.IssueReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.JournalReader;
import it.polito.dp2.BIB.sol1.jaxb.MyArticleType;
import it.polito.dp2.BIB.sol1.jaxb.MyBiblio;
import it.polito.dp2.BIB.sol1.jaxb.MyBookType;
import it.polito.dp2.BIB.sol1.jaxb.MyIssueType;
import it.polito.dp2.BIB.sol1.jaxb.MyJournalType;

public class MyBibReader implements BibReader {

	private MyBiblio biblio;
	private Map<Integer, MyBookReader> books;
	private Map<Integer, MyArticleReader> articles;
	private Map<String, MyJournalReader> journals;
	private Map<Integer, MyItemReader> items;
	private Map<Integer, List<Integer>> citations;   //elemento, chi lo cita
	private Map<Integer,MyIssueReader> issues;  //id issue e ISSN journal
	private Map<Integer, Set<Integer>> issToArt; //id issue e id article
	private Map<Integer, String> artToJou; //id art e issn jou
	private Map<String, ArrayList<Integer>> jouToIss; //issn jou e id issues

	public MyBibReader(MyBiblio biblio) {

		this.biblio = biblio;
		books = new HashMap<Integer, MyBookReader>();
		articles = new HashMap<Integer, MyArticleReader>();
		journals = new HashMap<String, MyJournalReader>();
		issues = new HashMap<Integer, MyIssueReader>();
		issToArt = new HashMap<Integer, Set<Integer>>();
		items = new HashMap<Integer, MyItemReader>();
		citations = new HashMap<Integer, List<Integer>>();
		artToJou = new HashMap<Integer, String>();
		jouToIss = new HashMap<String, ArrayList<Integer>>();

		setItems();
		setJournals();
		setCitations();
		setIssues();

	}

	/*
	 * ===============================================================================
	 * ================================== GETTERS  ===================================
	 * ==============================================================================*/

	@Override
	public BookReader getBook(String arg0) {

		if (arg0 != null) {

			for (BookReader b : this.books.values()) {
				if (b.getISBN().equals(arg0))
					return b;
			}

		}

		return null;

	}

	@Override
	public Set<ItemReader> getItems(String arg0, int arg1, int arg2) {

		Set<ItemReader> wantedItems = new HashSet<>();

		if (arg0 == null) // nessuna keyword
		{
			if ((arg1 < 0 || arg1 > 9999) && (arg2 < 0 || arg2 > 9999)) // since
																		// e to
																		// nulli
			{
				for (ItemReader i : this.items.values()) {
					wantedItems.add(i);
				}
			} else if ((arg1 < 0 || arg1 > 9999) && (arg2 > 0 || arg2 < 9999)) // since
																				// nullo
			{
				for (ItemReader i : this.items.values()) {
					if (i instanceof BookReader) {
						BookReader b = (BookReader) i;
						if (b.getYear() < arg2) {
							wantedItems.add(i);
						}
					} else if (i instanceof ArticleReader) {
						ArticleReader a = (ArticleReader) i;
						if (a.getIssue().getYear() < arg2) {
							wantedItems.add(i);
						}
					}
				}
			} else if ((arg1 > 0 || arg1 < 9999) && (arg2 < 0 || arg2 > 9999)) // to
																				// nullo
			{
				for (ItemReader i : this.items.values()) {
					if (i instanceof BookReader) {
						BookReader b = (BookReader) i;
						if (b.getYear() > arg1) {
							wantedItems.add(i);
						}
					} else if (i instanceof ArticleReader) {
						ArticleReader a = (ArticleReader) i;
						if (a.getIssue().getYear() > arg1) {
							wantedItems.add(i);
						}
					}
				}
			} else if ((arg1 > 0 || arg1 < 9999) && (arg2 > 0 || arg2 < 9999)) // entrambi
																				// validi
			{
				for (ItemReader i : this.items.values()) {
					if (i instanceof BookReader) {
						BookReader b = (BookReader) i;
						if (b.getYear() < arg2 && b.getYear() > arg1) {
							wantedItems.add(i);
						}
					} else if (i instanceof ArticleReader) {
						ArticleReader a = (ArticleReader) i;
						if (a.getIssue().getYear() < arg2 && a.getIssue().getYear() > arg1) {
							wantedItems.add(i);
						}
					}
				}
			}

		} else {
			if ((arg1 < 0 || arg1 > 9999) && (arg2 < 0 || arg2 > 9999)) // since
																		// e to
																		// nulli
			{
				for (ItemReader i : this.items.values()) {
					if (i.getTitle().contains(arg0)) {
						wantedItems.add(i);
					}

				}
			} else if ((arg1 < 0 || arg1 > 9999) && (arg2 > 0 || arg2 < 9999)) // since
																				// nullo
			{
				for (ItemReader i : this.items.values()) {
					if (i.getTitle().contains(arg0)) {

						if (i instanceof BookReader) {
							BookReader b = (BookReader) i;
							if (b.getYear() < arg2) {
								wantedItems.add(i);
							}
						} else if (i instanceof ArticleReader) {
							ArticleReader a = (ArticleReader) i;
							if (a.getIssue().getYear() < arg2) {
								wantedItems.add(i);
							}
						}

					}

				}
			} else if ((arg1 > 0 || arg1 < 9999) && (arg2 < 0 || arg2 > 9999)) // to
																				// nullo
			{
				for (ItemReader i : this.items.values()) {
					if (i.getTitle().contains(arg0)) {

						if (i instanceof BookReader) {
							BookReader b = (BookReader) i;
							if (b.getYear() > arg1) {
								wantedItems.add(i);
							}
						} else if (i instanceof ArticleReader) {
							ArticleReader a = (ArticleReader) i;
							if (a.getIssue().getYear() > arg1) {
								wantedItems.add(i);
							}
						}
					}
				}
			} else if ((arg1 > 0 || arg1 < 9999) && (arg2 > 0 || arg2 < 9999)) // entrambi
																				// validi
			{
				for (ItemReader i : this.items.values()) {
					if (i.getTitle().contains(arg0)) {

						if (i instanceof BookReader) {
							BookReader b = (BookReader) i;
							if (b.getYear() < arg2 && b.getYear() > arg1) {
								wantedItems.add(i);
							}
						} else if (i instanceof ArticleReader) {
							ArticleReader a = (ArticleReader) i;
							if (a.getIssue().getYear() < arg2 && a.getIssue().getYear() > arg1) {
								wantedItems.add(i);
							}
						}
					}

				}
			}
		}
		return wantedItems;
	}

	@Override
	public JournalReader getJournal(String arg0) {

		if (arg0 != null) {

			for (JournalReader j : this.journals.values()) {
				if (j.getISSN().equals(arg0))
					return j;
			}

		} 
		return null;
	}

	@Override
	public Set<JournalReader> getJournals(String arg0) {

		Set<JournalReader> wantedJournals = new HashSet<>();

		if (arg0 != null) {

			for (JournalReader j : this.journals.values()) {
				if (j.getPublisher().contains(arg0) || j.getTitle().contains(arg0))
					wantedJournals.add(j);
			}
		} else
			wantedJournals.addAll(this.journals.values());

		return wantedJournals;
	}

	/*
	 * ===============================================================================
	 * ================================== SETTERS  ===================================
	 * ==============================================================================*/

	public void setItems() {
		
		//CREARE MAPPE PER ITEM CON ID E ELEMENTO

		ArrayList<MyArticleType> art = null;
		ArrayList<MyBookType> boo = null;

		if (biblio != null) {
			boo = (ArrayList<MyBookType>) biblio.getBook();
			art = (ArrayList<MyArticleType>) biblio.getArticle();
		}
		if (boo != null && art != null) {
			for (MyArticleType a : art) {
				MyArticleReader ar = new MyArticleReader();
				
				ar.setTitle(a.getTitle());
				ar.setSubtitle(a.getSubtitle());
				ar.setAuthors(a.getAuthor());
				ar.setId(a.getId());
								
				if(issToArt.containsKey(a.getIssue())) //ISSUE
				{
					issToArt.get(a.getIssue()).add(a.getId());
				} else {
					Set<Integer> l = new HashSet<Integer>();
					l.add(a.getId());
					issToArt.put(a.getIssue(), l);
				}
				
				this.citations.put(a.getId(), a.getCitedBy()); //citazioni
				
				this.artToJou.put(a.getId(), a.getJournal());//JOURNAL
				
				this.articles.put(a.getId(), ar);
				this.items.put(a.getId(), ar);
			}

			for (MyBookType b : boo) {
				MyBookReader br = new MyBookReader();

				br.setTitle(b.getTitle());
				br.setSubtitle(b.getSubtitle());
				br.setAuthors(b.getAuthor());
				br.setISBN(b.getISBN());
				br.setPublisher(b.getPublisher());
				br.setYear(b.getYear().getYear());
				br.setId(b.getId());
				
				this.citations.put(b.getId(), b.getCitedBy()); //CITAZIONI
				
				this.books.put(b.getId(), br);
				this.items.put(b.getId(), br);
			}
		}

		return;

	}

	public void setJournals() {
		
		ArrayList<MyJournalType> jou = null;

		if (biblio != null) {
			jou = (ArrayList<MyJournalType>) biblio.getJournal();
		}
		
		if(jou != null){
			for (MyJournalType j : jou) {
				MyJournalReader jr = new MyJournalReader();
				
				jr.setISSN(j.getISSN());
				jr.setPublisher(j.getPublisher());
				jr.setTitle(j.getTitle());
				
				Set<IssueReader> set = new HashSet<>();
				
				for(MyIssueType i : j.getIssue())
				{
					MyIssueReader ir = new MyIssueReader();
					ir.setNumber(i.getNumber());
					ir.setYear(i.getYear());
					// ir.setJournal(jr);
					ir.setId(i.getId());

					/*if(issToArt.containsKey(i.getId()))
					{
						Set<ArticleReader> aSet = new HashSet<>();
						
						for(Integer l : issToArt.get(i.getId())){
							if(this.articles.entrySet().stream().filter(x -> x.getKey()==l)!=null){
								aSet.add(this.articles.entrySet().stream().filter(x -> x.getKey()==l).map(Map.Entry::getValue).findFirst().get());
							}
							for(ArticleReader a : this.articles){
								if(myHash(a.getTitle(), a.getIssue().getYear()) == l){
									aSet.add(a);
								}
							}
						}
						ir.setArticles(aSet);
						
					}	*/
					
					issues.put(i.getId(), ir);
					
					if(jouToIss.containsKey(j.getISSN())) //ISSUE
					{
						jouToIss.get(j.getISSN()).add(i.getId());
					} else {
						ArrayList<Integer> l = new ArrayList<Integer>();
						l.add(i.getId());
						jouToIss.put(j.getISSN(), l);
					}
					
					set.add(ir);
				}		
				
				jr.setIssues(set);
				
				this.journals.put(j.getISSN(), jr);
			}
		}

	}
	
	public void setCitations(){
		for(Integer i : this.citations.keySet()){ //per ogni item
			MyItemReader item = this.items.get(i);  
			Set<ItemReader> citingItems = new HashSet<ItemReader>();
			for(Integer ci : this.citations.get(i)){ //tutti gli elementi che lo citano
				citingItems.add(this.items.get(ci));
			}
			item.setCitingItems(citingItems);
		}
	}
	
	public void setIssues(){
		
		for(String s : this.jouToIss.keySet()){ //per tutti i journal
			MyJournalReader journal = this.journals.get(s);
			for(Integer i : this.jouToIss.get(s)){ //tutti gli issue di quel journal
				this.issues.get(i).setJournal(journal);
			}
		}
		
		
		for(Integer i : this.issToArt.keySet()){
			MyIssueReader issue = this.issues.get(i);
			Set<ArticleReader> set = new HashSet<>();
			for(Integer a : this.issToArt.get(i)){ //per tutti gli articoli con quell'issue
				this.articles.get(a).setIssue(issue);
				this.articles.get(a).setJournal(issue.getJournal());//setto anche il journal
				set.add(this.articles.get(a));
			}
			issue.setArticles(set); //setto gli articoli nell'issue
		}
	}
		
	/*private int myHash(String title, int year) {
		int hash = 7;
		int hash1 = 0;
		for (int i = 0; i < title.length(); i++) {
			hash1 += 32 * hash + title.charAt(i);
		}
		return (hash1 * year);
	}*/

}