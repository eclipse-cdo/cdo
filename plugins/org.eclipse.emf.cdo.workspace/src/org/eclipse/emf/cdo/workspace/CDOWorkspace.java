/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.CDOElement.StateProvider;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.Closeable;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;

import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.Set;

/**
 * Represents a local {@link CDOWorkspaceConfiguration#checkout() checkout} from a remote repository.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOWorkspace extends CDORevisionProvider, StateProvider, Closeable, INotifier
{
  /**
   * @since 4.2
   */
  public int getBranchID();

  public String getBranchPath();

  public long getTimeStamp();

  public boolean isFixed();

  public CDOWorkspaceBase getBase();

  public CDOView openView();

  public CDOView openView(ResourceSet resourceSet);

  public CDOTransaction openTransaction();

  public CDOTransaction openTransaction(ResourceSet resourceSet);

  public CDOTransaction update(CDOMerger merger);

  public CDOTransaction merge(CDOMerger merger, String branchPath);

  public CDOTransaction merge(CDOMerger merger, String branchPath, long timeStamp);

  public CDOCommitInfo checkin() throws CommitException;

  public CDOCommitInfo checkin(String comment) throws CommitException;

  public CDOChangeSetData compare(String branchPath);

  public CDOChangeSetData compare(String branchPath, long timeStamp);

  public CDOChangeSetData getLocalChanges();

  public void replace(String branchPath, long timeStamp);

  public void revert();

  /**
   * @since 4.1
   */
  public boolean isDirty();

  /**
   * An {@link IEvent event} fired when the overall state of the {@link CDOWorkspace workspace} changes between <i>dirty</i> and <i>clean</i>.
   *
   * @see CDOWorkspace#isDirty()
   * @author Eike Stepper
   * @since 4.1
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface DirtyStateChangedEvent extends IEvent
  {
    /**
     * @since 4.2
     */
    @Override
    public CDOWorkspace getSource();

    public boolean isDirty();
  }

  /**
   * An {@link IEvent event} fired when the state of objects in the {@link CDOWorkspace workspace} change between <i>new</i>, <i>dirty</i> and <i>clean</i>.
   *
   * @author Eike Stepper
   * @since 4.2
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface ObjectStatesChangedEvent extends IEvent
  {
    @Override
    public CDOWorkspace getSource();

    public Set<CDOID> getChangedIDs();
  }
}
