/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
 **************************************************************************/
package org.eclipse.emf.cdo.common;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
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

  public static final short SIGNAL_RESOURCE_PATH = 5;

  public static final short SIGNAL_LOAD_PACKAGE = 6;

  public static final short SIGNAL_LOAD_REVISION = 7;

  public static final short SIGNAL_LOAD_REVISION_BY_TIME = 8;

  public static final short SIGNAL_LOAD_REVISION_BY_VERSION = 9;

  public static final short SIGNAL_LOAD_CHUNK = 10;

  public static final short SIGNAL_VERIFY_REVISION = 11;

  public static final short SIGNAL_COMMIT_TRANSACTION = 12;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE1 = 13;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE2 = 14;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_COMMIT_TRANSACTION_PHASE3 = 15;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_COMMIT_TRANSACTION_CANCEL = 16;

  public static final short SIGNAL_INVALIDATION = 17;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_QUERY = 18;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_QUERY_CANCEL = 19;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_SYNC = 20;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_PASSIVE_UPDATE = 21;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_CHANGE_SUBSCRIPTION = 22;

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

  /**
   * @since 2.0
   */
  public static final byte QUERY_MORE_OBJECT = 1;

  /**
   * @since 2.0
   */
  public static final byte QUERY_DONE = 2;

  /**
   * @since 2.0
   */
  public static final byte QUERY_EXCEPTION = 3;

  /**
   * @since 2.0
   */
  public static final String QUERY_LANGUAGE_RESOURCES = "resources";
}
