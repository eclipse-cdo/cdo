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

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class CheckoutDecorator implements ILabelDecorator
{
  public CheckoutDecorator()
  {
  }

  public void dispose()
  {
  }

  public Image decorateImage(Image image, Object element)
  {
    return image;
  }

  public String decorateText(String text, Object element)
  {
    if (element instanceof CDOCheckout)
    {
      CDOCheckout checkout = (CDOCheckout)element;

      String branchPath = checkout.getBranchPath();
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
        text += " " + branchPath;
      }

      long timeStamp = checkout.getTimeStamp();
      if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
      {
        text += " " + CDOCommonUtil.formatTimeStamp(timeStamp);
      }
    }

    return text;
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  public void addListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles them.
  }

  public void removeListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles them.
  }
}
