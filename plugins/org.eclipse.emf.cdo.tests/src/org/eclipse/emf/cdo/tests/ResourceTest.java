package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.MEMStore;
import org.eclipse.emf.cdo.internal.server.NOOPStore;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

public class ResourceTest extends AbstractCDOTest
{
  /**
   * http://bugs.eclipse.org/238963
   * 
   * @throws Exception
   */
  public void testEmptyContents() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/my/resource");

      Product p = Model1Factory.eINSTANCE.createProduct();
      p.setName("test");
      p.setVat(VAT.VAT0);

      resource.getContents().add(p);
      transaction.commit();

      assertEquals(1, resource.getContents().size());
      session.close();
    }

    IStore store = getRepository().getStore();
    if (!(store instanceof MEMStore || store instanceof NOOPStore))
    {
      restartContainer();
    }

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/my/resource");
    EList<EObject> contents = resource.getContents();
    int size = contents.size();
    assertEquals(1, size);
    session.close();
  }
}
