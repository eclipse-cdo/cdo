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
package org.eclipse.emf.cdo.internal.workspace;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOWorkspaceBaseline implements InternalCDOWorkspaceBaseline
{
  private CDOPackageRegistry packageRegistry;

  private String branchPath;

  private long timeStamp;

  protected AbstractCDOWorkspaceBaseline()
  {
  }

  public void init(CDOPackageRegistry packageRegistry, String branchPath, long timeStamp)
  {
    this.packageRegistry = packageRegistry;
    this.branchPath = branchPath;
    this.timeStamp = timeStamp;
  }

  public CDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public String getBranchPath()
  {
    return branchPath;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public int updateAfterCommit(CDOTransaction transaction)
  {
    int added = 0;
    Collection<InternalCDORevision> revisions = ((InternalCDOTransaction)transaction).getCleanRevisions().values();
    for (InternalCDORevision revision : revisions)
    {
      if (!containsRevision(revision.getID()))
      {
        addRevision(revision);
        ++added;
      }
    }

    return added;
  }

  protected CDODataInput createCDODataInput(ExtendedDataInputStream edis) throws IOException
  {
    CDORevisionFactory revisionFactory = CDORevisionFactory.DEFAULT;
    CDOListFactory listFactory = CDOListFactory.DEFAULT;
    return CDOCommonUtil.createCDODataInput(edis, packageRegistry, null, null, revisionFactory, listFactory, null);
  }

  protected CDODataOutput createCDODataOutput(ExtendedDataOutputStream edos)
  {
    CDOIDProvider idProvider = CDOIDProvider.NOOP;
    return CDOCommonUtil.createCDODataOutput(edos, packageRegistry, idProvider);
  }

  protected abstract boolean containsRevision(CDOID id);

  protected abstract void removeRevision(CDOID id);

  protected abstract void addRevision(InternalCDORevision revision);
}
