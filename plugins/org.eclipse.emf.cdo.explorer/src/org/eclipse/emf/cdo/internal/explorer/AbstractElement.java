/*
 * Copyright (c) 2015, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.util.CDONameProvider;
import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class AbstractElement extends Notifier implements CDOExplorerElement, Adapter.Internal, IManagedContainerProvider
{
  public static final String ILLEGAL_LABEL_CHARACTERS = "/\\:;,";

  public static final String PROP_TYPE = "type";

  public static final String PROP_LABEL = "label";

  public static final String PROP_DESCRIPTION = "description";

  public static final String PROP_SERVER_BROWSER_PORT = "serverBrowserPort";

  private org.eclipse.emf.common.notify.Notifier target;

  private File folder;

  private String id;

  private String type;

  private String label;

  private String description;

  private int serverBrowserPort;

  public AbstractElement()
  {
  }

  public abstract AbstractManager<?> getManager();

  @Override
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  public final File getFolder()
  {
    return folder;
  }

  @Override
  public final String getID()
  {
    return id;
  }

  @Override
  public final String getType()
  {
    return type;
  }

  @Override
  public final String getLabel()
  {
    return label;
  }

  @Override
  public final void setLabel(String label)
  {
    if (!ObjectUtil.equals(this.label, label))
    {
      this.label = label;
      save();

      fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.PARENT);
    }
  }

  @Override
  public final String getDescription()
  {
    return description;
  }

  @Override
  public final void setDescription(String description)
  {
    if (!ObjectUtil.equals(this.description, description))
    {
      this.description = description;
      save();

      fireElementChangedEvent(ElementsChangedEvent.StructuralImpact.NONE);
    }
  }

  public final int getServerBrowserPort()
  {
    return serverBrowserPort;
  }

  public final void setServerBrowserPort(int serverBrowserPort)
  {
    if (this.serverBrowserPort != serverBrowserPort)
    {
      this.serverBrowserPort = serverBrowserPort;
      save();
    }
  }

  protected final void fireElementChangedEvent(ElementsChangedEvent.StructuralImpact structuralImpact)
  {
    AbstractManager<?> manager = getManager();
    if (manager != null)
    {
      manager.fireElementChangedEvent(structuralImpact, this);
    }
  }

  public String validateLabel(String label)
  {
    if (StringUtil.isEmpty(label.trim()))
    {
      return "Label is empty.";
    }

    if (ObjectUtil.equals(label, getLabel()))
    {
      return null;
    }

    AbstractManager<?> manager = getManager();
    if (manager != null && manager.getElementByLabel(label) != null)
    {
      return "Label is not unique.";
    }

    return null;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    if (adapter == CDONameProvider.class)
    {
      return new CDONameProvider()
      {
        @Override
        public String getName()
        {
          return label;
        }
      };
    }

    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public void notifyChanged(Notification notification)
  {
  }

  @Override
  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return target;
  }

  @Override
  public void setTarget(org.eclipse.emf.common.notify.Notifier newTarget)
  {
    target = newTarget;
  }

  @Override
  public void unsetTarget(org.eclipse.emf.common.notify.Notifier oldTarget)
  {
    if (target == oldTarget)
    {
      setTarget(null);
    }
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (obj.getClass() == getClass())
    {
      AbstractElement that = (AbstractElement)obj;
      return id.equals(that.getID());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getClass().hashCode() ^ id.hashCode();
  }

  @Override
  public int compareTo(CDOExplorerElement o)
  {
    String label1 = StringUtil.safe(label).toLowerCase();
    String label2 = StringUtil.safe(o.getLabel()).toLowerCase();
    return label1.compareTo(label2);
  }

  /**
   * @since 4.5
   */
  @Override
  public File getStateFolder(String path)
  {
    return getStateFolder(path, true);
  }

  @Override
  public File getStateFolder(String path, boolean createOnDemand)
  {
    File stateFolder = new File(folder, path);
    if (!stateFolder.exists())
    {
      if (!createOnDemand)
      {
        return null;
      }

      stateFolder.mkdirs();
    }

    return stateFolder;
  }

  @Override
  public void delete(boolean deleteContents)
  {
    if (deleteContents)
    {
      IOUtil.delete(folder);
    }
    else
    {
      String propertiesFileName = getManager().getPropertiesFileName();

      File from = new File(folder, propertiesFileName);
      File dest = new File(from.getParentFile(), from.getName() + ".removed");
      from.renameTo(dest);
    }
  }

  public void save()
  {
    String propertiesFileName = getManager().getPropertiesFileName();
    Properties properties = getProperties();
    saveProperties(propertiesFileName, properties);
  }

  @Override
  public Properties getProperties()
  {
    Properties properties = new Properties();
    collectProperties(properties);
    return properties;
  }

  protected final void saveProperties(String fileName, Properties properties)
  {
    AbstractManager.saveProperties(folder, fileName, properties, getClass().getSimpleName() + " " + fileName);
  }

  protected void init(File folder, String type, Properties properties)
  {
    this.folder = folder;
    id = folder.getName();

    this.type = type;
    label = properties.getProperty(PROP_LABEL);
    description = properties.getProperty(PROP_DESCRIPTION);

    String property = properties.getProperty(PROP_SERVER_BROWSER_PORT);
    if (property != null)
    {
      serverBrowserPort = Integer.parseInt(property);
    }
  }

  protected void collectProperties(Properties properties)
  {
    properties.setProperty(PROP_TYPE, type);
    properties.setProperty(PROP_LABEL, label);
    if (!StringUtil.isEmpty(description))
    {
      properties.setProperty(PROP_DESCRIPTION, description);
    }

    if (serverBrowserPort != 0)
    {
      properties.setProperty(PROP_SERVER_BROWSER_PORT, Integer.toString(serverBrowserPort));
    }
  }

  public static AbstractElement[] collect(Collection<?> c)
  {
    List<AbstractElement> result = new ArrayList<>();
    for (Object object : c)
    {
      if (object instanceof AbstractElement)
      {
        AbstractElement element = (AbstractElement)object;
        result.add(element);
      }
    }

    return result.toArray(new AbstractElement[result.size()]);
  }
}
