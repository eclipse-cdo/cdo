/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.lm.LMPackage;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.server.AbstractLifecycleManager.BaselineBranchPointProvider;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public class DropReviewBranchPointProvider implements BaselineBranchPointProvider
{
  public DropReviewBranchPointProvider()
  {
  }

  @Override
  public CDOBranchPointRef getBranchPoint(CDORevision baseline, CDORevisionProvider provider, Stream stream)
  {
    // Taken from DropReviewImpl.getBranchPoint().
    if (baseline.getEClass() == ReviewsPackage.Literals.DROP_REVIEW)
    {
      if (stream == null)
      {
        return null;
      }

      CDOBranchRef targetBranch = stream.getBranch();
      if (targetBranch == null)
      {
        return null;
      }

      long baseTimeStamp = getBaseTimeStamp(baseline, provider);
      return targetBranch.getPointRef(baseTimeStamp);
    }

    return null;
  }

  private long getBaseTimeStamp(CDORevision baseline, CDORevisionProvider provider)
  {
    CDOID deliveryID = (CDOID)((InternalCDORevision)baseline).getValue(ReviewsPackage.Literals.DROP_REVIEW__DELIVERY);
    if (CDOIDUtil.isNull(deliveryID))
    {
      return CDOBranchPoint.INVALID_DATE;
    }

    CDORevision delivery = provider.getRevision(deliveryID);
    CDOBranchPointRef mergeTarget = (CDOBranchPointRef)((InternalCDORevision)delivery).getValue(LMPackage.Literals.DELIVERY__MERGE_TARGET);
    return mergeTarget.getTimeStamp();
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends BaselineBranchPointProvider.Factory
  {
    public static final String TYPE = "DropReview"; //$NON-NLS-1$

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public BaselineBranchPointProvider create(String description) throws ProductCreationException
    {
      return new DropReviewBranchPointProvider();
    }
  }
}
