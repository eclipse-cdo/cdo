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
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface CDOWorkspaceBaseline
{
  public void init(CDOPackageRegistry packageRegistry, String branchPath, long timeStamp);

  public CDOPackageRegistry getPackageRegistry();

  public String getBranchPath();

  public long getTimeStamp();

  public int updateAfterCommit(CDOTransaction transaction);

  public CDORevision getRevision(CDOID id);

  public Set<CDOID> getIDs();
}
