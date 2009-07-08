/**
 * Copyright (c) 2009 Martin Taal, The Netherlands and others.
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

/**
 * Test different aspects of HQL querying using the CDO query api.
 * 
 * @author Martin Taal
 */
public class HibernateMultiValuedOfAttributeTest extends MultiValuedOfAttributeTest
{
  // overridden because not yet supported by CDO-Hibernate
  @Override
  public void testFeatureMaps() throws Exception
  {
  }

  @Override
  // overridden because there is a null value in the List created by the supermethod
  public void testListOfInteger() throws Exception
  {
    // List<Integer> list = new ArrayList<Integer>();
    // list.add(10);
    // list.add(null);
    // list.add(20);
    // testMultiValuedIOfAttribute(list, getModel5Package().getGenListOfInteger(), getModel5Package()
    // .getGenListOfInteger_Elements());
  }
}
