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

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Closeable;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * @author Eike Stepper
 */
public interface CDOWorkspace extends CDORevisionProvider, Closeable
{
  public CDOWorkspaceBaseline getBaseline();

  public CDOView openView();

  public CDOView openView(ResourceSet resourceSet);

  public CDOTransaction openTransaction();

  public CDOTransaction openTransaction(ResourceSet resourceSet);

  public CDOTransaction update(CDOMerger merger);

  public CDOTransaction update(CDOMerger merger, String branchPath);

  public CDOTransaction update(CDOMerger merger, String branchPath, long timeStamp);

  public void revert();

  public void replace(String branchPath, long timeStamp);

  public CDOCommitInfo commit(String comment) throws CommitException;

  public CDOChangeSetData getLocalChanges();

  public IRepository getLocalRepository();
}
