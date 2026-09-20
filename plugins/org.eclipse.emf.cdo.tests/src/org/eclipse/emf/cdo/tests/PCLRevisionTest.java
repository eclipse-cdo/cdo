/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0.
 */
package org.eclipse.emf.cdo.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.common.revision.CDOElementProxyImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesWithEqualsImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithEqualsImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListWithEqualsImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Focused regression tests for list and revision loaded-state accounting.
 */
@SuppressWarnings("deprecation")
public class PCLRevisionTest
{
  private static final EAttribute ATTRIBUTE = EcorePackage.Literals.EANNOTATION__SOURCE;

  public void testMarkerCompatibility()
  {
    assertSame(CDORevisionUtil.UNLOADED, CDORevisionUtil.UNINITIALIZED);
  }

  public void testFeatureAwareCollectionLoadingPolicy()
  {
    EAttribute attribute = EcorePackage.Literals.EANNOTATION__SOURCE;
    EReference containment = EcorePackage.Literals.EANNOTATION__CONTENTS;
    EReference nonContainment = EcorePackage.Literals.ECLASS__ESUPER_TYPES;
    CDOCollectionLoadingPolicyImpl policy = new CDOCollectionLoadingPolicyImpl(3, 2)
    {
      @Override
      public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
      {
        return feature == attribute ? 1 : feature == containment ? 4 : feature == nonContainment ? 6 : super.getResolveChunkSize(revision, feature);
      }
    };

    assertEquals(1, policy.getResolveChunkSize(null, attribute));
    assertEquals(4, policy.getResolveChunkSize(null, containment));
    assertEquals(6, policy.getResolveChunkSize(null, nonContainment));
    assertEquals(3, policy.getInitialChunkSize(null, attribute));
    assertEquals(2, policy.getResolveChunkSize());
  }

  public void testCollectionLoadingPolicyDoesNotExecuteLoading()
  {
    CDOCollectionLoadingPolicyImpl policy = new CDOCollectionLoadingPolicyImpl(1, 1);

    try
    {
      policy.resolveProxy(null, ATTRIBUTE, 0, 0);
      fail("A collection loading policy must not execute loading");
    }
    catch (UnsupportedOperationException expected)
    {
      // Expected: execution belongs to the session resolver.
    }
  }

  public void testConstructionLifecycle()
  {
    InternalCDOList full = (InternalCDOList)CDOListFactory.DEFAULT.createList(ATTRIBUTE, 3, 3, CDORevision.UNCHUNKED);
    full.set(0, "a");
    full.set(1, "b");
    full.set(2, "c");
    full.finishConstruction(false);
    assertTrue(full.isLoadedAt(0));
    assertTrue(full.isLoadedAt(1));
    assertTrue(full.isLoadedAt(2));

    InternalCDOList partial = (InternalCDOList)CDOListFactory.DEFAULT.createList(ATTRIBUTE, 3, 3, 1);
    partial.set(0, "a");
    partial.finishConstruction(true);
    assertTrue(partial.isLoadedAt(0));
    assertFalse(partial.isLoadedAt(1));
    assertFalse(partial.isLoadedAt(2));
    assertSame(CDORevisionUtil.UNLOADED, partial.get(1));
    assertSame(CDORevisionUtil.UNLOADED, partial.get(2));
    assertTrue(partial instanceof CDOPCLListWithEqualsImpl);

    CDOList reference = CDOListFactory.DEFAULT.createList(EcorePackage.Literals.EANNOTATION__CONTENTS, 2, 2, 1);
    assertTrue(reference instanceof CDOPCLListImpl);
  }

