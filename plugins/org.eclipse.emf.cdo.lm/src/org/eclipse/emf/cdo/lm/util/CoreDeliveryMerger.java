/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Eike Stepper
 * @since 1.3
 */
public class CoreDeliveryMerger implements LMMerger
{
  public CoreDeliveryMerger()
  {
  }

  @Override
  public long mergeDelivery(CDOSession session, CDOBranchPoint sourceBranchPoint, CDOBranch targetBranch)
  {
    CDOTransaction transaction = openTransaction(session, targetBranch);

    try
    {
      CDOMerger merger = createMerger();
      if (transaction.merge(sourceBranchPoint, merger) != null)
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

  protected CDOTransaction openTransaction(CDOSession session, CDOBranch branch)
  {
    return session.openTransaction(branch);
  }

  protected CDOMerger createMerger()
  {
    return new DefaultCDOMerger.PerFeature.ManyValued();
  }
}
