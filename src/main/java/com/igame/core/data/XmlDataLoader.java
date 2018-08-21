package com.igame.core.data;

import com.igame.core.log.ExceptionLog;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.net.URL;

/**
 * 
 * @author zhh
 *
 */
public class XmlDataLoader {



	public static Schema getSechemaByName(String schemaFile) {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			schema = sf.newSchema(ClassLoader.getSystemClassLoader().getSystemResource(schemaFile));
		} catch (SAXException saxe) {
			throw new Error("Error while getting schema", saxe);
		}
		return schema;
	}

	@SuppressWarnings("unchecked")
	public static <D> D loadData(Class<D> dataClass, String dataPath) {
		try {
			JAXBContext jc = JAXBContext.newInstance(dataClass);
			Unmarshaller un = jc.createUnmarshaller();
			URL systemResource = ClassLoader.getSystemClassLoader().getSystemResource("resource/" + dataPath);
			return (D) un.unmarshal(systemResource);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionLog.error(e);
			throw new Error("Error while loading data:[" + dataClass.getName() + "]", e);
		}
	}
}
