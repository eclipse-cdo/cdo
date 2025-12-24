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
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_CheckoutWizard;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_HistoryIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_RepositoriesView;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_TimeMachineView;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_ConnectingDisconnecting;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_TransactionalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_DuplicatingCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_RenamingCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_SwitchingBranch;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_SwitchingBranch.Doc_SwitchNewBranch;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_SwitchingBranchPoint;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_UsingOfflineCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_UsingOfflineCheckouts.Doc_OfflineUpdate;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Checking Out from Repositories
 * <p>
 * To be able to work with the models in a CDO repository they need to be checked out from that repository.
 * A checkout represents a <i>named</i> combination of the following pieces of information:
 * <ul>
 * <li> A configured repository (see {@link Doc_CreatingRepositories} for details on how to create and configure repositories),
 * <li> a {@link CDOBranchPoint branch point} that determines from what {@link CDOBranchPoint#getBranch() branch} and from
 *      what {@link CDOBranchPoint#getTimeStamp() time} the models of the checkout are shown, and
 * <li> an object to be used as the root object of the checkout's model tree. By default this is the
 *      {@link CDOView#getRootResource() root resource} of the repository.
 * </ul>
 * <p>
 * The most obvious way to start the checkout process is to select a repository or branch in the {@link Doc_RepositoriesView},
 * open the context menu and select the Checkout action or the Checkout As action. Other ways are explained in {@link Doc_CheckoutWizard},
 * {@link Doc_DuplicatingCheckouts}, and {@link Doc_HistoryIntegration}.
 * <p>
 * The Checkout action involves no further dialog interaction, but immediately creates a {@link Doc_TransactionalCheckouts transactional} checkout
 * with default values for all settings. The following sections describe the different {@link Doc_CheckoutWizard checkout wizard} pages in detail.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc04_CheckingOut
{
  /**
   * Selecting a Repository
   * <p>
   * The first page of the checkout wizard shows the list of the repositories that are configured in the current workspace.
   * The New Repository button opens the repository wizard that is explained in {@link Doc_CreatingRepositories}.
   * <p align="center">{@image checkout-wizard-repository.png}
   * <p>
   * Note that this first wizard page is automatically skipped if the workbench selection implied a specific repository
   * at the time the wizard was opened, such as a repository or branch being selected in the {@link Doc_RepositoriesView}.
   * In this case the Back button goes back to the repository selection page and allows to change the original selection.
   * <p>
   * After selecting or creating the repository to check out from pressing the Next button or double-clicking
   * the selected repository advances to {@link Doc_CheckoutType}.
   */
  public class Doc_CheckoutRepository
  {
  }

  /**
   * Selecting the Checkout Type
   * <p>
   * The second page of the checkout wizard allows to select the type of the checkout to create: <p align="center">{@image checkout-wizard-type.png}
   * <p>
   * After selecting the desired checkout type pressing the Next button or double-clicking
   * the selected checkout type advances to {@link Doc_CheckoutBranchPoint}.
   * <p>
   * The following sections describe the different types of checkouts and under what conditions they are avilable.
   */
  public class Doc_CheckoutType
  {
    /**
     * Online Transactional Checkouts
     * <p>
     * An online transactional checkout is based on an online {@link CDOTransaction transaction} to a repository and supports
     * the modification of the repository's resource tree and model elements in the {@link Doc_ProjectExplorerIntegration Project Explorer},
     * as well as the modification of model resources in the supported model editors. This type of checkout always operates
     * on the floating {@link CDOBranch#getHead() head} of the configured branch and displays the changes that other users commit
     * to that branch in real-time.
     * <p>
     * Online transactional checkouts can be created for any type of repository. The term "online" reflects the fact that this type
     * of checkout requires a {@link Doc_ConnectingDisconnecting connected} repository. Online checkouts do not store model state
     * locally in any form. If the associated repository is disconnected the online checkout is closed, as well.
     * <p>
     * This is the default checkout type that provides CDO's unprecedented real-time model collaboration functionality.
     *
     * @see Doc_SwitchingBranch
     */
    public class Doc_TransactionalCheckouts
    {
    }

    /**
     * Online Historical Checkouts
     * <p>
     * An online historical checkout is based on an online {@link CDOView view} to a repository and supports
     * the auditing of the repository's resource tree and model elements via the {@link Doc_TimeMachineView}.
     * This type of checkout always operates on the configured time stamp of the configured branch. As the historical state of
     * the models at that time stamp is immutable the changes that other users commit
     * to that branch are not displayed.
     * <p>
     * Online historical checkouts can only be created for repositories with the {@link Doc_CreatingRepositories versioning modes}
     * <b>auditing</b> or <b>branching</b>. The term "online" reflects the fact that this type of checkout requires a
     * {@link Doc_ConnectingDisconnecting connected} repository. Online checkouts do not store model state
     * locally in any form. If the associated repository is disconnected the online checkout is closed, as well.
     * <p>
     * This is the checkout type that provides CDO's unprecedented real-time time travel functionality.
     *
     * @see Doc_SwitchingBranchPoint
     */
    public class Doc_HistoricalCheckouts
    {
    }

    /**
     * Offline Checkouts
     * <p>
     * An offline checkout is based on a {@link CDOTransaction transaction} on the local replication of a repository and supports
     * the modification of the repository's resource tree and model elements in the {@link Doc_ProjectExplorerIntegration Project Explorer},
     * as well as the modification of model resources in the supported model editors.
     * This type of checkout always operates on the floating {@link CDOBranch#getHead() head} of the configured branch.
     * The changes that other users commit to that branch are not displayed until an {@link Doc_OfflineUpdate update operation} is performed.
     * <p>
     * Offline checkouts can only be created for repositories with the {@link Doc_CreatingRepositories ID generation}
     * option <b>UUIDs</b>. The term "offline" reflects the fact that this type of checkout does not require a
     * {@link Doc_ConnectingDisconnecting connected} repository. Offline checkouts replicate the model state into a
     * local database. If the associated repository is disconnected the offline checkout stays fully functional (except for
     * operations such as Checkin or Update, which require an active repository connection).
     * <p>
     * This is the checkout type that provides CDO's unprecedented offline modeling functionality.
     *
     * @see Doc_UsingOfflineCheckouts
     */
    public class Doc_OfflineCheckouts
    {
    }
  }

  /**
   * Selecting the Branch Point
   * <p>
   * A repository may contain the states of models and model elements from many different branches and time stamps. The third page of the
   * checkout wizard allows to select the branch point of the checkout to create: <p align="center">{@image checkout-wizard-branchpoint.png}
   * <p>
   * For {@link Doc_TransactionalCheckouts transactional checkouts} the time stamp is implied to be the {@link CDOBranch#getHead() head}
   * of the selected branch, so the wizard page is reduced to: <p align="center">{@image checkout-wizard-branch.png}
   * <p>
   * After selecting the desired branch and optionally the desired time stamp pressing the Next button or double-clicking
   * the selected branch advances to {@link Doc_CheckoutRootObject}.
   *
   * @see Doc_SwitchingBranchPoint
   * @see Doc_SwitchingBranch
   * @see Doc_SwitchNewBranch
   */
  public class Doc_CheckoutBranchPoint
  {
  }

  /**
   * Selecting the Root Object
   * <p>
   * CDO does <b>not</b> support the concept of <i>partial checkouts</i>, i.e., all actions will always operate on all models and model elements in the
   * repository. But by selecting a root object different from the {@link CDOView#getRootResource() root resource} of the repository
   * the folders, resources, and model elements displayed under the checkout in the {@link Doc_ProjectExplorerIntegration Project Explorer}
   * can be limited to the children of this root object. Visually the checkout node is equivalent to the selected root object:
   * <p align="center">{@image checkout-wizard-rootobject.png}
   * <p>
   * After selecting the desired root object pressing the Next button or double-clicking
   * the selected root object advances to {@link Doc_CheckoutName}.
   */
  public class Doc_CheckoutRootObject
  {
  }

  /**
   * Naming the Checkout
   * <p>
   * The fifth and last page of the checkout allows to review the choices of the previous pages and to change the proposed name/label
   * of the checkout to create: <p align="center">{@image checkout-wizard-name.png}
   * <p>
   * Pressing the Finish button will close the wizard and create the new checkout as specified.
   *
   * @see Doc_RenamingCheckouts
   * @see Doc05_UsingCheckouts
   */
  public class Doc_CheckoutName
  {
  }
}
