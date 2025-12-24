/*
 * Copyright (c) 2012, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerProvider;
import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class CustomizeableComposite extends Composite implements IManagedContainerProvider
{
  private final String productGroup;

  public CustomizeableComposite(Composite parent, String productGroup, int style)
  {
    super(parent, style);
    this.productGroup = productGroup;
    createUI();
  }

  public CustomizeableComposite(Composite parent, String productGroup)
  {
    this(parent, productGroup, SWT.NONE);
  }

  public final String getProductGroup()
  {
    return productGroup;
  }

  @Override
  public IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  protected void createUI()
  {
    IManagedContainer container = getContainer();
    customize(this, container, productGroup, null);
  }

  public static void customize(Composite composite, IManagedContainer container, String productGroup, Object data)
  {
    String description = data instanceof String ? (String)data : null;
    Set<String> factoryTypes = container.getFactoryTypes(productGroup);
    for (String type : factoryTypes)
    {
      CompositeCustomizer customizer = (CompositeCustomizer)container.getElement(productGroup, type, description);
      customizer.customize(composite, data);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface CompositeCustomizer
  {
    public void customize(Composite composite, Object data);
  }
}
