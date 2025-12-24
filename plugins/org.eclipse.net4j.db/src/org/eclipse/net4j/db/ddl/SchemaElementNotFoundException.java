/*
 * Copyright (c) 2013, 2016, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.ddl;

import org.eclipse.net4j.db.DBException;

/**
 * @since 4.2
 * @author Eike Stepper
 */
public class SchemaElementNotFoundException extends DBException
{
  private static final long serialVersionUID = 1L;

  private final IDBSchemaElement parent;

  private final IDBSchemaElement.SchemaElementType type;

  private final String name;

  public SchemaElementNotFoundException(IDBSchemaElement parent, IDBSchemaElement.SchemaElementType type, String name)
  {
    super(type.toString() + " " + name + " not found in " + parent.getSchemaElementType() + " " + parent);
    this.parent = parent;
    this.type = type;
    this.name = name;
  }

  public IDBSchemaElement getParent()
  {
    return parent;
  }

  public IDBSchemaElement.SchemaElementType getType()
  {
    return type;
  }

  public String getName()
  {
    return name;
  }
}
