/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;

/**
 * @author Eike Stepper
 */
public final class XMLUtil
{
  private XMLUtil()
  {
  }

  public static DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
  {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    return documentBuilderFactory.newDocumentBuilder();
  }

  public static Element loadRootElement(DocumentBuilder documentBuilder, File file) throws Exception
  {
    Document document = loadDocument(documentBuilder, file);
    return document.getDocumentElement();
  }

  public static Document loadDocument(DocumentBuilder documentBuilder, File file) throws SAXException, IOException
  {
    return documentBuilder.parse(file);
  }
}
