/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOCheckout;
import org.eclipse.emf.cdo.explorer.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.CDORepository;

import org.eclipse.net4j.util.container.SetContainer;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutManagerImpl extends SetContainer<CDOCheckout> implements CDOCheckoutManager
{
  public CDOCheckoutManagerImpl()
  {
    super(CDOCheckout.class);
  }

  public CDOCheckout[] getCheckouts()
  {
    return getElements();
  }

  public CDOCheckout connect(String label, CDORepository repository, String branchPath, long timeStamp,
      boolean readOnly, CDOID rootID)
  {
    CDOCheckout checkout = new OnlineCDOCheckout(this, repository, branchPath, timeStamp, readOnly, rootID, label);
    return addCheckout(checkout);
  }

  private CDOCheckout addCheckout(CDOCheckout checkout)
  {
    if (addElement(checkout))
    {
      CDORepositoryImpl repository = (CDORepositoryImpl)checkout.getRepository();
      repository.addCheckout(checkout);
      return checkout;
    }

    return null;
  }
}
