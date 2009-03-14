/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/230832    
 *    Simon McDuff - http://bugs.eclipse.org/233490    
 *    Simon McDuff - http://bugs.eclipse.org/213402
 */
package org.eclipse.emf.cdo.common.protocol;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOProtocolConstants
{
  public static final String PROTOCOL_NAME = "cdo";

  // //////////////////////////////////////////////////////////////////////
  // Signal IDs

  public static final short SIGNAL_OPEN_SESSION = 1;

  public static final short SIGNAL_LOAD_LIBRARIES = 2;

  public static final short SIGNAL_VIEWS_CHANGED = 3;

  public static final short SIGNAL_RESOURCE_ID = 4;

  public static final short SIGNAL_LOAD_PACKAGES = 6;

  public static final short SIGNAL_LOAD_REVISION = 7;

  public static final short SIGNAL_LOAD_REVISION_BY_TIME = 8;

  public static final short SIGNAL_LOAD_REVISION_BY_VERSION = 9;

  public static final short SIGNAL_LOAD_CHUNK = 10;

  public static final short SIGNAL_VERIFY_REVISION = 11;

  public static final short SIGNAL_COMMIT_TRANSACTION = 12;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE1 = 13;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE2 = 14;

  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE3 = 15;

  public static final short SIGNAL_COMMIT_TRANSACTION_CANCEL = 16;

  public static final short SIGNAL_COMMIT_NOTIFICATION = 17;

  public static final short SIGNAL_QUERY = 18;

  public static final short SIGNAL_QUERY_CANCEL = 19;

  public static final short SIGNAL_SYNC_REVISIONS = 20;

  public static final short SIGNAL_PASSIVE_UPDATE = 21;

  public static final short SIGNAL_CHANGE_SUBSCRIPTION = 22;

  public static final short SIGNAL_SET_AUDIT = 23;

  public static final short SIGNAL_REPOSITORY_TIME = 24;

  public static final short SIGNAL_LOCK_OBJECTS = 25;

  public static final short SIGNAL_UNLOCK_OBJECTS = 26;

  public static final short SIGNAL_OBJECT_LOCKED = 27;

  public static final short SIGNAL_GET_REMOTE_SESSIONS = 28;

  public static final short SIGNAL_UNSUBSCRIBE_REMOTE_SESSIONS = 29;

  public static final short SIGNAL_REMOTE_SESSION_NOTIFICATION = 30;

  // //////////////////////////////////////////////////////////////////////
  // Session Management

  public static final int ERROR_REPOSITORY_NOT_FOUND = -1;

  public static final int ERROR_NO_SESSION = -2;

  // //////////////////////////////////////////////////////////////////////
  // View Management

  public static final byte VIEW_TRANSACTION = 1;

  public static final byte VIEW_AUDIT = 2;

  public static final byte VIEW_READONLY = 3;

  public static final byte VIEW_CLOSED = 4;

  // //////////////////////////////////////////////////////////////////////
  // Query Support

  public static final String QUERY_LANGUAGE_RESOURCES = "resources";

  public static final String QUERY_LANGUAGE_RESOURCES_FOLDER_ID = "folder";

  public static final String QUERY_LANGUAGE_RESOURCES_EXACT_MATCH = "exactMatch";

  // //////////////////////////////////////////////////////////////////////
  // Locking Objects

  public static final int RELEASE_ALL_LOCKS = -1;

  // //////////////////////////////////////////////////////////////////////
  // Remote Sessions

  public static final int NO_MORE_REMOTE_SESSIONS = -1;

  public static final byte REMOTE_SESSION_OPENED = 1;

  public static final byte REMOTE_SESSION_CLOSED = 2;

  public static final byte REMOTE_SESSION_SUBSCRIBED = 3;

  public static final byte REMOTE_SESSION_UNSUBSCRIBED = 4;
}
