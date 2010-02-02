/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.branch;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOBranch extends Comparable<CDOBranch>
{
  public static final int MAIN_BRANCH_ID = 0;

  public static final String MAIN_BRANCH_NAME = "MAIN";

  public static final String PATH_SEPARATOR = "/";

  public boolean isMainBranch();

  public int getID();

  public String getName();

  public CDOBranchPoint getBase();

  public CDOBranchPoint getHead();

  public CDOBranchPoint getPoint(long timeStamp);

  public CDOBranchVersion getVersion(int version);

  public CDOBranchManager getBranchManager();

  public CDOBranch[] getBranches();

  public CDOBranch getBranch(String path);

  public CDOBranch createBranch(String name, long timeStamp);

  public CDOBranch createBranch(String name);
}
