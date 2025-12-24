/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_351921_Test extends AbstractCDOTest
{
  private EPackage pkg;

  private EClass cls;

  private EAttribute att;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    pkg = createUniquePackage();

    EDataType custom = EcoreFactory.eINSTANCE.createEDataType();
    custom.setInstanceClass(CustomType.class);
    custom.setName("CustomType");

    pkg.getEClassifiers().add(custom);

    cls = EMFUtil.createEClass(pkg, "Foobar", false, false);
    att = EMFUtil.createEAttribute(cls, "att", custom);
    att.setDefaultValue(null);

    CDOUtil.prepareDynamicEPackage(pkg);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    pkg = null;
    cls = null;
    att = null;
    super.doTearDown();
  }

  protected EAttribute getAtt()
  {
    return att;
  }

  public void testDefaultValueNull() throws CommitException
  {
    EObject obj = EcoreUtil.create(cls);

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOTransaction tx = session.openTransaction();
      CDOResource res = tx.createResource(getResourcePath("/test"));
      res.getContents().add(obj);
      tx.commit();
      tx.close();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOView v = session.openView();
      CDOResource res = v.getResource(getResourcePath("/test"));
      EObject persistent = res.getContents().get(0);

      CustomType pCustom = (CustomType)persistent.eGet(att);
      assertNull(pCustom);

      v.close();
      session.close();
    }
  }

  public static class CustomType
  {
    private int a = 0;

    private int b = 0;

    public CustomType(int _a, int _b)
    {
      a = _a;
      b = _b;
    }

    public CustomType(String literal)
    {
      String[] values = literal.split(";");
      if (values.length != 2)
      {
        throw new RuntimeException("Error: only 2 values allowed");
      }
      else
      {
        a = Integer.valueOf(values[0]);
        b = Integer.valueOf(values[1]);
      }
    }

    @Override
    public String toString()
    {
      return a + ";" + b;
    }

    public int getA()
    {
      return a;
    }

    public int getB()
    {
      return b;
    }
  }
}
