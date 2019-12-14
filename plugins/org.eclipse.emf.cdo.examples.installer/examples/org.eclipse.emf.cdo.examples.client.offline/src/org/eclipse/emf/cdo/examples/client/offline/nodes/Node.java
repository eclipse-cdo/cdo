/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.client.offline.nodes;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public final class Node implements IElement, Comparable<Node>
{
  private final NodeType type;

  private final Properties settings;

  private final Map<Class<?>, Object> objects = new HashMap<>();

  public Node(NodeType type, Properties settings)
  {
    this.type = type;
    this.settings = settings;
  }

  public Node(NodeType type)
  {
    this(type, copySettings(type));
  }

  public NodeManager getManager()
  {
    return type.getManager();
  }

  public File getFolder()
  {
    return new File(type.getManager().getRoot(), getName());
  }

  public NodeType getType()
  {
    return type;
  }

  public String getName()
  {
    return settings.getProperty(NodeType.NAME_PROPERTY);
  }

  @Override
  public Properties getSettings()
  {
    return settings;
  }

  public String getSetting(String key)
  {
    return settings.getProperty(key);
  }

  @Override
  public void showSettings()
  {
    type.showSettings(this);
  }

  @Override
  public Image getImage()
  {
    return type.getInstanceImage();
  }

  @Override
  public Composite getDetailsControl()
  {
    return type.getDetailsControl(this);
  }

  public Map<Class<?>, Object> getObjects()
  {
    return objects;
  }

  @SuppressWarnings("unchecked")
  public <T> T getObject(Class<T> type)
  {
    return (T)objects.get(type);
  }

  @SuppressWarnings("unchecked")
  public <T> T setObject(Class<T> type, T object)
  {
    if (object == null)
    {
      return (T)objects.remove(type);
    }

    return (T)objects.put(type, object);
  }

  public void start()
  {
    type.start(this);
  }

  public void stop()
  {
    type.stop(this);
  }

  @Override
  public String toString()
  {
    return getName();
  }

  @Override
  public int compareTo(Node o)
  {
    return getName().compareTo(o.getName());
  }

  private static Properties copySettings(NodeType type)
  {
    Properties settings = new Properties();
    settings.putAll(type.getSettings());
    return settings;
  }
}
