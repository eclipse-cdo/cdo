package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;
import org.eclipse.emf.cdo.util.CDOUtil;

import base.BaseFactory;
import base.BasePackage;
import derived.DerivedPackage;
import reference.ReferenceFactory;
import reference.ReferencePackage;
import interface_.InterfacePackage;

public class HbCDOAutomaticPackageRefTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/my/test/resource";

  public HbCDOAutomaticPackageRefTest()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());
  }

  @Override
  protected CDOSession openSession()
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    configuration.setLazyPackageRegistry();
    return configuration.openSession();
  }

  public void testPutPackage() throws Exception
  {
    try
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      CDOTransaction transaction = session.openTransaction();
      session.getPackageRegistry().putEPackage(BasePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(DerivedPackage.eINSTANCE);
      transaction.commit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }

  public void testPutPackageOneByOne() throws Exception
  {
    try
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      CDOTransaction transaction = session.openTransaction();
      session.getPackageRegistry().putEPackage(BasePackage.eINSTANCE);
      transaction.commit();
      session.getPackageRegistry().putEPackage(DerivedPackage.eINSTANCE);
      transaction.commit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }

  public void testOnlyBaseData() throws Exception
  {
    try
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(RESOURCE_PATH);
      resource.getContents().add(BaseFactory.eINSTANCE.createBaseClass());
      transaction.commit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }

  public void testOnlyReference() throws Exception
  {
    try
    {
      CDOSession session = openSession();
      session.getPackageRegistry().putEPackage(InterfacePackage.eINSTANCE);
      session.getPackageRegistry().putEPackage(ReferencePackage.eINSTANCE);

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(RESOURCE_PATH);
      resource.getContents().add(ReferenceFactory.eINSTANCE.createReference());
      transaction.commit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
}
