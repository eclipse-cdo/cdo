/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

import junit.framework.TestCase;

/**
 * See bug 201593
 *
 * @author Simon McDuff
 */
public class NonCDOResourceTest extends TestCase
{
  public void testNonCDOResource() throws Exception
  {
    Supplier supplier = Model1Factory.eINSTANCE.createSupplier();

    Resource resource = new ResourceImpl();
    resource.setURI(URI.createFileURI("/res1"));
    resource.getContents().add(supplier);

    assertEquals(resource, supplier.eResource());
  }
}
