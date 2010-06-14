/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EReference;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class XRefsQueryHandler implements IQueryHandler
{
  public XRefsQueryHandler()
  {
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    IStoreAccessor accessor = StoreThreadLocal.getAccessor();
    QueryContext xrefsContext = new QueryContext(info, context);
    accessor.queryXRefs(xrefsContext);

    CDOBranchPoint branchPoint = context;
    CDOBranch branch = branchPoint.getBranch();
    while (!branch.isMainBranch() && xrefsContext.getXRefIDs().size() < info.getMaxResults())
    {
      branchPoint = branch.getBase();
      branch = branchPoint.getBranch();

      xrefsContext.setBranchPoint(branchPoint);
      accessor.queryXRefs(xrefsContext);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  private static final class QueryContext implements IStoreAccessor.QueryXRefsContext
  {
    private CDOQueryInfo info;

    private IQueryContext context;

    private CDOBranchPoint branchPoint;

    private Set<CDOID> xrefIDs = new HashSet<CDOID>();

    public QueryContext(CDOQueryInfo info, IQueryContext context)
    {
      this.info = info;
      this.context = context;
      branchPoint = context;
    }

    public void setBranchPoint(CDOBranchPoint branchPoint)
    {
      this.branchPoint = branchPoint;
    }

    public Set<CDOID> getXRefIDs()
    {
      return xrefIDs;
    }

    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    public Set<CDOID> getTargetObjects()
    {
      // return (Set<CDOID>)info.getParameters().get(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES_FOLDER_ID);
      // TODO: implement XRefsQueryHandler.QueryContext.getTargetObjects()
      throw new UnsupportedOperationException();
    }

    public EReference[] getSourceReferences()
    {
      // TODO: implement XRefsQueryHandler.QueryContext.getSourceFeatures()
      throw new UnsupportedOperationException();
    }

    public int getMaxResults()
    {
      return info.getMaxResults();
    }

    public boolean addXRef(CDOID targetID, CDOID sourceID, EReference sourceReference, int sourceIndex)
    {
      if (xrefIDs.add(targetID))
      {
        return context.addResult(targetID);
      }

      return true;
    }

    public int compareTo(CDOBranchPoint o)
    {
      return branchPoint.compareTo(o);
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES);
    }

    @Override
    public IQueryHandler create(String description) throws ProductCreationException
    {
      return new XRefsQueryHandler();
    }

    public static IQueryHandler get(IManagedContainer container, String queryLanguage)
    {
      return (IQueryHandler)container.getElement(PRODUCT_GROUP, CDOProtocolConstants.QUERY_LANGUAGE_RESOURCES, null);
    }
  }
}
