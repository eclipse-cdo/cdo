/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.operation;

/**
 * @author Eike Stepper
 */
public interface IDebugOptions
{
  public boolean isDebugging();

  public void setDebugging(boolean debugging);

  public String getOption(String option);

  public String getOption(String option, String defaultValue);

  public boolean getBooleanOption(String option, boolean defaultValue);

  public int getIntegerOption(String option, int defaultValue);

  public void setOption(String option, String value);

  public void dispose();
}
