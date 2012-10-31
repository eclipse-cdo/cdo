/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.model;

import org.eclipse.emf.cdo.common.model.CDOClassInfo;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @since 4.2
 */
public interface InternalCDOClassInfo extends CDOClassInfo
{
  /**
   * Obtains a rule that filters/transforms the persist values of the given
   * {@code feature}.
   *
   * @param feature a feature to be persisted
   * @return a persistence filter rule for the {@code feature}, or {@code null}
   *    if it has no filter but just follows the default persistence rules
   */
  public PersistenceFilter getPersistenceFilter(EStructuralFeature feature);

  /**
   * Encapsulation of a rule for filtering the persistent values
   * of a {@linkplain EStructuralFeature feature} in some model element.
   * Some models (such as UML's Activity metaclass) require partial persistence
   * of features, persisting a subset of the values in a feature that are also
   * in some other feature (the filtering feature). Other models may apply other
   * transformations to features that require partial or otherwise custom
   * persistence rules.
   */
  public interface PersistenceFilter
  {
    /**
     * Get a subset or other transformation of the specified {@code value} of
     * a persistable feature, based on its dependencies.
     *
     * @param owner an object this filter is to be applied to.
     * @param value the value (which may be a collection) of the {@code owner}'s feature.
     *
     * @return the transformed value to persist. It should be a collection (possibly empty)
     * if the {@code value} is a collection, or a scalar (possibly {@code null}) if the
     * {@code value} is a scalar
     */
    public Object getPersistableValue(EObject owner, Object value);
  }
}
