/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Visits {@link CDORevisionData#get(org.eclipse.emf.ecore.EStructuralFeature, int) values} of a {@link CDORevisionData revision}.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public interface CDORevisionValueVisitor
{
  public void visit(EStructuralFeature feature, Object value, int index);
}
