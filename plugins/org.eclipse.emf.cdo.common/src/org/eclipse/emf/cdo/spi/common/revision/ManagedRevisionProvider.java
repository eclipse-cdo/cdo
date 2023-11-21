/*
 * Copyright (c) 2010-2012, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * A revision provider backed by a {@link CDORevisionManager revision manager} that provides revisions which are
 * {@link CDORevision#isValid(CDOBranchPoint) valid} at the configured {@link #getBranchPoint() branch point}.
 *
 * @author Eike Stepper
 * @since 4.0
 */
public class ManagedRevisionProvider implements CDORevisionProvider
{
  /**
   * @since 4.15
   */
  protected final InternalCDORevisionManager revisionManager;

  /**
   * @since 4.15
   */
  protected final CDOBranchPoint branchPoint;

  public ManagedRevisionProvider(CDORevisionManager revisionManager, CDOBranchPoint branchPoint)
  {
    this.branchPoint = branchPoint;
    this.revisionManager = (InternalCDORevisionManager)revisionManager;
  }

  public CDORevisionManager getRevisionManager()
  {
    return revisionManager;
  }

  public CDOBranchPoint getBranchPoint()
  {
    return branchPoint;
  }

  @Override
  public CDORevision getRevision(CDOID id)
  {
    return provideRevision(revisionManager, id, branchPoint);
  }

  /**
   * @since 4.22
   */
  public static CDORevision provideRevision(CDORevisionManager revisionManager, CDOID id, CDOBranchPoint branchPoint)
  {
    return revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
  }

  /**
   * @since 4.15
   */
  public CDORevisionProviderWithSynthetics withSynthetics()
  {
    return new WithSynthetics(revisionManager, branchPoint);
  }

  /**
   * A managed revision provider that can also provide {@link SyntheticCDORevision synthetic revisions}.
   *
   * @author Eike Stepper
   * @since 4.15
   */
  public static class WithSynthetics extends ManagedRevisionProvider implements CDORevisionProviderWithSynthetics
  {
    public WithSynthetics(CDORevisionManager revisionManager, CDOBranchPoint branchPoint)
    {
      super(revisionManager, branchPoint);
    }

    @Override
    public SyntheticCDORevision getSynthetic(CDOID id)
    {
      SyntheticCDORevision[] synthetic = { null };
      revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true, synthetic);
      return synthetic[0];
    }
  }
}
