/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.common.id;

/**
 * @author Simon McDuff
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOIDExternal extends CDOID
{
  public String getURI();
}
