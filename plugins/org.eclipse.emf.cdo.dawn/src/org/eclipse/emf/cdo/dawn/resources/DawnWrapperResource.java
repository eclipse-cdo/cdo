/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.resources;

import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.ecore.xmi.XMLResource;

/**
 * @author Martin Fluegge
 * @deprecated As of 4.4 use {@link CDOResource} directly.
 */
@Deprecated
public interface DawnWrapperResource extends CDOResource, XMLResource
{

}