  public void testFeatureAwareProxyEquality()
  {
    InternalCDOList attributeList = (InternalCDOList)CDOListWithElementProxiesImpl.FACTORY.createList(ATTRIBUTE, 2, 2, 1);
    assertTrue(attributeList instanceof CDOListWithElementProxiesWithEqualsImpl);
    assertFalse(attributeList.isFullyLoaded());
    attributeList.set(0, "value");
    assertEquals(0, attributeList.indexOf(new String("value")));
    assertEquals(0, attributeList.lastIndexOf(new String("value")));
    assertTrue(attributeList.contains(new String("value")));

    InternalCDOList referenceList = (InternalCDOList)CDOListWithElementProxiesImpl.FACTORY.createList(EcorePackage.Literals.EANNOTATION__CONTENTS, 2, 2, 1);
    assertTrue(referenceList instanceof CDOListWithElementProxiesImpl);
    assertFalse(referenceList instanceof CDOListWithElementProxiesWithEqualsImpl);
    assertFalse(referenceList.isFullyLoaded());
  }

  public void testServerIndexRepresentation()
  {
    InternalCDOList proxyList = new CDOPCLListImpl(2, 2, 0);
    proxyList.setWithoutFrozenCheck(1, new CDOElementProxyImpl(7));
    assertEquals(7, proxyList.getServerIndexAt(1));

    InternalCDOList ordinaryList = new CDOPCLListImpl(1, 0, 0);
    ordinaryList.add("loaded");
    assertEquals(0, ordinaryList.getServerIndexAt(0));
  }

  public void testTransitionsAndRevisionAggregation()
  {
    InternalCDORevision revision = new CDORevisionImpl(EcorePackage.Literals.EANNOTATION);
    CDOPCLListImpl first = new CDOPCLListImpl(3, 3, 1);
    CDOPCLListWithEqualsImpl second = new CDOPCLListWithEqualsImpl(2, 2, 1);
    revision.setList(ATTRIBUTE, first);
    assertFalse(revision.isUnchunked());
    revision.setList(ATTRIBUTE, second);
    assertFalse(revision.isUnchunked());
    second.loadValue(0, "a");
    assertFalse(revision.isUnchunked());
    second.loadValue(1, "b");
    assertTrue(revision.isUnchunked());
  }

  public void testDisjointLoadingAndFrozenMaterialization()
  {
    CDOPCLListImpl list = new CDOPCLListImpl(4, 4, 0);
    assertFalse(list.isFullyLoaded());
    list.set(0, "a");
    list.set(3, "d");
    assertFalse(list.isLoadedAt(1));
    assertFalse(list.isLoadedAt(2));
    assertEquals(2, list.getUnloadedCount());
    list.loadValue(2, "c");
    list.loadValue(1, "b");
    assertEquals(0, list.getUnloadedCount());
    assertTrue(list.isFullyLoaded());
    assertTrue(list.isLoadedAt(1));
  }

  public void testFailedMaterializationPreservesPartialState()
  {
    CDOPCLListImpl list = new CDOPCLListImpl(3, 3, 0);
    list.loadValue(1, "loaded");
    assertEquals(2, list.getUnloadedCount());

    try
    {
      list.loadValue(1, "duplicate");
      fail("Expected duplicate materialization rejection");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }

    assertEquals(2, list.getUnloadedCount());
    assertTrue(list.isLoadedAt(1));
    assertFalse(list.isLoadedAt(0));
    assertFalse(list.isLoadedAt(2));

    try
    {
      list.loadValue(0, CDORevisionUtil.UNLOADED);
      fail("Expected unloaded materialization rejection");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }

    assertEquals(2, list.getUnloadedCount());
    assertFalse(list.isLoadedAt(0));
  }

  public void testWholeListLoadedStateTracksMutations()
  {
    CDOPCLListImpl list = new CDOPCLListImpl(4, 0, 0);
    assertTrue(list.isFullyLoaded());

    list.add("loaded");
    assertTrue(list.isFullyLoaded());
    assertTrue(list.isLoadedAt(0));

    EObject directEObject = EcoreFactory.eINSTANCE.create(EcorePackage.Literals.EANNOTATION);
    list.add(directEObject);
    assertTrue(list.isFullyLoaded());
    assertTrue(list.isLoadedAt(0));
    assertTrue(list.isLoadedAt(1));

    list.add(CDORevisionUtil.UNLOADED);
    assertFalse(list.isFullyLoaded());
    assertTrue(list.isLoadedAt(0));
    assertTrue(list.isLoadedAt(1));
    assertFalse(list.isLoadedAt(2));

    list.remove(2);
    assertTrue(list.isFullyLoaded());

    list.add(CDORevisionUtil.UNLOADED);
    list.set(2, "loaded");
    assertTrue(list.isFullyLoaded());

    list.add(CDORevisionUtil.UNLOADED);
    list.clear();
    assertTrue(list.isFullyLoaded());
  }

