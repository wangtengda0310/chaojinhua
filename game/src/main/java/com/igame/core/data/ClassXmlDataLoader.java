package com.igame.core.data;

import com.igame.core.log.ExceptionLog;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URL;

public class ClassXmlDataLoader {
	private String resourceFolder;
	public ClassXmlDataLoader(String resourceFolder) {
		this.resourceFolder = resourceFolder;
	}

	@SuppressWarnings("unchecked")
	public <D> D loadData(Class<D> dataClass) {
		XmlRootElement annotation = dataClass.getAnnotation(XmlRootElement.class);
		return loadData(dataClass, annotation.name());
	}

	@SuppressWarnings("unchecked")
	public <D> D loadData(Class<D> dataClass, String resourceFile) {
		try {
			JAXBContext jc = JAXBContext.newInstance(dataClass);
			Unmarshaller un = jc.createUnmarshaller();
			String resource = resourceFolder + resourceFile;
			if (!resource.endsWith(".xml")) {
				resource += ".xml";
			}
			URL systemResource = ClassLoader.getSystemResource(resource);
			return (D) un.unmarshal(systemResource);
		} catch (Exception e) {
			ExceptionLog.error("",e);
			throw new Error("Error while loading data:[" + dataClass.getName() + "]", e);
		}
	}
}
