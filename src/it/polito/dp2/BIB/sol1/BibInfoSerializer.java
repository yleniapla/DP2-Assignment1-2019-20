package it.polito.dp2.BIB.sol1;

import it.polito.dp2.BIB.*;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.dp2.BIB.BibReaderFactory;

import it.polito.dp2.BIB.sol1.jaxb.MyArticleType;
import it.polito.dp2.BIB.sol1.jaxb.MyBiblio;
import it.polito.dp2.BIB.sol1.jaxb.MyBookType;
import it.polito.dp2.BIB.sol1.jaxb.MyIssueType;
import it.polito.dp2.BIB.sol1.jaxb.MyJournalType;
import it.polito.dp2.BIB.sol1.jaxb.ObjectFactory;

import org.xml.sax.SAXException;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

public class BibInfoSerializer {

	private BibReader monitor;
	private static String FileName;
	private MyBiblio biblio;
	public ObjectFactory of;
	private Map<String, List<String>> Citation;
	private Map<Integer, Set<Integer>> Issues;

	public BibInfoSerializer() throws BibReaderException {
		of = new ObjectFactory();
		// System.setProperty("it.polito.dp2.BIB.Random.sourceFileName",
		// "biblio_e.xml");
		BibReaderFactory factory = BibReaderFactory.newInstance();
		monitor = factory.newBibReader();
		biblio = new MyBiblio();
		this.Citation = new HashMap<>();
		this.Issues = new HashMap<>();
	}

	public BibInfoSerializer(BibReader monitor) {
		super();
		this.monitor = monitor;
	}

	public static void main(String[] args) {
		BibInfoSerializer wf;

		// check of parameter
		if (args.length != 1) {
			System.out.println("Error: wrong number of arguments - <FileName>");
			System.exit(1);
		} else {
			// first parameter is the xml ouput filename
			FileName = args[0];
		}

		try {
			wf = new BibInfoSerializer();
			wf.All();

			JAXBContext jc = JAXBContext.newInstance("it.polito.dp2.BIB.sol1.jaxb");
			Marshaller m = jc.createMarshaller();
			Schema schema = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(new File("xsd/biblio_e.xsd"));
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.setSchema(schema);
			m.marshal(wf.biblio, new File(FileName));

		} catch (BibReaderException | DatatypeConfigurationException | JAXBException | SAXException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void All() throws DatatypeConfigurationException {

		Items();
		Journals();

	}

	private void Items() throws DatatypeConfigurationException {
		// Get the list of Items
		Set<ItemReader> set = monitor.getItems(null, 0, 3000);

		// For each Item print related data
		for (ItemReader item : set) {

			if (item instanceof BookReader) {
				BookReader book = (BookReader) item;
				MyBookType b = new MyBookType();
				b.setPublisher(book.getPublisher());
				GregorianCalendar gc = new GregorianCalendar();
				gc.set(GregorianCalendar.YEAR, book.getYear());
				b.setYear(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
				b.setISBN(book.getISBN());
				b.setTitle(item.getTitle());

				if (item.getSubtitle() != null)
					b.setSubtitle(item.getSubtitle());
				for (String a : item.getAuthors())
					b.getAuthor().add(a);

				b.setId(myHash(item.getTitle(), book.getYear()));

				Set<ItemReader> citingItems = item.getCitingItems();
				List<String> cit = new ArrayList<>();
				for (ItemReader citing : citingItems) {
					cit.add(citing.getTitle());
					Integer id = 0;

					if (citing instanceof ArticleReader) {

						id = myHash(((ArticleReader) citing).getJournal().getTitle(),
								((ArticleReader) citing).getIssue().getYear());

					} else if (citing instanceof BookReader) {

						id = myHash(citing.getTitle(), ((BookReader) citing).getYear());

					}

					b.getCitedBy().add(id);
				}
				if (!Citation.containsKey(item.getTitle())) {
					Citation.put(item.getTitle(), cit);
				}

				biblio.getBook().add(b);

			}
			if (item instanceof ArticleReader) {
				ArticleReader article = (ArticleReader) item;
				MyArticleType a = new MyArticleType();
				a.setJournal(article.getJournal().getISSN());
				a.setIssue(myIssueHash(article.getIssue().getNumber(), article.getIssue().getYear()));
				a.setTitle(item.getTitle());

				if (item.getSubtitle() != null)
					a.setSubtitle(item.getSubtitle());
				for (String au : item.getAuthors()) {
					a.getAuthor().add(au);
				}

				a.setId(myHash(article.getJournal().getTitle(), article.getIssue().getYear()));
				
				if(Issues.containsKey(myIssueHash(article.getIssue().getNumber(), article.getIssue().getYear()))) //ISSUE
				{
					Issues.get(myIssueHash(article.getIssue().getNumber(), article.getIssue().getYear())).add(a.getId());
				} else {
					Set<Integer> l = new HashSet<Integer>();
					l.add(a.getId());
					Issues.put(myIssueHash(article.getIssue().getNumber(), article.getIssue().getYear()), l);
				}

				Set<ItemReader> citingItems = item.getCitingItems();
				List<String> cit = new ArrayList<>();
				for (ItemReader citing : citingItems) {
					cit.add(citing.getTitle());

					Integer id = 0;

					if (citing instanceof ArticleReader) {

						id = myHash(((ArticleReader) citing).getJournal().getTitle(),
								((ArticleReader) citing).getIssue().getYear());

					} else if (citing instanceof BookReader) {

						id = myHash(citing.getTitle(), ((BookReader) citing).getYear());

					}

					a.getCitedBy().add(id);
				}
				if (!Citation.containsKey(item.getTitle())) {
					Citation.put(item.getTitle(), cit);
				}

				biblio.getArticle().add(a);

			}
		}
	}

	private void Journals() throws DatatypeConfigurationException {
		// Get the list of journals
		Set<JournalReader> set = monitor.getJournals(null);

		for (JournalReader journal : set) {
			MyJournalType j = new MyJournalType();
			j.setTitle(journal.getTitle());
			j.setPublisher(journal.getPublisher());
			j.setISSN(journal.getISSN());

			for (IssueReader issue : journal.getIssues(0, 3000)) {
				MyIssueType i = new MyIssueType();
				i.setNumber(issue.getNumber());
				i.setYear(issue.getYear());
				i.setId(myIssueHash(issue.getNumber(), issue.getYear()));
				j.getIssue().add(i);
			}
			biblio.getJournal().add(j);
		}

	}

	private int myHash(String title, int year) {
		int hash = 7;
		int hash1 = 0;
		for (int i = 0; i < title.length(); i++) {
			hash1 += 32 * hash + title.charAt(i);
		}
		return (hash1 * year);
	}
	
	private int myIssueHash(int number, int year) {
		int hash = 7;
		int hash1 = 0;
		for (int i = 0; i < number; i++) {
			hash1 += 32 * hash + i;
		}
		return (hash1 * year);
	}

}

