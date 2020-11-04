/*
 * Copyright (c) 2009-2012, 2014-2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.ManagedContainerViewProvider;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Provides views from sessions registered in a {@link #getContainer() managed container}.
 * <p>
 * The URI format of this view provider is <code>cdo://{repository-uuid}[/{resource-path}]</code>.
 *
 * @author Victor Roldan Betancort
 */
public class PluginContainerViewProvider extends ManagedContainerViewProvider
{
  public static final PluginContainerViewProvider INSTANCE = new PluginContainerViewProvider();

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
    if (repoUUID == null)
    {
      return null;
    }

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
    String repositoryUUID = getRepositoryUUID(view);
    return getResourceURI(repositoryUUID, path);
  }

  public URI getResourceURI(String repositoryUUID, String path)
  {
    URI uri = URI.createHierarchicalURI(CDOURIUtil.PROTOCOL_NAME, repositoryUUID, null, null, null);
    return CDOURIUtil.appendResourcePath(uri, path);
  }

  @Override
  public URI getViewURI(URI uri)
  {
    if (uri == null)
    {
      return null;
    }

    String scheme = uri.scheme();
    if (scheme == null)
    {
      return null;
    }

    if (!scheme.equals(CDOURIUtil.PROTOCOL_NAME))
    {
      return null;
    }

    return super.getViewURI(uri);
  }

  protected CDOView openView(CDOSession session, ResourceSet resourceSet)
  {
    return session.openTransaction(resourceSet);
  }

  public static String getRepositoryUUID(CDOView view)
  {
    return view.getSession().getRepositoryInfo().getUUID();
  }

  public static String getRepositoryUUID(URI uri)
  {
    return uri.authority();
  }
}
