/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.protocol.model.core.CDOObjectClass;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * @author Eike Stepper
 */
public class RootMapping extends Mapping
{
  public RootMapping(VerticalMappingStrategy mappingStrategy)
  {
    super(mappingStrategy, getRootClass(mappingStrategy));
    mappingStrategy.initTable(getTable(), true);
  }

  @Override
  public VerticalMappingStrategy getMappingStrategy()
  {
    return (VerticalMappingStrategy)super.getMappingStrategy();
  }

  public void writeRevision(IDBStoreAccessor storeAccessor, CDORevisionImpl revision)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("INSERT INTO ");
    builder.append(getTable());
    builder.append(" VALUES (");
    appendRevisionInfo(builder, revision, true);
    builder.append(")");
    executeSQL(storeAccessor, builder.toString());
  }

  private static CDOObjectClass getRootClass(VerticalMappingStrategy mappingStrategy)
  {
    return mappingStrategy.getStore().getRepository().getPackageManager().getCDOCorePackage().getCDOObjectClass();
  }
}
