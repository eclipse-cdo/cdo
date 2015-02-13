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
package org.eclipse.emf.cdo.explorer.ui.handlers;

import org.eclipse.emf.cdo.explorer.ui.DeleteObjectsDialog;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.handlers.HandlerUtil;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class ObjectDeleteHandler extends AbstractObjectHandler
{
  public ObjectDeleteHandler()
  {
    super(null);
  }

  @Override
  protected void preRun(ExecutionEvent event) throws Exception
  {
    DeleteObjectsDialog dialog = new DeleteObjectsDialog(HandlerUtil.getActiveShell(event), getCheckout(), elements);
    if (dialog.open() != DeleteObjectsDialog.OK)
    {
      cancel();
    }
  }

  @Override
  protected boolean doExecute(ExecutionEvent event, List<EObject> transactionalElements, IProgressMonitor monitor)
  {
    for (EObject eObject : transactionalElements)
    {
      EcoreUtil.remove(eObject);
    }

    return true;
  }
}
