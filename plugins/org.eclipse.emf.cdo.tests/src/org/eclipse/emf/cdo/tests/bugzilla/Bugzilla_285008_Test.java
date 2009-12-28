/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Invalid listener might prevent CDOTransaction to close properly
 * <p>
 * See bug 285008
 * 
 * @author Andre Dietisheim
 */
public class Bugzilla_285008_Test extends AbstractCDOTest
{
  public void testBugzilla_285008() throws Exception
  {
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(Model3Package.eINSTANCE);
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.createResource("/res1");

      Class1 class1 = getModel3Factory().createClass1();
      Class2 class2a = getModel3SubpackageFactory().createClass2();
      Class2 class2b = getModel3SubpackageFactory().createClass2();
      class1.getClass2().add(class2a);
      class1.getClass2().add(class2b);

      res1.getContents().add(class1);
      res1.getContents().add(class2a);
      res1.getContents().add(class2b);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res1 = transaction.getResource("/res1");

      Class1 class1 = (Class1)res1.getContents().get(0);
      assertNotNull(class1);
      assertTrue(class1.isSetClass2());
    }
  }
}
