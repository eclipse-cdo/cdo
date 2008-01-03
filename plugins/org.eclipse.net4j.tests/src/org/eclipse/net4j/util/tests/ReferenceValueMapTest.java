/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.ref.ReferenceValueMap;

/**
 * @author Eike Stepper
 */
public class ReferenceValueMapTest extends AbstractOMTest
{
  public void testSameKey() throws Exception
  {
    ReferenceValueMap<String, Object> map = new ReferenceValueMap.Weak<String, Object>();
    for (int i = 0; i < 10; i++)
    {
      map.put("SIMON", new Object());
      System.gc();
      map.put("SIMON", new Object());
      assertTrue(map.size() >= 0);
    }
  }

  public void testDifferentKey() throws Exception
  {
    ReferenceValueMap<String, Object> map = new ReferenceValueMap.Weak<String, Object>();
    for (int i = 0; i < 10; i++)
    {
      map.put("SIMON", new Object());
      System.gc();
      map.put("SIMON2", new Object());
      assertTrue(map.size() >= 1);
    }
  }
}
