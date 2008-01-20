/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.internal.protocol.revision.InternalCDORevision;

import org.eclipse.net4j.db.ddl.IDBField;

import java.sql.ResultSet;

/**
 * @author Eike Stepper
 */
public interface IAttributeMapping extends IFeatureMapping
{
  public IDBField getField();

  public void appendValue(StringBuilder builder, InternalCDORevision revision);

  public void extractValue(ResultSet resultSet, int column, InternalCDORevision revision);
}
