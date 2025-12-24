/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

/**
 * @author Eike Stepper
 */
@Deprecated
public class NewResourceFolderActionDelegate extends NewResourceNodeActionDelegate
{
  @Deprecated
  public NewResourceFolderActionDelegate()
  {
    super(Messages.getString("NewResourceFolderAction_0")); //$NON-NLS-1$
  }

  @Deprecated
  @Override
  protected CDOResourceNode createNewResourceNode()
  {
    return EresourceFactory.eINSTANCE.createCDOResourceFolder();
  }
}
