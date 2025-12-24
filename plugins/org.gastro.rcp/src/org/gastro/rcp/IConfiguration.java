/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.rcp;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public interface IConfiguration
{
  public static final IConfiguration INSTANCE = org.gastro.internal.rcp.Configuration.INSTANCE;

  public String getPerspective();

  public String getStation();

  public String getServer();

  public String getRepository();

  public String getRestaurant();

  public Date getBusinessDay();
}
