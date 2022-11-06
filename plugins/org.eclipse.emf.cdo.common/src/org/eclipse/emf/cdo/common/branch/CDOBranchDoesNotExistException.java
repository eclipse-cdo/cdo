/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

import org.eclipse.emf.cdo.common.util.CDOException;

/**
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
