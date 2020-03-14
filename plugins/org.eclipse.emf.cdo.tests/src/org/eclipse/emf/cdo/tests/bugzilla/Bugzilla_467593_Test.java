/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.MyEnum;
import org.eclipse.emf.cdo.tests.model6.MyEnumList;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.util.Collections;

/**
 * Bug 467593 about NPE on {@link EList#hashCode()} call on object commit with a ref to the feature {@link EList} get before the commit.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_467593_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  public void testEListHashCodeOnRefBeforeCommitWithCDOResource() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource1 = transaction1.createResource(getResourcePath(RESOURCE_NAME));
    testEListHashCodeOnRefBeforeCommit(resource1);
  }

  public void testEListHashCodeOnRefBeforeCommitWithXMIResource() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("model6", new XMIResourceFactoryImpl());

    URI localMainResourceURI = URI.createFileURI(createTempFile("main", ".model6").getCanonicalPath());
    Resource resource1 = resourceSet.createResource(localMainResourceURI);

    testEListHashCodeOnRefBeforeCommit(resource1);
  }

  private void testEListHashCodeOnRefBeforeCommit(Resource resource) throws Exception
  {
    MyEnumList myEnumList = getModel6Factory().createMyEnumList();
    EList<MyEnum> myEnum = myEnumList.getMyEnum();
    myEnum.hashCode();
    resource.getContents().add(myEnumList);
    myEnum.hashCode();
    resource.save(Collections.emptyMap());
    myEnum.hashCode();
  }
}
