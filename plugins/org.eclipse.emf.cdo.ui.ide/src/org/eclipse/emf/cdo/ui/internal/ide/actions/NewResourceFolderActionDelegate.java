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

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.ui.internal.ide.messages.Messages;

/**
 * @author Eike Stepper
 */
public class NewResourceFolderActionDelegate extends NewResourceNodeActionDelegate
{
  public NewResourceFolderActionDelegate()
  {
    super(Messages.getString("NewResourceFolderAction_0")); //$NON-NLS-1$
  }

  @Override
  protected CDOResourceNode createNewResourceNode()
  {
    return EresourceFactory.eINSTANCE.createCDOResourceFolder();
  }
}
