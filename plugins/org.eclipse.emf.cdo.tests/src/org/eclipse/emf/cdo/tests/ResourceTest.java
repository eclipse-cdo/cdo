package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.MEMStore;
import org.eclipse.emf.cdo.internal.server.NOOPStore;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Product;
import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

public class ResourceTest extends AbstractCDOTest
{
  /**
   * http://bugs.eclipse.org/238963
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

  /**
   * http://bugs.eclipse.org/208689
   */
  public void testQueryResources() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/a/resource");
      createResource(transaction, "/a/a/resource");
      createResource(transaction, "/a/b/resource");
      createResource(transaction, "/a/c/resource");
      createResource(transaction, "/a/d/resource");
      createResource(transaction, "/a/e/resource");
      createResource(transaction, "/b/resource");
      createResource(transaction, "/b/a/resource");
      createResource(transaction, "/b/b/resource");
      createResource(transaction, "/b/c/resource");
      createResource(transaction, "/b/d/resource");
      createResource(transaction, "/b/e/resource");
      createResource(transaction, "/b/e/a/resource");
      createResource(transaction, "/b/e/b/resource");
      createResource(transaction, "/c/resource");
      createResource(transaction, "/c/a/resource");
      createResource(transaction, "/c/a/resource2");
      createResource(transaction, "/c/a/resource3");
      createResource(transaction, "/c/b/resource");
      createResource(transaction, "/c/c/resource");
      createResource(transaction, "/c/d/resource");
      createResource(transaction, "/c/e/resource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    queryResources(view, "/a", 6);
    queryResources(view, "/b", 8);
    queryResources(view, "/c", 8);
    queryResources(view, "/b/e", 3);
    queryResources(view, "/c/a", 3);
    session.close();
  }

  /**
   * http://bugs.eclipse.org/208689
   */
  public void testQueryModifiedResources() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/a/resource");
      createResource(transaction, "/a/a/resource");
      createResource(transaction, "/a/b/resource");
      createResource(transaction, "/a/c/resource");
      createResource(transaction, "/a/d/resource");
      createResource(transaction, "/a/e/resource");
      createResource(transaction, "/b/resource");
      createResource(transaction, "/b/a/resource");
      createResource(transaction, "/b/b/resource");
      createResource(transaction, "/b/c/resource");
      createResource(transaction, "/b/d/resource");
      createResource(transaction, "/b/e/resource");
      createResource(transaction, "/b/e/a/resource");
      createResource(transaction, "/b/e/b/resource");
      createResource(transaction, "/c/resource");
      createResource(transaction, "/c/a/resource");
      createResource(transaction, "/c/a/resource2");
      createResource(transaction, "/c/a/resource3");
      createResource(transaction, "/c/b/resource");
      createResource(transaction, "/c/c/resource");
      createResource(transaction, "/c/d/resource");
      createResource(transaction, "/c/e/resource");
      transaction.commit();
      modifyResource(transaction, "/a/resource");
      modifyResource(transaction, "/a/a/resource");
      modifyResource(transaction, "/a/b/resource");
      modifyResource(transaction, "/a/c/resource");
      modifyResource(transaction, "/a/d/resource");
      modifyResource(transaction, "/a/e/resource");
      modifyResource(transaction, "/b/resource");
      modifyResource(transaction, "/b/a/resource");
      modifyResource(transaction, "/b/b/resource");
      modifyResource(transaction, "/b/c/resource");
      modifyResource(transaction, "/b/d/resource");
      modifyResource(transaction, "/b/e/resource");
      modifyResource(transaction, "/b/e/a/resource");
      modifyResource(transaction, "/b/e/b/resource");
      modifyResource(transaction, "/c/resource");
      modifyResource(transaction, "/c/a/resource");
      modifyResource(transaction, "/c/a/resource2");
      modifyResource(transaction, "/c/a/resource3");
      modifyResource(transaction, "/c/b/resource");
      modifyResource(transaction, "/c/c/resource");
      modifyResource(transaction, "/c/d/resource");
      modifyResource(transaction, "/c/e/resource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    queryResources(view, "/a", 6);
    queryResources(view, "/b", 8);
    queryResources(view, "/c", 8);
    queryResources(view, "/b/e", 3);
    queryResources(view, "/c/a", 3);
    session.close();
  }

  private CDOResource createResource(CDOTransaction transaction, String path)
  {
    Product p = Model1Factory.eINSTANCE.createProduct();
    p.setName("test-" + path);
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.createResource(path);
    resource.getContents().add(p);
    return resource;
  }

  private CDOResource modifyResource(CDOTransaction transaction, String path)
  {
    Product p = Model1Factory.eINSTANCE.createProduct();
    p.setName("test-" + path + "-modified");
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.getResource(path);
    resource.getContents().add(p);
    return resource;
  }

  private void queryResources(CDOView view, String pathPrefix, int expected)
  {
    msg("Path prefix: " + pathPrefix);
    List<CDOResource> resources = view.queryResources(pathPrefix);
    for (CDOResource resource : resources)
    {
      msg("Result: " + resource.getPath());
    }

    assertEquals(expected, resources.size());
  }
}
