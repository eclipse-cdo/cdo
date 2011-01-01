/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
