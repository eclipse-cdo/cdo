/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui.dialogs;

import org.eclipse.emf.cdo.internal.ui.messages.Messages;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

public class OpenResourcesDialog extends FilteredResourcesSelectionDialog
{
  public OpenResourcesDialog(Shell parentShell)
  {
    super(parentShell, true, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
    setTitle(Messages.getString("OpenResourcesDialog.0")); //$NON-NLS-1$
    setInitialPattern("*.ecore"); //$NON-NLS-1$
  }
}
