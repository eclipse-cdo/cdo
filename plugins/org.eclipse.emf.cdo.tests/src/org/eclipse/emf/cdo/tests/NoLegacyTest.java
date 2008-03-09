/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.LegacySystemNotAvailableException;

import org.eclipse.emf.ecore.xml.type.ProcessingInstruction;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * @author Eike Stepper
 */
public class NoLegacyTest extends AbstractCDOTest
{
  public void testOpenLegacySession() throws Exception
  {
    CDOSession session = null;

    try
    {
      session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, false);
      fail("LegacySystemNotAvailableException expected");
    }
    catch (LegacySystemNotAvailableException ex)
    {
    }
    finally
    {
      if (session != null)
      {
        session.close();
      }
    }
  }

  public void testAttachLegacyObject() throws Exception
  {
    CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME, true);
    session.getPackageRegistry().putEPackage(XMLTypePackage.eINSTANCE);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    ProcessingInstruction pi = XMLTypeFactory.eINSTANCE.createProcessingInstruction();
    pi.setData("data");
    pi.setTarget("target");

    try
    {
      resource.getContents().add(pi);
      fail("LegacySystemNotAvailableException expected");
    }
    catch (LegacySystemNotAvailableException ex)
    {
    }
    finally
    {
      session.close();
    }
  }
}
