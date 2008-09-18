/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ResourcesQueryHandler implements IQueryHandler
{
  public ResourcesQueryHandler()
  {
  }

  public void executeQuery(final CDOQueryInfo info, final IQueryContext context)
  {
    IStoreReader storeReader = StoreThreadLocal.getStoreReader();
    storeReader.queryResources(new IStoreReader.QueryResourcesContext()
    {
      public String getPathPrefix()
      {
        return info.getQueryString();
      }

      public int getMaxResults()
      {
        return info.getMaxResults();
      }

      public boolean addResource(CDOID resourceID)
      {
        return context.addResult(resourceID);
      }
    });
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES);
    }

    public IQueryHandler create(String description) throws ProductCreationException
    {
      return new ResourcesQueryHandler();
    }

    public static IQueryHandler get(IManagedContainer container, String queryLanguage)
    {
      return (IQueryHandler)container.getElement(PRODUCT_GROUP, CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, null);
    }
  }
}
