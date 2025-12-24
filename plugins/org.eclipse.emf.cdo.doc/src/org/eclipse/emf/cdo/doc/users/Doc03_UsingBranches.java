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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.doc.operators.Doc01_ConfiguringRepositories.Element_repository.Property_supportingBranches;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_HistoryIntegration;
import org.eclipse.emf.cdo.doc.users.Doc01_UserInterface.Doc_RepositoriesView;
import org.eclipse.emf.cdo.doc.users.Doc02_ManagingRepositories.Doc_CreatingRepositories.Doc_LocalRepositories;
import org.eclipse.emf.cdo.doc.users.Doc05_UsingCheckouts.Doc_SwitchingBranch.Doc_SwitchNewBranch;
import org.eclipse.emf.cdo.server.IRepository;

/**
 * Working with Branches
 * <p>
 * {@link CDOBranch Branches} are a very powerful concept in CDO. They allow to isolate a sequence of commits from other
 * sequences of commits, as well as all the locks on the objects that are involved. A branch is represented by a
 * {@link CDOBranch#getBase() base point} and a {@link CDOBranch#getName() name}. The base point of a branch X is the
 * {@link CDOBranchPoint#getTimeStamp() timestamp} in the {@link CDOBranchPoint#getBranch() parent branch} where the
 * branch X forks off. As such all branches of a repository form a tree that is rooted at the
 * {@link CDOBranchManager#getMainBranch() main branch}, which itself has no proper base point (the base branch is
 * <code>null</code> and the base time is identical to the {@link IRepository#getCreationTime() creation time} of the repository).
 * <p>
 * The branch tree of a repository is displayed under that repository in the {@link Doc_RepositoriesView}. The main branch
 * is always represented by the repository node itself. The displayed branch tree is updated in real-time even when other users
 * create new branches or modify existing branches on their workstations.
 * <p>
 * Each repository has at least a main branch, but, to be able to work with branches in a meaningful way, the <i>versioning mode</i>
 * of a repository must be set to <i>branching</i>. See {@link Property_supportingBranches}
 * for instructions on how to do this for
 * remote repositories or {@link Doc_LocalRepositories} for instructions on how to do this for local repositories.
 * <p>
 * Note that the branching mode does usually not add much overhead in terms of storage consumption or processing time, but the
 * branching mode always includes the functionality and the characteristics of the auditing mode; and that mode may significantly
 * increase the size of the underlying database. In other words, using the auditing mode instead of the normal mode makes a big difference;
 * using the branching mode instead of the auditing mode not.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc03_UsingBranches
{
  /**
   * Creating Branches
   * <p>
   * There exist several ways to create new branches. The most explicit way is to use {@link Doc_RepositoriesView},
   * select a repository (with versioning mode set to branching) or an existing branch under that repository,
   * open the context menu on that repository or branch and select the New Branch action. The following dialog will pop up:
   * <p align="center">{@image branch-create.png}
   * <p>
   * The upper part of the dialog shows the current branch tree of the targeted repository.
   * The <b>base branch</b> of the new branch can be changed if needed.
   * <p>
   * The <b>base time stamp</b> must be specified. Three different options exist:
   * <ul>
   * <li> <b>Base</b> refers to the time stamp of the base point of the branch selected in the upper branch tree.
   *      That means the resulting new branch  will fork of the selected base branch at the same time when that base branch was forked off
   *      of its own base branch.
   * <li> <b>Time</b> allows to enter any valid time stamp. Invalid time stamps are those that are before the creation of the base branch
   *      or after the current time.
   * <li> <b>Head</b> refers to the current time. Note that the {@link CDOBranch#getHead() head} of a branch is a floating branch point,
   *      i.e., it always point to the current time in a branch. Nevertheless selecting this option will set the base time stamp of the
   *      new branch to the fixed point in time on the server when the branch is actually created.
   * </ul>
   * <p>
   * The <b>name</b> of the new branch must be unique among the child branches of the selected base branch.
   * <p>
   * Other ways to create branches are explained in {@link Doc_SwitchNewBranch} and {@link Doc_HistoryIntegration}.
   */
  public class Doc_CreatingBranches
  {
  }

  /**
   * Renaming Branches
   * <p>
   * Existing branches can be renamed at any point in time. The main branch of a repository can not be renamed.
   * <p>
   * To rename a branch select it in the {@link Doc_RepositoriesView}, press the F2 key or open the context menu on
   * that branch and select the Rename action. The following dialog will pop up: <p align="center">{@image branch-rename.png}
   * <p>
   * The entered new name for the selected branch is validated to be unique among the other child branches of the
   * selected branch's base branch.
   */
  public class Doc_RenamingBranches
  {
  }

  /**
   * Deleting Branches
   * <p>
   * Deleting branches is not yet supported.
   */
  public class Doc_DeletingBranches
  {
  }

  /**
   * Showing Branches in the History View
   * <p>
   * All branches can be shown in the {@link Doc_HistoryIntegration History view}: <p align="center">{@image history.png}
   * <p>
   * This is particularly useful if the "Link with Editor and Selection" button in the toolbar of the History view
   * is not enabled and the history page does not automatically adjust to the workbench selection.
   */
  public class Doc_BranchShowInHistory
  {
  }
}
