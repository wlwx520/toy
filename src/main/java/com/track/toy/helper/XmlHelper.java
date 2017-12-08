package com.track.toy.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlHelper {
	public static Element read(String path) {
		try {
			SAXReader saxReader = new SAXReader();
			File file = new File(path);
			Document document = saxReader.read(file);
			return document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void write(Document document, String path, String cs) {
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(cs);
			new XMLWriter(new FileWriter(path), format).write(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
