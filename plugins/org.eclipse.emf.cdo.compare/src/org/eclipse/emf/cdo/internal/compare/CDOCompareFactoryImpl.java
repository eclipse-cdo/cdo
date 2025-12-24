/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.compare;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.ComparePackage;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.impl.CompareFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentsEList;

/**
 * Bug 568373 - IllegalStateException in ReferenceChangeMerger: Couldn't add in target because its parent hasn't been merged yet.
 *
 * @author Eike Stepper
 */
public class CDOCompareFactoryImpl extends CompareFactoryImpl
{
  private static CompareFactory instance;

  public CDOCompareFactoryImpl()
  {
    synchronized (CDOCompareFactoryImpl.class)
    {
      instance = this;
    }
  }

  @Override
  public Match createMatch()
  {
    return new CDOMatchSpec();
  }

  public static CompareFactory getInstance()
  {
    synchronized (CDOCompareFactoryImpl.class)
    {
      if (instance == null)
      {
        new CDOCompareFactoryImpl();
      }

      return instance;
    }
  }

  /**
   * Bug 568373 - IllegalStateException in ReferenceChangeMerger: Couldn't add in target because its parent hasn't been merged yet.
   *
   * @author Eike Stepper
   */
  @SuppressWarnings("restriction")
  public static class CDOMatchSpec extends org.eclipse.emf.compare.internal.spec.MatchSpec
  {
    public CDOMatchSpec()
    {
    }

    /**
     * Bug 568373 - IllegalStateException in ReferenceChangeMerger: Couldn't add in target because its parent hasn't been merged yet.
     */
    @Override
    public EList<EObject> eContents()
    {
      EStructuralFeature[] eStructuralFeatures = { //
          ComparePackage.Literals.MATCH__DIFFERENCES, //
          ComparePackage.Literals.MATCH__SUBMATCHES };

      return new EContentsEList<>(this, eStructuralFeatures);
    }
  }
}
