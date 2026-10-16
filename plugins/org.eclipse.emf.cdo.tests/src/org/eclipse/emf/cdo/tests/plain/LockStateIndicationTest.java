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
import org.eclipse.emf.cdo.server.internal.net4j.protocol.LockStateIndication;
import org.eclipse.emf.cdo.spi.common.revision.StubCDORevision;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests lock-state prefetch traversal on the server.
 *
 * @author Eike Stepper
 */
public class LockStateIndicationTest extends PlainTest
{
  public void testInfinitePrefetchTraversesOverlappingRootsOnce() throws Exception
  {
    int size = 64;
    List<CDOID> ids = new ArrayList<>(size);

    for (int i = 1; i <= size; i++)
    {
      ids.add(CDOIDUtil.createLong(i));
    }

    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Node"); //$NON-NLS-1$

    EReference containment = EcoreFactory.eINSTANCE.createEReference();
    containment.setName("children"); //$NON-NLS-1$
    containment.setContainment(true);
    containment.setUpperBound(-1);
    containment.setEType(eClass);
    eClass.getEStructuralFeatures().add(containment);

    Map<CDOID, CDORevision> revisions = new HashMap<>();

    for (int i = 0; i < size; i++)
    {
      List<CDOID> children = i + 1 < size ? List.of(ids.get(i + 1)) : List.of();
      revisions.put(ids.get(i), new TestRevision(eClass, containment, children));
    }

    Map<CDOID, Integer> revisionRequests = new HashMap<>();
    CDORevisionProvider revisionProvider = id -> {
      revisionRequests.merge(id, 1, Integer::sum);
      return revisions.get(id);
    };

    LockStateIndication indication = new LockStateIndication(null);
    setField(indication, "targetIDs", new LinkedHashMap<>()); //$NON-NLS-1$
    setField(indication, "revisionProvider", revisionProvider); //$NON-NLS-1$

    Method prefetchLockStates = LockStateIndication.class.getDeclaredMethod("prefetchLockStates", int.class, CDOID.class, boolean.class); //$NON-NLS-1$
    prefetchLockStates.setAccessible(true);

    for (CDOID id : ids)
    {
      prefetchLockStates.invoke(indication, Integer.MAX_VALUE, id, true);
    }

    assertEquals(size, revisionRequests.size());

    for (CDOID id : ids)
    {
      assertEquals("Each revision should be traversed once", Integer.valueOf(1), revisionRequests.get(id)); //$NON-NLS-1$
    }

    Field targetIDsField = LockStateIndication.class.getDeclaredField("targetIDs"); //$NON-NLS-1$
    targetIDsField.setAccessible(true);
    assertEquals(size, ((Map<?, ?>)targetIDsField.get(indication)).size());
  }

  private static void setField(Object object, String name, Object value) throws Exception
  {
    Field field = object.getClass().getDeclaredField(name);
    field.setAccessible(true);
    field.set(object, value);
  }

  /**
   * Supplies containment values while using the real revision class metadata implementation.
   *
   * @author Eike Stepper
   */
  private static final class TestRevision extends StubCDORevision
  {
    private final EReference containment;

    private final List<CDOID> children;

    public TestRevision(EClass eClass, EReference containment, List<CDOID> children)
    {
      super(eClass);
      this.containment = containment;
      this.children = children;
    }

    @Override
    public Object getValue(EStructuralFeature feature)
    {
      return feature == containment ? children : super.getValue(feature);
    }
  }
}
