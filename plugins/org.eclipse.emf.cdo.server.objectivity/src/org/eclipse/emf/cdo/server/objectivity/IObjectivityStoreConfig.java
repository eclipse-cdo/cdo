/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ibrahim Sallam - initial API and implementation
 */
package org.eclipse.emf.cdo.server.objectivity;

import java.io.Serializable;

/**
 * Encapsulates Objectivity-specific back-end configuration settings.
 *
 * @author Ibrahim Sallam
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IObjectivityStoreConfig extends Serializable
{
  String getFdName();

  void resetFD();

  int getSessionMinCacheSize();

  int getSessionMaxCacheSize();

  /**
   * @since 4.0
   */
  String getLogPath();

  /**
   * @since 4.0
   */
  int getLogOption();
}
