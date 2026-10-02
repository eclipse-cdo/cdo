/*
 * Copyright (c) 2009-2012, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.id;

import org.eclipse.emf.cdo.common.id.CDOIDObject;

/**
 * Internal extension of an object ID that distinguishes the concrete storage subtype used for that identifier.
 * Revision and ID factories use this subtype when selecting the appropriate object-ID representation.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOIDObject extends CDOIDObject
{
  public ObjectType getSubType();
}
