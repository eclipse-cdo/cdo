package org.eclipse.net4j.util;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import java.io.OutputStream;
import java.util.LinkedList;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class XMLStack
{
  private static final AttributesImpl NO_ATTRIBUTES = new AttributesImpl();

  private TransformerHandler xmlHandler;

  private char[] newLine;

  private char[] indentation;

  private LinkedList<Element> stack = new LinkedList<Element>();

  private Element element;

  public XMLStack(OutputStream out) throws TransformerConfigurationException, SAXException
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

  public void setNewLine(String newLine)
  {
    this.newLine = newLine.toCharArray();
  }

  public void setIndentation(String indentation)
  {
    this.indentation = indentation.toCharArray();
  }

  public XMLStack element(String name) throws SAXException
  {
    flush();
    element = new Element(name);
    return this;
  }

  public XMLStack attribute(String name, Object value) throws SAXException
  {
    if (value != null)
    {
      return attributeOrNull(name, value);
    }

    return this;
  }

  public XMLStack attributeOrNull(String name, Object value) throws SAXException
  {
    checkElement();
    element.addAttribute(name, value);
    return this;
  }

  public XMLStack push() throws SAXException
  {
    newLine();
    element.start();

    stack.add(element);
    element = null;
    return this;
  }

  public XMLStack pop() throws SAXException
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
    for (int i = 0; i < stack.size(); i++)
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
