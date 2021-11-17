/*
 * Copyright (c) 2018, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.impl;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;

import java.util.Map;

/**
 * @author Eike Stepper
 *
 */
public class CDOEcoreFactoryImpl extends EcoreFactoryImpl
{
  public CDOEcoreFactoryImpl()
  {
  }

  @Override
  public EAnnotation createEAnnotation()
  {
    return new EAnnotationImpl();
  }

  @Override
  public Map.Entry<String, String> createEStringToStringMapEntry()
  {
    return new EStringToStringMapEntryImpl();
  }
}
