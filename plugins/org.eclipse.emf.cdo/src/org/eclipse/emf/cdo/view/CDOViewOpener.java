/*
 * Copyright (c) 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Can open new {@link CDOView views}.
 *
 * @author Eike Stepper
 * @since 4.4
 */
public interface CDOViewOpener
{
  /**
   * Opens and returns a new {@link CDOView view} on the given EMF {@link ResourceSet resource set}.
   */
  public CDOView openView(CDOBranchPoint target, ResourceSet resourceSet);

  /**
   * Opens and returns a {@link CDOView view} on the given EMF {@link ResourceSet resource set} by resuming a view that
   * has previously been made durable by calling {@link CDOView#enableDurableLocking(boolean)
   * CDOView.enableDurableLocking(true)}.
   */
  public CDOView openView(String durableLockingID, ResourceSet resourceSet);
}
