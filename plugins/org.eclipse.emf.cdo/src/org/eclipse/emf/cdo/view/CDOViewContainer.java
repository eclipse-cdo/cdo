/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Can open new {@link CDOView views} and provide access to openend views.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewContainer extends IContainer<CDOView>, CDOViewOpener
{
  /**
   * Returns an array of all open {@link CDOView views} and {@link CDOTransaction transactions} of this session.
   *
   * @see #openView()
   */
  public CDOView[] getViews();

  /**
   * Returns an array of all {@link CDOView views} and {@link CDOTransaction transactions} of this session that are open on the given branch.
   *
   * @since 4.2
   */
  public CDOView[] getViews(CDOBranch branch);

  /**
   * @since 4.0
   */
  public CDOView getView(int viewID);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   *
   * @see #openView()
   * @since 4.0
   */
  public CDOView openView(CDOBranchPoint target);

  /**
   * Opens and returns a new {@link CDOView view} on the given EMF {@link ResourceSet resource set}.
   *
   * @see #openView()
   * @since 3.0
   */
  public CDOView openView(CDOBranch branch, long timeStamp, ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openView(new ResourceSetImpl())</code>.
   *
   * @see #openView(CDOBranch, long, ResourceSet)
   * @since 3.0
   */
  public CDOView openView(CDOBranch branch, long timeStamp);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openView(new ResourceSetImpl())</code>.
   *
   * @see #openView(CDOBranch, long, ResourceSet)
   * @since 3.0
   */
  public CDOView openView(CDOBranch branch);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openView(new ResourceSetImpl())</code>.
   *
   * @see #openView(CDOBranch, long, ResourceSet)
   * @since 3.0
   */
  public CDOView openView(long timeStamp);

  /**
   * Opens and returns a new {@link CDOView view} on the given EMF {@link ResourceSet resource set}.
   *
   * @see #openView(CDOBranch, long, ResourceSet)
   * @since 4.0
   */
  public CDOView openView(ResourceSet resourceSet);

  /**
   * Opens and returns a new {@link CDOView view} on a new EMF {@link ResourceSet resource set}.
   * <p>
   * Same as calling <code>openView(new ResourceSetImpl())</code>.
   *
   * @see #openView(CDOBranch, long, ResourceSet)
   */
  public CDOView openView();

  /**
   * Opens and returns a {@link CDOView view} on a new EMF {@link ResourceSet resource set} by resuming a view that has
   * previously been made durable by calling {@link CDOView#enableDurableLocking(boolean)
   * CDOView.enableDurableLocking(true)}.
   * <p>
   * Same as calling <code>openView(durableLockingID, new ResourceSetImpl())</code>.
   *
   * @see #openView(String,ResourceSet)
   * @since 4.0
   */
  public CDOView openView(String durableLockingID);
}
