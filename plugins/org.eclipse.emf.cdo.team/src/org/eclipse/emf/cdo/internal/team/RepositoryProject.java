/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.team;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.team.IRepositoryProject;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.spi.net4j.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class RepositoryProject extends PlatformObject implements IRepositoryProject, IListener
{
  private IProject project;

  private CDOView view;

  public RepositoryProject(IProject project)
  {
    this.project = project;
  }

  public IProject getProject()
  {
    return project;
  }

  public synchronized CDOView getView()
  {
    if (view == null)
    {
      view = openView();
      view.addListener(this);
      view.getSession().addListener(this);
    }

    return view;
  }

  public void dispose()
  {
    project = null;
    if (view != null)
    {
      view.removeListener(this);
      view.getSession().removeListener(this);
      view.getSession().close();
      view = null;
    }
  }

  public void notifyEvent(IEvent event)
  {
    // if (event instanceof ILifecycleEvent)
    // {
    // ILifecycleEvent e = (ILifecycleEvent)event;
    // if (e.getKind() == ILifecycleEvent.Kind.DEACTIVATED)
    // {
    // view.removeListener(this);
    // view.getSession().removeListener(this);
    // view = null;
    // }
    // }
  }

  protected CDOView openView()
  {
    String connectorDescription = RepositoryTeamProvider.getConnectorDescription(project);
    String repositoryName = RepositoryTeamProvider.getRepositoryName(project);

    IConnector connector = getConnector(connectorDescription);
    if (connector == null)
    {
      throw new IllegalStateException("No connector for " + connectorDescription);
    }

    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    configuration.setLazyPackageRegistry();

    CDOSession session = configuration.openSession();
    return session.openView();
  }

  protected IConnector getConnector(String connectorDescription)
  {
    return (IConnector)getContainer().getElement(ConnectorFactory.PRODUCT_GROUP, "tcp", connectorDescription);
  }

  protected IPluginContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }
}
