/*
 * Copyright (c) 2016, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EObject;

import java.util.Map;

import base.BaseFactory;
import base.Document;
import base.Element;

/**
 * Bug 491859 - Referential integrity check fails when container is changed and previous container is deleted
 *
 * @author Eike Stepper
 */
public class Bugzilla_491859_Test extends AbstractCDOTest
{
  @Override
  protected void initTestProperties(Map<String, Object> properties)
  {
    super.initTestProperties(properties);
    properties.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testExchangeContainerObject() throws Exception
  {
    skipStoreWithoutQueryXRefs();

    //////////
    // Init //
    //////////

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    // document
    // document/root
    // document/root/element
    // document/root/element/child1
    // document/root/element/child2

    Document document = BaseFactory.eINSTANCE.createDocument();
    resource.getContents().add(document);

    Element root = BaseFactory.eINSTANCE.createElement();
    document.setRoot(root);

    Element element = BaseFactory.eINSTANCE.createElement();
    root.getSubelements().add(element);

    Element child1 = BaseFactory.eINSTANCE.createElement();
    element.getSubelements().add(child1);

    Element subChild1 = BaseFactory.eINSTANCE.createElement();
    child1.getSubelements().add(subChild1);

    Element subChild2 = BaseFactory.eINSTANCE.createElement();
    child1.getSubelements().add(subChild2);

    Element child2 = BaseFactory.eINSTANCE.createElement();
    element.getSubelements().add(child2);

    transaction.commit();

    //////////
    // Test //
    //////////

    // Now replace root by root_new
    replace(root);
    transaction.commit();

    // Now replace element by element_new
    Element newElement = replace(element);
    transaction.commit();

    assertTrue(newElement.getSubelements().contains(child1));
    assertTrue(newElement.getSubelements().contains(child2));

    // Now replace element by yet another element_new
    Element yetAnotherNewElement = replace(newElement);

    assertTrue(yetAnotherNewElement.getSubelements().contains(child1));
    assertTrue(yetAnotherNewElement.getSubelements().contains(child2));
    transaction.commit();
  }

  /**
   * Replace the <code>element</code> at its parent.
   *
   * @return the replacement
   */
  private Element replace(Element element)
  {
    Element replacement = BaseFactory.eINSTANCE.createElement();
    replacement.getSubelements().addAll(element.getSubelements());

    EObject parent = element.eContainer();
    if (parent instanceof Element)
    {
      Element parentElement = (Element)parent;
      int index = parentElement.getSubelements().indexOf(element);
      parentElement.getSubelements().set(index, replacement);
    }
    else
    {
      Document parentDocument = (Document)parent;
      parentDocument.setRoot(replacement);
    }

    return replacement;
  }
}
