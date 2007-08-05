/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.store;

import org.eclipse.emf.cdo.internal.protocol.model.CDOPackageImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.server.RepositoryPackageManager;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.model.CDOClassRef;

import org.eclipse.net4j.util.transaction.ITransaction;
import org.eclipse.net4j.util.transaction.TransactionUtil;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class NOOPStore extends Store
{
  private static final String TYPE = "noop";

  public NOOPStore()
  {
    super(TYPE);
  }

  public ITransaction createTransaction()
  {
    return TransactionUtil.createTransaction();
  }

  @Override
  protected AddPackageOperation createAddPackageOperation(RepositoryPackageManager packageManager,
      CDOPackageImpl cdoPackage)
  {
    return new AddPackageOperation(packageManager, cdoPackage)
    {
      @Override
      protected void update(ITransaction transaction, CDOPackageImpl cdoPackage)
      {
        // TODO Implement method .createAddPackageOperation()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected LoadPackageOperation createLoadPackageOperation(CDOPackageImpl cdoPackage)
  {
    return new LoadPackageOperation(cdoPackage)
    {
      @Override
      protected void queryAndFill(ITransaction transaction, CDOPackageImpl cdoPackage)
      {
        // TODO Implement method .createLoadPackageOperation()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected AddRevisionOperation createAddRevisionOperation(RevisionManager revisionManager, CDORevisionImpl revision)
  {
    return new AddRevisionOperation(revisionManager, revision)
    {
      @Override
      protected void update(ITransaction transaction, CDORevisionImpl revision)
      {
        // TODO Implement method .update()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected LoadRevisionOperation createLoadRevisionOperation(CDOID id)
  {
    return new LoadRevisionOperation(id)
    {
      @Override
      protected CDORevisionImpl query(ITransaction transaction, CDOID id) throws Exception
      {
        // TODO Implement method .query()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected LoadHistoricalRevisionOperation createLoadHistoricalRevisionOperation(CDOID id, long timeStamp)
  {
    return new LoadHistoricalRevisionOperation(id, timeStamp)
    {
      @Override
      protected CDORevisionImpl query(ITransaction transaction, CDOID id, long timeStamp) throws Exception
      {
        // TODO Implement method .query()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected RegisterResourceOperation createRegisterResourceOperation(CDOID id, String path,
      Map<CDOID, String> idToPathMap, Map<String, CDOID> pathToIDMap)
  {
    return new RegisterResourceOperation(id, path, idToPathMap, pathToIDMap)
    {
      @Override
      protected void update(ITransaction transaction, CDOID id, String path)
      {
        // TODO Implement method .createRegisterResourceOperation()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected LoadResourceIDOperation createLoadResourceIDOperation(String path)
  {
    return new LoadResourceIDOperation(path)
    {
      @Override
      protected CDOID query(ITransaction transaction, String path) throws Exception
      {
        // TODO Implement method .query()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected LoadResourcePathOperation createLoadResourcePathOperation(CDOID id)
  {
    return new LoadResourcePathOperation(id)
    {
      @Override
      protected String query(ITransaction transaction, CDOID id) throws Exception
      {
        // TODO Implement method .query()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }

  @Override
  protected QueryObjectTypeOperation createQueryObjectTypeOperation(CDOID id)
  {
    return new QueryObjectTypeOperation(id)
    {
      @Override
      protected CDOClassRef query(ITransaction transaction, CDOID id) throws Exception
      {
        // TODO Implement method .query()
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
  }
}