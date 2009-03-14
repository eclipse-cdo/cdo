/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db.mapping;

import org.eclipse.net4j.db.IDBAdapter;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class FeatureMapping
{
  private ClassMapping classMapping;

  private EStructuralFeature feature;

  public FeatureMapping(ClassMapping classMapping, EStructuralFeature feature)
  {
    this.classMapping = classMapping;
    this.feature = feature;
  }

  public ClassMapping getClassMapping()
  {
    return classMapping;
  }

  public EStructuralFeature getFeature()
  {
    return feature;
  }

  protected IDBAdapter getDBAdapter()
  {
    return getClassMapping().getMappingStrategy().getStore().getDBAdapter();
  }
}
