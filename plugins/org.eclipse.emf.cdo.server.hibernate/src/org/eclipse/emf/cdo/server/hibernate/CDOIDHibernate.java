/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - additional api
 **************************************************************************/
package org.eclipse.emf.cdo.server.hibernate;

import org.eclipse.emf.cdo.protocol.id.CDOIDObject;

import java.io.Serializable;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public interface CDOIDHibernate extends CDOIDObject
{

  public Serializable getId();

  public void setId(Serializable id);

  public String getEntityName();

  public void setEntityName(String entityName);

  public void setContent(byte[] content);

  public byte[] getContent();

}
