/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl.delta;

import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.util.container.IContainer;

import java.util.Map;

/**
 * @since 4.2
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDBTableDelta extends IDBDelta, IContainer<IDBTableElementDelta>, Comparable<IDBTableDelta>
{
  public IDBSchemaDelta getParent();

  public IDBFieldDelta[] getFieldDeltasSortedByPosition();

  public Map<String, IDBFieldDelta> getFieldDeltas();

  public Map<String, IDBIndexDelta> getIndexDeltas();

  public IDBTable getElement(IDBSchema schema);
}
