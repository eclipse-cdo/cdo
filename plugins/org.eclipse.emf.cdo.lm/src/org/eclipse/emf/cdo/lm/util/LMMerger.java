/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Eike Stepper
 */
public interface LMMerger
{
  public static final LMMerger CORE = new LMMerger()
  {
    @Override
    public long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch)
    {
      CDOTransaction transaction = session.openTransaction(targetBranch);

      try
      {
        DefaultCDOMerger cdoMerger = new DefaultCDOMerger.PerFeature.ManyValued();
        if (transaction.merge(sourceBranchPoint, cdoMerger) != null)
        {
          CDOCommitInfo commitInfo = transaction.commit();
          return commitInfo.getTimeStamp();
        }
      }
      catch (Exception ex)
      {
        transaction.rollback();
        throw new CDOException(ex);
      }
      finally
      {
        transaction.close();
      }

      return CDOBranchPoint.INVALID_DATE;
    }
  };

  public long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch);
}
