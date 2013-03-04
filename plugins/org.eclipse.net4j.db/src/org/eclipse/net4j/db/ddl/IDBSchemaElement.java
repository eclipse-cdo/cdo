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
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.IDBNamedElement;

/**
 * Specifies a hierachical namespace for elements in a {@link IDBSchema DB schema}.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDBSchemaElement extends IDBNamedElement
{
  public IDBSchema getSchema();

  /**
   * @since 4.2
   */
  @Deprecated
  public void setName(String name);

  public String getFullName();

  /**
   * @since 4.2
   */
  public void remove();
}
