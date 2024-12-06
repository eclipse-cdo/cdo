/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.util;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.session.CDOSession;

/**
 * Merges the commits of a source {@link Change change} into the {@link CDOBranch branch} of a target {@link Stream stream}
 * in order to create a {@link Delivery delivery}.
 *
 * @author Eike Stepper
 */
public interface LMMerger
{
  public static final LMMerger CORE = new CoreDeliveryMerger();

  /**
   * Merges the changes from the given source branch point into the given target branch and returns the time stamp of the
   * resulting commit into the merge target branch, or {@link CDOBranchPoint#INVALID_DATE CDOBranchPoint.INVALID_DATE}
   * if no commit has happened.
   */
  public long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch);
}
