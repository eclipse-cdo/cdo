/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=201366
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=213782
 * @author Simon McDuff
 */
public class RandomDeadLockTest extends AbstractCDOTest
{
	@Override
	protected void doSetUp() throws Exception 
	{
		super.doSetUp();
	    OMPlatform.INSTANCE.setDebugging(false);
	}

	public void testCreateManySession() throws Exception
  {
    {
      msg("Opening session");
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test2");
      transaction.commit();
      transaction.close();
      session.close();
    }

    for (int i = 0; i < 100; i++)
    {
    	msg("Loop " + i);
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource("/test2");
      Category category = Model1Factory.eINSTANCE.createCategory();
      resource.getContents().add(category);
      transaction.commit();
      transaction.close();
      session.close();
    }
  }
  
  public void testCreateManyTransaction() throws Exception
  {
      msg("Opening session");
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());
      transaction.createResource("/test2");
      transaction.commit();
      transaction.close();
      
      for (int i =0; i < 1000; i++)
      {
      	msg("Loop " + i);
    	   transaction = session.openTransaction(new ResourceSetImpl());
          CDOResource resource = transaction.getResource("/test2");
          Category category = Model1Factory.eINSTANCE.createCategory();
          resource.getContents().add(category);
       	  transaction.commit();  	
       	  transaction.close();
      }
      
      session.close();
    }  
}
