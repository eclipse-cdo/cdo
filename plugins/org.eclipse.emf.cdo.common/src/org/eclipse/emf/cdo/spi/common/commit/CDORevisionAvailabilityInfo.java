/*
 * Copyright (c) 2010-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.commit;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

import java.text.MessageFormat;
import java.util.Map;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDORevisionAvailabilityInfo implements CDORevisionProvider
{
  private final Map<CDOID, CDORevisionKey> availableRevisions = CDOIDUtil.createMap();

  private CDOBranchPoint branchPoint;

  private CDORevisionManager revisionManager;

  /**
   * @since 4.6
   */
  public CDORevisionAvailabilityInfo(CDOBranchPoint branchPoint, CDORevisionManager revisionManager)
  {
    this.branchPoint = branchPoint;
    this.revisionManager = revisionManager;
  }

  public CDORevisionAvailabilityInfo(CDOBranchPoint branchPoint)
  {
    this(branchPoint, null);
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  /**
   * @since 4.6
   */
  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
  }

  public Map<CDOID, CDORevisionKey> getAvailableRevisions()
  {
    return availableRevisions;
  }

  public void addRevision(CDORevisionKey key)
  {
    availableRevisions.put(key.getID(), key);
  }

  public void removeRevision(CDOID id)
  {
    availableRevisions.remove(id);
  }

  public boolean containsRevision(CDOID id)
  {
    return availableRevisions.containsKey(id);
  }

  @Override
  public CDORevision getRevision(CDOID id)
  {
    CDORevision revision = (CDORevision)availableRevisions.get(id);
    if (revision == null && revisionManager != null)
    {
      return revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
    }

    return revision;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDORevisionAvailabilityInfo[{0} -> {1} ]", branchPoint, availableRevisions.values());
  }
}
