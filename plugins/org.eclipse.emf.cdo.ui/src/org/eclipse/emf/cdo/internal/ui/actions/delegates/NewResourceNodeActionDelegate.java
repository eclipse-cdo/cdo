/*
 * Copyright (c) 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.actions.delegates;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.actions.ResourceNodeNameInputValidator;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;

/**
 * @author Eike Stepper
 */
@Deprecated
public abstract class NewResourceNodeActionDelegate extends TransactionalBackgroundActionDelegate
{
  private CDOResourceNode newResourceNode;

  @Deprecated
  public NewResourceNodeActionDelegate(String text)
  {
    super(text);
  }

  @Deprecated
  @Override
  protected CDOObject preRun(CDOObject object)
  {
    InputDialog dialog = new InputDialog(getTargetPart().getSite().getShell(), getText(), Messages.getString("NewResourceNodeAction_0"), null, //$NON-NLS-1$
        new ResourceNodeNameInputValidator((CDOResourceNode)object));
    if (dialog.open() == Dialog.OK)
    {
      setNewResourceNode(createNewResourceNode());
      getNewResourceNode().setName(dialog.getValue());
      return super.preRun(object);
    }

    return null;
  }

  @Deprecated
  @Override
  protected final void doRun(CDOTransaction transaction, CDOObject object, IProgressMonitor progressMonitor) throws Exception
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

  @Deprecated
  protected void setNewResourceNode(CDOResourceNode newResourceNode)
  {
    this.newResourceNode = newResourceNode;
  }

  @Deprecated
  protected CDOResourceNode getNewResourceNode()
  {
    return newResourceNode;
  }

  @Deprecated
  protected abstract CDOResourceNode createNewResourceNode();
}
