/*
 * Copyright (c) 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactoryKey;

/**
 * A {@link StructuredContentProvider structured content provider} that shows the {@link IFactoryKey#getType() factory
 * types} of the {@link #getInput() input}.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public class FactoryTypeContentProvider extends StructuredContentProvider<IManagedContainer>
{
  private String productGroup;

  public FactoryTypeContentProvider(String productGroup)
  {
    this.productGroup = productGroup;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  @Override
  public Object[] getElements(Object inputElement)
  {
    return getInput().getFactoryTypes(productGroup).toArray();
  }

  @Override
  protected void connectInput(IManagedContainer input)
  {
    input.addListener(this);
    input.getFactoryRegistry().addListener(this);
  }

  @Override
  protected void disconnectInput(IManagedContainer input)
  {
    input.removeListener(this);
    input.getFactoryRegistry().removeListener(this);
  }
}
