/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Creates extents of {@link EClass classes}.
 * <p>
 * The {@link #createExtent(EClass, AtomicBoolean) extent} of a {@link EClass class} X is the set of all {@link EObject objects} with <code>object.getEClass() == X</code>.
 *
 * @author Eike Stepper
 */
public interface OCLExtentCreator
{
  public Set<EObject> createExtent(EClass cls, AtomicBoolean canceled);
}
