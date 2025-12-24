/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public class TCPSelectorFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.selectors"; //$NON-NLS-1$

  public static final String TYPE = "tcp"; //$NON-NLS-1$

  public TCPSelectorFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  @Override
  public TCPSelector create(String description)
  {
    return new TCPSelector();
  }

  public static TCPSelector get(IManagedContainer container, String description)
  {
    return (TCPSelector)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
