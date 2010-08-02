/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

/**
 * @author Martin Fluegge
 */
public class DawnSWTBotUtil
{

  public static void initTest(SWTWorkbenchBot bot)
  {
    closeWelcomePage(bot);
  }

  public static void closeWelcomePage(SWTWorkbenchBot bot)
  {
    try
    {
      bot.viewByTitle("Welcome").close();
    }
    catch (WidgetNotFoundException ex)
    {
      // We can ignore this because it it thrown when the widget cannot be found which can be the case if another test
      // already closed the welcome screen.
    }
  }
}
