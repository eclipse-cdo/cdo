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

import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository;
import org.eclipse.emf.cdo.doc.operators.Doc02_ConfiguringAcceptors;
import org.eclipse.emf.cdo.doc.operators.Doc03_ManagingSecurity;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_HistoryIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_PreferencePages;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_ProjectExplorerIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_PropertySheetIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_RepositoriesView;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_SessionsView;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_TimeMachineView;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories.Doc_LocalRepositories;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories.Doc_RemoteRepositories;
import org.eclipse.emf.cdo.doc.users.Doc03_UsingBranches.Doc_CreatingBranches;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_HistoricalCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc04_CheckingOut.Doc_CheckoutType.Doc_OfflineCheckouts;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_DeletingCheckouts;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.tcp.ITCPAcceptor;

/**
 * Managing Repositories
 * <p>
 * CDO stores models and meta models in repositories. These repositories need to be created or,
 * if they already exist on a remote server, connected before they can be used in Eclipse.
 * The management of these repository connections happens in the {@link Doc_RepositoriesView}.
 * <p>
 * Currently CDO supports two different types of repository connections:
 * <ul>
 * <li>Connections to existing remote repositories, as explained in {@link Doc_RemoteRepositories}.
 * <li>Connections to newly created local repositories, as explained in {@link Doc_LocalRepositories}.
 * </ul>
 * Please note that the term {@link CDORepository repository} in the context of the CDO Explorer user interface refers to a <i>connection</i>
 * to a core-level {@link IRepository}, as opposed to referring to the {@link IRepository} itself. Information on
 * operating a CDO server and on creating and managing an {@link IRepository} can be found in the {@link org.eclipse.emf.cdo.doc.operators}.
 * <p>
 * The following sections explain how to create, rename, connect, disconnect, or delete repositories and how to work with branches.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ManagingRepositories
{
  /**
   * Creating Repositories and Connections
   * <p>
   * The {@link Doc_RepositoriesView} provides a uniform way to work with different types of CDO repositories.
   * Only at creation or connection time the differences between the repository types become apparent.
   * <p>
   * The following sections explain how to use the different repository creation or connection wizards. Each of them
   * can be started by clicking on the green plus button on the CDO Repositories view's toolbar:
   * <p align="center">{@image repositories-view.png}
   */
  public class Doc_CreatingRepositories
  {
    /**
     * Connecting to Remote Repositories
     * <p>
     * To create an online connection to an existing remote repository the first type option must be selected on the first wizard page:
     * <p align="center">{@image repo-wizard-type-remote.png}
     * <p>
     * A double-click on the type option or a single click on the Next button advances to the next wizard page, on which the label
     * of the new connection and information about the remote server, the repository on that server, and optionally login credentials need
     * to be entered:
     * <p align="center">{@image repo-wizard-remote.png}
     * <p>
     * The <b>label</b> identifies the connection to the remote repository. It must be unique among all
     * configured connections in the current workspace. It can also be changed later, see {@link Doc_RenamingConnections}.
     * <p>
     * The <b>host</b> and <b>port</b> fields specify the server of the repository to connect to, more exactly the {@link IAcceptor}
     * on that server that accepts connection requests for the targeted repository. See {@link Doc02_ConfiguringAcceptors} for operator details.
     * <p>
     * The <b>repositories</b> list shows the repositories that are discovered on the targeted server. Selecting a discovered repository
     * in this list copies the name of the selected repository into the repository name field.
     * <p>
     * The <b>repository name</b> field identifies the targeted repository on the specified CDO server.
     * See {@link Element_repository} on how to configure repository names on a CDO server.
     * <p>
     * The <b>user name</b> and <b>password</b> fields are only enabled if the targeted repository supports authentication.
     * See {@link Doc03_ManagingSecurity} on how to configure authentication and authorization in CDO repositories.
     */
    public class Doc_RemoteRepositories
    {
    }

    /**
     * Cloning Master Repositories
     * <p>
     * Cloning master repositories is not yet supported in the user interface.
     */
    public class Doc_CloneRepositories
    {
    }

    /**
     * Creating Local Repositories
     * <p>
     * To create a new local repository and add an online connection to it the third type option must be selected on the first wizard page:
     * <p align="center">{@image repo-wizard-type-local.png}
     * <p>
     * A double-click on the type option or a single click on the Next button advances to the next wizard page, on which the label
     * of the new connection and information about the new local repository need to be entered:
     * <p align="center">{@image repo-wizard-local.png}
     * <p>
     * The <b>label</b> identifies the connection to the new local repository. It must be unique among all
     * configured connections in the current workspace. It can also be changed later, see {@link Doc_RenamingConnections}.
     * <p>
     * The <b>repository name</b> field specifies the name of the new local repository. It must be unique among all local repositories
     * in the current workspace. The name of the repository can, in contrast to the label of the connection, not be changed anymore,
     * once the repository is created.
     * <p>
     * The <b>versioning mode</b> determines whether the history of changed models is preserved (for example to be used in
     * {@link Doc_HistoricalCheckouts} and with the {@link Doc_TimeMachineView}) and whether branches are supported.
     * The versioning mode of the repository can not be changed anymore, once the repository is created.
     * <p>
     * The <b>ID generation</b> determines where (server or client) and how (counters or UUIDs) IDs for new model elements are generated.
     * Server-side counter IDs are the more efficient option, but they prevent the repository to be used in replicated scenarios, such as
     * {@link Doc_CloneRepositories offline clone repositories} or {@link Doc_OfflineCheckouts offline checkouts}. For those scenarios
     * client-side UUIDs should be selected.
     * The ID generation of the repository can not be changed anymore, once the repository is created.
     * <p>
     * The optional <b>TCP port</b> field specifies on what port a {@link ITCPAcceptor} will accept incoming connection requests.
     * By default this option is disabled because local repositories run in the same Java VM as the hosting Eclipse IDE, and are
     * always accepting connection requests from there through an implicit {@link IJVMAcceptor}, too.
     */
    public class Doc_LocalRepositories
    {
    }
  }

  /**
   * Renaming Repository Connections
   * <p>
   * CDO supports renaming an existing repository connection, i.e., changing its label, at any time, even when checkouts from
   * that repository already exist. A repository connection can be renamed by selecting it and pressing the F2 key or
   * selecting the Rename action in the context menu. The following dialog will pop up: <p align="center">{@image repo-rename.png}
   * <p>
   * The dialog can only be finished with the OK button if the repository label is either unchanged or changed to a not existing label.
   */
  public class Doc_RenamingConnections
  {
  }

  /**
   * Deleting Repository Connections
   * <p>
   * An existing repository connection can be deleted by selecting that repository connection and pressing the Del key or selecting
   * the Delete action in the context menu. The following dialog will pop up: <p align="center">{@image repo-delete.png}
   * <p>
   * If checkouts exist for the repository connection to be deleted the following warning dialog will pop up <b>first</b>:
   * <p align="center">{@image repo-delete-checkouts.png}
   * <p>
   * If the deletion of the existing checkouts is confirmed the checkout deletion dialog is popped up as explained in {@link Doc_DeletingCheckouts}.
   */
  public class Doc_DeletingConnections
  {
  }

  /**
   * Connecting and Disconnecting Repositories
   * <p>
   * To work with a configured repository, e.g. to create branches (see {@link Doc_CreatingBranches}) or checkouts
   * (see {@link Doc04_CheckingOut}, the repository must be in <i>connected</i> state. Directly after creation a repository is
   * connected. Directly after the startup of Eclipse all repositories are in <i>disconnected</i> state.
   * <p>
   * If a repository is disconnected it can be connected by double-clicking it or by selecting Connect in its context menu.
   * It is automatically connected when an existing checkout from this repository is opened.
   * <p>
   * If a repository is connected it can be disconnected by selecting Disconnect in its context menu.
   * It is automatically disconnected when the {@link Doc_PreferencePages configured timeout period} has elapsed
   * and the repository wasn't used during that period.
   */
  public class Doc_ConnectingDisconnecting
  {
  }

  /**
   * Showing Repositories in Other Views
   * <p>
   * Repositories can be shown in a number of other views depending on the type of the repository
   * by selecting them, opening their context menu, opening the Show In sub menu, and selecting one of the Show In actions.
   * <p>
   * Local repositories can be shown in the following views: <p align="center">{@image repository-local-showin.png}
   * <p>
   * Remote repositories can be shown in the following views: <p align="center">{@image repository-remote-showin.png}
   */
  public class Doc_RepositoryShowIn
  {
    /**
     * Showing Repositories in the CDO Sessions View
     * <p>
     * All repositories can be shown in the {@link Doc_SessionsView CDO Sessions view}: <p align="center">{@image sessions-view.png}
     * <p>
     * A new {@link CDOSession session} with the same target repository as the selected repository is opened in the CDO Sessions view.
     */
    public class Doc_RepositoryShowInSessions
    {
    }

    /**
     * Showing Repositories in the CDO Server Browser
     * <p>
     * Local repositories can be shown in the CDO Server Browser: <p align="center">{@image server-browser.png}
     * <p>
     * The CDO Server Browser allows to introspect the internal data of the local repository
     * in a web browser. It is only meant to be used for test and debug purposes.
     * It is <b>not</b> meant to be a production tool that would scale to arbitrary repository sizes!
     */
    public class Doc_RepositoryShowInServerBrowser
    {
    }

    /**
     * Showing Repositories in the Properties View
     * <p>
     * All repositories can be shown in the {@link Doc_PropertySheetIntegration Properties view}: <p align="center">{@image property-sheet.png}
     */
    public class Doc_RepositoryShowInProperties
    {
    }

    /**
     * Showing Repositories in the History View
     * <p>
     * All repositories can be shown in the {@link Doc_HistoryIntegration History view}: <p align="center">{@image history.png}
     * <p>
     * This is particularly useful if the "Link with Editor and Selection" button in the toolbar of the History view
     * is not enabled and the history page does not automatically adjust to the workbench selection.
     */
    public class Doc_RepositoryShowInHistory
    {
    }

    /**
     * Showing Repositories in the Project Explorer
     * <p>
     * All repositories can be shown in the {@link Doc_ProjectExplorerIntegration Project Explorer}, which means that all
     * configured checkouts from the selected repository will be selected.
     */
    public class Doc_RepositoryShowInProjectExplorer
    {
    }

    /**
     * Showing Repositories in the System Explorer
     * <p>
     * All repositories can be shown in the System Explorer: <p align="center">{@image system-explorer-repository.png}
     * <p>
     * Each repository owns a dedicated directory under .metadata of the current workspace. This directory contains
     * the repository.properties file that contains the configuration of the repository. Local repositories also store the database
     * of the local repository in this directory.
     */
    public class Doc_RepositoryShowInSystemExplorer
    {
    }
  }
}
