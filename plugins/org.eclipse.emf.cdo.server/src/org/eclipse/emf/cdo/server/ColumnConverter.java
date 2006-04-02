/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;


import org.eclipse.net4j.core.Channel;
import org.eclipse.net4j.spring.Service;


public interface ColumnConverter extends Service
{
  /**
   * @param channel
   * @return
   */
  public Object fromChannel(Channel channel, int dataType);

  /**
   * 
   * @param channel
   * @param value
   */
  public void toChannel(Channel channel, int dataType, Object value);
}
