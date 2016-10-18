/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Test different aspects of HQL querying using the CDO query api.
 *
 * @author Martin Taal
 */
public class HibernateMultiValuedOfAttributeTest extends MultiValuedOfAttributeTest
{
  @Override
  // overridden because there is a null value in the List created by the supermethod
  public void testListOfInteger() throws Exception
  {
    List<Integer> list = new ArrayList<Integer>();
    list.add(10);
    list.add(13);
    list.add(20);
    testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfInteger(), getModel5Package().getGenListOfInteger_Elements());
  }
}
