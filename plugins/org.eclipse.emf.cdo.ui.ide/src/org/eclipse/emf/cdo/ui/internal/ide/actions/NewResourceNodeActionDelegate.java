/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.ResourceNodeNameInputValidator;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;

/**
 * @author Eike Stepper
 */
public abstract class NewResourceNodeActionDelegate extends TransactionalBackgroundActionDelegate
{
  private CDOResourceNode newResourceNode;

  public NewResourceNodeActionDelegate(String text)
  {
    super(text);
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
      setNewResourceNode(createNewResourceNode());
      getNewResourceNode().setName(dialog.getValue());
      return super.preRun(object);
    }

    return null;
  }

  @Override
  protected final void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor)
      throws Exception
  {
    if (object instanceof CDOResourceFolder)
    {
      ((CDOResourceFolder)object).getNodes().add(getNewResourceNode());
    }
    else
    {
      transaction.getRootResource().getContents().add(getNewResourceNode());
    }
  }

  protected void setNewResourceNode(CDOResourceNode newResourceNode)
  {
    this.newResourceNode = newResourceNode;
  }

  protected CDOResourceNode getNewResourceNode()
  {
    return newResourceNode;
  }

  protected abstract CDOResourceNode createNewResourceNode();
}
