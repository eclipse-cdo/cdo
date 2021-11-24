/*
 * Copyright (c) 2015, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.BaseLabelDecorator;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.CDOLabelDecorator;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelDecorator extends BaseLabelDecorator
{
  public static final Image ERROR_OVERLAY = SharedIcons.getImage(SharedIcons.OVR_ERROR);

  public CDOCheckoutLabelDecorator()
  {
  }

  @Override
  public Image decorateImage(Image image, Object element)
  {
    try
    {
      if (element instanceof CDOCheckout)
      {
        CDOCheckout checkout = (CDOCheckout)element;
        if (checkout.getError() != null)
        {
          return OM.getOverlayImage(image, ERROR_OVERLAY, 9, 7);
        }
      }

      CDOElement cdoElement = AdapterUtil.adapt(element, CDOElement.class);
      if (cdoElement != null)
      {
        element = cdoElement.getDelegate();
      }

      if (element instanceof EObject)
      {
        return CDOLabelDecorator.decorate(image, element);
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (Throwable ex)
    {
      if (LifecycleUtil.isActive(element))
      {
        OM.LOG.error(ex);
      }
    }

    return image;
  }

  @Override
  public String decorateText(String text, Object element)
  {
    try
    {
      CDOElement cdoElement = AdapterUtil.adapt(element, CDOElement.class);
      if (cdoElement != null)
      {
        element = cdoElement.getDelegate();
      }

      if (element instanceof CDOCheckout)
      {
        CDOCheckout checkout = (CDOCheckout)element;

        String branchPath = checkout.getBranchPath();
        if (branchPath != null)
        {
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
            if (checkout.isOffline())
            {
              text += "  [offline: " + branchPath + "]";
            }
            else
            {
              text += "  [" + branchPath + "]";
            }
          }
          else
          {
            if (checkout.isOffline())
            {
              text += "  [offline]";
            }
          }
        }

        long timeStamp = checkout.getTimeStamp();
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE)
        {
          text += "  " + CDOCommonUtil.formatTimeStamp(timeStamp);
        }
        else if (checkout.isReadOnly())
        {
          text += "  read-only";
        }

        if (checkout.isOffline())
        {
          if (checkout.isDirty())
          {
            text += "  dirty";
          }
          else
          {
            text += "  clean";
          }
        }

        String error = checkout.getError();
        if (error != null)
        {
          text += "  " + error;
        }
      }
    }
    catch (LifecycleException ex)
    {
      //$FALL-THROUGH$
    }
    catch (Throwable ex)
    {
      if (LifecycleUtil.isActive(element))
      {
        OM.LOG.error(ex);
      }
    }

    return text;
  }
}
