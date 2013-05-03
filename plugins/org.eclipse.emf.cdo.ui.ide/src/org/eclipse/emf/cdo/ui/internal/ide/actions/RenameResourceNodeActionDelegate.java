/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.internal.ide.actions;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.ResourceNodeNameInputValidator;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;

/**
 * @author Victor Roldan Betancort
 */
public class RenameResourceNodeActionDelegate extends TransactionalBackgroundActionDelegate
{
  private String newResourceName;

  public RenameResourceNodeActionDelegate()
  {
    super(Messages.getString("RenameResourceNodeActionDelegate.0")); //$NON-NLS-1$
  }

  @Override
  protected CDOObject preRun(CDOObject object)
  {
    InputDialog dialog = new InputDialog(
        getTargetPart().getSite().getShell(),
        getText(),
        Messages.getString("NewResourceNodeAction_0"), null, new ResourceNodeNameInputValidator((CDOResourceNode)object)); //$NON-NLS-1$
    if (dialog.open() == Dialog.OK)
    {
      setNewResourceName(dialog.getValue());
      return super.preRun(object);
    }

    cancel();

    return null;
  }

  private void setNewResourceName(String newName)
  {
    newResourceName = newName;
  }

  private String getNewResourceName()
  {
    return newResourceName;
  }

  @Override
  protected final void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor)
      throws Exception
  {
    if (object instanceof CDOResourceNode)
    {
      ((CDOResourceNode)object).setName(getNewResourceName());
    }
    else
    {
      throw new IllegalArgumentException("object is not a CDOResourceNode"); //$NON-NLS-1$
    }
  }

}
