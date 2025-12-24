/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.genmodel.impl;

import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreEMap;

/**
 * @author Eike Stepper
 */
public class GenAnnotationImpl extends org.eclipse.emf.codegen.ecore.genmodel.impl.GenAnnotationImpl
{
  protected GenAnnotationImpl()
  {
  }

  @Override
  public EMap<String, String> getDetails()
  {
    if (details == null)
    {
      // The super class implementation would pass EMF's EStringToStringMapEntryImpl,
      // but that's not compatible with CDO's replacement. So pass in CDO's version of it.
      details = new EcoreEMap<>(EcorePackage.Literals.ESTRING_TO_STRING_MAP_ENTRY, org.eclipse.emf.cdo.ecore.impl.EStringToStringMapEntryImpl.class, this,
          GenModelPackage.GEN_ANNOTATION__DETAILS);
    }

    return details;
  }
}
