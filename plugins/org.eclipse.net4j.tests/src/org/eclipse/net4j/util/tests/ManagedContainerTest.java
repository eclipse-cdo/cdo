/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.RandomizerFactory;

/**
 * @author Eike Stepper
 */
public class ManagedContainerTest extends AbstractOMTest
{
  public void testCountElements() throws Exception
  {
    IManagedContainer container = ContainerUtil.createContainer();
    ContainerUtil.prepareContainer(container);
    container.activate();
    assertEquals(0, container.countElements(RandomizerFactory.PRODUCT_GROUP));
    assertEquals(0, container.countElements(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE));

    Object element = container.getElement(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE, null);
    assertNotNull(element);
    assertEquals(1, container.countElements(RandomizerFactory.PRODUCT_GROUP));
    assertEquals(1, container.countElements(RandomizerFactory.PRODUCT_GROUP, RandomizerFactory.TYPE));
  }
}
