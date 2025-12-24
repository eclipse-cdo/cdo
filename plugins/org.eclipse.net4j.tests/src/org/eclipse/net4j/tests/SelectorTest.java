/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;

import junit.framework.TestCase;

/**
 * @author Eike Stepper
 */
public class SelectorTest extends TestCase
{
  public void testOpen() throws Exception
  {
    SelectorProvider provider = SelectorProvider.provider();
    System.out.println(provider.getClass().getName());
    for (int i = 0; i < 5; i++)
    {
      long start = System.currentTimeMillis();
      Selector selector = provider.openSelector();
      long duration = System.currentTimeMillis() - start;

      System.out.println(duration);
      selector.close();
    }
  }
}
