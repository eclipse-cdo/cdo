/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.util;

import org.eclipse.emf.cdo.common.CDOCommonSession.AuthorizableOperation;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public final class AuthorizableOperations
{
  public static final String ID_BRANCH_CREATE = "org.eclipse.emf.cdo.branch.Create";

  public static final String ID_BRANCH_RENAME = "org.eclipse.emf.cdo.branch.Rename";

  public static final String ID_BRANCH_DELETE = "org.eclipse.emf.cdo.branch.Delete";

  public static final String ID_TAG_CREATE = "org.eclipse.emf.cdo.tag.Create";

  public static final String ID_TAG_RENAME = "org.eclipse.emf.cdo.tag.Rename";

  public static final String ID_TAG_MOVE = "org.eclipse.emf.cdo.tag.Move";

  public static final String ID_TAG_DELETE = "org.eclipse.emf.cdo.tag.Delete";

  private AuthorizableOperations()
  {
  }

  public static AuthorizableOperation createBranch(int branchID, String name, int baseBranchID, long baseTimeStamp)
  {
    return new AuthorizableOperation(ID_BRANCH_CREATE) //
        .parameter("branchID", branchID) //
        .parameter("name", name) //
        .parameter("baseBranchID", baseBranchID) //
        .parameter("baseTimeStamp", baseTimeStamp);
  }

  public static AuthorizableOperation renameBranch(int branchID, String newName)
  {
    return new AuthorizableOperation(ID_BRANCH_RENAME) //
        .parameter("branchID", branchID) //
        .parameter("newName", newName);
  }

  public static AuthorizableOperation deleteBranch(int branchID)
  {
    return new AuthorizableOperation(ID_BRANCH_DELETE) //
        .parameter("branchID", branchID);
  }

  public static AuthorizableOperation createTag(String name, int branchID, long timeStamp)
  {
    return new AuthorizableOperation(ID_TAG_CREATE) //
        .parameter("name", name) //
        .parameter("branchID", branchID) //
        .parameter("timeStamp", timeStamp);
  }

  public static AuthorizableOperation renameTag(String oldName, String newName)
  {
    return new AuthorizableOperation(ID_TAG_RENAME) //
        .parameter("oldName", oldName) //
        .parameter("newName", newName);
  }

  public static AuthorizableOperation moveTag(String name, int branchID, long timeStamp)
  {
    return new AuthorizableOperation(ID_TAG_MOVE) //
        .parameter("name", name) //
        .parameter("branchID", branchID) //
        .parameter("timeStamp", timeStamp);
  }

  public static AuthorizableOperation deleteTag(String name)
  {
    return new AuthorizableOperation(ID_TAG_DELETE) //
        .parameter("name", name);
  }
}
