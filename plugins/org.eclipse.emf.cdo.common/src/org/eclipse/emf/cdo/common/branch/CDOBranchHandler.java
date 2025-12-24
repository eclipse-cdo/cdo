/*
 * Copyright (c) 2010-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

/**
 * A call-back interface that indicates the ability to <i>handle</i> branches that are passed from other entities.
 *
 * @see CDOBranchManager#getBranches(int, int, CDOBranchHandler)
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOBranchHandler
{
  /**
   * A call-back method that other entities can pass branches to.
   */
  public void handleBranch(CDOBranch branch);
}
