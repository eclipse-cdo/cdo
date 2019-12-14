/*
 * Copyright (c) 2008-2012, 2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiContained;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplMultiRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefContainerNPL;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainer;
import org.eclipse.emf.cdo.tests.model4.ImplSingleRefNonContainerNPL;
import org.eclipse.emf.cdo.tests.model4.MultiContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleContainedNPL;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4interfaces.IContainedElementNoParentLink;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefNonContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.INamedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefContainedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.ISingleRefNonContainedElement;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ComplexTest extends AbstractCDOTest
{
  private static long uniqueCounter = System.currentTimeMillis();

  private model4Factory factory;

  private CDOResource resource1;

  private CDOResource resource2;

  private CDOTransaction transaction;

  private CDOSession session;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    String path1 = getResourcePath("/resources/folder1/res" + uniqueCounter);
    String path2 = getResourcePath("/resources/folder2/res" + uniqueCounter);
    ++uniqueCounter;

    init(path1, path2);
    commit();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    factory = null;
    session = null;
    transaction = null;
    resource1 = null;
    resource2 = null;
    super.doTearDown();
  }

  private void purgeCaches()
  {
    // according to Eike's comment at Bug 249681, client caches are
    // ignored, if a new session is opened.
    // server caches are wiped by the clearCache call.
    String path1 = resource1.getPath();
    String path2 = resource2.getPath();

    transaction.close();
    session.close();

    clearCache(getRepository().getRevisionManager());
    init(path1, path2);
  }

  private void init(String path1, String path2)
  {
    factory = getModel4Factory();

    session = openSession();
    session.getPackageRegistry().putEPackage(getModel4InterfacesPackage());
    session.getPackageRegistry().putEPackage(getModel4Package());

    transaction = session.openTransaction();

    resource1 = transaction.getOrCreateResource(path1);
    resource2 = transaction.getOrCreateResource(path2);
  }

  private void commit()
  {
    try
    {
      transaction.commit();
    }
    catch (CommitException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public void testPlainSingleNonContainedBidirectional()
  {
    RefSingleNonContained container = factory.createRefSingleNonContained();
    SingleNonContainedElement element0 = factory.createSingleNonContainedElement();
    element0.setName("PlainSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (RefSingleNonContained)resource1.getContents().get(0);
    element0 = (SingleNonContainedElement)resource1.getContents().get(1);

    assertEquals(element0, container.getElement());
    assertEquals(container, element0.getParent());
    assertEquals("PlainSingleNonContainedBidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
  }

  public void testPlainSingleContainedBidirectional()
  {
    RefSingleContained container = factory.createRefSingleContained();
    SingleContainedElement element0 = factory.createSingleContainedElement();
    element0.setName("PlainSingleContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (RefSingleContained)CDOUtil.getEObject(resource1.getContents().get(0));
    element0 = container.getElement();

    assertEquals(container, element0.getParent());
    assertEquals("PlainSingleContainedBidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    assertContent(container, element0);
  }

  public void testPlainMultiNonContainedBidirectional()
  {
    RefMultiNonContained container = factory.createRefMultiNonContained();
    MultiNonContainedElement element0 = factory.createMultiNonContainedElement();
    element0.setName("PlainMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    MultiNonContainedElement element1 = factory.createMultiNonContainedElement();
    element1.setName("PlainMultiNonContainedBidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(3, resource1.getContents().size());

    container = (RefMultiNonContained)CDOUtil.getEObject(resource1.getContents().get(0));
    element0 = (MultiNonContainedElement)resource1.getContents().get(1);
    element1 = (MultiNonContainedElement)resource1.getContents().get(2);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals(container, element0.getParent());
    assertEquals(container, element1.getParent());
    assertEquals("PlainMultiNonContainedBidirectional-Element-0", element0.getName());
    assertEquals("PlainMultiNonContainedBidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
    assertContent(resource1, element1);
  }

  public void testPlainMultiContainedBidirectional()
  {
    RefMultiContained container = factory.createRefMultiContained();
    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("PlainMultiContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("PlainMultiContainedBidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (RefMultiContained)resource1.getContents().get(0);
    assertEquals(2, container.getElements().size());

    element0 = container.getElements().get(0);
    element1 = container.getElements().get(1);

    assertEquals(container, element0.getParent());
    assertEquals(container, element1.getParent());
    assertEquals("PlainMultiContainedBidirectional-Element-0", element0.getName());
    assertEquals("PlainMultiContainedBidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(container, element0);
    assertContent(container, element1);
  }

  public void testPlainSingleNonContainedUnidirectional()
  {
    RefSingleNonContainedNPL container = factory.createRefSingleNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (RefSingleNonContainedNPL)resource1.getContents().get(0);
    element0 = (ContainedElementNoOpposite)resource1.getContents().get(1);

    assertEquals(element0, container.getElement());
    assertEquals("PlainSingleNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
  }

  public void testPlainSingleContainedUnidirectional()
  {
    RefSingleContainedNPL container = factory.createRefSingleContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (RefSingleContainedNPL)resource1.getContents().get(0);
    element0 = container.getElement();

    assertEquals("PlainSingleContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    assertContent(container, element0);
  }

  public void testPlainMultiNonContainedUnidirectional()
  {
    RefMultiNonContainedNPL container = factory.createRefMultiNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("PlainMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(3, resource1.getContents().size());

    container = (RefMultiNonContainedNPL)resource1.getContents().get(0);
    element0 = (ContainedElementNoOpposite)resource1.getContents().get(1);
    element1 = (ContainedElementNoOpposite)resource1.getContents().get(2);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals("PlainMultiNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals("PlainMultiNonContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
    assertContent(resource1, element1);
  }

  public void testPlainMultiContainedUnidirectional()
  {
    RefMultiContainedNPL container = factory.createRefMultiContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("PlainMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("PlainMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (RefMultiContainedNPL)resource1.getContents().get(0);
    assertEquals(2, container.getElements().size());

    element0 = container.getElements().get(0);
    element1 = container.getElements().get(1);

    assertEquals("PlainMultiContainedUnidirectional-Element-0", element0.getName());
    assertEquals("PlainMultiContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(container, element0);
    assertContent(container, element1);
  }

  public void testGenRefSingleNonContainedUnidirectional()
  {
    GenRefSingleNonContained container = factory.createGenRefSingleNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (GenRefSingleNonContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)resource1.getContents().get(1);

    assertEquals(element0, container.getElement());
    assertEquals("GenRefSingleNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
  }

  public void testGenRefSingleContainedUnidirectional()
  {
    GenRefSingleContained container = factory.createGenRefSingleContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);

    resource1.getContents().add(element0);

    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (GenRefSingleContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)container.getElement();

    // assertEquals("GenRefSingleContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    // assertEquals(resource1, element0.eResource());

    assertContent(resource1, container);
    // assertContent(container, element0);
  }

  public void testGenRefMultiNonContainedUnidirectional()
  {
    GenRefMultiNonContained container = factory.createGenRefMultiNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("GenRefMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(3, resource1.getContents().size());

    container = (GenRefMultiNonContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)resource1.getContents().get(1);
    element1 = (ImplContainedElementNPL)resource1.getContents().get(2);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals("GenRefMultiNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals("GenRefMultiNonContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0);
    assertContent(resource1, element1);
  }

  public void testGenRefMultiContainedUnidirectional()
  {
    GenRefMultiContained container = factory.createGenRefMultiContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("GenRefMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("GenRefMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (GenRefMultiContained)resource1.getContents().get(0);
    assertEquals(2, container.getElements().size());

    element0 = (ImplContainedElementNPL)container.getElements().get(0);
    element1 = (ImplContainedElementNPL)container.getElements().get(1);

    assertEquals("GenRefMultiContainedUnidirectional-Element-0", element0.getName());
    assertEquals("GenRefMultiContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0.eResource());
    assertEquals(resource1, element1.eResource());

    assertContent(resource1, container);
    assertContent(container, element0);
    assertContent(container, element1);
  }

  public void testIfcimplSingleNonContainedBidirectional()
  {
    ImplSingleRefNonContainer container = factory.createImplSingleRefNonContainer();
    ImplSingleRefNonContainedElement element0 = factory.createImplSingleRefNonContainedElement();
    element0.setName("IfcimplSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (ImplSingleRefNonContainer)CDOUtil.getEObject(resource1.getContents().get(0));
    ISingleRefNonContainedElement element0_ = (ISingleRefNonContainedElement)resource1.getContents().get(1);

    assertEquals(element0_, container.getElement());
    assertEquals(container, element0_.getParent());
    assertEquals("IfcimplSingleNonContainedBidirectional-Element-0", ((ImplSingleRefNonContainedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0_);
  }

  public void testIfcimplSingleContainedBidirectional()
  {
    ImplSingleRefContainer container = factory.createImplSingleRefContainer();
    ImplSingleRefContainedElement element0 = factory.createImplSingleRefContainedElement();
    element0.setName("IfcimplSingleContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (ImplSingleRefContainer)CDOUtil.getEObject(resource1.getContents().get(0));
    ISingleRefContainedElement element0_ = container.getElement();

    assertEquals(container, element0_.getParent());
    assertEquals("IfcimplSingleContainedBidirectional-Element-0", ((ImplSingleRefContainedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());

    assertContent(resource1, container);
    assertContent(container, element0_);
  }

  public void testIfcimplMultiNonContainedBidirectional()
  {
    ImplMultiRefNonContainer container = factory.createImplMultiRefNonContainer();
    ImplMultiRefNonContainedElement element0 = factory.createImplMultiRefNonContainedElement();
    element0.setName("IfcimplMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplMultiRefNonContainedElement element1 = factory.createImplMultiRefNonContainedElement();
    element1.setName("IfcimplMultiNonContainedBidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(3, resource1.getContents().size());

    container = (ImplMultiRefNonContainer)resource1.getContents().get(0);
    IMultiRefNonContainedElement element0_ = (IMultiRefNonContainedElement)resource1.getContents().get(1);
    IMultiRefNonContainedElement element1_ = (IMultiRefNonContainedElement)resource1.getContents().get(2);

    assertEquals(element0_, container.getElements().get(0));
    assertEquals(element1_, container.getElements().get(1));
    assertEquals(container, element0_.getParent());
    assertEquals(container, element1_.getParent());
    assertEquals("IfcimplMultiNonContainedBidirectional-Element-0", ((ImplMultiRefNonContainedElement)element0_).getName());
    assertEquals("IfcimplMultiNonContainedBidirectional-Element-1", ((ImplMultiRefNonContainedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());
    assertEquals(resource1, element1_.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0_);
    assertContent(resource1, element1_);
  }

  public void testIfcimplMultiContainedBidirectional()
  {
    ImplMultiRefContainer container = factory.createImplMultiRefContainer();
    ImplMultiRefContainedElement element0 = factory.createImplMultiRefContainedElement();
    element0.setName("IfcimplMultiContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    ImplMultiRefContainedElement element1 = factory.createImplMultiRefContainedElement();
    element1.setName("IfcimplMultiContainedBidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (ImplMultiRefContainer)resource1.getContents().get(0);
    assertEquals(2, container.getElements().size());

    IMultiRefContainedElement element0_ = container.getElements().get(0);
    IMultiRefContainedElement element1_ = container.getElements().get(1);

    assertEquals(container, element0_.getParent());
    assertEquals(container, element1_.getParent());
    assertEquals("IfcimplMultiContainedBidirectional-Element-0", ((ImplMultiRefContainedElement)element0_).getName());
    assertEquals("IfcimplMultiContainedBidirectional-Element-1", ((ImplMultiRefContainedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());
    assertEquals(resource1, element1_.eResource());

    assertContent(resource1, container);
    assertContent(container, element0_);
    assertContent(container, element1_);
  }

  public void testIfcimplSingleNonContainedUnidirectional()
  {
    ImplSingleRefNonContainerNPL container = factory.createImplSingleRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(2, resource1.getContents().size());

    container = (ImplSingleRefNonContainerNPL)resource1.getContents().get(0);
    IContainedElementNoParentLink element0_ = (IContainedElementNoParentLink)resource1.getContents().get(1);

    assertEquals(element0_, container.getElement());
    assertEquals("IfcimplSingleNonContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0_);
  }

  public void testIfcimplSingleContainedUnidirectional()
  {
    ImplSingleRefContainerNPL container = factory.createImplSingleRefContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplSingleContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (ImplSingleRefContainerNPL)resource1.getContents().get(0);
    IContainedElementNoParentLink element0_ = container.getElement();

    assertEquals("IfcimplSingleContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());

    assertContent(resource1, container);
    assertContent(container, element0_);
  }

  public void testIfcimplMultiNonContainedUnidirectional()
  {
    ImplMultiRefNonContainerNPL container = factory.createImplMultiRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource1.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("IfcimplMultiNonContainedUnidirectional-Element-1");
    resource1.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(3, resource1.getContents().size());

    container = (ImplMultiRefNonContainerNPL)resource1.getContents().get(0);
    IContainedElementNoParentLink element0_ = (IContainedElementNoParentLink)resource1.getContents().get(1);
    IContainedElementNoParentLink element1_ = (IContainedElementNoParentLink)resource1.getContents().get(2);

    assertEquals(element0_, container.getElements().get(0));
    assertEquals(element1_, container.getElements().get(1));
    assertEquals("IfcimplMultiNonContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals("IfcimplMultiNonContainedUnidirectional-Element-1", ((INamedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());
    assertEquals(resource1, element1_.eResource());

    assertContent(resource1, container);
    assertContent(resource1, element0_);
    assertContent(resource1, element1_);
  }

  public void testIfcimplMultiContainedUnidirectional()
  {
    ImplMultiRefContainerNPL container = factory.createImplMultiRefContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("IfcimplMultiContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("IfcimplMultiContainedUnidirectional-Element-1");
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());

    container = (ImplMultiRefContainerNPL)resource1.getContents().get(0);
    assertEquals(2, container.getElements().size());

    IContainedElementNoParentLink element0_ = container.getElements().get(0);
    IContainedElementNoParentLink element1_ = container.getElements().get(1);

    assertEquals("IfcimplMultiContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals("IfcimplMultiContainedUnidirectional-Element-1", ((INamedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource1, element0_.eResource());
    assertEquals(resource1, element1_.eResource());

    assertContent(resource1, container);
    assertContent(container, element0_);
    assertContent(container, element1_);
  }

  public void testCrossResourcePlainSingleNonContainedBidirectional()
  {
    RefSingleNonContained container = factory.createRefSingleNonContained();
    SingleNonContainedElement element0 = factory.createSingleNonContainedElement();
    element0.setName("CrossResourcePlainSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(1, resource2.getContents().size());

    container = (RefSingleNonContained)CDOUtil.getEObject(resource1.getContents().get(0));
    element0 = (SingleNonContainedElement)resource2.getContents().get(0);

    assertEquals(element0, container.getElement());
    assertEquals(container, element0.getParent());
    assertEquals("CrossResourcePlainSingleNonContainedBidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
  }

  public void testCrossResourcePlainMultiNonContainedBidirectional()
  {
    RefMultiNonContained container = factory.createRefMultiNonContained();
    MultiNonContainedElement element0 = factory.createMultiNonContainedElement();
    element0.setName("CrossResourcePlainMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    MultiNonContainedElement element1 = factory.createMultiNonContainedElement();
    element1.setName("CrossResourcePlainMultiNonContainedBidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(2, resource2.getContents().size());

    container = (RefMultiNonContained)resource1.getContents().get(0);
    element0 = (MultiNonContainedElement)resource2.getContents().get(0);
    element1 = (MultiNonContainedElement)resource2.getContents().get(1);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals(container, element0.getParent());
    assertEquals(container, element1.getParent());
    assertEquals("CrossResourcePlainMultiNonContainedBidirectional-Element-0", element0.getName());
    assertEquals("CrossResourcePlainMultiNonContainedBidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());
    assertEquals(resource2, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
    assertContent(resource2, element1);
  }

  public void testCrossResourcePlainSingleNonContainedUnidirectional()
  {
    RefSingleNonContainedNPL container = factory.createRefSingleNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("CrossResourcePlainSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(1, resource2.getContents().size());

    container = (RefSingleNonContainedNPL)resource1.getContents().get(0);
    element0 = (ContainedElementNoOpposite)resource2.getContents().get(0);

    assertEquals(element0, container.getElement());
    assertEquals("CrossResourcePlainSingleNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
  }

  public void testCrossResourcePlainMultiNonContainedUnidirectional()
  {
    RefMultiNonContainedNPL container = factory.createRefMultiNonContainedNPL();
    ContainedElementNoOpposite element0 = factory.createContainedElementNoOpposite();
    element0.setName("CrossResourcePlainMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ContainedElementNoOpposite element1 = factory.createContainedElementNoOpposite();
    element1.setName("CrossResourcePlainMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(2, resource2.getContents().size());

    container = (RefMultiNonContainedNPL)resource1.getContents().get(0);
    element0 = (ContainedElementNoOpposite)resource2.getContents().get(0);
    element1 = (ContainedElementNoOpposite)resource2.getContents().get(1);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals("CrossResourcePlainMultiNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals("CrossResourcePlainMultiNonContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());
    assertEquals(resource2, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
    assertContent(resource2, element1);
  }

  public void testCrossResourceGenRefSingleNonContainedUnidirectional()
  {
    GenRefSingleNonContained container = factory.createGenRefSingleNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceGenRefSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(1, resource2.getContents().size());

    container = (GenRefSingleNonContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)resource2.getContents().get(0);

    assertEquals(element0, container.getElement());
    assertEquals("CrossResourceGenRefSingleNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
  }

  public void testCrossResourceGenRefMultiNonContainedUnidirectional()
  {
    GenRefMultiNonContained container = factory.createGenRefMultiNonContained();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceGenRefMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("CrossResourceGenRefMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(2, resource2.getContents().size());

    container = (GenRefMultiNonContained)resource1.getContents().get(0);
    element0 = (ImplContainedElementNPL)resource2.getContents().get(0);
    element1 = (ImplContainedElementNPL)resource2.getContents().get(1);

    assertEquals(element0, container.getElements().get(0));
    assertEquals(element1, container.getElements().get(1));
    assertEquals("CrossResourceGenRefMultiNonContainedUnidirectional-Element-0", element0.getName());
    assertEquals("CrossResourceGenRefMultiNonContainedUnidirectional-Element-1", element1.getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0.eResource());
    assertEquals(resource2, element1.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0);
    assertContent(resource2, element1);
  }

  public void testCrossResourceIfcimplSingleNonContainedBidirectional()
  {
    ImplSingleRefNonContainer container = factory.createImplSingleRefNonContainer();
    ImplSingleRefNonContainedElement element0 = factory.createImplSingleRefNonContainedElement();
    element0.setName("CrossResourceIfcimplSingleNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(1, resource2.getContents().size());

    container = (ImplSingleRefNonContainer)CDOUtil.getEObject(resource1.getContents().get(0));
    ISingleRefNonContainedElement element0_ = (ISingleRefNonContainedElement)resource2.getContents().get(0);

    assertEquals(element0_, container.getElement());
    assertEquals(container, element0_.getParent());
    assertEquals("CrossResourceIfcimplSingleNonContainedBidirectional-Element-0", ((ImplSingleRefNonContainedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0_.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0_);
  }

  public void testCrossResourceIfcimplMultiNonContainedBidirectional()
  {
    ImplMultiRefNonContainer container = factory.createImplMultiRefNonContainer();
    ImplMultiRefNonContainedElement element0 = factory.createImplMultiRefNonContainedElement();
    element0.setName("CrossResourceIfcimplMultiNonContainedBidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplMultiRefNonContainedElement element1 = factory.createImplMultiRefNonContainedElement();
    element1.setName("CrossResourceIfcimplMultiNonContainedBidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(2, resource2.getContents().size());

    container = (ImplMultiRefNonContainer)resource1.getContents().get(0);
    IMultiRefNonContainedElement element0_ = (IMultiRefNonContainedElement)resource2.getContents().get(0);
    IMultiRefNonContainedElement element1_ = (IMultiRefNonContainedElement)resource2.getContents().get(1);

    assertEquals(element0_, container.getElements().get(0));
    assertEquals(element1_, container.getElements().get(1));
    assertEquals(container, element0_.getParent());
    assertEquals(container, element1_.getParent());
    assertEquals("CrossResourceIfcimplMultiNonContainedBidirectional-Element-0", ((ImplMultiRefNonContainedElement)element0_).getName());
    assertEquals("CrossResourceIfcimplMultiNonContainedBidirectional-Element-1", ((ImplMultiRefNonContainedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0_.eResource());
    assertEquals(resource2, element1_.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0_);
    assertContent(resource2, element1_);
  }

  public void testCrossResourceIfcimplSingleNonContainedUnidirectional()
  {
    ImplSingleRefNonContainerNPL container = factory.createImplSingleRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceIfcimplSingleNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    container.setElement(element0);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(1, resource1.getContents().size());

    container = (ImplSingleRefNonContainerNPL)resource1.getContents().get(0);
    IContainedElementNoParentLink element0_ = (IContainedElementNoParentLink)resource2.getContents().get(0);

    assertEquals(element0_, container.getElement());
    assertEquals("CrossResourceIfcimplSingleNonContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0_.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0_);
  }

  public void testCrossResourceIfcimplMultiNonContainedUnidirectional()
  {
    ImplMultiRefNonContainerNPL container = factory.createImplMultiRefNonContainerNPL();
    ImplContainedElementNPL element0 = factory.createImplContainedElementNPL();
    element0.setName("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-0");
    resource1.getContents().add(container);
    resource2.getContents().add(element0);
    ImplContainedElementNPL element1 = factory.createImplContainedElementNPL();
    element1.setName("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-1");
    resource2.getContents().add(element1);
    container.getElements().add(element0);
    container.getElements().add(element1);
    commit();

    purgeCaches();
    assertEquals(1, resource1.getContents().size());
    assertEquals(2, resource2.getContents().size());

    container = (ImplMultiRefNonContainerNPL)resource1.getContents().get(0);
    IContainedElementNoParentLink element0_ = (IContainedElementNoParentLink)resource2.getContents().get(0);
    IContainedElementNoParentLink element1_ = (IContainedElementNoParentLink)resource2.getContents().get(1);

    assertEquals(element0_, container.getElements().get(0));
    assertEquals(element1_, container.getElements().get(1));
    assertEquals("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-0", ((INamedElement)element0_).getName());
    assertEquals("CrossResourceIfcimplMultiNonContainedUnidirectional-Element-1", ((INamedElement)element1_).getName());
    assertEquals(resource1, container.eResource());
    assertEquals(resource2, element0_.eResource());
    assertEquals(resource2, element1_.eResource());

    assertContent(resource1, container);
    assertContent(resource2, element0_);
    assertContent(resource2, element1_);
  }

  public void testMultipleTransactions3() throws CommitException
  {
    CDOTransaction transaction1 = session.openTransaction();
    String resource1path = getResourcePath("/resources/3/res" + uniqueCounter);
    CDOResource resource1 = transaction1.createResource(resource1path);
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    String resource2path = getResourcePath("/resources/4/res" + uniqueCounter++);
    CDOResource resource2 = transaction2.createResource(resource2path);
    transaction2.commit();

    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("MultipleTransactions-Element-0");

    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("MultipleTransactions-Element-0");

    MultiContainedElement element2 = factory.createMultiContainedElement();
    element2.setName("MultipleTransactions-Element-0");

    MultiContainedElement element3 = factory.createMultiContainedElement();
    element3.setName("MultipleTransactions-Element-0");

    resource1.getContents().add(element0);
    resource1.getContents().add(element1);
    resource1.getContents().add(element2);
    resource1.getContents().add(element3);

    transaction1.commit();

    RefMultiContained container = factory.createRefMultiContained();
    resource2.getContents().add(container);

    transaction2.commit();

    CDOTransaction transaction3 = session.openTransaction();
    EList<EObject> elements = transaction3.getResource(resource1path).getContents();
    EList<EObject> containers = transaction3.getResource(resource2path).getContents();
    List<EObject> elementToRemove = new ArrayList<>();
    for (EObject o : elements)
    {
      elementToRemove.add(o);
    }

    RefMultiContained container_work = (RefMultiContained)containers.get(0);
    for (EObject o : elementToRemove)
    {
      MultiContainedElement element_work = (MultiContainedElement)o;
      container_work.getElements().add(element_work);
      transaction3.commit();
    }
  }

  public void testMultipleTransactions2() throws CommitException
  {
    CDOTransaction transaction1 = session.openTransaction();
    String resource1path = getResourcePath("/resources/3/res" + uniqueCounter);
    CDOResource resource1 = transaction1.createResource(resource1path);
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    String resource2path = getResourcePath("/resources/4/res" + uniqueCounter++);
    CDOResource resource2 = transaction2.createResource(resource2path);
    transaction2.commit();

    MultiContainedElement element0 = factory.createMultiContainedElement();
    element0.setName("MultipleTransactions-Element-0");

    MultiContainedElement element1 = factory.createMultiContainedElement();
    element1.setName("MultipleTransactions-Element-0");

    MultiContainedElement element2 = factory.createMultiContainedElement();
    element2.setName("MultipleTransactions-Element-0");

    MultiContainedElement element3 = factory.createMultiContainedElement();
    element3.setName("MultipleTransactions-Element-0");

    resource1.getContents().add(element0);
    resource1.getContents().add(element1);
    resource1.getContents().add(element2);
    resource1.getContents().add(element3);

    transaction1.commit();

    RefMultiContained container = factory.createRefMultiContained();
    resource2.getContents().add(container);

    transaction2.commit();
    CDOResource resource1FromTx2 = transaction2.getResource(resource1path);
    EList<EObject> elements = resource1FromTx2.getContents();
    List<EObject> elementToRemove = new ArrayList<>();
    for (EObject o : elements)
    {
      elementToRemove.add(o);
    }

    for (EObject o : elementToRemove)
    {
      MultiContainedElement element_work = (MultiContainedElement)o;
      assertEquals(resource1FromTx2, CDOUtil.getCDOObject(element_work).cdoDirectResource());
      assertEquals(resource1FromTx2, element_work.eResource());

      container.getElements().add(element_work);

      assertEquals(null, CDOUtil.getCDOObject(element_work).cdoDirectResource());
      assertEquals(resource2, element_work.eResource());
      transaction2.commit();
    }
  }

  public void testMigrateContainmentSingle()
  {
    SingleContainedElement element = factory.createSingleContainedElement();
    element.setName("MigrateContainmentSingle-Element-0");

    RefSingleContained container1 = factory.createRefSingleContained();
    container1.setElement(element);
    resource1.getContents().add(container1);

    RefSingleContained container2 = factory.createRefSingleContained();
    resource2.getContents().add(container2);
    commit();

    container2.setElement(element);
    commit();

    assertEquals(container2, element.getParent());
    assertEquals(container2, element.eContainer());
    assertEquals(null, CDOUtil.getCDOObject(element).cdoDirectResource());

    assertEquals(null, container1.getElement());
    assertEquals(resource1, CDOUtil.getCDOObject(container1).cdoDirectResource());

    assertEquals(element, container2.getElement());
    assertEquals(resource2, CDOUtil.getCDOObject(container2).cdoDirectResource());
  }

  public void testMigrateContainmentMulti()
  {
    MultiContainedElement elementA = factory.createMultiContainedElement();
    elementA.setName("MigrateContainmentMulti-Element-A");

    MultiContainedElement elementB = factory.createMultiContainedElement();
    elementB.setName("MigrateContainmentMulti-Element-B");

    RefMultiContained container1 = factory.createRefMultiContained();
    container1.getElements().add(elementA);
    container1.getElements().add(elementB);

    resource1.getContents().add(container1);

    RefMultiContained container2 = factory.createRefMultiContained();
    resource2.getContents().add(container2);
    commit();

    container2.getElements().add(container1.getElements().get(0));
    commit();

    assertEquals(container2, elementA.getParent());
    assertEquals(container2, elementA.eContainer());
    assertEquals(null, CDOUtil.getCDOObject(elementA).cdoDirectResource());
    assertEquals(resource2, elementA.eResource());

    assertEquals(container1, elementB.getParent());
    assertEquals(container1, elementB.eContainer());
    assertEquals(null, CDOUtil.getCDOObject(elementB).cdoDirectResource());
    assertEquals(resource1, elementB.eResource());

    assertEquals(resource1, CDOUtil.getCDOObject(container1).cdoDirectResource());
    assertEquals(1, container1.getElements().size());
    assertEquals(elementB, container1.getElements().get(0));

    assertEquals(resource2, CDOUtil.getCDOObject(container2).cdoDirectResource());
    assertEquals(1, container2.getElements().size());
    assertEquals(elementA, container2.getElements().get(0));
  }
}
