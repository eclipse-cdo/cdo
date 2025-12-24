/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * Thrown from {@link CDOBranch#getBase()} or {@link CDOBranch#getName()} when the branch has been deleted remotely.
 *
 * @author Eike Stepper
 * @since 4.20
 */
public final class CDOBranchDoesNotExistException extends CDOException
{
  private static final long serialVersionUID = 1L;

  private final int branchID;

  public CDOBranchDoesNotExistException(int branchID)
  {
    super("The branch " + branchID + " does not exist");
    this.branchID = branchID;
  }

  public int getBranchID()
  {
    return branchID;
  }
}
