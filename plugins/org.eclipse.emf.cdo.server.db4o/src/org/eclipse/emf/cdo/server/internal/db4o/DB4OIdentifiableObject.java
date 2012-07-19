/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.server.db4o.IDB4OIdentifiableObject;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OIdentifiableObject implements IDB4OIdentifiableObject
{
  private String id;

  public String getId()
  {
    return id;
  }

  protected void setId(String id)
  {
    this.id = id;
  }
}
