/*
 * Copyright (c) 2009-2012, 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.InvalidURIException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.ManagedContainerViewProvider;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Provides <code>CDOView</code> from <code>CDOSession</code> registered in IPluginContainer
 *
 * @author Victor Roldan Betancort
 */
public class PluginContainerViewProvider extends ManagedContainerViewProvider
{
  public static final CDOViewProvider INSTANCE = new PluginContainerViewProvider();

  private static final String REGEX = "cdo:.*"; //$NON-NLS-1$

  private static final int PRIORITY = DEFAULT_PRIORITY - 200;

  public PluginContainerViewProvider()
  {
    super(IPluginContainer.INSTANCE, REGEX, PRIORITY);
  }

  @Override
  public CDOView getView(URI uri, ResourceSet resourceSet)
  {
    IManagedContainer container = getContainer();
    if (container == null)
    {
      return null;
    }

    String repoUUID = getRepositoryUUID(uri);
    for (Object element : container.getElements(CDOSessionFactory.PRODUCT_GROUP))
    {
      CDOSession session = (CDOSession)element;
      String uuid = session.getRepositoryInfo().getUUID();
      if (repoUUID.equals(uuid))
      {
        CDOView view = openView(session, resourceSet);
        if (view != null)
        {
          return view;
        }
      }
    }

    return null;
  }

  @Override
  public URI getResourceURI(CDOView view, String path)
  {
    if (StringUtil.isEmpty(path))
    {
      path = "";
    }
    else if (!path.startsWith("/"))
    {
      path = "/" + path;
    }

    String authority = view.getSession().getRepositoryInfo().getUUID();
    return URI.createURI(CDOURIUtil.PROTOCOL_NAME + "://" + authority + path);
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected CDOView openView(CDOSession session, ResourceSet resourceSet)
  {
    return session.openTransaction(resourceSet);
  }

  public static String getRepositoryUUID(URI uri)
  {
    try
    {
      if (!uri.hasAuthority())
      {
        throw new InvalidURIException(uri);
      }

      return uri.authority();
    }
    catch (InvalidURIException ex)
    {
      return null;
    }
  }
}
