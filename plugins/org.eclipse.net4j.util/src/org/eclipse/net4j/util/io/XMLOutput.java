/*
 * Copyright (c) 2010-2012, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.HexUtil;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class XMLOutput
{
  private static final AttributesImpl NO_ATTRIBUTES = new AttributesImpl();

  private TransformerHandler xmlHandler;

  private char[] newLine;

  private char[] indentation;

  private LinkedList<Element> stack = new LinkedList<>();

  private Element element;

  public XMLOutput(OutputStream out) throws TransformerConfigurationException, SAXException
  {
    setNewLine("\n");
    setIndentation("  ");
    SAXTransformerFactory factory = (SAXTransformerFactory)SAXTransformerFactory.newInstance();

    xmlHandler = factory.newTransformerHandler();

    Transformer transformer = xmlHandler.getTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    xmlHandler.setResult(new StreamResult(out));
    xmlHandler.startDocument();
  }

  /**
   * @since 3.20
   */
  public String getNewLine()
  {
    return new String(newLine);
  }

  public void setNewLine(String newLine)
  {
    this.newLine = newLine.toCharArray();
  }

  /**
   * @since 3.20
   */
  public String getIndentation()
  {
    return new String(indentation);
  }

  public void setIndentation(String indentation)
  {
    this.indentation = indentation.toCharArray();
  }

  public XMLOutput element(String name) throws SAXException
  {
    flush();
    element = new Element(name);
    return this;
  }

  public XMLOutput attribute(String name, Object value) throws SAXException
  {
    if (value != null)
    {
      return attributeOrNull(name, value);
    }

    return this;
  }

  public XMLOutput attributeOrNull(String name, Object value) throws SAXException
  {
    checkElement();
    element.addAttribute(name, value);
    return this;
  }

  public Writer characters() throws SAXException
  {
    return characters(true);
  }

  /**
   * @since 3.20
   */
  public Writer characters(boolean cdata) throws SAXException
  {
    checkElement();
    newLine();
    element.start();

    if (cdata)
    {
      xmlHandler.startCDATA();
    }

    return new Writer()
    {
      @Override
      public void write(char[] cbuf, int off, int len) throws IOException
      {
        try
        {
          xmlHandler.characters(cbuf, off, len);
        }
        catch (SAXException ex)
        {
          throw new IOException(ex);
        }
      }

      @Override
      public void flush() throws IOException
      {
        // Do nothing
      }

      @Override
      public void close() throws IOException
      {
        try
        {
          if (cdata)
          {
            xmlHandler.endCDATA();
          }

          element.end();
        }
        catch (SAXException ex)
        {
          throw new IOException(ex);
        }
        finally
        {
          element = null;
        }
      }
    };
  }

  public OutputStream bytes() throws SAXException
  {
    checkElement();
    newLine();
    element.start();
    xmlHandler.startCDATA();

    return new OutputStream()
    {
      @Override
      public void write(byte[] b, int off, int len) throws IOException
      {
        try
        {
          char[] cbuf = HexUtil.bytesToHex(b, off, len).toCharArray();
          xmlHandler.characters(cbuf, 0, cbuf.length);
        }
        catch (SAXException ex)
        {
          throw new IOException(ex);
        }
      }

      @Override
      public void write(int i) throws IOException
      {
        byte b = (byte)((i & 0xff) + Byte.MIN_VALUE);
        byte[] bs = { b };
        write(bs, 0, 1);
      }

      @Override
      public void close() throws IOException
      {
        try
        {
          xmlHandler.endCDATA();
          element.end();
        }
        catch (SAXException ex)
        {
          throw new IOException(ex);
        }
        finally
        {
          element = null;
        }
      }
    };
  }

  /**
   * @since 3.20
   */
  public String currentElement()
  {
    if (element == null)
    {
      return null;
    }

    return element.name;
  }

  /**
   * @since 3.20
   */
  public Map<String, String> currentAttributes()
  {
    if (element == null)
    {
      return null;
    }

    Map<String, String> map = new HashMap<>();

    int length = element.attributes.getLength();
    for (int i = 0; i < length; i++)
    {
      String name = element.attributes.getQName(i);
      String value = element.attributes.getValue(i);
      map.put(name, value);
    }

    return map;
  }

  /**
   * @since 3.20
   */
  public int level()
  {
    return stack.size();
  }

  public XMLOutput push() throws SAXException
  {
    newLine();
    element.start();

    stack.add(element);
    element = null;
    return this;
  }

  public XMLOutput pop() throws SAXException
  {
    flush();
    Element element = stack.removeLast();

    if (element.hasChildren())
    {
      newLine();
    }

    element.end();
    return this;
  }

  public void done() throws SAXException
  {
    while (!stack.isEmpty())
    {
      pop();
    }

    xmlHandler.endDocument();
  }

  private void flush() throws SAXException
  {
    if (element != null)
    {
      newLine();
      element.start();
      element.end();
      element = null;
    }
  }

  private void newLine() throws SAXException
  {
    xmlHandler.ignorableWhitespace(newLine, 0, newLine.length);

    int level = level();
    for (int i = 0; i < level; i++)
    {
      xmlHandler.ignorableWhitespace(indentation, 0, indentation.length);
    }
  }

  private void checkElement()
  {
    if (element == null)
    {
      throw new IllegalStateException("No element");
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Element
  {
    private String name;

    private AttributesImpl attributes;

    private boolean children;

    public Element(String name)
    {
      this.name = name;
    }

    public boolean hasChildren()
    {
      return children;
    }

    public void addChild()
    {
      children = true;
    }

    public void addAttribute(String name, Object value)
    {
      if (attributes == null)
      {
        attributes = new AttributesImpl();
      }

      if (value == null)
      {
        value = "";
      }

      attributes.addAttribute("", "", name, "", value.toString());
    }

    public void start() throws SAXException
    {
      if (!stack.isEmpty())
      {
        stack.getLast().addChild();
      }

      xmlHandler.startElement("", "", name, attributes == null ? NO_ATTRIBUTES : attributes);
    }

    public void end() throws SAXException
    {
      xmlHandler.endElement("", "", name);
    }
  }
}
