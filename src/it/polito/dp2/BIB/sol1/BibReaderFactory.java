package it.polito.dp2.BIB.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.BIB.BibReader;
import it.polito.dp2.BIB.BibReaderException;
import it.polito.dp2.BIB.sol1.jaxb.MyBiblio;


public class BibReaderFactory extends it.polito.dp2.BIB.BibReaderFactory {

	@Override
	public BibReader newBibReader() throws BibReaderException {
		BibReader myBiblio;
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance("it.polito.dp2.BIB.sol1.jaxb");
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
			Schema schema;
			
			schema = sf.newSchema(new File("xsd/biblio_e.xsd"));
			u.setSchema(schema);
			
			String filename = System.getProperty("it.polito.dp2.BIB.sol1.BibInfo.file");
			File f = new File(filename);
			
			if(!f.exists())
				System.out.println("ERROR: Input File Unreliable");
			
			JAXBElement<MyBiblio> element = u.unmarshal(new StreamSource(f), MyBiblio.class);
			MyBiblio biblio = (MyBiblio) element.getValue();
			
			myBiblio = new MyBibReader(biblio);
		} catch (JAXBException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BibReaderException(e, "Unmarshal failed");
		}
		return myBiblio;
	}

}
