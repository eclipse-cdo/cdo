/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import org.osgi.framework.Version;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class Release
{
  private IFile file;

  private String tag;

  private boolean integration;

  private String repository;

  private Map<String, Element> elements = new HashMap<String, Element>();

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

  public String getRepository()
  {
    return repository;
  }

  public Map<String, Element> getElements()
  {
    return Collections.unmodifiableMap(elements);
  }

  public int getSize()
  {
    return elements.size();
  }

  public static Version normalizeVersion(Version version)
  {
    return new Version(version.getMajor(), version.getMinor(), version.getMicro());
  }

  /**
   * @author Eike Stepper
   */
  public static class Element
  {
    private String name;

    private Version version;

    private Type type;

    public Element(String name, Version version, Type type)
    {
      this.name = name;
      this.version = normalizeVersion(version);
      this.type = type;
    }

    public String getName()
    {
      return name;
    }

    public Version getVersion()
    {
      return version;
    }

    public Type getType()
    {
      return type;
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
  private final class XMLHandler extends DefaultHandler
  {
    public XMLHandler()
    {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if ("release".equalsIgnoreCase(qName))
      {
        tag = getString(attributes, "tag");
        integration = getBoolean(attributes, "integration");
        repository = getString(attributes, "repository");
      }
      else if ("element".equalsIgnoreCase(qName))
      {
        String name = getString(attributes, "name");
        Version version = new Version(getString(attributes, "version"));
        Type type = getType(attributes, "type");

        Element element = new Element(name, version, type);
        elements.put(name, element);
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
      String type = getString(attributes, name);
      if ("org.eclipse.update.feature".equals(type))
      {
        return Type.FEATURE;
      }

      if ("osgi.bundle".equals(type))
      {
        return Type.PLUGIN;
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
