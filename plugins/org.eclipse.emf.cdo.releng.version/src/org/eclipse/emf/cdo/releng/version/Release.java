/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.version;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.osgi.framework.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Eike Stepper
 */
public class Release implements ElementResolver
{
  public static final String RELEASE_TAG = "release";

  public static final String FEATURE_TAG = "feature";

  public static final String PLUGIN_TAG = "plugin";

  public static final String TAG_ATTRIBUTE = "tag";

  public static final String INTEGRATION_ATTRIBUTE = "integration";

  public static final String NAME_ATTRIBUTE = "name";

  public static final String VERSION_ATTRIBUTE = "version";

  private static final String INDENT = "\t";

  private IFile file;

  private boolean integration;

  private String tag;

  private Map<Element, Element> elements = new HashMap<Element, Element>();

  public Release(IFile file)
  {
    this.file = file;
    integration = true;
    initTag();
  }

  Release(SAXParser parser, IFile file) throws CoreException, IOException, SAXException
  {
    this.file = file;

    XMLHandler handler = new XMLHandler();
    InputStream contents = null;

    try
    {
      contents = file.getContents();
      parser.parse(contents, handler);
      initTag();
    }
    finally
    {
      if (contents != null)
      {
        try
        {
          contents.close();
        }
        catch (Exception ex)
        {
          Activator.log(ex);
        }
      }
    }
  }

  private void initTag()
  {
    if (tag == null)
    {
      tag = UUID.randomUUID().toString();
    }
  }

  public IFile getFile()
  {
    return file;
  }

  public String getTag()
  {
    return tag;
  }

  public boolean isIntegration()
  {
    return integration;
  }

  public Map<Element, Element> getElements()
  {
    return elements;
  }

  public Element resolveElement(Element key)
  {
    return elements.get(key);
  }

  public int getSize()
  {
    return elements.size();
  }

  public void write() throws IOException, CoreException
  {
    StringBuilder builder = new StringBuilder();
    writeRelease(builder);

    String xml = builder.toString();
    ByteArrayInputStream contents = new ByteArrayInputStream(xml.getBytes("UTF-8"));
    if (file.exists())
    {
      file.setContents(contents, true, true, new NullProgressMonitor());
    }
    else
    {
      file.create(contents, true, new NullProgressMonitor());
    }
  }

  private void writeRelease(StringBuilder builder)
  {
    builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    builder.append("<" + RELEASE_TAG + " " + INTEGRATION_ATTRIBUTE + "=\"+integration+\" " + TAG_ATTRIBUTE + "=\""
        + tag + "\">\n");

    List<Element> list = new ArrayList<Element>(elements.keySet());
    Collections.sort(list, new Comparator<Element>()
    {
      public int compare(Element o1, Element o2)
      {
        int result = o1.getType().compareTo(o2.getType());
        if (result == 0)
        {
          result = o1.getName().compareTo(o2.getName());
        }

        if (result == 0)
        {
          result = o1.getVersion().compareTo(o2.getVersion());
        }

        return result;
      }
    });

    for (Element element : list)
    {
      writeElement(builder, element, INDENT);
    }

    builder.append("</" + RELEASE_TAG + ">\n");
  }

  private void writeElement(StringBuilder builder, Element element, String indent)
  {
    String name = element.getName();
    Version version = element.getVersion();

    builder.append(indent + "<" + element.getTag() + " " + NAME_ATTRIBUTE + "=\"" + name + "\" " + VERSION_ATTRIBUTE
        + "=\"" + version + "\"");

    List<Element> content = element.getChildren();
    if (content.isEmpty())
    {
      builder.append("/");
      writeElementEnd(builder, element);
    }
    else
    {
      writeElementEnd(builder, element);

      for (Element child : content)
      {
        writeElement(builder, child, indent + INDENT);
      }

      builder.append(indent + "</" + element.getTag() + ">\n");
    }
  }

  private void writeElementEnd(StringBuilder builder, Element element)
  {
    builder.append(">");
    if (element.getVersion().equals(Version.emptyVersion))
    {
      builder.append(" <!-- UNRESOLVED -->");
    }

    builder.append("\n");
  }

  /**
   * @author Eike Stepper
   */
  public class XMLHandler extends DefaultHandler
  {
    private Element parent;

    private int level;

    public XMLHandler()
    {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if (RELEASE_TAG.equalsIgnoreCase(qName))
      {
        tag = getString(attributes, TAG_ATTRIBUTE);
        integration = getBoolean(attributes, INTEGRATION_ATTRIBUTE);
      }
      else if (FEATURE_TAG.equalsIgnoreCase(qName))
      {
        Element element = createElement(Element.Type.FEATURE, attributes);
        if (++level == 1)
        {
          elements.put(element, element);
          parent = element;
        }
        else
        {
          parent.getChildren().add(element);
        }
      }
      else if (PLUGIN_TAG.equalsIgnoreCase(qName))
      {
        Element element = createElement(Element.Type.PLUGIN, attributes);
        if (++level == 1)
        {
          elements.put(element, element);
          parent = element;
        }
        else
        {
          parent.getChildren().add(element);
        }
      }
    }

    private Element createElement(Element.Type type, Attributes attributes) throws SAXException
    {
      String name = getString(attributes, NAME_ATTRIBUTE);
      Version version = new Version(getString(attributes, VERSION_ATTRIBUTE));

      return new Element(type, name, version);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      if (FEATURE_TAG.equalsIgnoreCase(qName) || PLUGIN_TAG.equalsIgnoreCase(qName))
      {
        --level;
      }
    }

    private String getString(Attributes attributes, String name) throws SAXException
    {
      String value = attributes.getValue(name);
      if (value != null)
      {
        return value;
      }

      throw new SAXException("Illegal value for " + name);
    }

    private boolean getBoolean(Attributes attributes, String name) throws SAXException
    {
      String value = attributes.getValue(name);
      if ("false".equalsIgnoreCase(value))
      {
        return false;
      }

      if ("true".equalsIgnoreCase(value))
      {
        return true;
      }

      throw new SAXException("Illegal value for " + name);
    }

    @Override
    public void error(SAXParseException exception) throws SAXException
    {
      addMarker(exception, IMarker.SEVERITY_ERROR);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException
    {
      addMarker(exception, IMarker.SEVERITY_ERROR);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException
    {
      addMarker(exception, IMarker.SEVERITY_WARNING);
    }

    private void addMarker(SAXParseException e, int severity)
    {
      try
      {
        Markers.addMarker(file, e.getMessage(), severity, e.getLineNumber());
      }
      catch (Exception ex)
      {
        Activator.log(ex);
      }
    }
  }
}
