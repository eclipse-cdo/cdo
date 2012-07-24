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

import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;

import org.osgi.framework.Version;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Element
{
  private Element.Type type;

  private String name;

  private Version version;

  private List<Element> children = new ArrayList<Element>();

  private Set<Element> allChildren;

  public Element(Element.Type type, String name, Version version)
  {
    this.type = type;
    this.name = name;
    this.version = VersionUtil.normalize(version);
    resolveVersion();
  }

  public Element(Element.Type type, String name, String version)
  {
    this(type, name, new Version(version));
  }

  public Element.Type getType()
  {
    return type;
  }

  public String getTag()
  {
    return type == Type.PLUGIN ? Release.PLUGIN_TAG : Release.FEATURE_TAG;
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

  public Set<Element> getAllChildren(ElementResolver resolver)
  {
    if (allChildren == null)
    {
      allChildren = new HashSet<Element>();
      for (Element child : children)
      {
        recurseChildren(resolver, child);
      }
    }

    return allChildren;
  }

  private void recurseChildren(ElementResolver resolver, Element element)
  {
    if (!allChildren.contains(element))
    {
      Element topElement = resolver.resolveElement(element);
      if (topElement == null)
      {
        allChildren.add(element);
        return;
      }

      allChildren.add(topElement);

      for (Element child : topElement.getChildren())
      {
        recurseChildren(resolver, child);
      }
    }
  }

  public Element getChild(ElementResolver resolver, Element key)
  {
    Set<Element> allChildren = getAllChildren(resolver);
    for (Element child : allChildren)
    {
      if (child.equals(key))
      {
        return child;
      }
    }

    return null;
  }

  @Override
  public String toString()
  {
    return "Element [type=" + type + ", name=" + name + ", version=" + version + "]";
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
