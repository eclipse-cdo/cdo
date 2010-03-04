/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetImpl extends CDOChangeSetDataImpl implements CDOChangeSet
{
  private CDOBranchPointRange range;

  public CDOChangeSetImpl(CDOBranchPointRange range, CDOChangeSetData data)
  {
    super(data.getNewObjects(), data.getChangedObjects(), data.getDetachedObjects());
    this.range = range;
  }

  public CDOBranchPoint getStartPoint()
  {
    return range.getStartPoint();
  }

  public CDOBranchPoint getEndPoint()
  {
    return range.getEndPoint();
  }

  public CDOBranchPoint getAncestorPoint()
  {
    return CDOBranchUtil.getAncestor(getStartPoint(), getEndPoint());
  }
}
