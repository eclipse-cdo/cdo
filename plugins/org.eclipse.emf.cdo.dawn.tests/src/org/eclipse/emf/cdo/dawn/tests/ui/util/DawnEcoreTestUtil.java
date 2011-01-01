/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.emf.cdo.dawn.appearance.DawnAppearancer;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;

import org.eclipse.emf.common.util.URI;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreTestUtil
{
  public static final String CREATION_WIZARD_NAME_GMF = "Dawn Ecore Diagram";

  public static final String CREATION_WIZARD_NAME_EMF = "Dawn Ecore Model";

  public static SWTBotGefEditor openNewEcoreGMFEditor(String diagramResourceName, SWTGefBot bot)
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(CREATION_WIZARD_NAME_GMF);
    bot.button("Next >").click();
    bot.button("Finish").click();
    SWTBotGefEditor editor = bot.gefEditor(diagramResourceName);
    return editor;
  }

  public static DawnSWTBotEMFEditor openEcoreEMFEditor(URI resourceURI, DawnEMFEditorBot bot)
  {
    String resourceName = resourceURI.lastSegment();
    final String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resourceName);

    final DawnEditorInput editorInput = new DawnEditorInput(resourceURI);

    UIThreadRunnable.asyncExec(new VoidResult()
    {
      public void run()
      {
        try
        {
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getPage()
              .openEditor(editorInput, editorID);
        }
        catch (PartInitException ex)
        {
          throw new RuntimeException(ex);
        }
      }
    });

    return bot.emfEditor(resourceName);
  }

  public static DawnSWTBotEMFEditor openNewEcoreEMFEditor(String resourceName, DawnEMFEditorBot bot)
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_EMF);
    bot.button("Next >").click();

    shell = bot.shell("New");
    shell.activate();

    SWTBotText fileSemanticNameLabel = bot.textWithLabel("File name:");
    fileSemanticNameLabel.setText(resourceName);

    bot.button("Next >").click();

    SWTBotCombo comboBox = bot.comboBox(0);// bot.ccomboBox(0);
    comboBox.setFocus();
    comboBox.setSelection("EPackage");

    bot.button("Finish").click();

    return bot.emfEditor(resourceName);
  }

  // public static List<SWTBotGefEditPart> getEClassEditParts(SWTBotGefEditor editor)
  // {
  // List<SWTBotGefEditPart> editParts = editor.editParts(new AbstractMatcher<EClassEditPart>()
  // {
  // @Override
  // protected boolean doMatch(Object item)
  // {
  // return item instanceof AClassEditPart;
  // }
  //
  // public void describeTo(Description description)
  // {
  // }
  // });
  // return editParts;
  // }

  public static void sleep(int seconds)
  {
    try
    {
      Thread.sleep(seconds);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static boolean showsConflict(EditPart editPart)
  {
    if (editPart instanceof AClassEditPart || editPart instanceof AInterfaceEditPart)
    {
      GraphicalEditPart e = (GraphicalEditPart)editPart;

      IFigure figure = e.getFigure();
      return ((LineBorder)figure.getBorder()).getColor().equals(DawnAppearancer.COLOR_DELETE_CONFLICT);
    }
    return false;
  }
}
