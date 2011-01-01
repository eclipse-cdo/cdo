/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OBlob extends DB4OWrappedValue<byte[]>
{
  public DB4OBlob(String id, byte[] value)
  {
    super(id, value);
  }
}
