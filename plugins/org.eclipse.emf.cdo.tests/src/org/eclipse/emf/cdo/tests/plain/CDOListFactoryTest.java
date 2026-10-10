/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithEqualsImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOPCLListWithEqualsImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;

import org.eclipse.emf.ecore.EcorePackage;

/**
 * Tests the default CDO list factory and its feature-specific list semantics.
 *
 * @author Eike Stepper
 */
public class CDOListFactoryTest extends PlainTest
{
  public void testCompleteListsPreserveFeatureEqualitySemantics()
  {
    CDOList attributeList = CDOListFactory.DEFAULT.createList(EcorePackage.Literals.EANNOTATION__SOURCE, 2, 0, CDORevision.UNCHUNKED);
    CDOList referenceList = CDOListFactory.DEFAULT.createList(EcorePackage.Literals.EANNOTATION__CONTENTS, 2, 0, CDORevision.UNCHUNKED);

    assertTrue(attributeList instanceof CDOListWithEqualsImpl);
    assertTrue(referenceList instanceof CDOListImpl);

    String attributeValue = new String("value"); //$NON-NLS-1$
    String referenceValue = new String("value"); //$NON-NLS-1$
    attributeList.add(attributeValue);
    referenceList.add(referenceValue);

    assertEquals(0, attributeList.indexOf(new String("value"))); //$NON-NLS-1$
    assertEquals(-1, referenceList.indexOf(new String("value"))); //$NON-NLS-1$
    assertEquals(0, referenceList.indexOf(referenceValue));
  }

  public void testPartialListsPreserveFeatureEqualitySemantics()
  {
    InternalCDOList attributeList = (InternalCDOList)CDOListFactory.DEFAULT.createList(EcorePackage.Literals.EANNOTATION__SOURCE, 2, 2, 1);
    InternalCDOList referenceList = (InternalCDOList)CDOListFactory.DEFAULT.createList(EcorePackage.Literals.EANNOTATION__CONTENTS, 2, 2, 1);

    assertTrue(attributeList instanceof CDOPCLListWithEqualsImpl);
    assertTrue(referenceList instanceof CDOPCLListImpl);

    String attributeValue = new String("value"); //$NON-NLS-1$
    String referenceValue = new String("value"); //$NON-NLS-1$
    attributeList.set(0, attributeValue);
    referenceList.set(0, referenceValue);
    attributeList.finishConstruction(true);
    referenceList.finishConstruction(true);

    assertEquals(0, attributeList.indexOf(new String("value"))); //$NON-NLS-1$
    assertEquals(-1, referenceList.indexOf(new String("value"))); //$NON-NLS-1$
    assertEquals(0, referenceList.indexOf(referenceValue));
    assertFalse(attributeList.isFullyLoaded());
    assertFalse(referenceList.isFullyLoaded());
    assertSame(CDORevisionUtil.UNLOADED, attributeList.get(1));
    assertSame(CDORevisionUtil.UNLOADED, referenceList.get(1));
  }

  @SuppressWarnings("deprecation")
  public void testImplementationFactoryIsDefaultFactory()
  {
    // Test https://github.com/eclipse-cdo/cdo/issues/214 for legacy call-sites.
    assertNotNull(CDOListImpl.FACTORY);
    assertSame(CDOListFactory.DEFAULT, CDOListImpl.FACTORY);
  }
}
