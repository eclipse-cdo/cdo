/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.OrderDetail;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;

import org.eclipse.emf.internal.cdo.session.CDOSessionFactory;

import org.eclipse.net4j.util.container.IPluginContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ViewProviderTest extends AbstractCDOTest
{
  private static final String REPO = RepositoryConfig.REPOSITORY_NAME;

  private static final String PATH = "/library/My.company";

  private URI uri;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    Product1 product = getModel1Factory().createProduct1();
    product.setName("ESC");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(PATH);
    resource.getContents().add(product);
    transaction.commit();
    session.close();

    uri = URI.createURI(getURIPrefix() + "/" + REPO + PATH + "?transactional=true");
  }

  public void testNormal() throws Exception
  {
    URI uri = CDOURIUtil.createResourceURI(REPO, PATH);
    IPluginContainer.INSTANCE.putElement(CDOSessionFactory.PRODUCT_GROUP, "my-type", "my-description", openSession());

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOResource resource = (CDOResource)resourceSet.getResource(uri, true);

    String path = resource.getPath();
    assertEquals(PATH, path);

    Product1 product = (Product1)resource.getContents().get(0);
    assertEquals("ESC", product.getName());
  }

  public void testConnectionAware() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    CDOResource resource = (CDOResource)resourceSet.getResource(uri, true);

    String path = resource.getPath();
    assertEquals(PATH, path);

    Product1 product = (Product1)resource.getContents().get(0);
    assertEquals("ESC", product.getName());
  }

  public void testSerialize() throws Exception
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    Map<String, Object> map = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    map.put("xmi", new XMIResourceFactoryImpl());

    CDOResource resource = (CDOResource)resourceSet.getResource(uri, true);
    Product1 product = (Product1)resource.getContents().get(0);

    OrderDetail orderDetail = getModel1Factory().createOrderDetail();
    orderDetail.setProduct(product);

    Resource volatileResource = resourceSet.createResource(URI.createURI("volatile.xmi"));
    volatileResource.getContents().add(orderDetail);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    volatileResource.save(baos, null);
    String xmi = baos.toString();
    msg("CHECK FOR: " + uri);
    msg(xmi);
    assertEquals(true, xmi.indexOf(uri.toString()) != -1);

    // resource.unload();
    // resourceSet.getResources().remove(resource);
    //
    // InternalEObject proxy = (InternalEObject)((InternalEObject)orderDetail).eGet(getModel1Package()
    // .getOrderDetail_Product(), false);
    // System.out.println(proxy.eProxyURI());
    // assertNotNull(eProxyURI);
  }
}
