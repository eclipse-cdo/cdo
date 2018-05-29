/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.AttributeTest;

/**
 * Disable some tests which won't work with hibernate (persisting java classes and java objects).
 *
 * @author Martin Taal
 */
public class HibernateAttributeTest extends AttributeTest
{

  @Override
  public void testJavaClass() throws Exception
  {
  }

  @Override
  public void testJavaObject() throws Exception
  {
  }

}
