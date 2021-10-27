/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.CDOClientProtocol;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.StringCompressor;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

import java.util.Collection;
import java.util.Iterator;

/**
 * Bug 517225: StringCompressor can create huge memory leak
 *
 * @author Eike Stepper
 */
public class Bugzilla_517225_Test extends AbstractCDOTest
{
  private static final int LEVELS = 3;

  private static final int CATEGORIES = 3;

  private static final int PRODUCTS = 15;

  @SuppressWarnings("unchecked")
  public void testStringCompressorLeak() throws Exception
  {
    // Initialize model.
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("res"));

      Category category = getModel1Factory().createCategory();
      category.setName("ROOT");

      Company company = getModel1Factory().createCompany();
      company.getCategories().add(category);
      resource.getContents().add(company);

      createModel(category, LEVELS, CATEGORIES, PRODUCTS);
      transaction.commit();
    }

    CDONet4jSession session = (CDONet4jSession)openSession();
    CDOClientProtocol protocol = (CDOClientProtocol)(CDOSessionProtocol)session.options().getNet4jProtocol();
    StringCompressor compressor = (StringCompressor)protocol.getPackageURICompressor();

    Collection<Integer> pendingAcknowledgements = (Collection<Integer>)ReflectUtil
        .getValue(ReflectUtil.getField(StringCompressor.class, "pendingAcknowledgements"), compressor);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("res"));
    for (Iterator<EObject> it = resource.eAllContents(); it.hasNext();)
    {
      it.next();
    }

    // Should be 9 with a HashSet instead of 728 with an ArrayList.
    assertTrue("pendingAcknowledgements: " + pendingAcknowledgements, pendingAcknowledgements.size() < 10);

    sleep(SignalProtocol.COMPRESSED_STRINGS_ACKNOWLEDGE_TIMEOUT + 1000);
    session.openView();

    pendingAcknowledgements = (Collection<Integer>)ReflectUtil.getValue(ReflectUtil.getField(StringCompressor.class, "pendingAcknowledgements"), compressor);
    assertEquals("pendingAcknowledgements: " + pendingAcknowledgements, 0, pendingAcknowledgements.size());
  }
}
