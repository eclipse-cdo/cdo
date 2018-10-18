/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.model.CDOPackageUnit;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.List;

/**
 * Interface to complement {@link IMappingStrategy}.
 *
 * @author Eike Stepper
 * @since 4.7
 */
public interface IMappingStrategy3 extends IMappingStrategy
{
  public INamingStrategy getNamingStrategy();

  public void setNamingStrategy(INamingStrategy namingStrategy);

  public String getUnsettableFieldName(EStructuralFeature feature);

  public boolean isMapped(EClass eClass);

  public List<EClass> getMappedClasses(CDOPackageUnit[] packageUnits);

  public IClassMapping createClassMapping(EClass eClass);

  public void clearClassMappings();
}
