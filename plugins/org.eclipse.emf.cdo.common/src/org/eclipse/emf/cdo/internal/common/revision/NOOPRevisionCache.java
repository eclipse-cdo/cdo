/*
 * Copyright (c) 2010-2013, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class NOOPRevisionCache extends Lifecycle implements InternalCDORevisionCache
{
  public static final NOOPRevisionCache INSTANCE = new NOOPRevisionCache();

  private static final List<CDORevision> EMPTY_LIST = Collections.emptyList();

  public NOOPRevisionCache()
  {
  }

  @Override
  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return this;
  }

  public boolean isSupportingBranches()
  {
    return true;
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    return null;
  }

  @Override
  public List<CDORevision> getCurrentRevisions()
  {
    return EMPTY_LIST;
  }

  @Override
  public void forEachCurrentRevision(Consumer<CDORevision> consumer)
  {
    // Do nothing.
  }

  public InternalCDORevision getRevision(CDOID id)
  {
    return null;
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return null;
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return null;
  }

  @Override
  public CDORevision internRevision(CDORevision revision)
  {
    return revision;
  }

  @Override
  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    return null;
  }

  @Override
  public void clear()
  {
    // Do nothing
  }

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    return Collections.emptyMap();
  }

  @Override
  public void getAllRevisions(List<InternalCDORevision> result)
  {
    // Do nothing
  }

  @Override
  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    return Collections.emptyList();
  }
}
