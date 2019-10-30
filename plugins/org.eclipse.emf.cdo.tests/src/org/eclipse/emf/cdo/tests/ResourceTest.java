/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Christian W. Damus (CEA) - CDOResource isLoading() support
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.util.CDODuplicateResourceException;
import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.ModelConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Order;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model5.Child;
import org.eclipse.emf.cdo.tests.model5.Parent;
import org.eclipse.emf.cdo.tests.model5.util.IsLoadingTestFixture;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ResourceTest extends AbstractCDOTest
{
  /**
   * Test logic not up to date with the new xmi:id format.
   */
  public void _testSaveXMI() throws Exception
  {
    ByteArrayOutputStream cdoOUT = new ByteArrayOutputStream();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/folder/res1"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      transaction.commit();
      resource.save(cdoOUT, null);
    }

    ByteArrayOutputStream xmiOUT = new ByteArrayOutputStream();
    {
      XMIResource resource = new XMIResourceImpl(URI.createFileURI("/folder/res1"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      resource.save(xmiOUT, null);
    }

    String xmiString = xmiOUT.toString();
    msg("XMI:\n\n" + xmiString);

    String cdoString = cdoOUT.toString();
    msg("CDO:\n\n" + cdoString);

    assertEquals(xmiString, cdoString);
  }

  public void testSaveXMI_WithXRef() throws Exception
  {
    ByteArrayOutputStream cdoOUT = new ByteArrayOutputStream();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/folder/res1"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(147.111f);
      orderDetail.setProduct(category.getProducts().get(0));
      resource.getContents().add(orderDetail);

      transaction.commit();
      resource.save(cdoOUT, null);
    }

    ByteArrayOutputStream xmiOUT = new ByteArrayOutputStream();
    {
      XMIResource resource = new XMIResourceImpl(URI.createFileURI("/folder/res1"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(147.111f);
      orderDetail.setProduct(category.getProducts().get(0));
      resource.getContents().add(orderDetail);

      resource.save(xmiOUT, null);
    }

    String xmiString = xmiOUT.toString();
    msg("XMI:\n\n" + xmiString);

    String cdoString = cdoOUT.toString();
    msg("CDO:\n\n" + cdoString);

    // TODO assertEquals(xmiString, cdoString);
  }

  public void testSaveXMI_WithXRef_OtherResource() throws Exception
  {
    ByteArrayOutputStream cdoOUT = new ByteArrayOutputStream();
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/folder/res1"));
      CDOResource resource2 = transaction.createResource(getResourcePath("/folder/res2"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(147.111f);
      orderDetail.setProduct(category.getProducts().get(0));
      resource2.getContents().add(orderDetail);

      transaction.commit();
      resource.save(cdoOUT, null);
      resource2.save(cdoOUT, null);
    }

    ByteArrayOutputStream xmiOUT = new ByteArrayOutputStream();
    {
      XMIResource resource = new XMIResourceImpl(URI.createFileURI("/folder/res1"));
      XMIResource resource2 = new XMIResourceImpl(URI.createFileURI("/folder/res2"));

      counter = 0;
      Category category = createCategoryTree(3);
      resource.getContents().add(category);

      OrderDetail orderDetail = getModel1Factory().createOrderDetail();
      orderDetail.setPrice(147.111f);
      orderDetail.setProduct(category.getProducts().get(0));
      resource2.getContents().add(orderDetail);

      resource.save(xmiOUT, null);
      resource2.save(xmiOUT, null);
    }

    String xmiString = xmiOUT.toString();
    msg("XMI:\n\n" + xmiString);

    String cdoString = cdoOUT.toString();
    msg("CDO:\n\n" + cdoString);

    // TODO assertEquals(xmiString, cdoString);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth1_Delete() throws Exception
  {
    attachDetachResourceDepth1(1, true, 0);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth1_Remove() throws Exception
  {
    attachDetachResourceDepth1(1, false, 0);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth2_Delete() throws Exception
  {
    attachDetachResourceDepth1(2, true, 1);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth2_Remove() throws Exception
  {
    attachDetachResourceDepth1(2, false, 1);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth3_Delete() throws Exception
  {
    attachDetachResourceDepth1(3, true, 2);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth3_Remove() throws Exception
  {
    attachDetachResourceDepth1(3, false, 2);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testAttachDetachResourceDepth3_Remove_Tree() throws Exception
  {
    attachDetachResourceDepth1(3, false, 1);
  }

  public void testRootResourceFromURI() throws Exception
  {
    URI rootResourceURI = null;
    URI resourceURI = null;
    String resourcePath = getResourcePath("/test1");
    {
      CDOSession session = openSession();
      ResourceSet resourceSet = new ResourceSetImpl();
      CDOTransaction transaction = session.openTransaction(resourceSet);

      resourceURI = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + resourcePath);
      Resource res1 = resourceSet.createResource(resourceURI);

      transaction.commit();
      rootResourceURI = EcoreUtil.getURI(transaction.getRootResource());
      resourceURI = EcoreUtil.getURI((EObject)res1);
    }

    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource rootResource = (CDOResource)resourceSet.getEObject(rootResourceURI, true);
    // assertProxy(rootResource);
    assertSame(rootResource, transaction.getRootResource());

    CDOResource resource = (CDOResource)resourceSet.getEObject(resourceURI, true);
    assertClean(resource, transaction);
    assertSame(resource, transaction.getResource(resourcePath));

    transaction.close();
    session.close();
  }

  // public void testRootResourceParent() throws Exception
  // {
  // CDOSession session = openSession();
  // ResourceSet resourceSet = new ResourceSetImpl();
  // CDOTransaction transaction = session.openTransaction(resourceSet);
  // CDOResource rootResource = transaction.getRootResource();
  // assertEquals(true, CDOIDUtil.isNull(rootResource.cdoRevision().data().getResourceID()));
  // assertEquals(null, rootResource.eResource());
  // assertEquals(false, transaction.getResourceSet().getResources().contains(rootResource));
  // }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testCreateResource_FromResourceSet() throws Exception
  {
    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + "/test1");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertActive(resource);
    assertNew(resource, transaction);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(createResourceURI(session, "test1"), resource.getURI());
    assertEquals("test1", resource.getName());
    assertEquals(null, resource.getFolder());

    transaction.getRootResource().getContents().contains(resource);
    transaction.commit();

    CDOObject cdoResource = resource;
    CDOObject cdoRootResource = transaction.getRootResource();
    assertClean(cdoResource, transaction);
    assertClean(cdoRootResource, transaction);
    assertEquals(CDOID.NULL, cdoResource.cdoRevision().data().getContainerID());
    assertEquals(cdoRootResource.cdoID(), cdoResource.cdoRevision().data().getResourceID());
    assertEquals(cdoRootResource.cdoID(), cdoRootResource.cdoRevision().data().getResourceID());
    assertEquals(true, transaction.getResourceSet().getResources().contains(resource));
    assertEquals(false, transaction.getResourceSet().getResources().contains(transaction.getRootResource()));// Bug
    // 346636

    transaction.getRootResource().getContents().remove(resource);
    assertEquals(false, transaction.getResourceSet().getResources().contains(resource));
    assertEquals(false, transaction.getResourceSet().getResources().contains(transaction.getRootResource()));// Bug
    // 346636
  }

  public void testCreateNestedResource_FromResourceSet() throws Exception
  {
    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + "/folder/test1");
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertActive(resource);
    assertNew(resource, transaction);
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    assertEquals(createResourceURI(session, "folder/test1"), resource.getURI());
    assertEquals("test1", resource.getName());

    CDOResourceFolder folder = resource.getFolder();
    assertNotNull(folder);
    assertEquals("folder", folder.getName());
    assertEquals(null, folder.getFolder());
  }

  public void testCreateResource_FromTransaction() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Test if Resource is well-formed after CDOResourceFactoryImpl.
    // Adapter will be called right after and could be used!
    transaction.getResourceSet().eAdapters().add(new TestAdapter());

    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    assertActive(resource);

    CDOResource resourceCopy = transaction.getOrCreateResource(getResourcePath("/test1"));
    assertEquals(resource, resourceCopy);
    assertNew(resource, transaction);
    assertEquals(createResourceURI(session, getResourcePath("test1")), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
  }

  public void testCreateResource_WithDeepPath() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/org/eclipse/net4j/core"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/org/eclipse/net4j/core"));
    assertEquals(createResourceURI(session, getResourcePath("/org/eclipse/net4j/core")), resource.getURI());
    assertEquals(transaction.getResourceSet(), resource.getResourceSet());
    session.close();
  }

  public void testLoadAbsentResource_FromResourceSet() throws Exception
  {
    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);

    final URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + "/test1");
    CDOResource resource = (CDOResource)resourceSet.getResource(uri, false);
    assertEquals(null, resource);

    try
    {
      resourceSet.getResource(uri, true);
    }
    catch (Exception ignore)
    {
    }

    transaction.close();
  }

  public void testRemoveResourceWithCloseView() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      ResourceSet rset = transaction.getResourceSet();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      assertActive(resource);

      transaction.commit();
      assertEquals(1, rset.getResources().size()); // Bug 346636
      assertEquals(1, CDOUtil.getViewSet(rset).getViews().length);

      transaction.close();
      assertEquals(0, CDOUtil.getViewSet(rset).getViews().length);
      assertEquals(0, rset.getResources().size());
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = (CDOResource)transaction.getResourceSet().getResource(CDOURIUtil.createResourceURI(transaction, getResourcePath("/test1")), true);
      assertNotNull(resource);
      assertEquals(transaction.getResourceSet(), resource.getResourceSet());
      assertEquals(1, transaction.getResourceSet().getResources().size());
      assertEquals(CDOState.PROXY, resource.cdoState());
      assertEquals(transaction, resource.cdoView());
      assertNull(resource.cdoRevision());
    }
  }

  public void testRemoveResourceByIndex() throws Exception
  {
    final int trees = 5;
    final int depth = 5;
    int count = 0;

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      for (int i = 0; i < trees; i++)
      {
        Category tree = createCategoryTree(depth);
        if (count == 0)
        {
          count = 1 + countObjects(tree);
        }

        resource.getContents().add(tree);
      }

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setStrongReferencePolicy(CDOAdapterPolicy.ALL);

    CDOResource resource = transaction.getResource(getResourcePath("/test1"));
    EList<EObject> contents = resource.getContents();
    int expected = ((InternalCDOTransaction)transaction).getObjects().size() + count;

    contents.remove(3);
    assertEquals(expected, ((InternalCDOTransaction)transaction).getObjects().size());
  }

  private int countObjects(EObject tree)
  {
    int count = 0;
    for (TreeIterator<EObject> it = tree.eAllContents(); it.hasNext();)
    {
      it.next();
      ++count;
    }

    return count;
  }

  public void testAttachResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    transaction.createResource(getResourcePath("/my/resource1"));
    assertEquals(1, transaction.getResourceSet().getResources().size());// Bug 346636

    transaction.commit();
    session.close();
  }

  public void testCommitMultipleResources() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/level1/level2-A/level3"));
      transaction.createResource(getResourcePath("/level1/level2-B/level3"));
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource(getResourcePath("/level1/level2-A/level3"));
    CDOResource resource2 = transaction.getResource(getResourcePath("/level1/level2-B/level3"));
    assertEquals(getResourcePath("/level1/level2-A/level3"), resource1.getPath());
    assertEquals(getResourcePath("/level1/level2-B/level3"), resource2.getPath());
    session.close();
  }

  public void testLoadMultipleResources() throws CommitException
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource(getResourcePath("/level1/level2-A/level3"));
      transaction.createResource(getResourcePath("/level1/level2-B/level3"));
      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.getResource(getResourcePath("/level1/level2-A/level3"));
    CDOResource resource2 = transaction.getResource(getResourcePath("/level1/level2-B/level3"));
    assertEquals(getResourcePath("/level1/level2-A/level3"), resource1.getPath());
    assertEquals(getResourcePath("/level1/level2-B/level3"), resource2.getPath());
    session.close();
  }

  public void testSetPathConflict() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource0 = transaction.createResource(getResourcePath("/my/resource0"));
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
    transaction.commit();

    try
    {
      resource0.setPath(resource1.getPath());
      fail("CDODuplicateResourceException expected");
    }
    catch (CDODuplicateResourceException expected)
    {
      // SUCCESS
    }
  }

  public void testSetFolder() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    CDOResourceFolder folder = transaction.createResourceFolder(getResourcePath("folder"));
    transaction.commit();

    resource.setFolder(folder);
    transaction.commit();
  }

  public void testSetFolderConflict() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    CDOResourceFolder folder = transaction.createResourceFolder(getResourcePath("folder"));
    transaction.createResource(getResourcePath("folder/res"));
    transaction.commit();

    try
    {
      resource.setFolder(folder);
      fail("CDODuplicateResourceException expected");
    }
    catch (CDODuplicateResourceException expected)
    {
      // SUCCESS
    }
  }

  public void testMove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    CDOResourceFolder folder = transaction.createResourceFolder(getResourcePath("folder"));
    transaction.commit();

    folder.getNodes().add(resource);
    transaction.commit();
  }

  public void testMoveConflict() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));
    CDOResourceFolder folder = transaction.createResourceFolder(getResourcePath("folder"));
    transaction.createResource(getResourcePath("folder/res"));
    transaction.commit();

    try
    {
      folder.getNodes().add(resource);
      fail("CDODuplicateResourceException expected");
    }
    catch (CDODuplicateResourceException expected)
    {
      // SUCCESS
    }
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  @CleanRepositoriesAfter(reason = "Root resource access")
  public void testMoveToRoot() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource rootResource = transaction.getRootResource();
    CDOResourceFolder resourceFolder = transaction.createResourceFolder("folder1");
    CDOResource resource = transaction.createResource("/folder1/resource");
    transaction.commit();

    CDORevisionData data = resource.cdoRevision().data();
    assertEquals(resourceFolder.cdoID(), data.getContainerID());
    assertEquals(true, CDOIDUtil.isNull(data.getResourceID()));

    EList<EObject> contents = rootResource.getContents();
    contents.add(resource);

    data = resource.cdoRevision().data();
    assertEquals(true, CDOIDUtil.isNull((CDOID)data.getContainerID()));
    assertEquals(rootResource.cdoID(), data.getResourceID());
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  @CleanRepositoriesAfter(reason = "Root resource access")
  public void testMoveFromRoot() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource rootResource = transaction.getRootResource();
    CDOResourceFolder resourceFolder = transaction.createResourceFolder("folder1");
    CDOResource resource = transaction.createResource("resource");
    transaction.commit();

    CDORevisionData data = resource.cdoRevision().data();
    assertEquals(true, CDOIDUtil.isNull((CDOID)data.getContainerID()));
    assertEquals(rootResource.cdoID(), data.getResourceID());

    EList<CDOResourceNode> nodes = resourceFolder.getNodes();
    nodes.add(resource);

    data = resource.cdoRevision().data();
    assertEquals(resourceFolder.cdoID(), data.getContainerID());
    assertEquals(true, CDOIDUtil.isNull(data.getResourceID()));
  }

  public void testNoUneededResourcesLoadOnMove() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    // Fill folder1
    transaction.createResource(getResourcePath("folder1/res1"));
    transaction.createResource(getResourcePath("folder1/res2"));

    // Fill folder2
    transaction.createResource(getResourcePath("folder2/res3"));
    transaction.createResource(getResourcePath("folder2/res4"));

    transaction.commit();
    session.close();
    session = openSession();
    transaction = session.openTransaction();
    assertTrue(transaction.getResourceSet().getResources().isEmpty());

    CDOResourceFolder folder1 = transaction.getResourceFolder(getResourcePath("folder1"));
    CDOResourceFolder folder2 = transaction.getResourceFolder(getResourcePath("folder2"));
    assertEquals(0, transaction.getResourceSet().getResources().size());

    // The "no duplicates" validation should not trigger the load of other resources
    folder1.addResource("res5");
    assertEquals(1, transaction.getResourceSet().getResources().size());

    // The "commit" operation should not trigger the load of other resources
    transaction.commit();
    assertEquals(1, transaction.getResourceSet().getResources().size());

    CDOResource resource1 = transaction.getResource(getResourcePath("folder1/res1"));
    assertEquals(2, transaction.getResourceSet().getResources().size());

    // The "no duplicates" validation should not trigger the load of other resources
    resource1.setFolder(folder2);
    assertEquals(2, transaction.getResourceSet().getResources().size());

    transaction.commit();
    assertEquals(2, transaction.getResourceSet().getResources().size());

    // The load of sibling resources is expected with an explicit call to getNodes().
    folder1.getNodes().add(resource1);
    assertEquals(3, transaction.getResourceSet().getResources().size());
  }

  public void testDuplicatePathAfterDetach() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
    transaction.commit();

    resource.delete(null);
    transaction.commit();

    transaction.createResource(getResourcePath("/my/resource"));
    transaction.commit();
    session.close();
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth0ToDepth0() throws Exception
  {
    changePath(0, 0);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth0ToDepth1() throws Exception
  {
    changePath(0, 1);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth0ToDepth2() throws Exception
  {
    changePath(0, 2);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth0ToDepth3() throws Exception
  {
    changePath(0, 3);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth3ToDepth3() throws Exception
  {
    changePath(3, 3);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth3ToDepth2() throws Exception
  {
    changePath(3, 2);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth3ToDepth1() throws Exception
  {
    changePath(3, 1);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangePathFromDepth3ToDepth0() throws Exception
  {
    changePath(3, 0);
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangeResourceURI() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      transaction.commit();

      URI uri = URI.createURI("cdo://repo1/renamed");
      assertEquals(createResourceURI(session, "/renamed"), uri);
      resource.setURI(uri);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    assertEquals(false, transaction.hasResource("/my/resource"));
    assertEquals(true, transaction.hasResource("/renamed"));
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testChangeResourceFolderURI() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));
      transaction.commit();

      URI uri = URI.createURI("cdo://repo1/renamed");
      assertEquals(createResourceURI(session, "/renamed"), uri);
      resource.setURI(uri);

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    assertEquals(false, transaction.hasResource("/my/resource"));
    assertEquals(true, transaction.hasResource("/renamed"));
  }

  public void testPathNotNull() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      assertEquals(getResourcePath("/res1"), resource.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res1")), resource.getURI());

      transaction.commit();
      assertEquals(getResourcePath("/res1"), resource.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res1")), resource.getURI());
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      assertEquals(getResourcePath("/res1"), resource.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res1")), resource.getURI());

      CDOResource resource2 = transaction.getOrCreateResource(getResourcePath("/res2"));
      assertEquals(getResourcePath("/res2"), resource2.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res2")), resource2.getURI());

      transaction.commit();
      assertEquals(getResourcePath("/res2"), resource2.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res2")), resource2.getURI());
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOView view = session.openView();
      CDOResource resource2 = view.getResource(getResourcePath("/res2"));
      assertEquals(getResourcePath("/res2"), resource2.getPath());
      assertEquals(createResourceURI(session, getResourcePath("/res2")), resource2.getURI());
      session.close();
    }
  }

  public void testPrefetchContents() throws Exception
  {
    {
      Company company = getModel1Factory().createCompany();
      company.getCategories().add(createCategoryTree(3));

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      resource.getContents().add(company);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getResource(getResourcePath("/res1"));
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);

    Company company = (Company)resource.getContents().get(0);
    System.out.println(company);

    session.close();
  }

  private static int counter;

  private Category createCategoryTree(int depth)
  {
    if (depth == 0)
    {
      return null;
    }

    Category category = getModel1Factory().createCategory();
    for (int i = 0; i < 2; i++)
    {
      Category child = createCategoryTree(depth - 1);
      if (child != null)
      {
        category.getCategories().add(child);
      }
    }

    for (int i = 0; i < 3; i++)
    {
      Product1 child = getModel1Factory().createProduct1();
      // generate a unique id
      String id = "test " + depth + "_" + i + "_" + ++counter;
      child.setName(id);
      category.getProducts().add(child);
    }

    return category;
  }

  /**
   * bug 208689
   */
  public void testQueryResources() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/aresource");
      createResource(transaction, "/aaresource");
      createResource(transaction, "/abresource");
      createResource(transaction, "/acresource");
      createResource(transaction, "/adresource");
      createResource(transaction, "/aeresource");
      createResource(transaction, "/bresource");
      createResource(transaction, "/baresource");
      createResource(transaction, "/bbresource");
      createResource(transaction, "/bcresource");
      createResource(transaction, "/bdresource");
      createResource(transaction, "/beresource");
      createResource(transaction, "/bearesource");
      createResource(transaction, "/bebresource");
      createResource(transaction, "/cresource");
      createResource(transaction, "/caresource");
      createResource(transaction, "/caresource2");
      createResource(transaction, "/caresource3");
      createResource(transaction, "/cbresource");
      createResource(transaction, "/ccresource");
      createResource(transaction, "/cdresource");
      createResource(transaction, "/ceresource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    queryResources(view, "a", 6);
    queryResources(view, "b", 8);
    queryResources(view, "c", 8);
    queryResources(view, "be", 3);
    queryResources(view, "ca", 3);
    session.close();
  }

  /**
   * bug 208689
   */
  public void testQueryResourcesAsync() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "/aresource");
      createResource(transaction, "/aaresource");
      createResource(transaction, "/abresource");
      createResource(transaction, "/acresource");
      createResource(transaction, "/adresource");
      createResource(transaction, "/aeresource");
      createResource(transaction, "/bresource");
      createResource(transaction, "/baresource");
      createResource(transaction, "/bbresource");
      createResource(transaction, "/bcresource");
      createResource(transaction, "/bdresource");
      createResource(transaction, "/beresource");
      createResource(transaction, "/bearesource");
      createResource(transaction, "/bebresource");
      createResource(transaction, "/cresource");
      createResource(transaction, "/caresource");
      createResource(transaction, "/caresource2");
      createResource(transaction, "/caresource3");
      createResource(transaction, "/cbresource");
      createResource(transaction, "/ccresource");
      createResource(transaction, "/cdresource");
      createResource(transaction, "/ceresource");
      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    queryResourcesAsync(view, "a", 6);
    queryResourcesAsync(view, "b", 8);
    queryResourcesAsync(view, "c", 8);
    queryResourcesAsync(view, "be", 3);
    queryResourcesAsync(view, "ca", 3);
    session.close();
  }

  /**
   * bug 208689
   */
  public void testQueryModifiedResources() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "aresource");
      createResource(transaction, "aaresource");
      createResource(transaction, "abresource");
      createResource(transaction, "acresource");
      createResource(transaction, "adresource");
      createResource(transaction, "aeresource");

      createResource(transaction, "bresource");
      createResource(transaction, "baresource");
      createResource(transaction, "bbresource");
      createResource(transaction, "bcresource");
      createResource(transaction, "bdresource");
      createResource(transaction, "beresource");
      createResource(transaction, "bearesource");
      createResource(transaction, "bebresource");

      createResource(transaction, "cresource");
      createResource(transaction, "caresource");
      createResource(transaction, "caresource2");
      createResource(transaction, "caresource3");
      createResource(transaction, "cbresource");
      createResource(transaction, "ccresource");
      createResource(transaction, "cdresource");
      createResource(transaction, "ceresource");
      transaction.commit();

      renameResource(transaction, "aresource", "aresource2"); // Must still match.
      renameResource(transaction, "aaresource", "Zaresource"); // Must no longer match.

      deleteResource(transaction, "caresource2");
      deleteResource(transaction, "caresource3");

      createResource(transaction, "xxresource");
      createResource(transaction, "xyresource");
      createResource(transaction, "xzresource");

      queryResources(transaction, "a", 5); // 1 match less than 6.
      queryResources(transaction, "b", 8);
      queryResources(transaction, "be", 3);
      queryResources(transaction, "c", 6); // 2 matches less than 8.
      queryResources(transaction, "ca", 1); // 2 matches less than 3.
      queryResources(transaction, "x", 3);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    queryResources(view, "a", 5); // 1 match less than 6.
    queryResources(view, "b", 8);
    queryResources(view, "be", 3);
    queryResources(view, "c", 6); // 2 matches less than 8.
    queryResources(view, "ca", 1); // 2 matches less than 3.
    queryResources(view, "x", 3);
    session.close();
  }

  public void testQueryModifiedResourcesAsync() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      createResource(transaction, "aresource");
      createResource(transaction, "aaresource");
      createResource(transaction, "abresource");
      createResource(transaction, "acresource");
      createResource(transaction, "adresource");
      createResource(transaction, "aeresource");

      createResource(transaction, "bresource");
      createResource(transaction, "baresource");
      createResource(transaction, "bbresource");
      createResource(transaction, "bcresource");
      createResource(transaction, "bdresource");
      createResource(transaction, "beresource");
      createResource(transaction, "bearesource");
      createResource(transaction, "bebresource");

      createResource(transaction, "cresource");
      createResource(transaction, "caresource");
      createResource(transaction, "caresource2");
      createResource(transaction, "caresource3");
      createResource(transaction, "cbresource");
      createResource(transaction, "ccresource");
      createResource(transaction, "cdresource");
      createResource(transaction, "ceresource");
      transaction.commit();

      renameResource(transaction, "aresource", "aresource2"); // Must still match.
      renameResource(transaction, "aaresource", "Zaresource"); // Must no longer match.

      deleteResource(transaction, "caresource2"); // Must no longer match.
      deleteResource(transaction, "caresource3"); // Must no longer match.

      createResource(transaction, "xxresource"); // Must match now.
      createResource(transaction, "xyresource"); // Must match now.
      createResource(transaction, "xzresource"); // Must match now.

      queryResourcesAsync(transaction, "a", 5); // 1 match less than 6.
      queryResourcesAsync(transaction, "b", 8);
      queryResourcesAsync(transaction, "be", 3);
      queryResourcesAsync(transaction, "c", 6); // 2 matches less than 8.
      queryResourcesAsync(transaction, "ca", 1); // 2 matches less than 3.
      queryResourcesAsync(transaction, "x", 3);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOView view = session.openView();
    queryResourcesAsync(view, "a", 5); // 1 match less than 6.
    queryResourcesAsync(view, "b", 8);
    queryResourcesAsync(view, "be", 3);
    queryResourcesAsync(view, "c", 6); // 2 matches less than 8.
    queryResourcesAsync(view, "ca", 1); // 2 matches less than 3.
    queryResourcesAsync(view, "x", 3);
    session.close();
  }

  @CleanRepositoriesBefore(reason = "Root resource access")
  public void testDeleteResourceFromRoot() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Product1 p = getModel1Factory().createProduct1();
    p.setName("Product1");
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.createResource("/res-" + System.currentTimeMillis());
    resource.getContents().add(p);

    CDOID resourceID = resource.cdoID();

    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    CDOID objectID = object.cdoID();

    transaction.commit();
    resource.delete(null);
    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath("/resource1")));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResource() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    CDOID objectID = object.cdoID();

    transaction.commit();
    resource.delete(null);
    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath("/resource1")));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    CDOID objectID = object.cdoID();

    transaction.commit();
    resource.delete(null);
    transaction.commit();
    transaction.close();

    clearCache(getRepository().getRevisionManager());
    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath("/resource1")));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    CDOID objectID = object.cdoID();

    transaction.commit();
    assertEquals(true, view.hasResource(getResourcePath("/resource1")));
    assertEquals(resource.getURI(), view.getResource(getResourcePath("/resource1")).getURI());

    resource.delete(null);
    commitAndSync(transaction, view);
    transaction.close();

    assertEquals(false, view.hasResource(getResourcePath("/resource1")));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceDifferentSessionFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/resource1");
    CDOID resourceID = resource.cdoID();

    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    CDOID objectID = object.cdoID();

    transaction.commit();
    assertEquals(true, view.hasResource(getResourcePath("/resource1")));
    assertEquals(resource.getURI(), view.getResource(getResourcePath("/resource1")).getURI());

    resource.delete(null);
    commitAndSync(transaction, view);
    transaction.close();

    clearCache(getRepository().getRevisionManager());
    assertEquals(false, view.hasResource(getResourcePath("/resource1")));

    try
    {
      view.getResourceNode("/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      CDOResource resource1 = view.getResource(getResourcePath("/resource1"));
      assertNull(resource1);
      fail("Exception expected");
      // TODO Fails on automated build:
      // junit.framework.AssertionFailedError: Exception expected
      // at org.eclipse.emf.cdo.tests.ResourceTest.testDeleteResourceDifferentSessionFresh(ResourceTest.java:859)
      // at org.eclipse.net4j.tests.AbstractOMTest.runBare(AbstractOMTest.java:86)
      // at org.eclipse.net4j.tests.AbstractOMTest.run(AbstractOMTest.java:108)
      // at org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite$TestWrapper.runTest(ConfigTestSuite.java:126)
      // at org.eclipse.test.EclipseTestRunner.run(EclipseTestRunner.java:332)
      // at org.eclipse.test.EclipseTestRunner.run(EclipseTestRunner.java:202)
      // at org.eclipse.test.CoreTestApplication.runTests(CoreTestApplication.java:35)
      // at org.eclipse.test.CoreTestApplication.run(CoreTestApplication.java:31)
      // at org.eclipse.equinox.internal.app.EclipseAppContainer.callMethodWithException(EclipseAppContainer.java:574)
      // at org.eclipse.equinox.internal.app.EclipseAppHandle.run(EclipseAppHandle.java:196)
      // at org.eclipse.equinox.internal.app.MainApplicationLauncher.run(MainApplicationLauncher.java:32)
      // at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.runApplication(EclipseAppLauncher.java:110)
      // at org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher.start(EclipseAppLauncher.java:79)
      // at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:368)
      // at org.eclipse.core.runtime.adaptor.EclipseStarter.run(EclipseStarter.java:179)
      // at org.eclipse.equinox.launcher.Main.invokeFramework(Main.java:559)
      // at org.eclipse.equinox.launcher.Main.basicRun(Main.java:514)
      // at org.eclipse.equinox.launcher.Main.run(Main.java:1311)
      // at org.eclipse.equinox.launcher.Main.main(Main.java:1287)
      // at org.eclipse.core.launcher.Main.main(Main.java:34)
    }
    catch (Exception expected)
    {
      expected.printStackTrace();
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolder() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();

    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath("/folder/resource1")));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/folder/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      CDOObject result = view.getObject(folderID);
      fail("ObjectNotFoundException expected");
      assertEquals(null, result);
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));
    transaction.commit();

    CDOResourceFolder folder = resource.getFolder();
    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    transaction.commit();
    transaction.close();

    clearCache(getRepository().getRevisionManager());
    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath("/folder/resource1")));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/folder/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getObject(folderID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderDifferentSession() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOResourceFolder folder = resource.getFolder();
    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));

    transaction.commit();
    assertEquals(true, view.hasResource(getResourcePath("/folder/resource1")));
    assertEquals(resource.getURI(), view.getResource(getResourcePath("/folder/resource1")).getURI());

    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    commitAndSync(transaction, view);
    transaction.close();

    assertEquals(false, view.hasResource(getResourcePath("/folder/resource1")));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/folder/resource1"));
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      CDOObject result = view.getObject(folderID);
      fail("ObjectNotFoundException expected");
      assertEquals(null, result);
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  public void testDeleteResourceFolderDifferentSessionFresh() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOSession session2 = openSession();
    CDOView view = session2.openView();

    CDOResource resource = createResource(transaction, "/folder/resource1");
    CDOResourceFolder folder = resource.getFolder();
    CDOObject object = CDOUtil.getCDOObject(resource.getContents().get(0));

    transaction.commit();
    assertEquals(true, view.hasResource(getResourcePath("/folder/resource1")));
    assertEquals(resource.getURI(), view.getResource(getResourcePath("/folder/resource1")).getURI());

    CDOID folderID = folder.cdoID();
    CDOID resourceID = resource.cdoID();
    CDOID objectID = object.cdoID();

    folder.delete(null);
    commitAndSync(transaction, view);
    transaction.close();

    clearCache(getRepository().getRevisionManager());
    assertEquals(false, view.hasResource(getResourcePath("/folder/resource1")));

    try
    {
      view.getResourceNode("/folder");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResourceNode("/folder/resource1");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      view.getResource(getResourcePath("/folder/resource1"));
      // TODO Fails on automated build
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    try
    {
      CDOObject result = view.getObject(folderID);
      fail("ObjectNotFoundException expected");
      assertEquals(null, result);
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(resourceID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    try
    {
      view.getObject(objectID);
      fail("ObjectNotFoundException expected");
    }
    catch (ObjectNotFoundException expected)
    {
    }

    session.close();
  }

  /**
   * Create resource with the following pattern /test1/test2/test3 for a depth 3. <br>
   * After it will remove the resource with the following rule:<br>
   * if calldelete is true <code>resource.delete(null)</code> <br>
   * if calldelete is false it will use the depthtoRemove to call <code>object.remove(resource)</code><br>
   * deptToRemove = /0/1/2/...<br>
   * It will remove it from parent folder (depthtoRemove - 1);
   */
  private void attachDetachResourceDepth1(int depth, boolean callDelete, int depthtoRemove) throws Exception
  {
    CDOSession session = openSession();
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOTransaction transaction = session.openTransaction(resourceSet);
    CDOResource rootResource = transaction.getRootResource();
    assertSame(rootResource, rootResource.eResource());
    String path = "";
    List<String> names = new ArrayList<String>();
    for (int i = 0; i < depth; i++)
    {
      String name = "test" + String.valueOf(i + 1);
      names.add(name);
      path += "/" + name;
    }

    final URI uri = URI.createURI("cdo://" + session.getRepositoryInfo().getUUID() + path);
    CDOResource resource = (CDOResource)resourceSet.createResource(uri);
    assertEquals(names.get(names.size() - 1), resource.getName());

    transaction.commit();
    List<CDOResourceNode> nodesList = new ArrayList<CDOResourceNode>();
    CDOResource resourceByLookup = null;
    CDOResourceNode next = null;
    for (int i = 0; i < depth; i++)
    {
      if (i == 0)
      {
        next = (CDOResourceNode)rootResource.getContents().get(0);
      }
      else
      {
        next = ((CDOResourceFolder)next).getNodes().get(0);
      }

      nodesList.add(next);
    }

    resourceByLookup = (CDOResource)next;
    assertSame(resource, resourceByLookup);
    assertClean(resourceByLookup, transaction);
    assertEquals(true, resourceSet.getResources().contains(resourceByLookup));

    CDOObject cdoParent = null;
    CDOObject cdoRootResource = CDOUtil.getCDOObject(rootResource);
    for (int i = 0; i < depth; i++)
    {
      CDOResourceNode resourceNode = nodesList.get(i);
      CDOObject cdoResourceNode = CDOUtil.getCDOObject(resourceNode);

      if (i == 0)
      {
        assertEquals(cdoRootResource.cdoID(), cdoResourceNode.cdoRevision().data().getResourceID());
        assertEquals(CDOID.NULL, cdoResourceNode.cdoRevision().data().getContainerID());
      }
      else
      {
        assertEquals(CDOID.NULL, cdoResourceNode.cdoRevision().data().getResourceID());
        assertEquals(cdoParent.cdoID(), cdoResourceNode.cdoRevision().data().getContainerID());
      }

      cdoParent = cdoResourceNode;
    }

    if (callDelete)
    {
      resource.delete(null);
      depthtoRemove = depth;
    }
    else
    {
      CDOResourceNode node = nodesList.get(depthtoRemove);
      if (depthtoRemove == 0)
      {
        rootResource.getContents().remove(node);
      }
      else
      {
        CDOResourceFolder parentFolder = (CDOResourceFolder)nodesList.get(depthtoRemove - 1);
        assertEquals(parentFolder, node.getFolder());
        parentFolder.getNodes().remove(node);
      }
    }

    for (int i = depthtoRemove; i < depth; i++)
    {
      CDOResourceNode transientNode = nodesList.get(i);
      assertTransient(transientNode);
      if (transientNode instanceof CDOResource)
      {
        assertEquals(false, resourceSet.getResources().contains(transientNode));
      }

      assertEquals(null, transientNode.eResource());
      if (i == depthtoRemove)
      {
        assertEquals(null, transientNode.eContainer());
      }
      else
      {
        assertEquals(cdoParent, transientNode.eContainer());
      }

      cdoParent = transientNode;
    }

    transaction.commit();
  }

  private void changePath(int depthFrom, int depthTo) throws Exception
  {
    String prefixA = "testA";
    String prefixB = "testB";

    String oldPath = createPath(prefixA, depthFrom, "test");
    String newPath = createPath(prefixB, depthTo, "test2");
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(oldPath);
      Order order = getModel1Factory().createPurchaseOrder();
      resource.getContents().add(order);

      String path = CDOURIUtil.extractResourcePath(resource.getURI());
      assertEquals(oldPath, path);
      assertEquals(depthFrom, CDOURIUtil.analyzePath(resource.getURI()).size() - 1);

      transaction.commit();

      CDOID idBeforeChangePath = CDOUtil.getCDOObject(resource).cdoID();
      CDOID idBeforeChangePathOrder = CDOUtil.getCDOObject(order).cdoID();

      msg("New path");
      resource.setPath(newPath);
      path = CDOURIUtil.extractResourcePath(resource.getURI());
      assertEquals(depthTo, CDOURIUtil.analyzePath(resource.getURI()).size() - 1);
      assertEquals(newPath, path);
      transaction.commit();

      CDOID idAfterChangePath = CDOUtil.getCDOObject(resource).cdoID();
      assertEquals(idBeforeChangePath, idAfterChangePath);

      CDOID idAfterChangePathOrder = CDOUtil.getCDOObject(order).cdoID();
      assertEquals(idBeforeChangePathOrder, idAfterChangePathOrder);

      Resource resourceRenamed = transaction.getResourceSet().getResource(createResourceURI(session, newPath), false);

      assertEquals(resource, resourceRenamed);
      assertClean(resource, transaction);
      assertClean(order, transaction);
      session.close();
    }

    clearCache(getRepository().getRevisionManager());
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    try
    {
      URI uri = createResourceURI(session, oldPath);
      transaction.getResourceSet().getResource(uri, true);
      fail("Doesn't exist");
    }
    catch (Exception ex)
    {
    }

    Resource resource = transaction.getResourceSet().getResource(createResourceURI(session, newPath), true);
    assertNotNull(resource);
  }

  @SuppressWarnings("deprecation")
  private URI createResourceURI(CDOSession session, String path)
  {
    return CDOURIUtil.createResourceURI(session, path);
  }

  private String createPath(String namePrefix, int depth, String name)
  {
    String path = "";
    for (int i = 0; i < depth; i++)
    {
      String localName = namePrefix + String.valueOf(i + 1);
      path += "/" + localName;
    }

    path += "/" + name;
    return path;
  }

  private CDOResource createResource(CDOTransaction transaction, String path)
  {
    Product1 p = getModel1Factory().createProduct1();
    p.setName("test-" + path);
    p.setVat(VAT.VAT0);

    CDOResource resource = transaction.createResource(getResourcePath(path));
    resource.getContents().add(p);
    return resource;
  }

  private void renameResource(CDOTransaction transaction, String path, String newName)
  {
    CDOResource resource = transaction.getResource(getResourcePath(path));
    resource.setName(newName);
  }

  private void deleteResource(CDOTransaction transaction, String path)
  {
    CDOResource resource = transaction.getResource(getResourcePath(path));
    EcoreUtil.remove(resource);
  }

  private void queryResources(CDOView view, String namePrefix, int expected)
  {
    msg("Name prefix: " + namePrefix);
    CDOResourceFolder folder = (CDOResourceFolder)view.getResourceNode(getResourcePath(null));
    List<CDOResourceNode> nodes = view.queryResources(folder, namePrefix, false);
    for (CDOResourceNode node : nodes)
    {
      msg("Result: " + node.getPath());
    }

    assertEquals(expected, nodes.size());
  }

  private void queryResourcesAsync(CDOView view, String namePrefix, int expected)
  {
    msg("Name prefix: " + namePrefix);
    CDOResourceFolder folder = (CDOResourceFolder)view.getResourceNode(getResourcePath(null));
    CloseableIterator<CDOResourceNode> nodes = view.queryResourcesAsync(folder, namePrefix, false);

    int count = 0;
    for (CloseableIterator<CDOResourceNode> it = nodes; it.hasNext();)
    {
      CDOResourceNode node = it.next();
      msg("Result: " + node.getPath());
      ++count;
    }

    assertEquals(expected, count);
    nodes.close();
  }

  /**
   * See bug 353249.
   */
  public void testGetResourceNodeContract()
  {
    CDOView view = openSession().openView();

    try
    {
      view.getResourceNode("SomePath/That/DoesntExist");
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCCESS
    }
  }

  public void testTextResource() throws Exception
  {
    final char[] document = "This can be a looooong document".toCharArray();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOTextResource resource = transaction.createTextResource(getResourcePath("/my/resource1"));
    resource.setContents(new CDOClob(new CharArrayReader(document)));
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getTextResource(getResourcePath("/my/resource1"));

    CDOClob clob = resource.getContents();
    Reader reader = null;

    try
    {
      reader = clob.getContents();
      CharArrayWriter writer = new CharArrayWriter();
      IOUtil.copyCharacter(reader, writer);
      assertEquals(true, Arrays.equals(document, writer.toCharArray()));
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  public void testBinaryResource() throws Exception
  {
    final byte[] document = "This can be a looooong document".getBytes();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOBinaryResource resource = transaction.createBinaryResource(getResourcePath("/my/resource1"));
    resource.setContents(new CDOBlob(new ByteArrayInputStream(document)));
    transaction.commit();
    session.close();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.getBinaryResource(getResourcePath("/my/resource1"));

    CDOBlob blob = resource.getContents();
    InputStream inputStream = null;

    try
    {
      inputStream = blob.getContents();
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      IOUtil.copyBinary(inputStream, outputStream);
      assertEquals(true, Arrays.equals(document, outputStream.toByteArray()));
    }
    finally
    {
      IOUtil.close(inputStream);
    }

    session.close();
  }

  /**
   * Bug 393164: Test the support for {@code XMLResource}-like
   * {@link org.eclipse.emf.ecore.resource.Resource.Internal#isLoading()}
   * behavior in {@link CDOResource}s for legacy models.
   */
  @Requires(ModelConfig.CAPABILITY_LEGACY)
  public void testResourceIsLoading_legacy() throws Exception
  {
    final IsLoadingTestFixture fixture = IsLoadingTestFixture.newInstance();

    try
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource = transaction.createResource(getResourcePath("/my/resource1"));

      Parent parent = getModel5Factory().createParent();
      resource.getContents().add(parent);

      parent.setName("parent");
      fixture.assertNotReportedLoading(resource, parent);

      Child child1 = getModel5Factory().createChild();
      parent.getChildren().add(child1);
      child1.setName("child1");
      fixture.assertNotReportedLoading(resource, child1);

      Child child2 = getModel5Factory().createChild();
      parent.getChildren().add(child2);
      child2.setName("child2");
      fixture.assertNotReportedLoading(resource, child2);

      transaction.commit();
      session.close();

      session = openSession();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/my/resource1"));

      for (Iterator<EObject> iter = resource.getAllContents(); iter.hasNext();)
      {
        // every object in the resource detected that it was being loaded
        fixture.assertReportedLoading(resource, iter.next());
      }

      session.close();
    }
    finally
    {
      fixture.dispose();
    }
  }

  /**
   * Bug 397629: Test the {@link org.eclipse.emf.ecore.resource.Resource.Internal#isLoading()}
   * behavior in {@link CDOResource}s for legacy models when resolving cross-document references
   * (bottom-up resource loading).
   */
  @Requires(ModelConfig.CAPABILITY_LEGACY)
  public void testResourceIsLoading_crossResourceRef() throws Exception
  {
    final IsLoadingTestFixture fixture = IsLoadingTestFixture.newInstance();

    try
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));
      CDOResource resource2 = transaction.createResource(getResourcePath("/my/resource2"));

      Parent parent = getModel5Factory().createParent();
      resource1.getContents().add(parent);
      parent.setName("parent");

      Parent container = getModel5Factory().createParent();
      resource2.getContents().add(container);
      container.setName("Fake parent container");

      Child child = getModel5Factory().createChild();
      container.getChildren().add(child);
      child.setName("child");

      // cross-resource reference
      parent.setFavourite(child);

      fixture.assertNotReportedLoading(resource1, parent);
      fixture.assertNotReportedLoading(resource2, child);

      transaction.commit();
      session.close();

      session = openSession();
      transaction = session.openTransaction();
      resource1 = transaction.getResource(getResourcePath("/my/resource1"));
      resource2 = transaction.getResource(getResourcePath("/my/resource2"));

      // resolve all cross-references out of resource1
      EcoreUtil.resolveAll((Resource)resource1);
      for (Iterator<EObject> iter = resource2.getAllContents(); iter.hasNext();)
      {
        // every object in the resource detected that it was being loaded
        fixture.assertReportedLoading(resource2, iter.next());
      }

      session.close();
    }
    finally
    {
      fixture.dispose();
    }
  }

  public void testNameChecks()
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("/my/resource1"));

    assertEquals("resource1", resource1.getName());
    assertEquals("resource1", resource1.getBasename());
    assertEquals("", resource1.getExtension());

    try
    {
      resource1.setName("/resource1");
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }

    resource1.setExtension("tar.gz");
    assertEquals("resource1.tar.gz", resource1.getName());
    assertEquals("resource1", resource1.getBasename());
    assertEquals("tar.gz", resource1.getExtension());

    resource1.setBasename("model");
    assertEquals("model.tar.gz", resource1.getName());
    assertEquals("model", resource1.getBasename());
    assertEquals("tar.gz", resource1.getExtension());

    try
    {
      resource1.setBasename("model.xyz");
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }

    resource1.setExtension("");
    assertEquals("model", resource1.getName());
    assertEquals("model", resource1.getBasename());
    assertEquals("", resource1.getExtension());

    resource1.setExtension("ecore");
    resource1.setBasename("");
    assertEquals(".ecore", resource1.getName());
    assertEquals("", resource1.getBasename());
    assertEquals("ecore", resource1.getExtension());

    try
    {
      resource1.setName(null);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }

    try
    {
      resource1.setName("");
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }

    try
    {
      resource1.setName(".");
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
      // SUCCESS
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class TestAdapter extends AdapterImpl
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      super.notifyChanged(msg);
      if (msg.getNewValue() instanceof CDOResource)
      {
        ((CDOResource)msg.getNewValue()).getPath();
      }
    }

    @Override
    public void setTarget(Notifier newTarget)
    {
    }

    @Override
    public boolean isAdapterForType(Object type)
    {
      return super.isAdapterForType(type);
    }
  }
}
