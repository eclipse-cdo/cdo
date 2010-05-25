/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper            - maintenance
 */
package org.eclipse.emf.cdo.internal.common.model;

import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public interface InternalExtendedCDOPackageRegistry extends InternalCDOPackageRegistry
{
  public Object getWithDelegation(String nsURI, boolean resolve);

  public Set<String> getAllKeys();
}
