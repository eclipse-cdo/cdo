/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.resources.impl;

import org.eclipse.emf.cdo.dawn.resources.DawnResourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceFactoryImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;

import org.eclipse.emf.common.util.URI;

/**
 * @author Martin Fluegge
 * @deprecated As of 4.4 use {@link CDOResourceFactoryImpl} directly.
 */
@Deprecated
public class DawnResourceFactoryImpl extends CDOResourceFactoryImpl implements DawnResourceFactory
{
  @Deprecated
  @Override
  protected CDOResourceImpl createCDOResource(URI uri)
  {
    uri = URI.createURI(uri.toString().replace("dawn:", "cdo:"));
    // return new DawnWrapperResourceImpl(uri);

    return new CDOResourceImpl(uri);
  }
}
