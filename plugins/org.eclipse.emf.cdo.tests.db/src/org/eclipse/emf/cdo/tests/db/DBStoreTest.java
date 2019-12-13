/*
 * Copyright (c) 2009-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.PurchaseOrder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.jdbc.DelegatingConnection;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.security.IUserAware;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.Connection;
import java.util.GregorianCalendar;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

/**
 * @author Stefan Winkler
 */
public class DBStoreTest extends AbstractCDOTest
{
  public static void main(String[] args) throws Exception
  {
    Connection connection = null;

    try
    {
      final String userName = "test_repo1";
      OracleDataSource dataSource = OracleConfig.createDataSourceForUser(userName);

      class UserConnection extends DelegatingConnection.Default implements IUserAware
      {
        public UserConnection(Connection delegate)
        {
          super(delegate);
        }

        @Override
        public String getUserID()
        {
          return userName;
        }
      }

      connection = new UserConnection(dataSource.getConnection());

      List<String> names = DBUtil.getAllTableNames(connection, null);
      System.out.println(names);
    }
    finally
    {
      DBUtil.close(connection);
    }
  }

  // Bug 256462
  public void testInsertNull() throws Exception
  {
    CDOSession s = openSession();
    CDOTransaction t = s.openTransaction();
    CDOResource r = t.createResource(getResourcePath("/dbStoreTest"));

    Company c = Model1Factory.eINSTANCE.createCompany();
    c.setName(null);
    r.getContents().add(c);

    t.commit();
  }

  public void testStoreStringTrailingBackslash()
  {
    storeRetrieve("foobar\\");
  }

  public void testStoreStringContainingBackslash()
  {
    storeRetrieve("foo\\bar");
  }

  public void testStoreStringTrailingSingleQuote()
  {
    storeRetrieve("foobar'");
  }

  public void testStoreStringContainingSingleQuote()
  {
    storeRetrieve("foo'bar");
  }

  public void testStoreStringTrailingDoubleQuote()
  {
    storeRetrieve("foobar\"");
  }

  public void testStoreStringContainingDoubleQuote()
  {
    storeRetrieve("foo\"bar");
  }

  public void testStoreStringTrailingTwoSingleQuote()
  {
    storeRetrieve("foobar''");
  }

  public void testStoreStringContainingTwoSingleQuote()
  {
    storeRetrieve("foo''bar");
  }

  // Bug 217255
  public void testStoreDate() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test"));

    PurchaseOrder o = Model1Factory.eINSTANCE.createPurchaseOrder();
    o.setDate(new GregorianCalendar(2008, 11, 24, 12, 34, 56).getTime());

    resource.getContents().add(o);
    transaction.commit();

    transaction.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test"));

    assertEquals(1, resource.getContents().size());
    o = (PurchaseOrder)resource.getContents().get(0);
    assertEquals(new GregorianCalendar(2008, 11, 24, 12, 34, 56).getTime(), o.getDate());
  }

  public void testStoreCustom() throws CommitException
  {
    EPackage pkg = createUniquePackage();

    EDataType dt = EcoreFactory.eINSTANCE.createEDataType();
    dt.setName("custom");
    dt.setInstanceClass(Custom.class);
    pkg.getEClassifiers().add(dt);

    EClass clz = EMFUtil.createEClass(pkg, "customClass", false, false);
    EAttribute att = EMFUtil.createEAttribute(clz, "customAtt", dt);

    Custom cust1 = new Custom(2, 5);
    Custom cust1ref = new Custom(2, 5);
    Custom cust2 = new Custom(5, 2);
    Custom cust2ref = new Custom(5, 2);

    assertEquals(cust1ref, cust1);
    assertEquals(cust2ref, cust2);
    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    {
      EObject obj1 = EcoreUtil.create(clz);
      EObject obj2 = EcoreUtil.create(clz);

      obj1.eSet(att, cust1);
      obj2.eSet(att, cust2);

      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test"));
      resource.getContents().add(obj1);
      resource.getContents().add(obj2);
      transaction.commit();
      transaction.close();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(pkg);
      CDOView view = session.openView();
      CDOResource resource = view.getResource(getResourcePath("/test"));

      assertEquals(2, resource.getContents().size());

      EObject obj1 = resource.getContents().get(0);
      EObject obj2 = resource.getContents().get(1);

      assertEquals(cust1ref, obj1.eGet(att));
      assertEquals(cust2ref, obj2.eGet(att));

      view.close();
      session.close();
    }

  }

  public static class Custom
  {
    private int first;

    private int second;

    public Custom(String emfString)
    {
      int sep = emfString.indexOf('!');
      first = Integer.parseInt(emfString.substring(0, sep));
      second = Integer.parseInt(emfString.substring(sep + 1));
    }

    public Custom(int first, int second)
    {
      this.first = first;
      this.second = second;
    }

    public int getFirst()
    {
      return first;
    }

    public int getSecond()
    {
      return second;
    }

    @Override
    public boolean equals(Object other)
    {
      if (other instanceof Custom)
      {
        return first == ((Custom)other).first && second == ((Custom)other).second;
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return (first + 3 * second) % 65536;
    }

    // -------------------------------------------
    // - EMF String serialization
    @Override
    public String toString()
    {
      return Integer.valueOf(first) + "!" + Integer.valueOf(second);
    }

    public static Custom valueOf(String s)
    {
      return new Custom(s);
    }
  }

  private void storeRetrieve(String s)
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/test"));

    Company e = Model1Factory.eINSTANCE.createCompany();
    e.setName(s);
    // this escapes only the string!
    // resulting string only contains one backslash

    resource.getContents().add(e);

    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }

    transaction.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    session = openSession();
    CDOView view = session.openView();
    resource = view.getResource(getResourcePath("/test"));

    assertEquals(1, resource.getContents().size());
    e = (Company)resource.getContents().get(0);
    assertEquals(s, e.getName());
  }

  public void testUnderscoreFeature() throws Exception
  {
    EPackage pkg = createUniquePackage();
    EClass cls = EMFUtil.createEClass(pkg, "foo", false, false);
    EAttribute att = EMFUtil.createEAttribute(cls, "_bar", EcorePackage.eINSTANCE.getEString());

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    EObject foo = EcoreUtil.create(cls);
    foo.eSet(att, "foobar");
    resource.getContents().add(foo);
    transaction.commit();
    session.close();
  }

  public void testUnderscoreClass() throws Exception
  {
    EPackage pkg = createUniquePackage();
    EClass cls = EMFUtil.createEClass(pkg, "foo", false, false);
    EAttribute att = EMFUtil.createEAttribute(cls, "_bar", EcorePackage.eINSTANCE.getEString());

    if (!isConfig(LEGACY))
    {
      CDOUtil.prepareDynamicEPackage(pkg);
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test"));

    EObject foo = EcoreUtil.create(cls);
    foo.eSet(att, "foobar");
    resource.getContents().add(foo);
    transaction.commit();
    session.close();
  }
}
