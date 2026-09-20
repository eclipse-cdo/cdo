/*
 * Copyright (c) 2025-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.programmers.client;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewContainer;

/**
 * Branching and Versioning
 * <p>
 * CDO history is easiest to understand as a coordinate system. A model object has a stable {@link CDOID identity}; its
 * modeled values are stored in successive {@link CDORevision revisions}. A revision belongs to a {@link CDOBranch
 * branch}, has a per-object integer version, and is valid for a time interval on that branch. A {@link CDOBranchPoint}
 * selects one branch and one time. A view reads the model at such a coordinate, while a transaction edits the current
 * state at the head of a branch.
 * <p>
 * This chapter explains the application-facing model of branches, branch points, revisions, historical views, and
 * merges. Session-level branch and tag management is covered in {@link Doc03_WorkingWithSessions}; view creation and
 * lifecycle are covered in {@link Doc04_WorkingWithViews}; and transaction-side merge and revert operations are covered
 * in {@link Doc05_WorkingWithTransactions}.
 *
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc08_BranchingAndVersioning
{
  /**
   * Mental Model
   * <p>
   * A CDO model object is the application-level object obtained from a resource in a view. Its {@link CDOID CDOID} is
   * its identity: the ID remains the same while the object changes, and an ID alone does not select a historical state.
   * Each committed state of that object is represented by a {@link CDORevision}. The revision carries the ID, branch,
   * version, creation timestamp, and revised timestamp, together with the modeled values.
   * <p>
   * A branch is a named stream of commits. The main branch is the root stream; a child branch starts at a fixed point in
   * its parent and then receives its own commits. A branch point is the pair of a branch and a time on that branch. A
   * timestamp identifies a historical point, while {@link CDOBranchPoint#UNSPECIFIED_DATE UNSPECIFIED_DATE} identifies
   * the floating head of the branch. Consequently, "the current object" is shorthand for the object at a branch head,
   * not a branch-independent object.
   * <p>
   * A normal view can follow a branch head and update as new commits arrive. A historical view fixes its branch point and
   * exposes the immutable state that was valid there. These coordinates are the important distinction: a version number
   * is useful for one object's revision on one branch, but it is not a complete coordinate for a whole repository state.
   */
  public class MentalModel
  {
  }

  /**
   * Branches and Branch Points
   * <p>
   * Branches form a tree rooted at {@link CDOBranchManager#getMainBranch() MAIN}. A normal branch has a positive
   * technical ID; the main branch has ID {@link CDOBranch#MAIN_BRANCH_ID 0}. Branch names are mutable and are unique
   * among the direct children of their parent, not necessarily throughout the repository. Use a branch ID or the full
   * path returned by {@link CDOBranch#getPathName()} when a durable or unambiguous reference is needed.
   * <p>
   * A branch has a fixed {@link CDOBranch#getBase() base} branch point in its parent and a floating
   * {@link CDOBranch#getHead() head}. Creating a branch without a timestamp bases it at the current time; the overload
   * that accepts a timestamp lets an application branch from a selected historical point. A branch can be renamed with
   * {@link CDOBranch#setName(String)} and, where supported, deleted together with its sub-branches.
   * <p>
   * A branch point matters whenever code must name an exact repository state: it is used to open a historical view,
   * request a revision, compare states, or describe the source or base of a merge. The head is intentionally floating;
   * a historical point remains fixed even when later commits advance the branch.
   * <p>
   * For branch-manager lookup, enumeration, events, and deletion, see the {@link Doc03_WorkingWithSessions} chapter.
   */
  public class BranchesAndBranchPoints
  {
    /**
     * Creates a child branch from the current head of the main branch.
     *
     * @param session the open session
     * @param branchName the name of the child branch
     * @return the newly created branch
     * @snip
     */
    public CDOBranch createBranch(CDOSession session, String branchName)
    {
      CDOBranchManager branchManager = session.getBranchManager();
      CDOBranch mainBranch = branchManager.getMainBranch();
      return mainBranch.createBranch(branchName);
    }
  }

  /**
   * Versions and Revisions
   * <p>
   * {@link CDOBranchVersion} is the pair of a branch and an integer version. The version is assigned to one object's
   * successive revisions on that branch, beginning with {@link CDOBranchVersion#FIRST_VERSION 1}. It is not a global
   * commit number, a timestamp, or a complete model version. Two revisions with the same integer version on different
   * branches are unrelated unless their branch coordinates also match.
   * <p>
   * The public API expresses the version value through {@code getVersion()} on {@code CDORevision} and
   * {@code CDOVersionProvider}; there is no independent repository-wide {@code CDOVersion} object. Use
   * {@link CDOBranch#getVersion(int)} when constructing a branch-version coordinate, and
   * {@link org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request#getRevisionByVersion(CDOID, CDOBranchVersion)} when
   * an object must be loaded by that coordinate.
   * <p>
   * A {@link CDORevision} is immutable system information for one object between two commits. Its ID identifies the
   * object, its branch and version identify the revision in that branch, and its timestamps describe the interval in
   * which it is valid. Historical revisions report {@link CDORevision#isHistorical() isHistorical()}; a current revision
   * at a branch head has an unspecified revised time. A revision can be missing at a branch point because the object had
   * not yet been created, or because it was already detached there.
   */
  public class VersionsAndRevisions
  {
    /**
     * Loads one object revision at an explicitly selected branch point.
     *
     * @param session the open session
     * @param id the object's stable ID
     * @param branchPoint the branch and time to inspect
     * @return the revision, or {@code null} if the object did not exist at that point
     * @snip
     */
    public CDORevision loadRevision(CDOSession session, CDOID id, CDOBranchPoint branchPoint)
    {
      return session.getRevisionManager().request().getRevision(id, branchPoint);
    }
  }

  /**
   * Historical Views and Navigating History
   * <p>
   * Open a read-only view at a branch, at a branch point, or at a timestamp on a branch with the corresponding
   * {@link CDOViewContainer#openView(CDOBranch, long) openView()} overload. The concise branch-point form is
   * {@link CDOViewContainer#openView(CDOBranchPoint)}. A view opened with {@link CDOBranchPoint#UNSPECIFIED_DATE} follows the
   * branch head; a view opened with a real timestamp is historical and remains at that time until its target is changed.
   * {@link CDOView#setBranchPoint(CDOBranchPoint)} can move a read-only view to another coordinate when the application
   * wants to browse history without creating another view.
   * <p>
   * Objects and resources obtained from a read-only view represent the selected state and cannot be mutated. Close the
   * view when it is no longer needed. If a referenced object did not exist at the selected time, loading its revision can
   * yield {@code null} and the corresponding historical graph does not contain that object. Historical inspection is
   * therefore different from restoring data: use a transaction and the transaction APIs for an actual change.
   * <p>
   * For the complete view-type and lifecycle discussion, see {@link Doc04_WorkingWithViews}.
   */
  public class HistoricalViews
  {
    /**
     * Opens a fixed historical view and reads the root resource at that point.
     *
     * @param session the open session
     * @param branch the branch to inspect
     * @param timeStamp a repository timestamp on the branch
     * @snip
     */
    public void openHistoricalView(CDOSession session, CDOBranch branch, long timeStamp)
    {
      CDOView view = session.openView(branch, timeStamp);
      try
      {
        System.out.println("Historical root: " + view.getRootResource());
      }
      finally
      {
        view.close();
      }
    }

    /**
     * An object's commit history is available through {@link CDOObject#cdoHistory()} and a view also provides commit
     * history for its objects. These histories describe commit metadata; they are not a replacement for loading the
     * object's revisions. Use the session's {@link org.eclipse.emf.cdo.common.revision.CDORevisionManager revision
     * manager} with a branch point or branch version when the actual historical values are needed. This separation keeps
     * application code explicit about whether it needs commit metadata or model state.
     */
    public class HistoryNavigation
    {
    }
  }

  /**
   * Comparing Historical State
   * <p>
   * To compare two states, first identify both with complete branch points and then use
   * {@link CDOSession#compareRevisions(CDOBranchPoint, CDOBranchPoint)} or
   * {@link CDOView#compareRevisions(CDOBranchPoint)}. These APIs return {@link CDOChangeSetData}, which describes the
   * object-level changes between the selected states. They do not turn two model objects into a generic EMF comparison
   * editor, and a version number by itself is not enough to select either state.
   * <p>
   * For a single object, loading two {@link CDORevision revisions} and calling
   * {@link CDORevision#compare(CDORevision)} provides a revision delta. For a repository-level comparison, prefer the
   * change-set APIs. The result is data for inspection or application to a transaction; it is not itself a commit and
   * it does not alter either historical state.
   */
  public class ComparingVersions
  {
    /**
     * Computes the change set between two explicitly identified states.
     *
     * @param session the open session
     * @param source the state to compare from
     * @param target the state to compare to
     * @return the computed change set
     * @snip
     */
    public CDOChangeSetData compareStates(CDOSession session, CDOBranchPoint source, CDOBranchPoint target)
    {
      return session.compareRevisions(source, target);
    }
  }

  /**
   * Branching, Transactions, and Merge Concepts
   * <p>
   * Open a transaction on a branch to edit that branch's head. Commits made by the transaction create new revisions on
   * that branch and are isolated from the parent branch until an application explicitly merges changes. The transaction
   * is always current-state/read-write; opening a historical read-only view is not a way to edit an old point.
   * <p>
   * A merge has a source state or source branch and a target transaction. CDO computes changes relative to a source base
   * and, where needed, a target base; the selected {@link org.eclipse.emf.cdo.transaction.CDOMerger merger} applies the
   * result to the target transaction. Conflicts belong to the transaction conflict model. Merge produces local changes,
   * so the application must resolve conflicts and commit the transaction before the target branch history changes.
   * <p>
   * The detailed merge overloads, conflict handling, and commit lifecycle are documented in
   * {@link Doc05_WorkingWithTransactions}. This chapter supplies the branch-point vocabulary needed to choose source
   * and base states.
   */
  public class BranchTransactionsAndMerges
  {
  }

  /**
   * Revert, Tags, and Repository Capability
   * <p>
   * Viewing an old branch point is read-only inspection. Reverting is a transaction operation that creates local changes
   * intended to restore the transaction toward a historical branch point; it is not a deletion of repository history.
   * Merging is different again: it applies changes from a source state into a target transaction. The resulting changes
   * become repository history only after commit. See {@link Doc05_WorkingWithTransactions#RevertingChanges} for the
   * operation-level distinction.
   * <p>
   * A {@link org.eclipse.emf.cdo.common.branch.CDOBranchTag tag} is a named, movable reference to a branch point. Tags
   * are useful for naming releases or other meaningful historical coordinates, but they do not freeze a point when moved.
   * Branch and tag creation, lookup, and management belong to {@link Doc03_WorkingWithSessions#BranchManager}.
   * <p>
   * Branching is repository-dependent. Check {@link CDOCommonRepository#isSupportingBranches()} before relying on child
   * branches. The main branch exists in every repository mode, while sub-branches require branching support. Historical
   * views and tags depend on auditing support; check {@link CDOCommonRepository#isSupportingAudits()} when those features
   * are required. The repository's {@link CDOCommonRepository#getMode()} summarizes these capabilities.
   */
  public class RevertTagsAndCapability
  {
  }

  /**
   * Practical Guidance
   * <p>
   * Keep the branch and time (or branch and version for one object) explicit in history-sensitive code. Use branches for
   * genuinely divergent model histories, historical views for read-only inspection, revision or change-set APIs for
   * comparison, and transactions for merge or revert work. Do not treat an integer version as a repository-wide model
   * version, and do not confuse a historical view with an operation that changes or rewrites repository history.
   */
  public class PracticalGuidance
  {
  }
}
