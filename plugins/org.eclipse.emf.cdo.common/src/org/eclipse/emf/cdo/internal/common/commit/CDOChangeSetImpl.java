/*
 * Copyright (c) 2010-2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetImpl extends CDOChangeSetDataImpl implements CDOChangeSet
{
  private CDOBranchPoint startPoint;

  private CDOBranchPoint endPoint;

  public CDOChangeSetImpl(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    super(data.getNewObjects(), data.getChangedObjects(), data.getDetachedObjects());
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  @Override
  public CDOBranchPoint getStartPoint()
  {
    return startPoint;
  }

  @Override
  public CDOBranchPoint getEndPoint()
  {
    return endPoint;
  }

  @Override
  public CDOBranchPoint getAncestorPoint()
  {
    return CDOBranchUtil.getAncestor(startPoint, endPoint);
  }
}
