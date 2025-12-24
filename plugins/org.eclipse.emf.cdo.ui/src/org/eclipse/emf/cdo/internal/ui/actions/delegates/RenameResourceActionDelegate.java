/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.ResourceNodeNameInputValidator;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;

/**
 * @author Victor Roldan Betancort
 */
@Deprecated
public class RenameResourceActionDelegate extends TransactionalBackgroundActionDelegate
{
  private String newResourceName;

  @Deprecated
  public RenameResourceActionDelegate()
  {
    super(Messages.getString("RenameResourceActionDelegate.0")); //$NON-NLS-1$
  }

  @Deprecated
  @Override
  protected CDOObject preRun(CDOObject object)
  {
    InputDialog dialog = new InputDialog(getTargetPart().getSite().getShell(), getText(), Messages.getString("NewResourceNodeAction_0"), null, //$NON-NLS-1$
        new ResourceNodeNameInputValidator((CDOResourceNode)object));
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

  @Deprecated
  @Override
  protected final void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception
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
