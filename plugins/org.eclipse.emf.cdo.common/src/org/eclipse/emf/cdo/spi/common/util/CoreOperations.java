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

import org.eclipse.net4j.util.security.operations.AuthorizableOperation;

/**
 * An {@link AuthorizableOperation authorizable operation} factory for the operations of the CDO core.
 *
 * @author Eike Stepper
 * @since 4.15
 */
public final class CoreOperations
{
  public static final String ID_CREATE_BRANCH = "org.eclipse.emf.cdo.CreateBranch";

  public static final String ID_RENAME_BRANCH = "org.eclipse.emf.cdo.RenameBranch";

  public static final String ID_DELETE_BRANCH = "org.eclipse.emf.cdo.DeleteBranch";

  public static final String ID_CREATE_TAG = "org.eclipse.emf.cdo.CreateTag";

  public static final String ID_RENAME_TAG = "org.eclipse.emf.cdo.RenameTag";

  public static final String ID_MOVE_TAG = "org.eclipse.emf.cdo.MoveTag";

  public static final String ID_DELETE_TAG = "org.eclipse.emf.cdo.DeleteTag";

  private CoreOperations()
  {
  }

  public static AuthorizableOperation createBranch(int branchID, String name, int baseBranchID, long baseTimeStamp)
  {
    return AuthorizableOperation.builder(ID_CREATE_BRANCH) //
        .parameter("branchID", branchID) //
        .parameter("name", name) //
        .parameter("baseBranchID", baseBranchID) //
        .parameter("baseTimeStamp", baseTimeStamp) //
        .build();
  }

  public static AuthorizableOperation renameBranch(int branchID, String newName)
  {
    return AuthorizableOperation.builder(ID_RENAME_BRANCH) //
        .parameter("branchID", branchID) //
        .parameter("newName", newName) //
        .build();
  }

  public static AuthorizableOperation deleteBranch(int branchID)
  {
    return AuthorizableOperation.builder(ID_DELETE_BRANCH) //
        .parameter("branchID", branchID) //
        .build();
  }

  public static AuthorizableOperation createTag(String name, int branchID, long timeStamp)
  {
    return AuthorizableOperation.builder(ID_CREATE_TAG) //
        .parameter("name", name) //
        .parameter("branchID", branchID) //
        .parameter("timeStamp", timeStamp) //
        .build();
  }

  public static AuthorizableOperation renameTag(String oldName, String newName)
  {
    return AuthorizableOperation.builder(ID_RENAME_TAG) //
        .parameter("oldName", oldName) //
        .parameter("newName", newName) //
        .build();
  }

  public static AuthorizableOperation moveTag(String name, int branchID, long timeStamp)
  {
    return AuthorizableOperation.builder(ID_MOVE_TAG) //
        .parameter("name", name) //
        .parameter("branchID", branchID) //
        .parameter("timeStamp", timeStamp) //
        .build();
  }

  public static AuthorizableOperation deleteTag(String name)
  {
    return AuthorizableOperation.builder(ID_DELETE_TAG) //
        .parameter("name", name) //
        .build();
  }
}
