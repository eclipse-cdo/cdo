/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

/**
 * A {@link CDOViewEvent view event} fired when the branch point of a {@link CDOView view} has changed.
 *
 * @author Victor Roldan Betancort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOViewTargetChangedEvent extends CDOViewEvent
{
  /**
   * @since 4.2
   */
  public CDOBranchPoint getOldBranchPoint();

  public CDOBranchPoint getBranchPoint();
}
