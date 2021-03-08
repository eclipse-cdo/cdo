/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.properties;

import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutProperties;

import org.eclipse.net4j.util.AdapterUtil;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public final class CheckoutPropertyPage extends AbstractPropertyPage<CDOCheckout>
{
  public CheckoutPropertyPage()
  {
    super(CDOCheckoutProperties.INSTANCE, CDOCheckoutProperties.CATEGORY_CHECKOUT, "id", "type", "label", "folder");
  }

  @Override
  protected CDOCheckout convertElement(IAdaptable element)
  {
    return AdapterUtil.adapt(element, CDOCheckout.class);
  }

  @Override
  protected Control createControl(Composite parent, String name, String description, String value)
  {
    if ("folder".equals(name))
    {
      return createFileLink(parent, name, description, value);
    }

    return super.createControl(parent, name, description, value);
  }
}
