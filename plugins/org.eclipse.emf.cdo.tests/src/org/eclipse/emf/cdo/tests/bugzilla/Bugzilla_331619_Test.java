/*
 * Copyright (c) 2010-2013, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collections;
import java.util.List;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_331619_Test extends AbstractCDOTest
{
  @SuppressWarnings("unchecked")
  public void testAbstractXRef() throws CommitException
  {
    skipStoreWithoutQueryXRefs();

    // set up model
    EPackage pkg = createUniquePackage();
    EClass ref = EMFUtil.createEClass(pkg, "referencee", false, false);
    EAttribute refName = EMFUtil.createEAttribute(ref, "refname", EcorePackage.eINSTANCE.getEString());

    EClass abs = EMFUtil.createEClass(pkg, "abstractClass", true, false);
    EReference reference = EMFUtil.createEReference(abs, "reference", ref, false, true);
    reference.setContainment(false);

    EClass con = EMFUtil.createEClass(pkg, "concreteClass", false, false);
    con.getESuperTypes().add(abs);
    EAttribute conName = EMFUtil.createEAttribute(con, "conname", EcorePackage.eINSTANCE.getEString());

    CDOUtil.prepareDynamicEPackage(pkg);

    // write model
    {
      CDOSession session = openSession();
      CDOTransaction tx = session.openTransaction();
      CDOResource refres = tx.createResource(getResourcePath("/test/referenced"));
      CDOResource conres = tx.createResource(getResourcePath("/test/concrete"));

      EObject[] oRef = new EObject[6];
      for (int i = 0; i < 6; i++)
      {
        oRef[i] = EcoreUtil.create(ref);
        oRef[i].eSet(refName, "ref" + i);
        refres.getContents().add(oRef[i]);
      }

      EObject con1 = EcoreUtil.create(con);
      con1.eSet(conName, "con1");
      ((EList<EObject>)con1.eGet(reference)).add(oRef[1]);
      ((EList<EObject>)con1.eGet(reference)).add(oRef[5]);
      conres.getContents().add(con1);

      EObject con2 = EcoreUtil.create(con);
      con2.eSet(conName, "con2");
      ((EList<EObject>)con2.eGet(reference)).add(oRef[4]);
      ((EList<EObject>)con2.eGet(reference)).add(oRef[1]);
      conres.getContents().add(con2);

      tx.commit();
      tx.close();
      session.close();
    }

    // read model
    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource res = view.getResource(getResourcePath("/test/referenced"));

      EObject oRef = res.getContents().get(0);
      refName = (EAttribute)oRef.eClass().getEStructuralFeature("refname");
      assertEquals("ref0", oRef.eGet(refName));
      List<CDOObjectReference> results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(0, results.size());

      oRef = res.getContents().get(1);
      assertEquals("ref1", oRef.eGet(refName));
      results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(2, results.size());

      conName = (EAttribute)results.get(0).getSourceObject().eClass().getEStructuralFeature("conname");
      String conName1 = (String)results.get(0).getSourceObject().eGet(conName);
      String conName2 = (String)results.get(1).getSourceObject().eGet(conName);

      assertEquals(true, conName1.equals("con1") || conName1.equals("con2"));
      assertEquals(true, conName2.equals("con1") || conName2.equals("con2"));
      assertEquals(true, !conName1.equals(conName2));

      oRef = res.getContents().get(2);
      assertEquals("ref2", oRef.eGet(refName));
      results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(0, results.size());

      oRef = res.getContents().get(3);
      assertEquals("ref3", oRef.eGet(refName));
      results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(0, results.size());

      oRef = res.getContents().get(4);
      assertEquals("ref4", oRef.eGet(refName));
      results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(1, results.size());
      assertEquals("con2", (String)results.get(0).getSourceObject().eGet(conName));

      oRef = res.getContents().get(5);
      assertEquals("ref5", oRef.eGet(refName));
      results = view.queryXRefs(Collections.singleton(CDOUtil.getCDOObject(oRef)));
      assertEquals(1, results.size());
      assertEquals("con1", (String)results.get(0).getSourceObject().eGet(conName));
    }
  }
}
