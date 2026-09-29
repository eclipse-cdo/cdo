/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.server.IRepositoryProtector;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.net4j.util.properties.IPropertiesContainer;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * Tests the per-element properties exposed by repository protector elements.
 */
public class RepositoryProtectorElementPropertiesTest extends PlainTest
{
  public void testPropertiesRegistryIsStableAndMutable()
  {
    IRepositoryProtector.Element element = new TestUserAuthenticator();
    IPropertiesContainer container = element;
    assertNotNull(container);

    IRegistry<String, Object> properties = container.properties();
    assertSame(properties, container.properties());

    Object value = new Object();
    properties.put("key", value); //$NON-NLS-1$
    assertSame(value, properties.get("key")); //$NON-NLS-1$
    assertSame(value, properties.remove("key")); //$NON-NLS-1$
    assertFalse(properties.containsKey("key")); //$NON-NLS-1$
  }

  public void testElementsHaveIndependentProperties()
  {
    IRepositoryProtector.Element first = new TestUserAuthenticator();
    IRepositoryProtector.Element second = new TestUserAuthenticator();
    Object firstValue = new Object();
    Object secondValue = new Object();

    first.properties().put("shared-key", firstValue); //$NON-NLS-1$
    second.properties().put("shared-key", secondValue); //$NON-NLS-1$
    assertSame(firstValue, first.properties().get("shared-key")); //$NON-NLS-1$
    assertSame(secondValue, second.properties().get("shared-key")); //$NON-NLS-1$

    first.properties().clear();
    assertTrue(first.properties().isEmpty());
    assertSame(secondValue, second.properties().get("shared-key")); //$NON-NLS-1$

    second.properties().remove("shared-key"); //$NON-NLS-1$
    assertTrue(second.properties().isEmpty());
    assertTrue(first.properties().isEmpty());
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestUserAuthenticator extends IRepositoryProtector.UserAuthenticator
  {
    public TestUserAuthenticator()
    {
    }

    @Override
    public IRepositoryProtector.UserInfo authenticateUser(String userID, char[] password)
    {
      return null;
    }
  }
}
