/*
 * Copyright (c) 2007, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.ecore.EPackage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IPackageClosure
{
  public static final Set<EPackage> EMPTY_CLOSURE = Collections.emptySet();

  public Set<EPackage> calculate(Collection<EPackage> ePackages);
}
