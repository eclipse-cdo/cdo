/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259402
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.db.ddl.IDBField;

import java.sql.ResultSet;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface IAttributeMapping extends IFeatureMapping
{
  public IDBField getField();

  public void appendValue(StringBuilder builder, CDORevision revision);

  public void appendValue(StringBuilder builder, Object value);

  public void extractValue(ResultSet resultSet, int column, InternalCDORevision revision);

  public Object getRevisionValue(InternalCDORevision revision);
}
