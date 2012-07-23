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

import org.eclipse.emf.cdo.releng.version.Release.Element.Type;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

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

/**
 * @author Eike Stepper
 */
public class Release
{
  public static final String RELEASE_TAG = "release";

  public static final String ELEMENT_TAG = "element";

  public static final String INCLUDES_TAG = "includes";

  public static final String TAG_ATTRIBUTE = "tag";

  public static final String INTEGRATION_ATTRIBUTE = "integration";

  public static final String TYPE_ATTRIBUTE = "type";

  public static final String NAME_ATTRIBUTE = "name";

  public static final String VERSION_ATTRIBUTE = "version";

  private static final String INDENT = "\t";

  private IFile file;

  private String tag;

  private boolean integration;

  private Map<Element, Element> elements = new HashMap<Element, Element>();

  public Release(IFile file)
  {
    this.file = file;
    tag = "";
    integration = true;
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
    builder.append("<" + RELEASE_TAG + " " + TAG_ATTRIBUTE + "=\"\" " + INTEGRATION_ATTRIBUTE + "=\"" + integration
        + "\">\n");

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
      writeElement(builder, element, INDENT, ELEMENT_TAG);
    }

    builder.append("</" + RELEASE_TAG + ">\n");
  }

  private void writeElement(StringBuilder builder, Element element, String indent, String tag)
  {
    String type = element.getType().toString().toLowerCase();
    String name = element.getName();
    Version version = element.getVersion();

    builder.append(indent + "<" + tag + " " + TYPE_ATTRIBUTE + "=\"" + type + "\" " + NAME_ATTRIBUTE + "=\"" + name
        + "\" " + VERSION_ATTRIBUTE + "=\"" + version + "\"");

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
        writeElement(builder, child, indent + INDENT, INCLUDES_TAG);
      }

      builder.append(indent + "</" + tag + ">\n");
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
  public static class Element
  {
    private Type type;

    private String name;

    private Version version;

    private List<Element> children = new ArrayList<Element>();

    private List<Element> allChildren;

    public Element(Type type, String name, Version version)
    {
      this.type = type;
      this.name = name;
      this.version = VersionUtil.normalize(version);
      resolveVersion();
    }

    public Element(Type type, String name, String version)
    {
      this(type, name, new Version(version));
    }

    public Type getType()
    {
      return type;
    }

    public String getName()
    {
      return name;
    }

    public Version getVersion()
    {
      return version;
    }

    public List<Element> getChildren()
    {
      return children;
    }

    public List<Element> getAllChildren()
    {
      if (allChildren == null)
      {
        allChildren = new ArrayList<Element>();
        for (Element child : children)
        {
          recurseChildren(child);
        }
      }

      return allChildren;
    }

    private void recurseChildren(Element element)
    {
      allChildren.add(element);
      for (Element child : element.getChildren())
      {
        recurseChildren(child);
      }
    }

    public Element getChild(Element childElement)
    {
      List<Element> allChildren = getAllChildren();
      int index = allChildren.indexOf(childElement);
      if (index != -1)
      {
        return allChildren.get(index);
      }

      return null;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (name == null ? 0 : name.hashCode());
      result = prime * result + (getType() == null ? 0 : getType().hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (!(obj instanceof Element))
      {
        return false;
      }

      Element other = (Element)obj;
      if (name == null)
      {
        if (other.name != null)
        {
          return false;
        }
      }
      else if (!name.equals(other.name))
      {
        return false;
      }

      if (getType() != other.getType())
      {
        return false;
      }

      return true;
    }

    public boolean isUnresolved()
    {
      return version.equals(Version.emptyVersion);
    }

    private void resolveVersion()
    {
      if (isUnresolved())
      {
        Version resolvedVersion;
        if (type == Element.Type.PLUGIN)
        {
          resolvedVersion = getPluginVersion(name);
        }
        else
        {
          resolvedVersion = getFeatureVersion(name);
        }

        if (resolvedVersion != null)
        {
          version = resolvedVersion;
        }
      }
    }

    private Version getPluginVersion(String name)
    {
      IPluginModelBase pluginModel = PluginRegistry.findModel(name);
      if (pluginModel != null)
      {
        Version version = pluginModel.getBundleDescription().getVersion();
        return VersionUtil.normalize(version);
      }

      return null;
    }

    private Version getFeatureVersion(String name)
    {
      IModel componentModel = ReleaseManager.INSTANCE.getComponentModel(this);
      if (componentModel != null)
      {
        return VersionBuilder.getComponentVersion(componentModel);
      }

      return version;
    }

    /**
     * @author Eike Stepper
     */
    public static enum Type
    {
      FEATURE, PLUGIN
    }
  }

  /**
   * @author Eike Stepper
   */
  public class XMLHandler extends DefaultHandler
  {
    private Element parent;

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
      else if (ELEMENT_TAG.equalsIgnoreCase(qName))
      {
        parent = createElement(attributes);
        elements.put(parent, parent);
      }
      else if (INCLUDES_TAG.equalsIgnoreCase(qName))
      {
        Element child = createElement(attributes);
        parent.getChildren().add(child);
      }
    }

    private Element createElement(Attributes attributes) throws SAXException
    {
      Type type = getType(attributes, TYPE_ATTRIBUTE);
      String name = getString(attributes, NAME_ATTRIBUTE);
      Version version = new Version(getString(attributes, VERSION_ATTRIBUTE));

      return new Element(type, name, version);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
      if (ELEMENT_TAG.equalsIgnoreCase(qName))
      {
        parent = null;
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

    private Type getType(Attributes attributes, String name) throws SAXException
    {
      String type = getString(attributes, name).toUpperCase();
      return Type.valueOf(type);
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
