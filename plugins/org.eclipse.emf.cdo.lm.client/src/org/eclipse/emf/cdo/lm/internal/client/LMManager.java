/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager;
import org.eclipse.emf.cdo.explorer.CDOExplorerManager.ElementsChangedEvent;

import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public abstract class LMManager<EE extends CDOExplorerElement, EM extends CDOExplorerManager<EE>, D> extends Container<D>
{
  public static final String REPOSITORY_TYPE_SYSTEM = "System";

  public static final String REPOSITORY_TYPE_MODULE = "Module";

  public static final String PROP_REPOSITORY_TYPE = "repositoryType";

  public static final String PROP_SYSTEM_NAME = "systemName";

  public static final String PROP_MODULE_NAME = "moduleName";

  public static final String PROP_MODULE_TYPE = "moduleType";

  public static final String PROP_BASELINE_ID = "baselineID";

  private static final String STATE_FOLDER_NAME = "lm";

  private static final String STATE_FILE_NAME = "lm.properties";

  private static final boolean DEBUG = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.lm.internal.client.LMManager.DEBUG");

  private final IListener explorerManagerListener = new ContainerEventAdapter<EE>()
  {
    @Override
    protected void onAdded(IContainer<EE> container, EE explorerElement)
    {
      explorerElementAdded(explorerElement);
    }

    @Override
    protected void onRemoved(IContainer<EE> container, EE explorerElement)
    {
      explorerElementRemoved(explorerElement);
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      notifyExplorerElementEvent(event);
    }
  };

  private final EM explorerManager;

  private final Class<EE> explorerElementType;

  protected final Map<EE, D> descriptors = new HashMap<>();

  protected int count;

  protected LMManager(EM explorerManager, Class<EE> explorerElementType)
  {
    this.explorerManager = explorerManager;
    this.explorerElementType = explorerElementType;
  }

  public D getDescriptor(EE explorerElement)
  {
    synchronized (this)
    {
      return descriptors.get(explorerElement);
    }
  }

  public D[] getDescriptors()
  {
    List<D> result = new ArrayList<>(count);
    forEachDescriptor(d -> result.add(d));
    return result.toArray(newArray(result.size()));
  }

  public void forEachDescriptor(Consumer<D> consumer)
  {
    synchronized (this)
    {
      for (D descriptor : descriptors.values())
      {
        if (filterDescriptor(descriptor))
        {
          consumer.accept(descriptor);
        }
      }
    }
  }

  @Override
  public D[] getElements()
  {
    return getDescriptors();
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (this)
    {
      return count == 0;
    }
  }

  protected final void forEachExplorerElement(Consumer<EE> conumer)
  {
    for (EE explorerElement : explorerManager.getElements())
    {
      conumer.accept(explorerElement);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    synchronized (explorerManager)
    {
      forEachExplorerElement(explorerElement -> explorerElementAdded(explorerElement));
      explorerManager.addListener(explorerManagerListener);
    }

    if (DEBUG)
    {
      IPluginContainer.INSTANCE.putElement("___" + getClass().getSimpleName(), "debug", null, this);
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    explorerManager.removeListener(explorerManagerListener);
    super.doDeactivate();
  }

  protected boolean filterDescriptor(D descriptor)
  {
    return true;
  }

  protected abstract D[] newArray(int size);

  protected abstract void explorerElementAdded(EE explorerElement);

  protected abstract void explorerElementRemoved(EE explorerElement);

  protected void explorerElementChanged(EE explorerElement)
  {
    // Do nothing.
  }

  protected void notifyExplorerElementEvent(IEvent event)
  {
    if (event instanceof ElementsChangedEvent)
    {
      ElementsChangedEvent e = (ElementsChangedEvent)event;
      for (Object element : e.getChangedElements())
      {
        if (explorerElementType.isInstance(element))
        {
          @SuppressWarnings("unchecked")
          EE explorerElement = (EE)element;

          explorerElementChanged(explorerElement);
        }
      }
    }
  }

  protected final void deleteStateFolder(EE explorerElement)
  {
    File stateFolder = getLMStateFolder(explorerElement, false);
    if (stateFolder != null)
    {
      IOUtil.delete(stateFolder); // TODO That CAN mean delete on exit ;-(
    }
  }

  @SuppressWarnings("restriction")
  protected final void saveLMProperties(EE explorerElement, Properties properties)
  {
    File stateFolder = getLMStateFolder(explorerElement, true);
    org.eclipse.emf.cdo.internal.explorer.AbstractManager.saveProperties(stateFolder, STATE_FILE_NAME, properties,
        getClass().getSimpleName() + " " + STATE_FILE_NAME);
  }

  @SuppressWarnings("restriction")
  public static <EE extends CDOExplorerElement> Properties loadLMProperties(EE explorerElement)
  {
    File stateFolder = explorerElement.getStateFolder(STATE_FOLDER_NAME);
    return org.eclipse.emf.cdo.internal.explorer.AbstractManager.loadProperties(stateFolder, STATE_FILE_NAME);
  }

  public static File getLMStateFolder(CDOExplorerElement element, boolean createFolderOnDemand)
  {
    return element.getStateFolder(STATE_FOLDER_NAME, createFolderOnDemand);
  }
}
