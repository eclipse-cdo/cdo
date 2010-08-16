/**
 * Copyright (c) 2010 Martin Fluegge (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreTestUtil
{
  public static final String A_CLASS = "AClass";

  public static final String A_INTERFACE = "AInterface";

  public static final String A_ATTRIBUTE = "AAttribute";

  public static final String A_OPERATION = "AOperation";

  public static final String CONNECTION_IHERITS = "inherits";

  public static final String CONNECTION_IMPLEMENTS = "implements";

  public static final String CONNECTION_ASSOCIATION = "association";

  public static final String CONNECTION_AGGREGATION = "aggregation";

  public static final String CONNECTION_COMPOSITION = "composition";

  public static SWTBotGefEditor openNewAcoreEditor(String diagramResourceName, SWTGefBot bot)
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select("Dawn Acore Diagram");
    bot.button("Next >").click();
    bot.button("Finish").click();

    SWTBotGefEditor editor = bot.gefEditor(diagramResourceName);
    return editor;
  }
}
