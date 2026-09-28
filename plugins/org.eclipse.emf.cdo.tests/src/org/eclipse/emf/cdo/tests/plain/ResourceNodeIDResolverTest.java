/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.server.internal.security.ResourceNodeIDResolver;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;

import java.util.HashMap;

/**
 * Tests server-side resource-node resolution, including direct-resource precedence.
 *
 * @author Eike Stepper
 */
public class ResourceNodeIDResolverTest extends PlainTest
{
  private final RevisionProvider provider = new RevisionProvider();

  public void testResourceNodeUsesOwnID()
  {
    CDORevisionImpl node = revision(EresourcePackage.Literals.CDO_RESOURCE, 1);

    assertTrue(ResourceNodeIDResolver.isResourceNode(EresourcePackage.Literals.CDO_RESOURCE));
    assertEquals(node.getID(), ResourceNodeIDResolver.resolve(node, provider));
  }

  public void testOrdinaryModelClassesAreNotResourceNodes()
  {
    assertFalse(ResourceNodeIDResolver.isResourceNode(EcoreFactory.eINSTANCE.createEClass()));
  }

  public void testDirectResourceIDWins()
  {
    CDORevisionImpl resourceA = revision(EresourcePackage.Literals.CDO_RESOURCE, 2);
    CDORevisionImpl object = revision(EcoreFactory.eINSTANCE.createEClass(), 3);
    object.setContainerID(resourceA.getID());
    object.setResourceID(id(4));

    assertEquals(id(4), ResourceNodeIDResolver.resolve(object, provider));
  }

  public void testDeepContainmentWalksToResourceNode()
  {
    CDORevisionImpl resource = revision(EresourcePackage.Literals.CDO_RESOURCE, 5);
    CDORevisionImpl middle = revision(EcoreFactory.eINSTANCE.createEClass(), 6);
    middle.setContainerID(resource.getID());
    CDORevisionImpl child = revision(EcoreFactory.eINSTANCE.createEClass(), 7);
    child.setContainerID(middle.getID());

    assertEquals(resource.getID(), ResourceNodeIDResolver.resolve(child, provider));
  }

  public void testCrossResourceContainmentUsesDirectResource()
  {
    CDORevisionImpl resourceA = revision(EresourcePackage.Literals.CDO_RESOURCE, 8);
    CDORevisionImpl resourceB = revision(EresourcePackage.Literals.CDO_RESOURCE, 9);
    CDORevisionImpl parent = revision(EcoreFactory.eINSTANCE.createEClass(), 10);
    parent.setContainerID(resourceA.getID());
    CDORevisionImpl child = revision(EcoreFactory.eINSTANCE.createEClass(), 11);
    child.setContainerID(parent.getID());
    child.setResourceID(resourceB.getID());

    assertEquals(resourceB.getID(), ResourceNodeIDResolver.resolve(child, provider));
  }

  public void testUnresolvableOwnershipReturnsNull()
  {
    CDORevisionImpl child = revision(EcoreFactory.eINSTANCE.createEClass(), 12);
    child.setContainerID(id(13));

    assertNull(ResourceNodeIDResolver.resolve(child, provider));
  }

  private CDORevisionImpl revision(EClass eClass, long id)
  {
    CDORevisionImpl result = new CDORevisionImpl(eClass);
    result.setID(id(id));
    provider.put(result);
    return result;
  }

  private static CDOID id(long id)
  {
    return CDOIDUtil.createLong(id);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RevisionProvider extends HashMap<CDOID, CDORevision> implements CDORevisionProvider
  {
    private static final long serialVersionUID = 1L;

    public RevisionProvider()
    {
    }

    public void put(CDORevision revision)
    {
      put(revision.getID(), revision);
    }

    @Override
    public CDORevision getRevision(CDOID id)
    {
      return get(id);
    }
  }
}
