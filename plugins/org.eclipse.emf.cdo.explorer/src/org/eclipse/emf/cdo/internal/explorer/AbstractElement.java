/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.internal.explorer.bundle.OM;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public abstract class AbstractElement extends Notifier implements CDOExplorerElement, Adapter.Internal
{
  public static final String PROP_ID = "id";

  public static final String PROP_TYPE = "type";

  public static final String PROP_LABEL = "label";

  private org.eclipse.emf.common.notify.Notifier target;

  private File folder;

  private String id;

  private String type;

  private String label;

  public AbstractElement()
  {
  }

  public final File getFolder()
  {
    return folder;
  }

  public final String getID()
  {
    return id;
  }

  public final String getType()
  {
    return type;
  }

  public final String getLabel()
  {
    return label;
  }

  public final void setLabel(String label)
  {
    this.label = label;
    save();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  public void notifyChanged(Notification notification)
  {
  }

  public org.eclipse.emf.common.notify.Notifier getTarget()
  {
    return target;
  }

  public void setTarget(org.eclipse.emf.common.notify.Notifier newTarget)
  {
    target = newTarget;
  }

  public void unsetTarget(org.eclipse.emf.common.notify.Notifier oldTarget)
  {
    if (target == oldTarget)
    {
      setTarget(null);
    }
  }

  public boolean isAdapterForType(Object type)
  {
    return false;
  }

  public void save()
  {
    folder.mkdirs();

    Properties properties = new Properties();
    collectProperties(properties);

    OutputStream out = null;

    try
    {
      File file = new File(folder, ".properties");
      out = new FileOutputStream(file);

      properties.store(out, getClass().getSimpleName());
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

  protected void init(File folder, String type, Properties properties)
  {
    this.folder = folder;
    id = folder.getName();

    this.type = type;
    label = properties.getProperty(PROP_LABEL);
  }

  protected void collectProperties(Properties properties)
  {
    properties.put("type", type);
    properties.put("label", label);
  }
}
