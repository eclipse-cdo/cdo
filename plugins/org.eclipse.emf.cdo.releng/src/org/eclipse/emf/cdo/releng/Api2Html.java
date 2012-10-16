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
package org.eclipse.emf.cdo.releng;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Eike Stepper
 */
public class Api2Html extends DefaultHandler
{
  private static final String ANNOTATION = "annotation";

  private static final String ENUM = "enum";

  private static final String INTERFACE = "interface";

  private static final String CLASS = "class";

  private static final String PLUS = "plus.gif";

  private static final String MINUS = "minus.gif";

  private int lastNodeID;

  private Category breaking = new Category("Breaking Changes");

  private Category compatible = new Category("Compatible Changes");

  private ClassLoader classLoader;

  private String buildQualifier;

  private File pluginsFolder;

  private File tpFolder;

  public Api2Html(File folder, String buildQualifier, File pluginsFolder, File tpFolder) throws Exception
  {
    this.buildQualifier = buildQualifier;
    this.pluginsFolder = pluginsFolder;
    this.tpFolder = tpFolder;

    File xmlFile = new File(folder, "api.xml");
    InputStream in = new FileInputStream(xmlFile);

    try
    {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(in, this);
    }
    finally
    {
      in.close();
    }

    File htmlFile = new File(folder, "api.html");
    generate(htmlFile);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
  {
    if ("delta".equalsIgnoreCase(qName))
    {
      try
      {
        String componentID = attributes.getValue("componentId");
        String typeName = attributes.getValue("type_name");
        String elementType = attributes.getValue("element_type");
        String kind = attributes.getValue("kind");
        String message = attributes.getValue("message");

        if (componentID == null || componentID.length() == 0)
        {
          System.out.println("No componentID: " + message);
          return;
        }

        if (typeName == null || typeName.length() == 0)
        {
          System.out.println("No typeName: " + message);
          return;
        }

        if (message.startsWith("The re-exported type"))
        {
          return;
        }

        String componentVersion = "";
        int pos = componentID.indexOf('(');
        if (pos != -1)
        {
          componentVersion = componentID.substring(pos + 1, componentID.length() - 1);
          componentID = componentID.substring(0, pos);

        }

        message = remove(message, typeName + ".");
        message = remove(message, " in an interface that is tagged with '@noimplement'");
        message = remove(message, " for interface " + typeName);
        message = remove(message, " for class " + typeName);
        message = remove(message, " to " + typeName);
        if (message.startsWith("The deprecation modifiers has"))
        {
          message = "The deprecation modifier has" + message.substring("The deprecation modifiers has".length());
        }

        Category category = "true".equals(attributes.getValue("compatible")) ? compatible : breaking;
        Map<String, Component> components = category.getComponents();

        Component component = components.get(componentID);
        if (component == null)
        {
          component = new Component(componentID);
          components.put(componentID, component);
        }

        component.setComponentVersion(componentVersion);

        Type type = component.getTypes().get(typeName);
        if (type == null)
        {
          type = new Type(typeName);
          component.getTypes().put(typeName, type);
        }

        type.setElementType(elementType);

        Change change = new Change(message, kind);
        type.getChanges().add(change);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }

  private String determineElementType(String typeName) throws MalformedURLException
  {
    if (classLoader == null)
    {
      classLoader = createClassLoader();
    }

    try
    {
      Class<?> c = classLoader.loadClass(typeName);
      if (c.isAnnotation())
      {
        return ANNOTATION;
      }

      if (c.isEnum())
      {
        return ENUM;
      }

      if (c.isInterface())
      {
        return INTERFACE;
      }
    }
    catch (Throwable ex)
    {
      System.err.println(ex.getMessage());
      return null;
    }

    return CLASS;
  }

  private ClassLoader createClassLoader() throws MalformedURLException
  {
    List<URL> urls = new ArrayList<URL>();
    for (File plugin : pluginsFolder.listFiles())
    {
      if (plugin.isDirectory())
      {
        File bin = new File(plugin, "bin");
        if (bin.isDirectory())
        {
          urls.add(bin.toURI().toURL());
        }
      }
    }

    for (File plugin : tpFolder.listFiles())
    {
      urls.add(plugin.toURI().toURL());
    }

    return new URLClassLoader(urls.toArray(new URL[urls.size()]));
  }

  private void generate(File htmlFile) throws Exception
  {
    PrintStream out = new PrintStream(htmlFile);

    try
    {
      out.println("<!DOCTYPE HTML>");
      out.println("<html>");
      out.println("<head>");
      out.println("<title>API Evolution Report for CDO " + buildQualifier + "</title>");
      out.println("<link rel=stylesheet type='text/css' href='api.css'>");
      out.println("<base href='http://www.eclipse.org/cdo/images/api/'>");
      out.println("<script type='text/javascript'>");
      out.println("  function toggle(id)");
      out.println("  {");
      out.println("    e = document.getElementById(id);");
      out.println("    e.style.display = (e.style.display == '' ? 'none' : '');");
      out.println("    img = document.getElementById('img_' + id);");
      out.println("    img.src = (e.style.display == 'none' ? '" + PLUS + "' : '" + MINUS + "');");
      out.println("  }");
      out.println("</script>");
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>API Evolution Report for CDO <a href='http://www.eclipse.org/cdo/downloads/#"
          + buildQualifier.replace('-', '_') + "'>" + buildQualifier + "</a></h1>");

      breaking.generate(out, "");
      out.println("<p/>");
      compatible.generate(out, "");

      out.println("</body>");
      out.println("</html>");
    }
    finally
    {
      out.close();
    }
  }

  private List<String> sortedKeys(Map<String, ?> map)
  {
    List<String> list = new ArrayList<String>(map.keySet());
    Collections.sort(list);
    return list;
  }

  private String remove(String string, String remove)
  {
    int pos = string.indexOf(remove);
    if (pos != -1)
    {
      string = string.substring(0, pos) + string.substring(pos + remove.length());
    }

    return string;
  }

  public static void main(String[] args) throws Exception
  {
    if (args.length == 0)
    {
      // Just for local testing!
      args = new String[] { "/develop", "R20120918-0947", "/develop/git/cdo/plugins",
          "/develop/ws/cdo/.buckminster/tp/plugins" };
    }

    new Api2Html(new File(args[0]), args[1], new File(args[2]), new File(args[3]));
  }

  /**
   * @author Eike Stepper
   */
  public static final class Version implements Comparable<Version>
  {
    private static final String SEPARATOR = ".";

    private int major = 0;

    private int minor = 0;

    private int micro = 0;

    public Version(String version)
    {
      StringTokenizer st = new StringTokenizer(version, SEPARATOR, true);
      major = Integer.parseInt(st.nextToken());

      if (st.hasMoreTokens())
      {
        st.nextToken();
        minor = Integer.parseInt(st.nextToken());

        if (st.hasMoreTokens())
        {
          st.nextToken();
          micro = Integer.parseInt(st.nextToken());
        }
      }
    }

    @Override
    public String toString()
    {
      return major + SEPARATOR + minor + SEPARATOR + micro;
    }

    public int compareTo(Version o)
    {
      if (o == this)
      {
        return 0;
      }

      int result = major - o.major;
      if (result != 0)
      {
        return result;
      }

      result = minor - o.minor;
      if (result != 0)
      {
        return result;
      }

      result = micro - o.micro;
      if (result != 0)
      {
        return result;
      }

      return 0;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class AbstractNode
  {
    private final String text;

    public AbstractNode(String text)
    {
      this.text = text;
    }

    public String getText()
    {
      return text.replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }

    public String getIcon()
    {
      return "";
    }

    public void generate(PrintStream out, String indent) throws Exception
    {
      String href = getHref();
      out.print(indent + getIcon() + " " + (href != null ? "<a href='" + href + "' target='_blank'>" : "") + getText()
          + (href != null ? "</a>" : ""));
    }

    protected String getHref()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  protected abstract class AbstractTreeNode extends AbstractNode
  {
    private int id;

    public AbstractTreeNode(String text)
    {
      super(text);
      id = ++lastNodeID;
    }

    @Override
    public void generate(PrintStream out, String indent) throws Exception
    {
      out.print(indent + "<div class='" + getClass().getSimpleName().toLowerCase()
          + "'><a href=\"javascript:toggle('node" + id + "')\"><img src='" + (isCollapsed() ? PLUS : MINUS)
          + "' id='img_node" + id + "'></a> ");
      super.generate(out, "");
      out.println("</div>");

      out.println(indent + "<div id=\"node" + id + "\" style='" + (isCollapsed() ? "display:none; " : "")
          + "margin-left:20px;'>");

      generateChildren(out, indent + "  ");

      out.println(indent + "</div>");
    }

    protected abstract void generateChildren(PrintStream out, String indent) throws Exception;

    protected boolean isCollapsed()
    {
      return true;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Category extends AbstractTreeNode
  {
    private final Map<String, Component> components = new HashMap<String, Component>();

    public Category(String text)
    {
      super(text);
    }

    public Map<String, Component> getComponents()
    {
      return components;
    }

    @Override
    protected void generateChildren(PrintStream out, String indent) throws Exception
    {
      for (String key : sortedKeys(components))
      {
        Component component = components.get(key);
        component.generate(out, indent);
      }
    }

    @Override
    protected boolean isCollapsed()
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Component extends AbstractTreeNode
  {
    private final Map<String, Type> types = new HashMap<String, Type>();

    private Version componentVersion;

    public Component(String text)
    {
      super(text);
    }

    public void setComponentVersion(String componentVersion)
    {
      Version version = new Version(componentVersion);
      if (this.componentVersion == null || this.componentVersion.compareTo(version) < 0)
      {
        this.componentVersion = version;
      }
    }

    @Override
    public String getText()
    {
      return super.getText() + "&nbsp;-&nbsp;" + componentVersion;
    }

    @Override
    public String getIcon()
    {
      return "<img src='plugin.gif'>";
    }

    public Map<String, Type> getTypes()
    {
      return types;
    }

    @Override
    protected void generateChildren(PrintStream out, String indent) throws Exception
    {
      for (String key : sortedKeys(types))
      {
        Type type = types.get(key);
        type.generate(out, indent);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Type extends AbstractTreeNode
  {
    private final List<Change> changes = new ArrayList<Change>();

    private String elementType;

    public Type(String text)
    {
      super(text);
    }

    @Override
    public String getText()
    {
      return super.getText().replace('$', '.');
    }

    @Override
    public String getIcon()
    {
      try
      {
        return "<img src='" + getElementType() + ".gif'>";
      }
      catch (Exception ex)
      {
        return super.getIcon();
      }
    }

    public List<Change> getChanges()
    {
      return changes;
    }

    public void setElementType(String elementType)
    {
      if ("CLASS_ELEMENT_TYPE".equals(elementType))
      {
        this.elementType = CLASS;
      }
      else if ("INTERFACE_ELEMENT_TYPE".equals(elementType))
      {
        this.elementType = INTERFACE;
      }
      else if ("ENUM_ELEMENT_TYPE".equals(elementType))
      {
        this.elementType = ENUM;
      }
      else if ("ANNOTATION_ELEMENT_TYPE".equals(elementType))
      {
        this.elementType = ANNOTATION;
      }
    }

    public String getElementType() throws Exception
    {
      if (elementType == null)
      {
        String typeName = super.getText();
        elementType = determineElementType(typeName);
      }

      return elementType;
    }

    @Override
    protected void generateChildren(PrintStream out, String indent) throws Exception
    {
      for (Change change : changes)
      {
        change.generate(out, indent);
      }
    }

    @Override
    protected String getHref()
    {
      return "http://download.eclipse.org/modeling/emf/cdo/drops/" + buildQualifier
          + "/help/org.eclipse.emf.cdo.doc/javadoc/" + super.getText().replace('.', '/').replace('$', '.') + ".html";
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class Change extends AbstractNode
  {
    private final String kind;

    public Change(String text, String kind)
    {
      super(text);
      if ("REMOVED".equals(kind))
      {
        this.kind = "removal";
      }
      else if ("ADDED".equals(kind))
      {
        this.kind = "addition";
      }
      else
      {
        this.kind = "change";
      }
    }

    @Override
    public String getIcon()
    {
      try
      {
        return "<img src='" + kind + ".gif'>";
      }
      catch (Exception ex)
      {
        return super.getIcon();
      }
    }

    @Override
    public void generate(PrintStream out, String indent) throws Exception
    {
      out.print(indent + "<img src='empty.gif'>");
      super.generate(out, "");
      out.println("<br>");
    }
  }
}
