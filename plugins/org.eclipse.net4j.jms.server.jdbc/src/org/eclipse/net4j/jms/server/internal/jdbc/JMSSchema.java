/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server.internal.jdbc;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.db.ddl.IDBIndex;
import org.eclipse.net4j.db.ddl.IDBTable;
import org.eclipse.net4j.spi.db.DBSchema;

/**
 * @author Eike Stepper
 */
public class JMSSchema extends DBSchema
{
  public static final JMSSchema INSTANCE = new JMSSchema();

  /**
   * DBTable Destinations
   */
  public static final IDBTable DESTINATIONS = INSTANCE.addTable("destinations");

  public static final IDBField DESTINATIONS_NAME = //
  DESTINATIONS.addField("name", DBType.VARCHAR, 255);

  public static final IDBField DESTINATIONS_TYPE = //
  DESTINATIONS.addField("type", DBType.INTEGER);

  public static final IDBIndex INDEX_DESTINATIONS_PK = //
  DESTINATIONS.addIndex(IDBIndex.Type.PRIMARY_KEY, DESTINATIONS_NAME);

  /**
   * DBTable Messages
   */
  public static final IDBTable MESSAGES = INSTANCE.addTable("messages");

  public static final IDBField MESSAGES_ID = //
  MESSAGES.addField("id", DBType.VARCHAR);

  public static final IDBField MESSAGES_DESTINATION = //
  MESSAGES.addField("destination", DBType.VARCHAR);

  public static final IDBField MESSAGES_PRIORITY = //
  MESSAGES.addField("priority", DBType.INTEGER);

  public static final IDBIndex INDEX_MESSAGES_PK = //
  MESSAGES.addIndex(IDBIndex.Type.PRIMARY_KEY, MESSAGES_ID);

  /**
   * Queries
   */
  public static final String QUERY = "SELECT " + DESTINATIONS_TYPE + ", " + MESSAGES_ID + ", " + MESSAGES_PRIORITY
      + " FROM " + DESTINATIONS + ", " + MESSAGES + " WHERE " + DESTINATIONS_NAME + "=" + MESSAGES_DESTINATION
      + " AND " + DESTINATIONS_NAME + "=?";

  private JMSSchema()
  {
    super("JMS");
  }

  static
  {
    INSTANCE.lock();
  }
}
