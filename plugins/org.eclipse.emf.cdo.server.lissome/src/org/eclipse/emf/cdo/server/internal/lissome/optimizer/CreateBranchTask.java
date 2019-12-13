/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome.optimizer;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.server.internal.lissome.db.IndexWriter;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CreateBranchTask implements OptimizerTask
{
  private int branchID;

  private String name;

  private CDOBranchPoint base;

  private long pointer;

  public CreateBranchTask(int branchID, String name, CDOBranchPoint base, long pointer)
  {
    this.branchID = branchID;
    this.name = name;
    this.base = base;
    this.pointer = pointer;
  }

  public int getBranchID()
  {
    return branchID;
  }

  public String getName()
  {
    return name;
  }

  public CDOBranchPoint getBase()
  {
    return base;
  }

  public long getPointer()
  {
    return pointer;
  }

  @Override
  public void execute(Optimizer optimizer) throws IOException
  {
    IndexWriter indexWriter = optimizer.getStore().getIndex().getWriter();
    indexWriter.createBranch(branchID, name, base, pointer);
    indexWriter.commit();
  }
}
