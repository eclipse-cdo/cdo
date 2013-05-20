/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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
public class DB4OClob extends DB4OWrappedValue<char[]>
{
  public DB4OClob(String id, char[] value)
  {
    super(id, value);
  }

}
