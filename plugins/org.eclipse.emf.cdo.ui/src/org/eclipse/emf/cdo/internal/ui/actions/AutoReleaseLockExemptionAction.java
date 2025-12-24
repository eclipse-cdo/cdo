/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.ui.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Eike Stepper
 */
public class AutoReleaseLockExemptionAction extends EditingDomainAction
{
  public static final String ID = "AutoReleaseLockExemption"; //$NON-NLS-1$

  private static final String TITLE = Messages.getString("AutoReleaseLockExemptionAction.1"); //$NON-NLS-1$

  private CDOObject selectedObject;

  public AutoReleaseLockExemptionAction()
  {
    super(TITLE);
    setId(ID);
  }

  public void selectionChanged(IStructuredSelection selection)
  {
    selectedObject = null;

    if (selection != null && selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      if (element instanceof EObject)
      {
        EObject object = (EObject)element;
        selectedObject = CDOUtil.getCDOObject(object);
      }
    }
  }

  public boolean init()
  {
    if (selectedObject != null && selectedObject.cdoWriteLock().isLocked())
    {
      CDOTransaction transaction = (CDOTransaction)selectedObject.cdoView();
      setChecked(transaction.options().isAutoReleaseLocksExemption(selectedObject));
      return true;
    }

    return false;
  }

  @Override
  public void update()
  {
    setEnabled(true);
  }

  @Override
  protected void doRun(IProgressMonitor progressMonitor) throws Exception
  {
    if (selectedObject != null)
    {
      CDOTransaction transaction = (CDOTransaction)selectedObject.cdoView();
      if (transaction.options().isAutoReleaseLocksExemption(selectedObject))
      {
        transaction.options().removeAutoReleaseLocksExemptions(false, selectedObject);
      }
      else
      {
        transaction.options().addAutoReleaseLocksExemptions(false, selectedObject);
      }
    }

    update();
  }
}
