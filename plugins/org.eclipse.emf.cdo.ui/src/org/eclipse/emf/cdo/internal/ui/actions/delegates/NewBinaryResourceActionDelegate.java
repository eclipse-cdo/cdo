/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;

/**
 * @author Eike Stepper
 */
@Deprecated
public class NewBinaryResourceActionDelegate extends NewResourceNodeActionDelegate
{
  @Deprecated
  public NewBinaryResourceActionDelegate()
  {
    super(Messages.getString("NewBinaryResourceAction_0")); //$NON-NLS-1$
  }

  @Deprecated
  @Override
  protected CDOResourceNode createNewResourceNode()
  {
    return EresourceFactory.eINSTANCE.createCDOBinaryResource();
  }
}
