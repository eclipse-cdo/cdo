/*******************************************************************************
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.resources.impl;

import org.eclipse.emf.cdo.dawn.resources.DawnResourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceFactoryImpl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Martin Fluegge
 */
public class DawnResourceFactoryImpl extends CDOResourceFactoryImpl implements DawnResourceFactory
{
  @Override
  public Resource createResource(URI uri)
  {
    uri = URI.createURI(uri.toString().replace("dawn:", "cdo:"));
    // String path = CDOURIUtil.extractResourcePath(uri);
    DawnWrapperResourceImpl resource = new DawnWrapperResourceImpl(uri);
    // resource.setRoot(CDOURIUtil.SEGMENT_SEPARATOR.equals(path));
    resource.setExisting(isGetResource());
    return resource;
  }
}
