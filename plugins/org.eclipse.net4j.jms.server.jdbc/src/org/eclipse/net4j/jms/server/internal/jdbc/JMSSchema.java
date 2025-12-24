/*
 * Copyright (c) 2007-2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server.internal.jdbc;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBSchema;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.ddl.InternalDBSchema;

/**
 * @author Eike Stepper
 */
public class JMSSchema
{
  public static final IDBSchema INSTANCE = DBUtil.createSchema("JMS");

  /**
   * DBTable Destinations
   */
  public static final IDBTable DESTINATIONS = INSTANCE.addTable("destinations"); //$NON-NLS-1$

  public static final IDBField DESTINATIONS_NAME = //
      DESTINATIONS.addField("name", DBType.VARCHAR, 255); //$NON-NLS-1$

  public static final IDBField DESTINATIONS_TYPE = //
      DESTINATIONS.addField("type", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBIndex INDEX_DESTINATIONS_PK = //
      DESTINATIONS.addIndex(IDBIndex.Type.PRIMARY_KEY, DESTINATIONS_NAME);

  /**
   * DBTable Messages
   */
  public static final IDBTable MESSAGES = INSTANCE.addTable("messages"); //$NON-NLS-1$

  public static final IDBField MESSAGES_ID = //
      MESSAGES.addField("id", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBField MESSAGES_DESTINATION = //
      MESSAGES.addField("destination", DBType.VARCHAR); //$NON-NLS-1$

  public static final IDBField MESSAGES_PRIORITY = //
      MESSAGES.addField("priority", DBType.INTEGER); //$NON-NLS-1$

  public static final IDBIndex INDEX_MESSAGES_PK = //
      MESSAGES.addIndex(IDBIndex.Type.PRIMARY_KEY, MESSAGES_ID);

  /**
   * Queries
   */
  public static final String QUERY = "SELECT " + DESTINATIONS_TYPE + ", " + MESSAGES_ID + ", " + MESSAGES_PRIORITY //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      + " FROM " + DESTINATIONS + ", " + MESSAGES + " WHERE " + DESTINATIONS_NAME + "=" + MESSAGES_DESTINATION //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      + " AND " + DESTINATIONS_NAME + "=?"; //$NON-NLS-1$ //$NON-NLS-2$

  static
  {
    ((InternalDBSchema)INSTANCE).lock();
  }
}
