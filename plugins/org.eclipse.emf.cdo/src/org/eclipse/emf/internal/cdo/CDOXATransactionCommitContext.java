/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackage;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.id.CDOIDExternalTempImpl;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOXATransactionImpl.CDOXAState;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXATransactionCommitContext implements Callable<Object>, CDOIDProvider, CDOCommitContext
{
  private CDOXATransactionImpl transactionManager;

  private IProgressMonitor progressMonitor;

  private CDOXAState state;

  private CommitTransactionResult result;

  private CDOCommitContext delegateCommitContext;

  private Map<CDOIDExternalTempImpl, InternalCDOTransaction> requestedIDs = new HashMap<CDOIDExternalTempImpl, InternalCDOTransaction>();

  private Map<InternalCDOObject, CDOIDExternalTempImpl> objectToID = new HashMap<InternalCDOObject, CDOIDExternalTempImpl>();

  public CDOXATransactionCommitContext(CDOXATransactionImpl manager, CDOCommitContext commitContext)
  {
    transactionManager = manager;
    delegateCommitContext = commitContext;
  }

  public CDOXATransactionImpl getTransactionManager()
  {
    return transactionManager;
  }

  public void setProgressMonitor(IProgressMonitor progressMonitor)
  {
    this.progressMonitor = progressMonitor;
  }

  public CDOXAState getState()
  {
    return state;
  }

  public void setState(CDOXAState state)
  {
    this.state = state;
  }

  public CommitTransactionResult getResult()
  {
    return result;
  }

  public void setResult(CommitTransactionResult result)
  {
    this.result = result;
  }

  public InternalCDOTransaction getTransaction()
  {
    return delegateCommitContext.getTransaction();
  }

  public Map<CDOIDExternalTempImpl, InternalCDOTransaction> getRequestedIDs()
  {
    return requestedIDs;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return delegateCommitContext.getDirtyObjects();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return delegateCommitContext.getNewObjects();
  }

  public List<CDOPackage> getNewPackages()
  {
    return delegateCommitContext.getNewPackages();
  }

  public Map<CDOID, CDOResource> getNewResources()
  {
    return delegateCommitContext.getNewResources();
  }

  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return delegateCommitContext.getDetachedObjects();
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return delegateCommitContext.getRevisionDeltas();
  }

  public Object call() throws Exception
  {
    state.handle(this, progressMonitor);
    return true;
  }

  public CDOID provideCDOID(Object idOrObject)
  {
    CDOID id = getTransaction().provideCDOID(idOrObject);

    if (id instanceof CDOIDExternalTempImpl)
    {
      if (idOrObject instanceof InternalEObject)
      {
        CDOIDExternalTempImpl proxyTemp = (CDOIDExternalTempImpl)id;
        if (!requestedIDs.containsKey(proxyTemp))
        {
          InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject((InternalEObject)idOrObject);
          InternalCDOTransaction cdoTransaction = (InternalCDOTransaction)cdoObject.cdoView();
          getTransactionManager().add(cdoTransaction, proxyTemp);
          requestedIDs.put(proxyTemp, cdoTransaction);
          objectToID.put(cdoObject, proxyTemp);
        }
      }
      else
      {
        throw new ImplementationError("Object should be an EObject " + idOrObject);
      }
    }

    return id;
  }

  public void preCommit()
  {
    delegateCommitContext.preCommit();
  }

  public void postCommit(CommitTransactionResult result)
  {
    final CDOReferenceAdjuster defaultReferenceAdjuster = result.getReferenceAdjuster();
    result.setReferenceAdjuster(new CDOReferenceAdjuster()
    {
      public Object adjustReference(Object id)
      {
        CDOIDExternalTempImpl externalID = objectToID.get(id);
        if (externalID != null)
        {
          id = externalID;
        }

        return defaultReferenceAdjuster.adjustReference(id);
      }
    });

    delegateCommitContext.postCommit(result);
  }
};
