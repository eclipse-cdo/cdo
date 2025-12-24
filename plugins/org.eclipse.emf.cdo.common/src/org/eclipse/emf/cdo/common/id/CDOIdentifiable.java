/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.id;

/**
 * An identifiable object.
 *
 * @author Eike Stepper
 * @see CDOIDUtil#getCDOID(Object)
 * @since 4.3
 */
public interface CDOIdentifiable
{
  public CDOID getID();
}