  public void testRevisionTraversalSkipsUnloadedValues()
  {
    InternalCDORevision revision = new CDORevisionImpl(EcorePackage.Literals.EANNOTATION);
    CDOPCLListImpl list = new CDOPCLListImpl(2, 2, 1);
    list.set(0, "loaded");
    revision.setList(EcorePackage.Literals.EANNOTATION__CONTENTS, list);

    List<Object> values = new ArrayList<>();
    CDORevisionUtil.forEachValue(revision, EcorePackage.Literals.EANNOTATION__CONTENTS, values::add);
    assertEquals(1, values.size());
    assertEquals("loaded", values.get(0));

    List<Integer> indexes = new ArrayList<>();
    revision.accept((feature, value, index) -> {
      if (feature == EcorePackage.Literals.EANNOTATION__CONTENTS)
      {
        indexes.add(index);
      }
    });

    assertEquals(1, indexes.size());
    assertEquals(Integer.valueOf(0), indexes.get(0));
    assertFalse(list.isFullyLoaded());
  }

  public void testRejectingAndEqualitySpecificLists()
  {
    CDOListImpl identity = new CDOListImpl(2, 0);
    assertTrue(identity.isFullyLoaded());
    Object value = new String("value");
    identity.add(value);
    assertEquals(-1, identity.indexOf(new String("value")));

    try
    {
      identity.add(CDORevisionUtil.UNLOADED);
      fail("Expected unloaded value rejection");
    }
    catch (IllegalArgumentException expected)
    {
      // Expected.
    }

    CDOListWithEqualsImpl equals = new CDOListWithEqualsImpl(2, 0);
    equals.add(value);
    assertTrue(equals.isFullyLoaded());
    assertEquals(0, equals.indexOf(new String("value")));
  }

  public void testFrozenListCanMaterialize()
  {
    InternalCDORevision revision = new CDORevisionImpl(EcorePackage.Literals.EANNOTATION);
    CDOPCLListImpl list = new CDOPCLListImpl(1, 1, 0);
    revision.setList(ATTRIBUTE, list);
    revision.freeze();

    try
    {
      list.add("nope");
      fail("Expected frozen mutation rejection");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }

    list.loadValue(0, "loaded");
    assertTrue(revision.isUnchunked());
  }

  public void testListOwnerLifecycle()
  {
    TestOwner ownerA = new TestOwner();
    TestOwner ownerB = new TestOwner();

    CDOListImpl rejecting = new CDOListImpl(0, 0);
    rejecting.setOwner(ownerA);
    rejecting.setOwner(ownerA);
    rejecting.setOwner(null);
    rejecting.setOwner(ownerB);

    try
    {
      rejecting.setOwner(ownerA);
      rejecting.setOwner(ownerB);
      fail("Expected owner conflict");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }

    CDOPCLListImpl capable = new CDOPCLListImpl(2, 2, 0);
    capable.setOwner(ownerA);
    assertEquals(2, ownerA.unloadedDelta);
    capable.setOwner(ownerA);
    assertEquals(2, ownerA.unloadedDelta);
    capable.setOwner(null);
    assertEquals(0, ownerA.unloadedDelta);
    capable.setOwner(ownerB);
    assertEquals(2, ownerB.unloadedDelta);

    try
    {
      capable.setOwner(ownerA);
      fail("Expected owner conflict");
    }
    catch (IllegalStateException expected)
    {
      // Expected.
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class TestOwner implements InternalCDOList.Owner
  {
    private int unloadedDelta;

    @Override
    public boolean isFrozen()
    {
      return false;
    }

    @Override
    public void unloadedCountChanged(int delta)
    {
      unloadedDelta += delta;
    }
  }
}
