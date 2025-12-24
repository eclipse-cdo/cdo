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

import org.eclipse.emf.cdo.doc.online.EMFCompareGuide;
import org.eclipse.emf.cdo.doc.online.EMFFormsGuide;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories.Doc_LocalRepositories;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_HistoricalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_OfflineCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_TransactionalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_ComparingCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc07_UsingModels.Doc_EditingModelElements;
import org.eclipse.emf.cdo.doc.users.Doc09_TechnicalBackground.Doc_BackgroundCompare;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.view.CDOView.Options;

/**
 * Elements of the User Interface
 * <p>
 * The CDO user interface consists of a number of CDO-specific views, editors, and preference pages,
 * as well as some integrations with existing Eclipse views. These user interface elements can be added to
 * and used in any Eclipse perspective, or more comprehensively in the {@link Doc_ExplorerPerspective CDO Explorer}
 * perspective.
 * <p>
 * The following sections describe the various user interface elements and explain their purpose.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc01_UserInterface
{
  /**
   * CDO Explorer Perspective
   * <p>
   * The CDO Explorer perspective is a convenient selection of those views and shortcuts that are most
   * commonly used when working with CDO. It has the following default layout: <p align="center">{@image explorer-perspective.png}
   */
  public class Doc_ExplorerPerspective
  {
  }

  /**
   * CDO Repositories View
   * <p>
   * The CDO Repositories view displays the various repositories and connections that are configured for use in the
   * current workspace. It allows to create new repositories and connections, rename or delete existing ones,
   * manage the branches in them, and finally checking out from them: <p align="center">{@image repositories-view.png}
   * <p>
   * In contrast to sessions in the {@link Doc_SessionsView} repositories and connections configured in the
   * CDO Repositories view are remembered across Eclipse restarts.
   *
   * @see Doc02_ManagingRepositories
   * @see Doc04_CheckingOut
   *
   */
  public class Doc_RepositoriesView
  {
  }

  /**
   * CDO Administration View
   * <p>
   * The CDO Administration view displays configured servers and lists their discovered repositories in real-time.
   * New servers can be added, existing ones can be removed. New repositories can be created in the servers and existing
   * ones can be deleted. If a repository supports security, i.e., authentication and authorization, an editor for the
   * various security concepts, such as users, groups, roles, and permissions can be opened on it:
   * <p align="center">{@image administration-view.png}
   */
  public class Doc_AdministrationView
  {
  }

  /**
   * CDO Collaboration View
   * <p>
   * The CDO Collaboration view displays the other users that are connected to the current repository, more
   * exactly their sessions. When double-clicking a user in this view a dialog pops up asking you for a short message
   * to send to the selected user. The targeted user needs to be subscribed to receiving messages. This view is a very
   * simple example of a custom chat protocol on top of the CDO protocol: <p align="center">{@image collaboration-view.png}
   * <p>
   * The details of remote message subscriptions and how to contribute custom message handlers is explained
   * in {@link CDORemoteSessionManager}.
   */
  public class Doc_CollaborationView
  {
  }

  /**
   * CDO Watch List View
   * <p>
   * The CDO Watch List view is a simple example of {@link Options#addChangeSubscriptionPolicy(org.eclipse.emf.cdo.view.CDOAdapterPolicy)
   * change subscriptions}. Model objects can be dragged from anywhere
   * and dropped on the watch list view to create a table entry that shows the latest changes from any user to the
   * selected model object: <p align="center">{@image watchlist-view.png}
   */
  public class Doc_WatchListView
  {
  }

  /**
   * CDO Time Machine View
   * <p>
   * The CDO Time Machine view offers a slider control that can be used to look at models at different historical times.
   * The time slider is only enabled when a model or model object in a historical checkout is selected:
   * <p align="center">{@image timemachine-view.png}
   * <p>
   * The resource tree in the selected historical checkout as well as the contents of all editors opened on this checkout
   * changes in real-time while the slider control is dragged back and forth in time.
   *
   * @see Doc_HistoricalCheckouts
   */
  public class Doc_TimeMachineView
  {
  }

  /**
   * CDO Sessions View
   * <p>
   * The CDO Sessions view provides a rather technical approach to working with the models in repositories and
   * is mostly superseded now by more convenient functionality of the {@link Doc_RepositoriesView CDO Repositories view}
   * and the {@link Doc_ProjectExplorerIntegration Project Explorer integration}. Nevertheless, it can still be used to
   * open sessions to remote repositories, open model views and transactions on sessions, browse the resource trees
   * of repositories, open model editors on selected resources, and more: <p align="center">{@image sessions-view.png}
   * <p>
   * Please note that sessions opened in the CDO Sessions view are <b>not</b> remembered across Eclipse restarts.
   */
  public class Doc_SessionsView
  {
  }

  /**
   * CDO Server Browser
   * <p>
   * The CDO Server Browser allows to introspect the internal data of {@link Doc_LocalRepositories local repositories} or
   * locally replicated repositories of {@link Doc_OfflineCheckouts offline checkouts}
   * in a web browser: <p align="center">{@image server-browser.png}
   * <p>
   * It is only meant to be used for test and debug purposes.
   * It is <b>not</b> meant to be a production tool that would scale to arbitrary repository sizes!
   */
  public class Doc_ServerBrowser
  {
  }

  /**
   * CDO Checkout Wizard
   * <p>
   * CDO provides an Eclipse Import wizard to create checkouts from repositories without opening the
   * {@link Doc_RepositoriesView}: <p align="center">{@image import-wizard.png}
   *
   * @see Doc04_CheckingOut
   */
  public class Doc_CheckoutWizard
  {
  }

  /**
   * CDO Model Editor
   * <p>
   * The CDO model editor is a generic editor for the model resources in a repository. It displays the contents of the resource
   * in an editable, structured tree and allows to modify the tree structure of the resource via the New Child, New Sibling,
   * or Delete context menu actions. Model elements can be moved or copied using drag and drop. Their attributes and references
   * can be edited in the {@link Doc_PropertySheetIntegration Properties view}.
   * <p>
   * The CDO model editor is a generated EMF editor with some additional features such as real-time locking decoration and early conflict
   * detection: <p align="center">{@image model-editor.png}
   * <p>
   * Whether the CDO model editor actually supports editing the displayed model depends on whether the checkout of the model is
   * {@link Doc_TransactionalCheckouts transactional} or not.
   */
  public class Doc_ModelEditor
  {
  }

  /**
   * Project Explorer Integration
   * <p>
   * As of CDO 4.4 the preferred way to work with models in or from repositories is the Project Explorer integration.
   * This integration is centered around the concept of a <i>checkout</i>. Checkouts can be created in the {@link Doc_RepositoriesView}
   * or in the Project Explorer view with the {@link Doc_CheckoutWizard CDO Checkout wizard}. Once created, checkouts are displayed
   * in the Project Explorer like workspace projects and decorated with a small blue repository image. In contrast to
   * workspace projects the displayed checkouts have no physical representation in the workspace.
   * <p>
   * The folders, resources, and model elements of the checked-out repository are displayed under the checkout and can be
   * modified directly via drag and drop to move or copy them, or via double-click to open the {@link Doc_ModelEditor CDO model editor}
   * on a resource or an EMF Forms dialog on a model element: <p align="center">{@image project-explorer.png}
   * <p>
   * The context menu offers various functions for checkouts, such as renaming, closing, or deleting them, showing them in
   * different views, switching them to different branches or branch points, comparing them with different branches or branch points,
   * and merging from different branches or branch points.
   * <p>
   * In contrast to sessions in the {@link Doc_SessionsView} checkouts created in the
   * Project Explorer view are remembered across Eclipse restarts.
   *
   * @see Doc04_CheckingOut
   * @see Doc05_UsingCheckouts
   */
  public class Doc_ProjectExplorerIntegration
  {
  }

  /**
   * Property Sheet Integration
   * <p>
   * CDO integrates with Eclipse's Properties view and provides various semantic and technical information about the
   * element selected in the current perspective: <p align="center">{@image property-sheet.png}
   * <p>
   * For model elements selected in editors of {@link Doc_TransactionalCheckouts transactional} checkouts the
   * property sheet integration supports the modification of the element's attribute and reference values.
   */
  public class Doc_PropertySheetIntegration
  {
  }

  /**
   * History Integration
   * <p>
   * CDO integrates with Eclipse's History view and displays the commit tree of the selected repository, branch, checkout, or
   * model element: <p align="center">{@image history.png}
   * <p>
   * If the History view does not react to selection changes activate the "Link with Editor and Selection" button
   * in the History view's toolbar.
   * <p>
   * The commit tree is updated in real-time when local transactions are committed but also when other users commit their changes
   * from other workstations.
   * <p>
   * If the repository that the current commit tree is based upon supports branching (see {@link Doc03_UsingBranches}) new branches
   * can be forked off of the selected commit directly in the History view.
   * <p>
   * New checkouts can also be created directly from the selected commit by opening the context menu and selecting the Checkout action
   * or the Checkout As action. See {@link Doc_CheckoutWizard} or {@link Doc04_CheckingOut} for details about the checkout process.
   */
  public class Doc_HistoryIntegration
  {
  }

  /**
   * Compare Integration
   * <p>
   * CDO integrates with EMF Compare to provide efficient and scalable compare editors. Different branches
   * or different branch points can be compared with each other: <p align="center">{@image compare.png}
   * <p>
   * CDO also provides efficient and scalable merge editors. Different branches
   * or different branch points can be merged from each other: <p align="center">{@image merge.png}
   * <p>
   * Comparisons and merges are started either from the {@link Doc_HistoryIntegration History view}, from the
   * context menus of {@link Doc05_UsingCheckouts checkouts}, or by dragging and dropping branches, branch points, or checkouts
   * onto other checkouts in the {@link Doc_ProjectExplorerIntegration Project Explorer} while pressing the Shift key
   * (for comparisons) or the Ctrl key (for merges).
   *
   * @see EMFCompareGuide
   * @see Doc_ComparingCheckouts
   * @see Doc_BackgroundCompare
   */
  public class Doc_CompareIntegration
  {
  }

  /**
   * Forms Integration
   * <p>
   * CDO integrates with EMF Forms to provide generic editing dialogs for the model
   * element that are displayed in the {@link Doc_ProjectExplorerIntegration Project Explorer}: <p align="center">{@image object-edit.png}
   *
   * @see EMFFormsGuide
   * @see Doc_EditingModelElements
   */
  public class Doc_FormsIntegration
  {
  }

  /**
   * Preference Pages
   * <p>
   * The only setting that can currently be adjusted on the CDO Explorer preference page is the number of minutes
   * to keep a repository connected after it has been used the last time: <p align="center">{@image preferences.png}
   */
  public class Doc_PreferencePages
  {
  }
}
