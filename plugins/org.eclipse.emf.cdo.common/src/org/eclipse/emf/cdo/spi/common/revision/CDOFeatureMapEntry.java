/*
 * Copyright (c) 2009, 2011, 2012, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - derived from CDOFeatureMapEntryImpl
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @since 3.0
 * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
 * @author Martin Taal
 */
@Deprecated
public interface CDOFeatureMapEntry extends org.eclipse.emf.ecore.util.FeatureMap.Entry
{
  @Deprecated
  public void setEStructuralFeature(EStructuralFeature feature);

  @Deprecated
  public void setValue(Object value);
}
