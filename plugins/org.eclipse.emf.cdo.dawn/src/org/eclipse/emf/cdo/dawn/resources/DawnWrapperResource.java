/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
