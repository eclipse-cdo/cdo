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
 * A CDO {@link CDOException exception} thrown from {@link CDOBranch#createBranch(String, long) CDOBranch.createBranch()}
 * if the {@link CDOBranch#getPathName() path name} of the branch to be created already exists.
 *
 * @author Eike Stepper
 * @since 4.16
 */
public class CDODuplicateBranchException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public CDODuplicateBranchException(CDOBranch branch)
  {
    super("The branch " + branch.getPathName() + " already exists");
  }
}
