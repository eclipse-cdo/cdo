/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.http.internal.server;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.IRandomizer;

/**
 * @author Eike Stepper
 */
public class RandomizerInjector implements IElementProcessor
{
  public static final String TYPE = null;

  public RandomizerInjector()
  {
  }

  @Override
  public Object process(IManagedContainer container, String productGroup, String factoryType, String description, Object element)
  {
    if (element instanceof HTTPAcceptor)
    {
      HTTPAcceptor acceptor = (HTTPAcceptor)element;
      if (acceptor.getRandomizer() == null)
      {
        acceptor.setRandomizer(getRandomizer(container));
      }
    }

    return element;
  }

  protected IRandomizer getRandomizer(IManagedContainer container)
  {
    return (IRandomizer)container.getElement("org.eclipse.net4j.randomizers", "default", TYPE); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
