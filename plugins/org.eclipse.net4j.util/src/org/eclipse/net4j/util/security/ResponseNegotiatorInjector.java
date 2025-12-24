/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ResponseNegotiatorInjector implements IElementProcessor
{
  private INegotiator negotiator;

  public ResponseNegotiatorInjector(INegotiator negotiator)
  {
    this.negotiator = negotiator;
  }

  public INegotiator getNegotiator()
  {
    return negotiator;
  }

  @Override
  public Object process(IManagedContainer container, String productGroup, String factoryType, String description, Object element)
  {
    if (element instanceof INegotiatorAware)
    {
      INegotiatorAware negotiatorAware = (INegotiatorAware)element;
      if (negotiatorAware.getNegotiator() == null)
      {
        if (filterElement(productGroup, factoryType, description, negotiatorAware))
        {
          if (negotiator != null)
          {
            negotiatorAware.setNegotiator(negotiator);
          }
        }
      }
    }

    return element;
  }

  protected abstract boolean filterElement(String productGroup, String factoryType, String description, INegotiatorAware negotiatorAware);
}
