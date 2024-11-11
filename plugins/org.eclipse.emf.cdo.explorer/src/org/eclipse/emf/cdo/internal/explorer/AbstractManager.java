/*
 * Copyright (c) 2015, 2016, 2019, 2020, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent.StructuralImpact;
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.ecore.EObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 */
public abstract class AbstractManager<T extends CDOExplorerElement> extends SetContainer<T> implements CDOExplorerManager<T>
{
  private static final Pattern LABEL_PATTERN = Pattern.compile("(.+?) *\\([0-9]+\\)");

  private static final boolean DEBUG = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.internal.explorer.AbstractManager.DEBUG");

  private final File folder;

  private final Map<String, T> elementMap = new HashMap<>();

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
          try
          {
            readElement(child);
          }
          catch (Exception ex)
          {
            OM.LOG.error(ex);
          }
        }
      }
    }
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public abstract String getPropertiesFileName();

  public final File getFolder()
  {
    return folder;
  }

  @Override
  public String getUniqueLabel(String label)
  {
    Set<String> names = new HashSet<>();

    for (T element : getElements())
    {
      names.add(element.getLabel());
    }

    Matcher matcher = LABEL_PATTERN.matcher(label);
    if (matcher.matches())
    {
      label = matcher.group(1);
    }

    for (int i = 1; i < Integer.MAX_VALUE; i++)
    {
      String name = i == 1 ? label : label + " (" + i + ")";
      if (!names.contains(name))
      {
        return name;
      }
    }

    throw new IllegalStateException("Too many elements");
  }

  public T getElement(String id)
  {
    return elementMap.get(id);
  }

  public T getElementByLabel(String label)
  {
    synchronized (this)
    {
      for (T element : getSet())
      {
        if (ObjectUtil.equals(element.getLabel(), label))
        {
          return element;
        }
      }
    }

    return null;
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

      T element = addElement(child, properties, true);
      return element;
    }
  }

  private void readElement(File folder)
  {
    Properties properties = loadProperties(folder, getPropertiesFileName());
    if (properties != null)
    {
      addElement(folder, properties, false);
    }
  }

  private T addElement(File folder, Properties properties, boolean newElement)
  {
    String type = properties.getProperty("type");

    T element = createElement(type);
    ((AbstractElement)element).init(folder, type, properties);

    if (newElement)
    {
      ((AbstractElement)element).save();
    }

    LifecycleUtil.activate(element);
    addElement(element);
    elementMap.put(element.getID(), element);
    return element;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (DEBUG)
    {
      IPluginContainer.INSTANCE.putElement("___" + getClass().getSimpleName(), "debug", null, this);
    }
  }

  protected abstract T createElement(String type);

  public void fireElementChangedEvent(StructuralImpact structuralImpact, Object changedElement)
  {
    if (changedElement instanceof CDOObject)
    {
      CDOObject cdoObject = (CDOObject)changedElement;
      changedElement = CDOUtil.getEObject(cdoObject);
    }

    Object[] objects = { changedElement };
    fireEvent(new ElementsChangedImpl(this, structuralImpact, objects));
  }

  public void fireElementsChangedEvent(Object[] objects)
  {
    for (int i = 0; i < objects.length; i++)
    {
      Object object = objects[i];
      if (object instanceof CDOObject)
      {
        CDOObject cdoObject = (CDOObject)object;
        EObject instance = CDOUtil.getEObject(cdoObject);
        if (instance != cdoObject)
        {
          objects[i] = instance;
        }
      }
    }

    fireEvent(new ElementsChangedImpl(this, StructuralImpact.NONE, objects));
  }

  public static void saveProperties(File folder, String fileName, Properties properties, String comment)
  {
    OutputStream out = null;

    try
    {
      folder.mkdirs();

      File file = new File(folder, fileName);
      out = new FileOutputStream(file);

      properties.store(out, comment);
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      IOUtil.close(out);
    }
  }

  public static Properties loadProperties(File folder, String fileName)
  {
    File file = new File(folder, fileName);
    if (file.isFile())
    {
      FileInputStream in = null;

      try
      {
        in = new FileInputStream(file);

        Properties properties = new Properties();
        properties.load(in);

        return properties;
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
      finally
      {
        IOUtil.close(in);
      }
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class ElementsChangedImpl extends Event implements ElementsChangedEvent
  {
    private static final long serialVersionUID = 1L;

    private final StructuralImpact structuralImpact;

    private final Object[] changedElements;

    public ElementsChangedImpl(CDOExplorerManager<?> manager, StructuralImpact structuralImpact, Object[] objects)
    {
      super(manager);
      changedElements = objects;
      this.structuralImpact = structuralImpact;
    }

    @Override
    public CDOExplorerManager<?> getSource()
    {
      return (CDOExplorerManager<?>)super.getSource();
    }

    @Override
    public StructuralImpact getStructuralImpact()
    {
      return structuralImpact;
    }

    @Override
    public final Object[] getChangedElements()
    {
      return changedElements;
    }
  }
}
