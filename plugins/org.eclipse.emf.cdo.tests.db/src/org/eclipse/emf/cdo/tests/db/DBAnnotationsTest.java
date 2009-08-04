/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import junit.framework.Assert;

/**
 * Test different DB annotations.
 * 
 * @author Kai Schlamp
 */
public class DBAnnotationsTest extends AbstractCDOTest
{
  public void testLengthAnnotationPositive() throws Exception
  {
    msg("Opening session");
    EPackage model1 = (EPackage)EcoreUtil.copy(getModel1Package());
    addLengthAnnotation(model1, "8");
    CDOSession session = openSession(model1);

    disableConsole();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product that has a name with an allowed string length.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    product.eSet(eClass.getEStructuralFeature("name"), "01234567");

    resource.getContents().add(product);
    transaction.commit();
  }

  public void testLengthAnnotationNegative() throws Exception
  {
    // HSQL does not support length annotations
    skipConfig(AllTestsDBHsqldb.Hsqldb.INSTANCE);
    skipConfig(AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE);

    msg("Opening session");
    EPackage model1 = (EPackage)EcoreUtil.copy(getModel1Package());
    addLengthAnnotation(model1, "8");
    CDOSession session = openSession(model1);

    disableConsole();
    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product that has a name with an invalid string length.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    product.eSet(eClass.getEStructuralFeature("name"), "012345678");

    resource.getContents().add(product);

    try
    {
      transaction.commit();
      fail("Committing too long data did not result in an exception");
    }
    catch (Exception success)
    {
    }
  }

  public void testLengthAnnotationByMetaData()
  {
    msg("Opening session");
    EPackage model1 = (EPackage)EcoreUtil.copy(getModel1Package());
    addLengthAnnotation(model1, "8");
    CDOSession session = openSession(model1);

    disableConsole();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a product.");
    EClass eClass = (EClass)model1.getEClassifier("Product1");
    EObject product = model1.getEFactoryInstance().create(eClass);
    resource.getContents().add(product);

    transaction.commit();

    msg("Check if column size was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        DatabaseMetaData metaData = getStatement().getConnection().getMetaData();
        ResultSet rset = metaData.getColumns(null, null, "PRODUCT1", "NAME");
        rset.next();
        Assert.assertEquals("8", rset.getString(7));
      }
    }.verify();
  }

  public void testTypeAnnotationByMetaData()
  {
    // HSQL does not support length annotations
    skipConfig(AllTestsDBHsqldb.Hsqldb.INSTANCE);
    skipConfig(AllTestsDBHsqldbNonAudit.HsqldbNonAudit.INSTANCE);

    msg("Opening session");
    EPackage model1 = (EPackage)EcoreUtil.copy(getModel1Package());
    addTypeAnnotation(model1, "CLOB");
    CDOSession session = openSession(model1);

    disableConsole();

    msg("Opening transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Creating resource");
    CDOResource resource = transaction.createResource("/test1");

    msg("Commit a category.");
    EClass eClass = (EClass)model1.getEClassifier("Category");
    EObject category = model1.getEFactoryInstance().create(eClass);

    resource.getContents().add(category);
    transaction.commit();

    msg("Check if column type was correctly set.");
    new DBStoreVerifier(getRepository())
    {
      @Override
      protected void doVerify() throws Exception
      {
        DatabaseMetaData metaData = getStatement().getConnection().getMetaData();
        ResultSet rset = metaData.getColumns(null, null, "CATEGORY", "NAME");
        rset.next();
        Assert.assertEquals("CLOB", rset.getString(6));
      }
    }.verify();
  }

  private void addLengthAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnLength", value);

    EClass product1 = (EClass)model1.getEClassifier("Product1");
    EStructuralFeature element = product1.getEStructuralFeature(Model1Package.PRODUCT1__NAME);
    element.getEAnnotations().add(annotation);
  }

  private void addTypeAnnotation(EPackage model1, String value)
  {
    EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
    annotation.setSource("http://www.eclipse.org/CDO/DBStore");
    annotation.getDetails().put("columnType", value);

    EClass category = (EClass)model1.getEClassifier("Category");
    EStructuralFeature element = category.getEStructuralFeature(Model1Package.CATEGORY__NAME);
    element.getEAnnotations().add(annotation);
  }
}
