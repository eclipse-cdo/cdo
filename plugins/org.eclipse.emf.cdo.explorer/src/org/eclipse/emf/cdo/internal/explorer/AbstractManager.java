/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class AbstractManager<T extends CDOExplorerElement> extends SetContainer<T> implements
    CDOExplorerManager<T>
{
  private final File folder;

  private final Map<String, T> elementMap = new HashMap<String, T>();

  public AbstractManager(Class<T> componentType, File folder)
  {
    super(componentType);

    this.folder = folder;
    folder.mkdirs();

    File[] children = folder.listFiles();
    if (children != null)
    {
      for (File child : children)
      {
        if (child.isDirectory())
        {
          readElement(child);
        }
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public final File getFolder()
  {
    return folder;
  }

  public T getElement(String id)
  {
    return elementMap.get(id);
  }

  public T newElement(Properties properties)
  {
    int i = 0;
    for (;;)
    {
      String id = Integer.toString(++i);
      if (elementMap.containsKey(id))
      {
        continue;
      }

      File child = new File(folder, id);
      if (child.exists())
      {
        continue;
      }

      T element = addElement(child, properties);
      ((AbstractElement)element).save();
      return element;
    }
  }

  private void readElement(File folder)
  {
    FileInputStream in = null;

    try
    {
      File file = new File(folder, ".properties");
      in = new FileInputStream(file);

      Properties properties = new Properties();
      properties.load(in);

      addElement(folder, properties);
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  private T addElement(File folder, Properties properties)
  {
    String type = properties.getProperty("type");

    T element = createElement(type);
    ((AbstractElement)element).init(folder, type, properties);

    addElement(element);
    elementMap.put(element.getID(), element);
    return element;
  }

  protected abstract T createElement(String type);
}
