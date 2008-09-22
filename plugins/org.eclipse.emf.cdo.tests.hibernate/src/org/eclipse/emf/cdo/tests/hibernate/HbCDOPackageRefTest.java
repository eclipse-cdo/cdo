package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.StoreRepositoryProvider;

import reference.ReferenceFactory;
import reference.ReferencePackage;
import interface_.InterfacePackage;

public class HbCDOPackageRefTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/my/test/resource";

  public HbCDOPackageRefTest()
  {
    StoreRepositoryProvider.setInstance(HbStoreRepositoryProvider.getInstance());
  }

  public void testOnlyReference() throws Exception
  {
    try
    {
      CDOSession session = openLazySession();
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
