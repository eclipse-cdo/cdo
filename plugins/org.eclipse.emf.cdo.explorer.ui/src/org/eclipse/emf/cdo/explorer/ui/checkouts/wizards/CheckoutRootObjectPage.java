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
package org.eclipse.emf.cdo.explorer.ui.checkouts.wizards;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl;

import org.eclipse.swt.widgets.Composite;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CheckoutRootObjectPage extends CheckoutWizardPage
{
  private CDOID rootID;

  public CheckoutRootObjectPage()
  {
    super("New Checkout", "Select the type of the new checkout.");
  }

  public final CDOID getRootID()
  {
    return rootID;
  }

  public final void setRootID(CDOID rootID)
  {
    this.rootID = rootID;
  }

  @Override
  protected void createUI(Composite parent)
  {
  }

  @Override
  protected boolean doValidate() throws Exception
  {
    return true;
  }

  @Override
  protected void fillProperties(Properties properties)
  {
    properties.setProperty("rootID", CDOCheckoutImpl.getCDOIDString(rootID));
  }
}
