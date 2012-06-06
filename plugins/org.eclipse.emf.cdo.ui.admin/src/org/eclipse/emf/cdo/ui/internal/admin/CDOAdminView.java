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
package org.eclipse.emf.cdo.ui.internal.admin;

import org.eclipse.emf.cdo.admin.CDOAdminClient;
import org.eclipse.emf.cdo.admin.CDOAdminUtil;
import org.eclipse.emf.cdo.common.admin.CDOAdminRepository;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.ui.Net4jItemProvider;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.ui.container.ElementWizardAction;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.spi.net4j.ConnectorFactory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CDOAdminView extends ContainerView
{
  public final static String ID = "org.eclipse.emf.cdo.ui.admin.CDOAdminView"; //$NON-NLS-1$

  private final Map<IConnector, CDOAdminClient> admins = new HashMap<IConnector, CDOAdminClient>();

  private IAction newConnectorAction = new ElementWizardAction(getShell(), "New Connector",
      "Open a new Net4j connector",
      org.eclipse.net4j.ui.shared.SharedIcons
          .getDescriptor(org.eclipse.net4j.ui.shared.SharedIcons.ETOOL_ADD_CONNECTOR), ConnectorFactory.PRODUCT_GROUP,
      getContainer());

  private IListener containerListener = new ContainerEventAdapter<Object>()
  {
    @Override
    protected void onAdded(org.eclipse.net4j.util.container.IContainer<Object> container, Object element)
    {
    }

    @Override
    protected void onRemoved(org.eclipse.net4j.util.container.IContainer<Object> container, Object element)
    {
    }
  };

  public CDOAdminView()
  {
    // getContainer().addListener(containerListener);
  }

  @Override
  public void dispose()
  {
    // getContainer().removeListener(containerListener);
    super.dispose();
  }

  @Override
  protected Control createUI(Composite parent)
  {
    return super.createUI(parent);
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new Net4jItemProvider(new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof IConnector;
      }
    })
    {
      @Override
      public Object getParent(Object element)
      {
        if (element instanceof CDOAdminRepository)
        {
          CDOAdminRepository repository = (CDOAdminRepository)element;
          CDOAdminClient admin = (CDOAdminClient)repository.getAdmin();
          return admin.getConnector();
        }

        return super.getParent(element);
      }

      @Override
      public Object[] getChildren(Object element)
      {
        if (element instanceof IConnector)
        {
          IConnector connector = (IConnector)element;

          CDOAdminClient admin;
          synchronized (admins)
          {
            admin = admins.get(connector);
            if (admin == null)
            {
              admin = CDOAdminUtil.openAdmin(connector);
              admin.addListener(new LifecycleEventAdapter()
              {
                @Override
                protected void onDeactivated(ILifecycle lifecycle)
                {
                  IConnector key = ((CDOAdminClient)lifecycle).getConnector();
                  synchronized (admins)
                  {
                    admins.remove(key);
                  }
                }

                @Override
                protected void notifyOtherEvent(IEvent event)
                {
                  if (event instanceof IContainerEvent)
                  {
                    refreshViewer(false);
                  }
                }
              });

              admins.put(connector, admin);
            }
          }

          return admin.getRepositories();
        }

        return super.getChildren(element);
      }

      @Override
      public String getText(Object obj)
      {
        if (obj instanceof IConnector)
        {
          IConnector connector = (IConnector)obj;
          return connector.getURL();
        }

        if (obj instanceof CDOAdminRepository)
        {
          CDOAdminRepository repository = (CDOAdminRepository)obj;
          return repository.getName() + " [" + repository.getType() + ", " + repository.getState() + "]";
        }

        return super.getText(obj);
      }

      @Override
      public Image getImage(Object obj)
      {
        if (obj instanceof CDOAdminRepository)
        {
          return SharedIcons.getImage(SharedIcons.OBJ_REPO);
        }

        return super.getImage(obj);
      }
    };
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    manager.add(newConnectorAction);
    super.fillLocalToolBar(manager);
  }
}
