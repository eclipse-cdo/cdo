/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.views;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.ui.widgets.TimeSlider;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class CDOTimeMachineView extends ViewPart implements ISelectionListener
{
  public static final String ID = "org.eclipse.emf.cdo.ui.CDOTimeMachineView"; //$NON-NLS-1$

  private TimeSlider timeSlider;

  public CDOTimeMachineView()
  {
  }

  @Override
  public void createPartControl(Composite parent)
  {
    timeSlider = new TimeSlider(parent, SWT.NONE);

    IWorkbenchPage page = getSite().getPage();
    selectionChanged(null, page.getSelection());
    page.addSelectionListener(this);
  }

  @Override
  public void dispose()
  {
    getSite().getPage().removeSelectionListener(this);
    timeSlider.disconnect();
    super.dispose();
  }

  @Override
  public void setFocus()
  {
    timeSlider.setFocus();
  }

  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection selection)
  {
    CDOView view = getView(selection);
    if (view != null && view.properties().get(CDOView.PROP_TIME_MACHINE_DISABLED) == Boolean.TRUE)
    {
      view = null;
    }

    timeSlider.connect(view, null);
  }

  private CDOView getView(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      for (Iterator<?> it = ssel.iterator(); it.hasNext();)
      {
        Object element = it.next();
        if (element instanceof CDOElement)
        {
          element = ((CDOElement)element).getDelegate();
        }

        if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;
          CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
          if (cdoObject != null)
          {
            CDOView view = cdoObject.cdoView();
            if (view != null && view.isReadOnly())
            {
              return view;
            }
          }
        }
        else
        {
          CDOView view = AdapterUtil.adapt(element, CDOView.class);
          if (view != null && view.isReadOnly())
          {
            return view;
          }
        }
      }
    }

    return null;
  }

  // protected String formatTimeSliderLabel(long timeStamp)
  // {
  // CDOBranchPoint branchPoint = CDOBranchUtil.normalizeBranchPoint(view.getBranch(), timeStamp);
  // return branchPoint.getBranch().getPathName() + " [" + CDOCommonUtil.formatTimeStamp(timeStamp) + "]";
  // }
}
