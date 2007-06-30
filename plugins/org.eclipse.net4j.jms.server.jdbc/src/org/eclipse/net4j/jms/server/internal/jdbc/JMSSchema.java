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
package org.eclipse.net4j.jms.server.internal.jdbc;

import org.eclipse.net4j.db.IField;
import org.eclipse.net4j.db.IIndex;
import org.eclipse.net4j.db.ITable;
import org.eclipse.net4j.internal.db.Schema;

/**
 * @author Eike Stepper
 */
public class JMSSchema extends Schema
{
  public static final JMSSchema INSTANCE = new JMSSchema();

  /**
   * Table Destinations
   */
  public static final ITable DESTINATIONS = INSTANCE.addTable("destinations");

  public static final IField DESTINATIONS_NAME = //
  DESTINATIONS.addField("name", IField.Type.VARCHAR, 255);

  public static final IField DESTINATIONS_TYPE = //
  DESTINATIONS.addField("type", IField.Type.INTEGER);

  public static final IIndex INDEX_DESTINATIONS_PK = //
  DESTINATIONS.addIndex(IIndex.Type.PRIMARY_KEY, DESTINATIONS_NAME);

  /**
   * Table Messages
   */
  public static final ITable MESSAGES = INSTANCE.addTable("messages");

  public static final IField MESSAGES_ID = //
  MESSAGES.addField("id", IField.Type.VARCHAR);

  public static final IField MESSAGES_DESTINATION = //
  MESSAGES.addField("destination", IField.Type.VARCHAR);

  public static final IField MESSAGES_PRIORITY = //
  MESSAGES.addField("priority", IField.Type.INTEGER);

  public static final IIndex INDEX_MESSAGES_PK = //
  MESSAGES.addIndex(IIndex.Type.PRIMARY_KEY, MESSAGES_ID);

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
