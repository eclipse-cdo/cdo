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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.doc.online.EMFCompareGuide;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository.Property_supportingAudits;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository.Property_supportingBranches;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_CompareIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_HistoryIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_PropertySheetIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_RepositoriesView;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_SessionsView;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_TimeMachineView;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_ConnectingDisconnecting;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories.Doc_LocalRepositories;
import org.eclipse.emf.cdo.doc.users.Doc03_UsingBranches.Doc_CreatingBranches;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_HistoricalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_OfflineCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_TransactionalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundCompare;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Working with Checkouts
 * <p>
 * The key concept of working with models in the {@link Doc_ProjectExplorerIntegration Project Explorer} is a {@link CDOCheckout checkout}.
 * See {@link Doc04_CheckingOut} for details on how to to create {@link Doc_TransactionalCheckouts online transactional checkouts},
 * {@link Doc_HistoricalCheckouts online historical checkouts}, and {@link Doc_OfflineCheckouts offline checkouts}.
 * <p>
 * A checkout represents a <i>named</i> combination of the following pieces of information:
 * <ul>
 * <li> A configured repository (see {@link Doc_CreatingRepositories} for details on how to create and configure repositories),
 * <li> a {@link CDOBranchPoint branch point} that determines from what {@link CDOBranchPoint#getBranch() branch} and from
 *      what {@link CDOBranchPoint#getTimeStamp() time} the models of the checkout are shown, and
 * <li> an object to be used as the root object of the checkout's model tree. By default this is the
 *      {@link CDOView#getRootResource() root resource} of the repository.
 * </ul>
 * <p>
 * Once created, checkouts are displayed in the Project Explorer like workspace projects and decorated with a small blue repository image.
 * In contrast to workspace projects the displayed checkouts have no physical representation in the workspace. <p align="center">{@image project-explorer.png}
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc05_UsingCheckouts
{
  /**
   * Renaming Checkouts
   * <p>
   * CDO supports renaming an existing checkout, i.e., changing its label, at any time. A checkout can be renamed by selecting it
   * and pressing the F2 key or selecting the Rename action in the context menu. The following dialog will pop up:
   * <p align="center">{@image checkout-rename.png}
   * <p>
   * The dialog can only be finished with the OK button if the checkout label is either unchanged or changed to a not existing label.
   */
  public class Doc_RenamingCheckouts
  {
  }

  /**
   * Duplicating Checkouts
   * <p>
   * Sometimes it is useful to have two or more checkouts from the same repository in the Project Explorer.
   * A checkout can be duplicated by selecting it, opening the context menu, and selecting the Duplicate Checkout action.
   * The result is an identical checkout with a unique default label: <p align="center">{@image checkout-duplicate.png}
   */
  public class Doc_DuplicatingCheckouts
  {
  }

  /**
   * Opening and Closing Checkouts
   * <p>
   * To work with a configured checkout the checkout must be in <i>opened</i> state. Directly after creation a checkout is
   * open. Directly after the startup of Eclipse all checkouts are in <i>closed</i> state.
   * <p>
   * If a checkout is closed it can be opened by double-clicking it or by selecting Open Checkout in its context menu.
   * <p>
   * If a checkout is open it can be closed by selecting Close Checkout in its context menu.
   */
  public class Doc_OpeningClosing
  {
  }

  /**
   * Deleting Checkouts
   * <p>
   * An existing checkout can be deleted by selecting that checkout and pressing the Del key or selecting
   * the Delete action in the context menu. Several checkouts can be deleted at once.
   * The following dialog will pop up: <p align="center">{@image checkout-delete.png}
   * <p>
   * Deleting a checkout does not delete the associated repository or any data in it.
   * If the "Delete checkout contents on disk" checkbox is selected (i.e., checked) the locally replicated data of
   * the possibly selected offline checkouts is deleted permanently!
   */
  public class Doc_DeletingCheckouts
  {
  }

  /**
   * Switching the Branch of a Checkout
   * <p>
   * Online transactional checkouts from repositories that are configured with the versioning mode <b>branching</b> can be switched
   * to other {@link CDOBranch branches} by selecting the checkout, opening the context menu and selecting the Switch To sub menu:
   * <p align="center">{@image switch-to-branch.png}
   * <p>
   * See {@link Property_supportingBranches} for instructions on how to enable branching for
   * remote repositories, {@link Doc_LocalRepositories} for instructions on how to enable branching for local repositories.
   * <p>
   * The Switch To sub menu consists of the following action groups:
   * <ul>
   * <li> The New Branch action is explained in {@link Doc_SwitchNewBranch}.
   * <li> A history list of actions that switch to branches that were previously used in the selected checkout.
   * <li> The Other Branch action is explained in {@link Doc_SwitchOtherBranch}.
   * <li> A list of actions that switch to the branches of other transactional checkouts from the same repository as the selected checkout.
   * </ul>
   * <p>
   * Switching the branch of a checkout is also possible by dragging elements and dropping them onto the target checkout without
   * holding any modifier keys pressed. The following elements can be dropped onto the target:
   * <ul>
   * <li> Branches of the same repository as the target checkout
   * <li> The repository of the target checkout (inducing its main branch)
   * <li> Online transactional checkouts from the same repository as the selected checkout (inducing their branches)
   * </ul>
   * <p>
   * When a checkout is switched to a different branch the nested resource nodes and model elements, as well as all the contents
   * of all open resource editors are changed instantly to reflect the state of the new branch.
   *
   * @see Doc_RepositoriesView
   */
  public class Doc_SwitchingBranch
  {
    /**
     * Switching to a New Branch
     * <p>
     * Online transactional checkouts can be switched to a newly created branch with the Switch To -> New Branch context menu action.
     * The New Branch dialog will pop up: <p align="center">{@image branch-create.png}
     * <p>
     * See {@link Doc_CreatingBranches} for an explanation on how to use the New Branch dialog.
     */
    public class Doc_SwitchNewBranch
    {
    }

    /**
     * Switching to an Other Branch
     * <p>
     * Online transactional checkouts can be switched to any other branch with the Switch To -> Other Branch context menu action.
     * The Select Branch Point dialog (without a time stamp area) will pop up: <p align="center">{@image branch-select.png}.
     */
    public class Doc_SwitchOtherBranch
    {
    }
  }

  /**
   * Switching the Time Stamp of a Checkout
   * <p>
   * Online historical checkouts from repositories that are configured with the versioning modes <b>auditing</b> or <b>branching</b> can be switched
   * to other {@link CDOBranchPoint branch points} by selecting the checkout, opening the context menu and selecting the Switch To sub menu:
   * <p align="center">{@image switch-to-branchpoint.png}
   * <p>
   * See {@link Property_supportingAudits} for instructions on how to enable auditing remote repositories,
   * {@link Property_supportingBranches}for instructions on how to enable branching for remote repositories,
   * or {@link Doc_LocalRepositories} for instructions on how to enable auditing or branching for local repositories.
   * <p>
   * The Switch To sub menu consists of the following action groups:
   * <ul>
   * <li> A history list of actions that switch to branch points that were previously used in the selected checkout.
   * <li> The Other Branch Point action is explained in {@link Doc_SwitchOtherBranchPoint}.
   * <li> The Commit action is explained in {@link Doc_SwitchCommit}.
   * <li> A list of actions that switch to the branch points of other historical checkouts from the same repository as the selected checkout.
   * </ul>
   * <p>
   * Switching the branch point of a checkout is also possible by dragging elements and dropping them onto the target checkout without
   * holding any modifier keys pressed. The following elements can be dropped on to the target:
   * <ul>
   * <li> Branches of the same repository as the target checkout (inducing their {@link CDOBranch#getHead() heads}
   * <li> The repository of the target checkout (inducing the head of its main branch)
   * <li> Online historical checkouts from the same repository as the selected checkout (inducing their branch points)
   * <li> Commits, for example from the {@link Doc_HistoryIntegration History view}
   * </ul>
   * <p>
   * Switching the branch point of a checkout is also possible by using the {@link Doc_TimeMachineView}.
   * <p>
   * When a checkout is switched to a different branch point the nested resource nodes and model elements, as well as all the contents
   * of all open resource editors are changed instantly to reflect the state of the new branch point.
   *
   * @see Doc_RepositoriesView
   * @see Doc_TimeMachineView
   * @see Doc_HistoryIntegration
   */
  public class Doc_SwitchingBranchPoint
  {
    /**
     * Switching to an Other Branch Point
     * <p>
     * Online historical checkouts can be switched to any other branch point with the Switch To -> Other Branch Point context menu action.
     * The Select Branch Point dialog (with a time stamp area) will pop up: <p align="center">{@image branchpoint-select.png}.
     */
    public class Doc_SwitchOtherBranchPoint
    {
    }

    /**
     * Switching to a Commit
     * <p>
     * Online historical checkouts can be switched to a commit with the Switch To -> Commit context menu action.
     * The Select Commit dialog will pop up: <p align="center">{@image commit-select.png}.
     */
    public class Doc_SwitchCommit
    {
    }
  }

  /**
   * Comparing Checkouts
   * <p>
   * All types of checkouts can be compared with other {@link CDOBranchPoint#getTimeStamp() time stamps} or
   * other {@link CDOBranchPoint#getBranch() branches} from the same repository
   * by selecting the checkout, opening the context menu and selecting the Compare With sub menu:
   * <p align="center">{@image compare-with.png}
   * <p>
   * The Compare With sub menu consists of the following action groups:
   * <ul>
   * <li> A history list of actions that compare with branch points that were previously used in the selected checkout.
   * <li> The Other Branch action is explained in {@link Doc_CompareOtherBranch}.
   * <li> The Other Branch Point action is explained in {@link Doc_CompareOtherBranchPoint}.
   * <li> The Commit action is explained in {@link Doc_CompareCommit}.
   * <li> A list of actions that compare with the branch points of other checkouts from the same repository as the selected checkout.
   * </ul>
   * <p>
   * Comparing a checkout is also possible by dragging elements and dropping them onto the target checkout with
   * the Shift and Ctrl keys pressed. The following elements can be dropped onto the target:
   * <ul>
   * <li> Branches of the same repository as the target checkout (inducing their {@link CDOBranch#getHead() heads}
   * <li> The repository of the target checkout (inducing the head of its main branch)
   * <li> Checkouts from the same repository as the selected checkout (inducing their branch points)
   * <li> Commits, for example from the {@link Doc_HistoryIntegration History view}
   * </ul>
   * <p>
   * When a checkout is compared with a different branch point an EMF Compare editor is opened with the left side
   * showing the selected compare source (e.g., the drag source) and the right side showing the selected compare target
   * (e.g., the drop target): <p align="center">{@image compare.png}
   *
   * @see Doc_RepositoriesView
   * @see Doc_HistoryIntegration
   * @see Doc_CompareIntegration
   * @see EMFCompareGuide
   */
  public class Doc_ComparingCheckouts
  {
    /**
     * Comparing with an Other Branch
     * <p>
     * Checkouts can be compared with any other branch with the Compare With -> Other Branch context menu action.
     * The Select Branch Point dialog (without a time stamp area) will pop up: <p align="center">{@image branch-select.png}.
     */
    public class Doc_CompareOtherBranch
    {
    }

    /**
     * Comparing with an Other Branch Point
     * <p>
     * Checkouts can be compared with any other branch point with the Compare With -> Other Branch Point context menu action.
     * The Select Branch Point dialog (with a time stamp area) will pop up: <p align="center">{@image branchpoint-select.png}.
     */
    public class Doc_CompareOtherBranchPoint
    {
    }

    /**
     * Comparing with a Commit
     * <p>
     * Checkouts can be compared with a commit with the Compare With -> Commit context menu action.
     * The Select Commit dialog will pop up: <p align="center">{@image commit-select.png}.
     */
    public class Doc_CompareCommit
    {
    }
  }

  /**
   * Merging Checkouts
   * <p>
   * Offline and online transactional checkouts can be merged from other {@link CDOBranchPoint#getTimeStamp() time stamps} or
   * other {@link CDOBranchPoint#getBranch() branches} from the same repository
   * by selecting the checkout, opening the context menu and selecting the Merge From sub menu:
   * <p align="center">{@image merge-from.png}
   * <p>
   * The Merge From sub menu consists of the following action groups:
   * <ul>
   * <li> A history list of actions that merge from branch points that were previously used in the selected checkout.
   * <li> The Other Branch action is explained in {@link Doc_MergeOtherBranch}.
   * <li> The Other Branch Point action is explained in {@link Doc_MergeOtherBranchPoint}.
   * <li> The Commit action is explained in {@link Doc_MergeCommit}.
   * <li> A list of actions that merge from the branch points of other checkouts from the same repository as the selected checkout.
   * </ul>
   * <p>
   * Merging a checkout is also possible by dragging elements and dropping them onto the target checkout with
   * the Shift and Ctrl keys pressed. The following elements can be dropped onto the target:
   * <ul>
   * <li> Branches of the same repository as the target checkout (inducing their {@link CDOBranch#getHead() heads}
   * <li> The repository of the target checkout (inducing the head of its main branch)
   * <li> Checkouts from the same repository as the selected checkout (inducing their branch points)
   * <li> Commits, for example from the {@link Doc_HistoryIntegration History view}
   * </ul>
   * <p>
   * When a checkout is merged from a different branch point an EMF Merge editor is opened with the left side
   * showing the selected merge source (e.g., the drag source) and the right side showing the selected merge target
   * (e.g., the drop target): <p align="center">{@image merge.png}
   *
   * @see Doc_RepositoriesView
   * @see Doc_HistoryIntegration
   * @see Doc_CompareIntegration
   * @see EMFCompareGuide
   */
  public class Doc_MergingCheckouts
  {
    /**
     * Merging from an Other Branch
     * <p>
     * Checkouts can be merged from any other branch with the Merge From -> Other Branch context menu action.
     * The Select Branch Point dialog (without a time stamp area) will pop up: <p align="center">{@image branch-select.png}.
     */
    public class Doc_MergeOtherBranch
    {
    }

    /**
     * Merging from an Other Branch Point
     * <p>
     * Checkouts can be merged from any other branch point with the Merge From -> Other Branch Point context menu action.
     * The Select Branch Point dialog (with a time stamp area) will pop up: <p align="center">{@image branchpoint-select.png}.
     */
    public class Doc_MergeOtherBranchPoint
    {
    }

    /**
     * Merging from a Commit
     * <p>
     * Checkouts can be merged from a commit with the Merge From -> Commit context menu action.
     * The Select Commit dialog will pop up: <p align="center">{@image commit-select.png}.
     */
    public class Doc_MergeCommit
    {
    }
  }

  /**
   * Showing Checkouts in Other Views
   * <p>
   * Checkouts can be shown in a number of other views depending on the type of the checkout
   * by selecting them, opening their context menu, opening the Show In sub menu, and selecting one of the Show In actions.
   * <p>
   * Online transactional checkouts can be shown in the following views: <p align="center">{@image checkout-transactional-showin.png}
   * <p>
   * Online historical checkouts can be shown in the following views: <p align="center">{@image checkout-historical-showin.png}
   * <p>
   * Offline checkouts can be shown in the following views: <p align="center">{@image checkout-offline-showin.png}
   */
  public class Doc_CheckoutShowIn
  {
    /**
     * Showing Checkouts in the CDO Sessions View
     * <p>
     * Online checkouts can be shown in the {@link Doc_SessionsView CDO Sessions view}: <p align="center">{@image sessions-view.png}
     * <p>
     * A new {@link CDOSession session} with the same target repository as the selected checkout is opened in the CDO Sessions view.
     * A new {@link CDOTransaction transaction} is opened on that session, pointing at the same branch point as the selected checkout.
     */
    public class Doc_CheckoutShowInSessions
    {
    }

    /**
     * Showing Checkouts in the CDO Time Machine View
     * <p>
     * Online histrical checkouts can be shown in the {@link Doc_TimeMachineView CDO Time Machine view}: <p align="center">{@image timemachine-view.png}
     */
    public class Doc_CheckoutShowInTimeMachine
    {
    }

    /**
     * Showing Checkouts in the CDO Server Browser
     * <p>
     * Offline checkouts can be shown in the CDO Server Browser: <p align="center">{@image server-browser.png}
     * <p>
     * The CDO Server Browser allows to introspect the internal data of the locally replicated repository
     * in a web browser. It is only meant to be used for test and debug purposes.
     * It is <b>not</b> meant to be a production tool that would scale to arbitrary repository sizes!
     */
    public class Doc_CheckoutShowInServerBrowser
    {
    }

    /**
     * Showing Checkouts in the Properties View
     * <p>
     * All checkouts can be shown in the {@link Doc_PropertySheetIntegration Properties view}: <p align="center">{@image property-sheet.png}
     */
    public class Doc_CheckoutShowInProperties
    {
    }

    /**
     * Showing Checkouts in the History View
     * <p>
     * All checkouts can be shown in the {@link Doc_HistoryIntegration History view}: <p align="center">{@image history.png}
     * <p>
     * This is particularly useful if the "Link with Editor and Selection" button in the toolbar of the History view
     * is not enabled and the history page does not automatically adjust to the workbench selection.
     */
    public class Doc_CheckoutShowInHistory
    {
    }

    /**
     * Showing Checkouts in the System Explorer
     * <p>
     * All checkouts can be shown in the System Explorer: <p align="center">{@image system-explorer-checkout.png}
     * <p>
     * Each checkout owns a dedicated directory under .metadata of the current workspace. This directory contains
     * the checkout.properties file that contains the configuration of the checkout. Offline checkouts also store the database
     * of the locally replicated repository and baseline data of dirty model elements in this directory.
     */
    public class Doc_CheckoutShowInSystemExplorer
    {
    }
  }

  /**
   * Working with Offline Checkouts
   * <p>
   * <b>Online</b> checkouts operate directly on the data stored in their repository and therefore require an active network
   * connection; i.e., the repository must be in {@link Doc_ConnectingDisconnecting connected state}.
   * They are ideally suited to support real-time collaboration on models, but they can only be open when the underlying repository
   * is reachable, which is not necessarily always the case for remote repositories.
   * <p>
   * <b>Offline</b> checkouts, in contrast, operate on locally replicated data of their repository and therefore do not require an active network
   * connection; i.e., the repository can be in {@link Doc_ConnectingDisconnecting disconnected state}.
   * They are ideally suited to support offline work on models even during periods when the underlying repository is not reachable.
   * Collaboration on the contained models is restricted to explicit synchronization times, i.e., when {@link Doc_OfflineUpdate updating}
   * the checkout from remote or {@link Doc_OfflineCheckin checking it in} to remote.
   * <p>
   * Offline checkouts, as well as all their models and model elements are decorated with their local editing state,
   * i.e., <b>clean</b>, <b>dirty</b>, or <b>new</b>.
   * <p>
   * Apart from these subtle differences offline checkouts are designed to behave in a very similar way as online checkouts.
   * In addition a few special operations are available for them, which are explained in the following nested sections.
   *
   * @see Doc_OfflineCheckouts
   */
  public class Doc_UsingOfflineCheckouts
  {
    /**
     * Updating an Offline Checkout
     * <p>
     * Because the models and model elements in an offline checkout are not updated in real-time they need to be updated manually
     * at appropriate times, e.g., before attempting to {@link Doc_OfflineCheckin check it in}  to a remotely modified repository.
     * <p>
     * To update an offline checkout from remote the checkout is selected and the Update action chosen from its context menu.
     * The update process tries to merge all trivial conflicts they may arise. Trivial conflicts are:
     * <ul>
     * <li> Changes to multi-valued {@link EStructuralFeature features} on both sides of the same model element.
     * <li> Changes to different single-valued {@link EStructuralFeature features} on both sides of the same model element.
     * </ul>
     * <p>
     * If non-trivial changes are detected, i.e., changes to the same single-valued {@link EStructuralFeature feature} on both sides of the
     * same model element, an EMF Merge editor is opened: <p align="center">{@image merge.png}
     * <p>
     * The left side of the merge editor shows the remote repository and the right side shows the selected offline checkout.
     * Saving the merge editor applies the needed changes to the local model elements and closed the merge editor. The update/merge
     * process can be canceled by closing the merge editor without saving it.
     * <p>
     * Updating an offline checkout is a <b>remote operation</b>.
     *
     * @see Doc_CompareIntegration
     * @see Doc_BackgroundCompare
     */
    public class Doc_OfflineUpdate
    {
    }

    /**
     * Checking In an Offline Checkout
     * <p>
     * After having worked with the models and model elements in an offline checkout for a while it may seem appropriate
     * to check in the changes that accumulated during this period back into the remote repository.
     * <p>
     * To update an offline checkout from remote the checkout is selected and the Checkin action chosen from its context menu.
     * <p>
     * Checking in does not strictly
     * require the local offline checkout to be up-to-date as long as the local changes and the remote changes are not conflicting
     * (i.e., neither trivial nor non-trivial conflicts exist). If conflicts are detected during the check-in process the following
     * dialog pops up, asking to {@link Doc_OfflineUpdate update} the checkout first: <p align="center">{@image checkin-error.png}
     * <p>
     * Checking in an offline checkout is a <b>remote operation</b>.
     */
    public class Doc_OfflineCheckin
    {
    }

    /**
     * Reverting an Offline Checkout
     * <p>
     * Sometimes it may seem useful to discard all local editing state of the models and model elements in an offline checkout,
     * effectively reverting the checkout to the most recently {@link Doc_OfflineUpdate updated} state.
     * <p>
     * To revert an offline checkout the checkout is selected and the Revert action chosen from its context menu.
     * The following confirmation dialog will pop up: <p align="center">{@image revert.png}
     * <p>
     * Reverting an offline checkout is a <b>local operation</b>.
     */
    public class Doc_OfflineRevert
    {
    }
  }
}
