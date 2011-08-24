/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.properties;

import org.eclipse.core.expressions.PropertyTester;

import java.util.List;

/**
 * Tests properties of receiver objects against expected values.
 * 
 * @author Eike Stepper
 * @since 3.2
 */
public class DefaultPropertyTester<RECEIVER> extends PropertyTester implements IProperties<RECEIVER>
{
  private final IProperties<RECEIVER> properties;

  public DefaultPropertyTester(IProperties<RECEIVER> properties)
  {
    this.properties = properties;
  }

  public Property<RECEIVER> getProperty(String name)
  {
    return properties.getProperty(name);
  }

  public List<Property<RECEIVER>> getProperties()
  {
    return properties.getProperties();
  }

  public void add(Property<RECEIVER> property)
  {
    properties.add(property);
  }

  public boolean test(Object receiver, String propertyName, Object[] args, Object expectedValue)
  {
    Property<RECEIVER> property = getProperty(propertyName);
    if (property == null)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    RECEIVER typed = (RECEIVER)receiver;
    return property.testValue(typed, args, expectedValue);
  }
}
