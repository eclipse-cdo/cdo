/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article.impl;

import org.eclipse.emf.cdo.releng.doc.article.ArticlePackage;
import org.eclipse.emf.cdo.releng.doc.article.XmlFormatter;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.sun.javadoc.SeeTag;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Xml Formatter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.impl.XmlFormatterImpl#getFile <em>File</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class XmlFormatterImpl extends FormatterImpl implements XmlFormatter
{
  private static final String CALLOUT = "callout";

  private static final String CALLOUT_MARKER = "<!--" + CALLOUT + "-->";

  private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

  /**
   * The default value of the '{@link #getFile() <em>File</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getFile()
   * @generated
   * @ordered
   */
  protected static final File FILE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFile() <em>File</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getFile()
   * @generated
   * @ordered
   */
  protected File file = FILE_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected XmlFormatterImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ArticlePackage.Literals.XML_FORMATTER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public File getFile()
  {
    return file;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setFile(File newFile)
  {
    File oldFile = file;
    file = newFile;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, ArticlePackage.XML_FORMATTER__FILE, oldFile, file));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ArticlePackage.XML_FORMATTER__FILE:
      return getFile();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ArticlePackage.XML_FORMATTER__FILE:
      setFile((File)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.XML_FORMATTER__FILE:
      setFile(FILE_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ArticlePackage.XML_FORMATTER__FILE:
      return FILE_EDEFAULT == null ? file != null : !FILE_EDEFAULT.equals(file);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (file: ");
    result.append(file);
    result.append(')');
    return result.toString();
  }

  public String getDefaultTitle(SeeTag embedderTag)
  {
    return file.getName();
  }

  public String getTopLeftEditorIcon(String imagePath)
  {
    return imagePath + "editor-top-left-xml.png";
  }

  public String getSnippetHtml(PrintWriter out, String id, String title)
  {
    XmlHandler handler = new XmlHandler();

    try
    {
      SAXParser parser = FACTORY.newSAXParser();
      parser.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
      parser.parse(file, handler);
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }

    return handler.getHtml();
  }

  public String getCalloutMarker()
  {
    return CALLOUT_MARKER;
  }

  /**
   * @author Eike Stepper
   */
  private static final class XmlHandler extends DefaultHandler implements LexicalHandler
  {
    StringBuilder builder = new StringBuilder();

    StringBuilder element;

    public XmlHandler()
    {
    }

    public String getHtml()
    {
      return builder.toString();
    }

    @Override
    public void startDocument() throws SAXException
    {
      builder
          .append("<font color=\"#0000e1\">&lt;?xml version=<font color=\"#000080\">'1.0'</font> encoding=<font color=\"#000080\">'UTF-8'</font>?&gt;</font><br/>\n");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      appendElement(false);

      element = new StringBuilder();
      element.append("&lt;");
      element.append(qName);

      for (int i = 0; i < attributes.getLength(); i++)
      {
        String name = attributes.getQName(i);
        String value = attributes.getValue(i);

        element.append("&nbsp;");
        element.append(name);

        element.append("<font color=\"#000080\">");
        element.append("=\"");
        element.append(value);
        element.append("\"");
        element.append("</font>");
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      if (!appendElement(true))
      {
        builder.append("<font color=\"#0000e1\">");
        builder.append("&lt;/");
        builder.append(qName);
        builder.append("&gt;");
        builder.append("</font>");
      }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException
    {
      appendElement(false);
      appendCharacters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
    {
      appendElement(false);
      appendCharacters(ch, start, length);
    }

    public void comment(char[] ch, int start, int length) throws SAXException
    {
      appendElement(false);

      String comment = new String(ch, start, length).trim();
      if (comment.equals(CALLOUT))
      {
        builder.append(CALLOUT_MARKER);
      }
      else
      {
        builder.append("<font color=\"#3f7f5f\">&lt;!--");
        appendCharacters(ch, start, length);
        builder.append("--&gt;</font>");
      }
    }

    private boolean appendElement(boolean end)
    {
      if (element != null)
      {
        builder.append("<font color=\"#0000e1\">");
        builder.append(element.toString());

        if (end)
        {
          builder.append("/");
        }

        builder.append("&gt;");
        builder.append("</font>");

        element = null;
        return true;
      }

      return false;
    }

    private void appendCharacters(char[] ch, int start, int length)
    {
      for (int i = start; length > 0; i++, length--)
      {
        String c = convert(ch[i]);
        builder.append(c);
      }
    }

    private String convert(char c)
    {
      switch (c)
      {
      case '&':
        return "&amp;";

      case '<':
        return "&lt;";

      case '>':
        return "&gt;";

      case ' ':
        return "&nbsp;";

      case '\n':
        return "<br/>\n";
      }

      return new String(new char[] { c });
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException
    {
    }

    public void endDTD() throws SAXException
    {
    }

    public void startEntity(String name) throws SAXException
    {
    }

    public void endEntity(String name) throws SAXException
    {
    }

    public void startCDATA() throws SAXException
    {
    }

    public void endCDATA() throws SAXException
    {
    }
  }

} // XmlFormatterImpl
