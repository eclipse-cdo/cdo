/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model3.MetaRef;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EReference;

/**
 * @author Eike Stepper
 */
public class MetaTest extends AbstractCDOTest
{
  public void testMetaReference() throws Exception
  {
    skipStoreWithoutExternalReferences();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      MetaRef metaRef = getModel3Factory().createMetaRef();
      metaRef.setEPackageRef(getModel3Package());
      res.getContents().add(metaRef);
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    MetaRef metaRef = (MetaRef)res.getContents().get(0);
    assertEquals(getModel3Package(), metaRef.getEPackageRef());
  }

  public void testMetaReferenceAttachFirst() throws Exception
  {
    skipStoreWithoutExternalReferences();

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/res"));

      MetaRef metaRef = getModel3Factory().createMetaRef();
      res.getContents().add(metaRef);
      metaRef.setEPackageRef(getModel3Package());
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res"));

    MetaRef metaRef = (MetaRef)res.getContents().get(0);
    assertEquals(getModel3Package(), metaRef.getEPackageRef());
  }

  public void testMetaReference2() throws Exception
  {
    skipStoreWithoutExternalReferences();

    EReference targetRef = getModel3SubpackagePackage().getClass2_Class1();

    {
      MetaRef metaRef = getModel3Factory().createMetaRef();
      metaRef.setEReferenceRef(targetRef);

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(metaRef);

      transaction.commit();

      // EReference sourceRef = getModel3Package().getMetaRef_EReferenceRef();
      // CDORevisionData data = CDOUtil.getCDOObject(metaRef).cdoRevision().data();
      // Object id = data.get(sourceRef, -1);
      // assertInstanceOf(CDOIDMeta.class, id);

      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    MetaRef metaRef = (MetaRef)resource.getContents().get(0);
    assertEquals(targetRef, metaRef.getEReferenceRef());
  }
}
