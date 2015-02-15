/*
 * Copyright (c) 2009-2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.BaseLabelDecorator;
import org.eclipse.emf.cdo.internal.explorer.checkouts.OfflineCDOCheckout;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelDecorator extends BaseLabelDecorator
{
  public CDOCheckoutLabelDecorator()
  {
  }

  @Override
  public String decorateText(String text, Object element)
  {
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;

      if (checkout.isOpen())
      {
        CDOBranch branch = checkout.getView().getBranch();
        String branchPath = branch.getPathName();
        if (branchPath.startsWith(CDOBranch.MAIN_BRANCH_NAME))
        {
          branchPath = branchPath.substring(CDOBranch.MAIN_BRANCH_NAME.length());
        }

        if (branchPath.startsWith(CDOBranch.PATH_SEPARATOR))
        {
          branchPath = branchPath.substring(CDOBranch.PATH_SEPARATOR.length());
        }

        if (branchPath.length() != 0)
        {
          text += "  " + branchPath;
        }

        long timeStamp = checkout.getTimeStamp();
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          text += "  " + CDOCommonUtil.formatTimeStamp(timeStamp);
        }

        if (element instanceof OfflineCDOCheckout)
        {
          OfflineCDOCheckout offlineCheckout = (OfflineCDOCheckout)element;
          if (offlineCheckout.isDirty())
          {
            text += "  [dirty]";
          }
        }
      }
    }

    return text;
  }
}
