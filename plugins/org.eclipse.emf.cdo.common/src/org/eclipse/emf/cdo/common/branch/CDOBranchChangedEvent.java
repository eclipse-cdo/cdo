/*
 * Copyright (c) 2013, 2016, 2019, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link CDOBranchManager branch manager} when a new {@link CDOBranch branch} has
 * been created.
 *
 * @author Eike Stepper
 * @since 4.3
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOBranchChangedEvent extends IEvent, CDOBranchProvider
{
  @Override
  public CDOBranchManager getSource();

  @Override
  public CDOBranch getBranch();

  /**
   * @since 4.15
   */
  public int[] getBranchIDs();

  public ChangeKind getChangeKind();

  /**
   * Enumerates the kinds of branch changes.
   *
   * @author Eike Stepper
   */
  public enum ChangeKind
  {
    CREATED, RENAMED, DELETED
  }
}
