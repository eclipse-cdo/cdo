/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.testrecorder;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchImpl;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class TestRecorderBranch extends CDOBranchImpl
{
  public TestRecorderBranch(InternalCDOBranchManager branchManager, int id, String name, CDOBranchPoint base)
  {
    super(branchManager, id, name, base);
  }

  @Override
  public void setName(String name)
  {
    super.setName(name);
    TestRecorder.INSTANCE.renameBranch(this);
  }
}
