/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public final class CheckoutPropertyPage extends AbstractPropertyPage<CDOCheckout>
{
  private Button prefetchButton;

  public CheckoutPropertyPage()
  {
    super(CDOCheckoutProperties.INSTANCE, CDOCheckoutProperties.CATEGORY_CHECKOUT, "id", "type", "label", "folder", "prefetch");
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

    if ("prefetch".equals(name))
    {
      prefetchButton = new Button(parent, SWT.CHECK);
      prefetchButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
      prefetchButton.setSelection(getInput().isPrefetch());
      return prefetchButton;
    }

    return super.createControl(parent, name, description, value);
  }

  @Override
  public boolean performOk()
  {
    boolean prefetch = prefetchButton.getSelection();
    getInput().setPrefetch(prefetch);

    return super.performOk();
  }
}
