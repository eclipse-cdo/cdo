/*
 * Copyright (c) 2009-2012, 2014-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 215688
 *    Simon McDuff - bug 213402
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonTransaction;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetDataProvider;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOResourceNodeNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.Predicate;
import org.eclipse.net4j.util.options.IOptionsEvent;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * A read-write view to the <em>current</em> (i.e. latest) state of the object graph in the repository.
 *
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransaction extends CDOView, CDOCommonTransaction, CDOUserTransaction, CDOChangeSetDataProvider
{
  /**
   * Returns <code>true</code> if this transaction is not closed and contains uncommitted changes, <code>false</code>
   * otherwise.
   */
  public boolean isDirty();

  /**
   * Returns <code>true</code> if this transaction contains local modifications that are conflicting with remote
   * modifications, <code>false</code> otherwise.
   */
  public boolean hasConflict();

  /**
   * Returns the set of objects that are conflicting with remote modifications or an empty set if {@link #hasConflict()} returns <code>false</code>.
   */
  public Set<CDOObject> getConflicts();

  /**
   * Merges the changes from the given source branch into this transaction and possibly considers previous merges
   * from that branch by inspecting the {@link CDOCommitInfo#getMergeSource() merge source}
   * information of the {@link CDOSession#getCommitInfoManager() commit history}.
   *
   * @see CDOTransaction#merge(CDOBranchPoint, CDOMerger)
   * @since 4.6
   */
  public CDOChangeSetData merge(CDOBranch source, CDOMerger merger);

  /**
   * Merges the changes from the given source point into this transaction and possibly considers previous merges
   * from that {@link CDOBranchPoint#getBranch() branch} by inspecting the {@link CDOCommitInfo#getMergeSource() merge source}
   * information of the {@link CDOSession#getCommitInfoManager() commit history}.
   *
   * @since 3.0
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger);

  /**
   * Merges the changes between the given source base point and the given source point into this transaction.
   * <p>
   * <b>Warning:</b> If the branch of this transaction already contains merges from the given source point range
   * (i.e., if this merge is a "remerge") this method will likely fail. One of the following methods should be used instead:
   * <p>
   * <ul>
   * <li> {@link #merge(CDOBranchPoint, CDOBranchPoint, CDOBranchPoint, CDOMerger)}
   * <li> {@link #merge(CDOBranchPoint, CDOMerger)}
   * <li> {@link #merge(CDOBranch, CDOMerger)}
   * </ul>
   *
   * @since 4.0
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger);

  /**
   * Merges the changes between the given source base point and the given source point into this transaction.
   * <p>
   * When specifying an adequate target base point this method is able to perform a proper "remerge".
   *
   * @see #merge(CDOBranchPoint, CDOMerger)
   * @see #merge(CDOBranch, CDOMerger)
   * @since 4.6
   */
  public CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOBranchPoint targetBase, CDOMerger merger);

  /**
   * @since 4.0
   */
  public CDOResourceFolder createResourceFolder(String path) throws CDOResourceNodeNotFoundException;

  /**
   * @since 4.0
   */
  public CDOResourceFolder getOrCreateResourceFolder(String path);

  /**
   * @see ResourceSet#createResource(URI)
   */
  public CDOResource createResource(String path);

  public CDOResource getOrCreateResource(String path);

  /**
   * @since 4.2
   */
  public CDOTextResource createTextResource(String path);

  /**
   * @since 4.2
   */
  public CDOTextResource getOrCreateTextResource(String path);

  /**
   * @since 4.2
   */
  public CDOBinaryResource createBinaryResource(String path);

  /**
   * @since 4.2
   */
  public CDOBinaryResource getOrCreateBinaryResource(String path);

  /**
   * @since 4.0
   */
  public void addTransactionHandler(CDOTransactionHandlerBase handler);

  /**
   * @since 4.0
   */
  public void removeTransactionHandler(CDOTransactionHandlerBase handler);

  /**
   * @since 3.0
   */
  public CDOTransactionHandler[] getTransactionHandlers();

  /**
   * @since 4.0
   */
  public CDOTransactionHandler1[] getTransactionHandlers1();

  /**
   * @since 4.0
   */
  public CDOTransactionHandler2[] getTransactionHandlers2();

  /**
   * @since 3.0
   */
  public CDOSavepoint setSavepoint();

  /**
   * @since 4.1
   */
  public CDOSavepoint getFirstSavepoint();

  /**
   * @since 3.0
   */
  public CDOSavepoint getLastSavepoint();

  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 2.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 3.0
   */
  public CDOSavepoint[] exportChanges(OutputStream out) throws IOException;

  /**
   * @since 3.0
   */
  public CDOSavepoint[] importChanges(InputStream in, boolean reconstructSavepoints) throws IOException;

  public long getLastCommitTime();

  /**
   * Returns the comment to be used in the next commit operation.
   *
   * @see CDOCommitInfo#getComment()
   * @since 3.0
   */
  public String getCommitComment();

  /**
   * Sets the comment to be used in the next commit operation.
   *
   * @see CDOCommitInfo#getComment()
   * @since 3.0
   */
  public void setCommitComment(String comment);

  /**
   * @since 4.0
   */
  public void setCommittables(Set<? extends EObject> committables);

  /**
   * @since 4.0
   */
  public Set<? extends EObject> getCommittables();

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, boolean considerDirtyState);

  /**
   * @since 4.0
   */
  public CDOQuery createQuery(String language, String queryString, Object context, boolean considerDirtyState);

  /**
   * @since 4.8
   */
  public <T> CommitResult<T> commit(Callable<T> callable, Predicate<Long> retry, IProgressMonitor monitor)
      throws ConcurrentAccessException, CommitException, Exception;

  /**
   * @since 4.8
   */
  public <T> CommitResult<T> commit(Callable<T> callable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException, Exception;

  /**
  * @since 4.8
  */
  public CDOCommitInfo commit(Runnable runnable, Predicate<Long> retry, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException;

  /**
   * @since 4.8
   */
  public CDOCommitInfo commit(Runnable runnable, int attempts, IProgressMonitor monitor) throws ConcurrentAccessException, CommitException;

  public Options options();

  /**
   * Encapsulates a set of notifying {@link CDOTransaction transaction} configuration options.
   *
   * @author Simon McDuff
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Options extends CDOView.Options
  {
    /**
     * @since 4.3
     */
    public static final CDOUndoDetector DEFAULT_UNDO_DETECTOR = CDOUndoDetector.ALL_FEATURES;

    /**
     * @since 4.5
     */
    public static final long DEFAULT_COMMIT_INFO_TIMEOUT = 60000;

    /**
     * Returns the {@link CDOTransaction transaction} of this options object.
     *
     * @since 4.0
     */
    public CDOTransaction getContainer();

    /**
     * Returns the undo detector of this transaction.
     *
     * @since 4.3
     */
    public CDOUndoDetector getUndoDetector();

    /**
     * Sets the undo detector of this transaction.
     *
     * @since 4.3
     */
    public void setUndoDetector(CDOUndoDetector undoDetector);

    /**
     * Returns a copy of the conflict resolver list of this transaction.
     */
    public CDOConflictResolver[] getConflictResolvers();

    /**
     * Sets the conflict resolver list of this transaction.
     */
    public void setConflictResolvers(CDOConflictResolver[] resolvers);

    /**
     * Adds a conflict resolver to the list of conflict resolvers of this transaction.
     */
    public void addConflictResolver(CDOConflictResolver resolver);

    /**
     * Removes a conflict resolver from the list of conflict resolvers of this transaction.
     */
    public void removeConflictResolver(CDOConflictResolver resolver);

    /**
     * Get the {@link CDOStaleReferenceCleaner} to be used to clean stale references when receiving
     * remote changes on invalidation.
     *
     * @since 4.4
     */
    public CDOStaleReferenceCleaner getStaleReferenceCleaner();

    /**
     * Set the {@link CDOStaleReferenceCleaner} to be used to clean stale references when receiving
     * remote changes on invalidation.
     *
     * @since 4.4
     */
    public void setStaleReferenceCleaner(CDOStaleReferenceCleaner staleReferenceCleaner);

    /**
     * Returns <code>true</code> if locks in this transaction will be released when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} are called, <code>false</code> otherwise.
     * <p>
     * The default value is <code>true</code>.
     *
     * @see #getAutoReleaseLocksExemptions()
     */
    public boolean isAutoReleaseLocksEnabled();

    /**
     * Specifies whether locks in this transaction will be released when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} are called.
     * <p>
     * If set to <code>false</code> all locks will be kept when {@link CDOTransaction#commit()} or
     * {@link CDOTransaction#rollback()} are called.
     * <p>
     * The default value is <code>true</code>.
     *
     * @see #getAutoReleaseLocksExemptions()
     */
    public void setAutoReleaseLocksEnabled(boolean on);

    /**
     * Returns the set of {@link EObject objects} that are to be treated as exemptions to the {@link #isAutoReleaseLocksEnabled()} option.
     * <p>
     * That means:
     * <p>
     * <ul>
     * <li> If {@link #isAutoReleaseLocksEnabled()} returns <code>true</code>, the locks on the objects in this set are <b>not</b> released
     *      when {@link CDOTransaction#commit()} or {@link CDOTransaction#rollback()} are called.
     * <li> If {@link #isAutoReleaseLocksEnabled()} returns <code>false</code>, the locks on the objects in this set <b>are</b> released nevertheless
     *      when {@link CDOTransaction#commit()} or {@link CDOTransaction#rollback()} are called.
     * </ul>
     * <p>
     * The returned set is unmodifiable. To modify the set use the {@link #clearAutoReleaseLocksExemptions() clearAutoReleaseLocksExemptions()},
     * {@link #addAutoReleaseLocksExemptions(boolean, EObject...) addAutoReleaseLocksExemption()},
     * and {@link #removeAutoReleaseLocksExemptions(boolean, EObject...) removeAutoReleaseLocksExemption()} methods.
     * <p>
     * <b>Implementation note</b>: This set stores weak references to the contained objects.
     *
     * @see #clearAutoReleaseLocksExemptions()
     * @see #addAutoReleaseLocksExemptions(boolean, EObject...)
     * @see #removeAutoReleaseLocksExemptions(boolean, EObject...)
     * @since 4.6
     */
    public Set<? extends EObject> getAutoReleaseLocksExemptions();

    /**
     * Returns <code>true</code> if the given object is treated as an exemption to the {@link #isAutoReleaseLocksEnabled()} option,
     * <code>false</code> otherwise.
     *
     * @see #getAutoReleaseLocksExemptions()
     * @since 4.6
     */
    public boolean isAutoReleaseLocksExemption(EObject object);

    /**
     * Clears the set of {@link EObject objects} that are to be treated as exemptions to the {@link #isAutoReleaseLocksEnabled()} option.
     *
     * @see #getAutoReleaseLocksExemptions()
     * @see #addAutoReleaseLocksExemptions(boolean, EObject...)
     * @see #removeAutoReleaseLocksExemptions(boolean, EObject...)
     * @since 4.6
     */
    public void clearAutoReleaseLocksExemptions();

    /**
     * Adds the given {@link EObject object} to the set of objects that are to be treated as exemptions to the {@link #isAutoReleaseLocksEnabled()} option.
     *
     * @see #getAutoReleaseLocksExemptions()
     * @see #clearAutoReleaseLocksExemptions()
     * @see #removeAutoReleaseLocksExemptions(boolean, EObject...)
     * @since 4.6
     */
    public void addAutoReleaseLocksExemptions(boolean recursive, EObject... objects);

    /**
     * Removes the given {@link EObject object} from the set of objects that are to be treated as exemptions to the {@link #isAutoReleaseLocksEnabled()} option.
     *
     * @see #getAutoReleaseLocksExemptions()
     * @see #clearAutoReleaseLocksExemptions()
     * @see #addAutoReleaseLocksExemptions(boolean, EObject...)
     * @since 4.6
     */
    public void removeAutoReleaseLocksExemptions(boolean recursive, EObject... objects);

    /**
     * Returns the number of milliseconds to wait for the transaction update when {@link CDOTransaction#commit()} is called.
     * <p>
     * Default value is 10000.
     *
     * @since 4.5
     */
    public long getCommitInfoTimeout();

    /**
    * Specifies the number of milliseconds to wait for the transaction update when {@link CDOTransaction#commit()} is called.
    * <p>
    * Default value is 10000.
    *
    * @since 4.5
    */
    public void setCommitInfoTimeout(long commitInfoTimeout);

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#setUndoDetector(CDOUndoDetector) undo detector} option has changed.
     *
     * @author Eike Stepper
     * @since 4.3
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface UndoDetectorEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#addConflictResolver(CDOConflictResolver) conflict resolvers} option has changed.
     *
     * @author Eike Stepper
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface ConflictResolversEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#setStaleReferenceCleaner(CDOStaleReferenceCleaner) stale reference cleaner} option has changed.
     *
     * @author Eike Stepper
     * @since 4.4
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface StaleReferenceCleanerEvent extends IOptionsEvent
    {
    }

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#setAutoReleaseLocksEnabled(boolean) auto release locks enabled} or
     * {@link Options#getAutoReleaseLocksExemptions() auto release locks exemptions} options have changed.
     *
     * @author Eike Stepper
     * @since 3.0
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface AutoReleaseLocksEvent extends IOptionsEvent
    {
      /**
       * An {@link AutoReleaseLocksEvent auto release locks options event} fired from transaction {@link CDOTransaction#options() options} when the
       * {@link Options#setAutoReleaseLocksEnabled(boolean) auto release locks enabled} option has changed.
       *
       * @author Eike Stepper
       * @since 4.6
       * @noextend This interface is not intended to be extended by clients.
       * @noimplement This interface is not intended to be implemented by clients.
       */
      public interface AutoReleaseLocksEnabledEvent extends AutoReleaseLocksEvent
      {
      }

      /**
       * An {@link AutoReleaseLocksEvent auto release locks options event} fired from transaction {@link CDOTransaction#options() options} when the
       * {@link Options#getAutoReleaseLocksExemptions() auto release locks exemptions} option has changed.
       *
       * @author Eike Stepper
       * @since 4.6
       * @noextend This interface is not intended to be extended by clients.
       * @noimplement This interface is not intended to be implemented by clients.
       */
      public interface AutoReleaseLocksExemptionsEvent extends AutoReleaseLocksEvent
      {
      }
    }

    /**
     * An {@link IOptionsEvent options event} fired from transaction {@link CDOTransaction#options() options} when the
     * {@link Options#setCommitInfoTimeout(long) commit info timeout} option has changed.
     *
     * @author Eike Stepper
     * @since 4.5
     * @noextend This interface is not intended to be extended by clients.
     * @noimplement This interface is not intended to be implemented by clients.
     */
    public interface CommitInfoTimeout extends IOptionsEvent
    {
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.8
   */
  public static final class CommitResult<T>
  {
    private final T result;

    private final CDOCommitInfo info;

    public CommitResult(T result, CDOCommitInfo info)
    {
      this.result = result;
      this.info = info;
    }

    public T getResult()
    {
      return result;
    }

    public CDOCommitInfo getInfo()
    {
      return info;
    }
  }
}
