/*
 * Copyright (c) 2010-2013, 2019-2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.CDOCommonRepository;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;

import java.util.LinkedHashSet;

/**
 * Manages a tree of {@link CDOBranch branches} and notifies about changes in this branch tree.
 * <p>
 * The branch tree is represented by a {@link #getMainBranch() main} branch, which, like all
 * {@link CDOBranch#getBranches() sub} branches, offers the major part of the branching functionality. A branch manager
 * provides additional methods to find branches by their unique integer ID or by their fully qualified path name, as
 * well as asynchronous bulk queries.
 * <p>
 * A branch manager can fire the following events:
 * <ul>
 * <li> {@link CDOBranchChangedEvent} after a new branch has been created, renamed or deleted.
 * </ul>
 * <p>
 * Branch managers are usually associated with the following entities:
 * <ul>
 * <li> <code>org.eclipse.emf.cdo.session.CDOSession</code>
 * <li> <code>org.eclipse.emf.cdo.server.IRepository</code>
 * </ul>
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOBranchManager extends INotifier
{
  /**
   * Returns the repository this branch manager is associated with.
   *
   * @since 4.2
   */
  public CDOCommonRepository getRepository();

  /**
   * Returns the main branch of the branch tree managed by this branch manager.
   * <p>
   * The main branch has the fixed {@link CDOBranch#MAIN_BRANCH_NAME name} "MAIN" and the fixed
   * {@link CDOBranch#MAIN_BRANCH_ID ID} 0 (zero).
   */
  public CDOBranch getMainBranch();

  /**
   * Returns the branch with the given unique integer ID.
   * <p>
   * Note that this method never returns <code>null</code>. Due to the lazy loading nature of branch managers this
   * method returns a transparent <i>branch proxy</i> if the branch is not already loaded in the internal <i>branch
   * cache</i>. This can result in unchecked exceptions being thrown from calls to arbitrary branch methods if the ID
   * that the proxy was created with does not exist in the branch tree.
   */
  public CDOBranch getBranch(int branchID);

  /**
   * Returns the branch with the given absolute path.
   *
   * @param path
   *          A concatenation of the names of all branches from the {@link #getMainBranch() main branch} to the
   *          requested branch, separated by {@link CDOBranch#PATH_SEPARATOR slashes} ("/" characters). Example:
   *          "MAIN/team1/smith".
   */
  public CDOBranch getBranch(String path);

  /**
   * Passes all branches with IDs in the given range to the given {@link CDOBranchHandler#handleBranch(CDOBranch) branch
   * handler} and returns the number of handler invocations.
   * <p>
   * This is a blocking call.
   */
  public int getBranches(int startID, int endID, CDOBranchHandler handler);

  /**
   * @since 4.15
   */
  public LinkedHashSet<CDOBranch> getBranches(int rootID);

  /**
   * @since 4.11
   */
  public CDOBranchTag createTag(String name, CDOBranchPoint branchPoint);

  /**
   * @since 4.11
   */
  public CDOBranchTag getTag(String name);

  /**
   * @since 4.11
   */
  public CDOTagList getTagList();

  /**
   * A {@link IContainer container} of all {@link CDOBranchTag branch tags} in the scope of a {@link CDOBranchManager branch manager}.
   * <p>
   * In addition to the {@link IContainer container} events a tag list can fire the following events:
   * <ul>
   * <li> {@link TagRenamedEvent} when a branch tag was renamed.
   * <li> {@link TagMovedEvent} when a branch tag was moved.
   * </ul>
   *
   * @author Eike Stepper
   * @since 4.11
   */
  public interface CDOTagList extends IContainer<CDOBranchTag>
  {
    /**
     * Returns the {@link CDOBranchManager branch manager} that manages this {@link CDOTagList tag list}, never <code>null</code>.
     */
    public CDOBranchManager getBranchManager();

    public CDOBranchTag[] getTags();

    public CDOBranchTag[] getTags(CDOBranch branch);

    /**
     * An {@link IEvent event} fired from a {@link CDOTagList tag list}.
     *
     * @author Eike Stepper
     */
    public interface TagListEvent extends IEvent
    {
      public CDOTagList getTagList();
    }

    /**
     * A {@link TagListEvent tag list event} fired when a branch tag was renamed.
     *
     * @author Eike Stepper
     */
    public interface TagRenamedEvent extends TagListEvent, CDOBranchTag.TagRenamedEvent
    {
    }

    /**
     * A {@link TagListEvent tag list event} fired when a branch tag was moved.
     *
     * @author Eike Stepper
     */
    public interface TagMovedEvent extends TagListEvent, CDOBranchTag.TagMovedEvent
    {
    }
  }

  /**
   * A {@link IListener listener} that dispatches {@link CDOBranchChangedEvent branch changed events} to methods that can be
   * overridden by extenders.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public static class EventAdapter implements IListener
  {
    public EventAdapter()
    {
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent e = (CDOBranchChangedEvent)event;
        notifyBranchChangedEvent(e);
      }
      else
      {
        notifyOtherEvent(event);
      }
    }

    protected void notifyBranchChangedEvent(CDOBranchChangedEvent event)
    {
      switch (event.getChangeKind())
      {
      case CREATED:
        onBranchCreated(event.getBranch());
        break;
      case RENAMED:
        onBranchRenamed(event.getBranch());
        break;
      case DELETED:
        onBranchesDeleted(event.getBranch(), event.getBranchIDs());
        break;
      }
    }

    protected void onBranchCreated(CDOBranch branch)
    {
    }

    protected void onBranchRenamed(CDOBranch branch)
    {
    }

    protected void onBranchesDeleted(CDOBranch rootBranch, int[] branchIDs)
    {
    }

    protected void notifyOtherEvent(IEvent event)
    {
    }
  }
}
