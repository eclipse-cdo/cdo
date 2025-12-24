/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.doc.users;

import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_TransactionalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc07_UsingModels.Doc_EditingModelElementsEditor;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Collaborating in Real-Time
 * <p>
 * CDO supports real-time collaboration on models by transferring the changes that one user
 * {@link CDOTransaction#commit(org.eclipse.core.runtime.IProgressMonitor) commits} to the repository to all
 * other users connected to the same repository and transparently weaving those changes into their model copies.
 * <p align="center">{@image collaborating.png}
 * <p>
 * With CDO the local model copies (in particular with {@link Doc_TransactionalCheckouts online transactional checkouts}) do not need to be
 * <i>updated</i> manually; they are automatically updated (almost) at the time they are changed by other users.
 * <p>
 * As real-time collaboration relies on committing a {@link CDOTransaction transaction} it applies only to
 * online transactional checkouts and the {@link Doc_EditingModelElementsEditor editors}
 * opened on online transactional models. Saving a model editor commits the underlying transaction.
 * <p>
 * The data integrity of the models and model elements in a repository is guaranteed by {@link LockType#WRITE write locks}
 * that are acquired <b>per model element</b>. {@link LockType#READ Read locks} and {@link LockType#OPTION write options},
 * as well as {@link CDOView#enableDurableLocking() durable locks} are supported by the core-level APIs
 * but not by the CDO Explorer's user interface.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc08_Collaborating
{
  /**
   * Optimistic Locking
   * <p>
   * By default model elements are locked optimistically, that is, the CDO server <i>implicitly</i> acquires and releases locks while executing
   * a commit operation. These implicit locks are not visible to the committing user or any other user of the same repository.
   * <p>
   * Optimistic locking provides for the highest possible degree of concurrency but it also comes with a non-zero risk of commit conflicts
   * that are only detected when a commit operation is executed by the CDO server and, as a consequence, rejected. Because of
   * {@link Doc_EarlyConflictDetection} the risk of conflicts that are detected that late in the commit process is generally much lower
   * than, for example, in pure database-based applications.
   * <p>
   * To completely eliminate the risk of commit conflicts {@link Doc_PessimisticLocking} must be used.
   */
  public class Doc_OptimisticLocking
  {
    /**
     * Early Conflict Detection
     * <p>
     * As the local model copies of a user are automatically updated (almost) at the time they are changed by other users
     * CDO can anticipate the conflict potential of the local changes early, in particular before an attempt to commit these changes is even made.
     * The {@link Doc_EditingModelElementsEditor CDO Model Editor} decorates such conflicting model elements with a red-colored font,
     * indicating that the underlying {@link CDOTransaction transaction} can not be successfully committed anymore.
     * <p align="center">{@image early-conflict.png}
     * <p>
     * {@link Doc_AutomaticConflictResolution} and {@link Doc_InteractiveConflictResolution}, if enabled, may have an impact on what exact types
     * of changes are considered a conflict.
     */
    public class Doc_EarlyConflictDetection
    {
    }

    /**
     * Automatic Conflict Resolution
     * <p>
     * Each time a local transaction is notified of a remote change by the CDO server and local conflicts are detected (see {@link Doc_EarlyConflictDetection})
     * these conflicts are categorized as being either <i>trivial</i> conflicts or <i>non-trivial</i> conflicts. Trivial conflicts are:
     * <ul>
     * <li> Changes to multi-valued {@link EStructuralFeature features} on both sides (local and remote) of the same model element.
     * <li> Changes to different single-valued {@link EStructuralFeature features} on both sides (local and remote) of the same model element.
     * </ul>
     * <p>
     * Trivial conflicts are merged automatically into the local transaction, i.e., no user interaction is involved.
     * <p>
     * When non-trivial changes are detected, i.e., changes to the same single-valued {@link EStructuralFeature feature} on both sides (local and remote)
     * of the same model element, automatic conflict resolution is suspended for all model elements until the next local commit operation.
     * During this period all incoming change notifications are accumulated and remembered for possible {@link Doc_InteractiveConflictResolution}
     * at commit time.
     */
    public class Doc_AutomaticConflictResolution
    {
    }

    /**
     * Interactive Conflict Resolution
     * <p>
     * If {@link Doc_AutomaticConflictResolution} has detected non-trivial conflicts in a local {@link CDOTransaction transaction} and
     * an attempt is made to commit this transaction the following dialog pops up:
     * <p align="center">{@image late-conflict.png}
     * <p>
     * The dialog shows an overview of how many local model elements are added, changed, and removed. One of several conflict resolution
     * actions has to be selected by the user:
     * <ul>
     * <li> If the Merge action is selected an EMF Merge editor is opened with the left side showing the remote changes and
     *      the right side showing the local changes:
     *      <p align="center">{@image merge.png}
     *      <p>
     *      Saving this merge editor commits the merged local transaction. Note that new non-trivial conflicts may have been detected
     *      in the meantime, in which case interactive conflict resolution is triggered again.
     * <li> If the Rollback action is selected the local transaction is {@link CDOTransaction#rollback() rolled back} and the local model copies
     *      are automatically updated to their latest remote versions. As a result all local changes will be lost and eventually need to be
     *      re-applied and committed again.
     * </ul>
     */
    public class Doc_InteractiveConflictResolution
    {
    }
  }

  /**
   * Pessimistic Locking
   * <p>
   * Sometimes it seems not desirable to risk commit conflicts as they can occur with {@link Doc_OptimisticLocking}.
   * In these cases CDO supports the acquisition of <i>explicit</i> locks on selected models (see {@link Doc_TreeLocking}) and model elements.
   * <p>
   * Pessimistic locking support consists of:
   * <ul>
   * <li> Lock and unlock actions
   * <li> Lock state visualization
   * </ul>
   * <p>
   * Whether custom user interface components, such as model editors or views, support local actions and/or lock state visualization depends
   * on the implementation of those components. The CDO Model Editor's context menu offers lock actions for model elements that are not locked
   * by anyone and unlock actions for model elements that are locked by the current user. Both the CDO Model Editor and the
   * {@link Doc_ProjectExplorerIntegration} support lock state visualization by decorating model elements that are locked by the current user
   * with a green lock icon (indicating that they can be modified) and model elements that are locked by other users with a red lock icon
   * (indicating that they can not be modified):
   * <p align="center">{@image pessimistic-locking.png}
   * <p>
   * Note that a CDO editor generally operates in the context of a separate transaction, in particular not in the context of the
   * {@link CDOView read-only view} of the associated checkout, which explains why, in the screen shot above, both checkouts show the
   * locked model elements with a red lock icon decoration. In other words, while a model element is locked in a CDO editor it can not be
   * modified directly in the associated checkout via the Project Explorer.
   */
  public class Doc_PessimisticLocking
  {
    /**
     * Tree Locking
     * <p>
     * Sometimes it is desirable to lock not just a single model element but to atomically lock the tree of model elements rooted at the
     * selected model element. The CDO Model Editor's context menu offers a Lock Tree action for model elements that are not locked by
     * anyone and an Unlock Tree action for model elements that are locked by the current user.
     */
    public class Doc_TreeLocking
    {
    }
  }

  /**
   * Automatic Locking
   * <p>
   * With automatic locking turned on for a particular {@link CDOTransaction transaction} write locks are automatically acquired
   * for model elements at the time these model elements are modified the first time.
   * <p>
   * Automatic locking is not yet supported for checkouts.
   */
  public class Doc_AutomaticLocking
  {
  }

  /**
   * Automatic Committing
   * <p>
   * With automatic committing turned on for a particular {@link CDOTransaction transaction} that transaction is automatically committed
   * each time a model element is modified. This can be very useful when the primary purpose of a repository is to support real-time
   * collaboration between a number of users.
   * <p>
   * On the other hand with automatic committing multiple logically related changes are no longer
   * isolated in single composed commits. This  can be especially undesirable in repositories with auditing or branching support
   * because the databases of these types of repositories monotonously grow with the number of commits.
   * <p>
   * Automatic committing is not yet supported for checkouts.
   */
  public class Doc_AutomaticCommitting
  {
  }
}
