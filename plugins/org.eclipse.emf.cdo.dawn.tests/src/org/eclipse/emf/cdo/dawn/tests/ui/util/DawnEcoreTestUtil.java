/*
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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.ui.DawnColorConstants;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecoretools.diagram.edit.parts.EClassEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnEcoreTestUtil
{
  public static final String CREATION_WIZARD_NAME_GMF = "Dawn Ecore Diagram";

  public static final String CREATION_WIZARD_NAME_EMF = "Dawn Ecore Model";

  public static final String E_CLASS = "EClass";

  public static final String E_REFERENCE = "EReference";

  public static final String E_ATTRIBUTE = "EAttribute";

  public static final String E_OPERATION = "EOperation";

  public static final String INHERITANCE = "Inheritance";

  public static final String E_DATATYPE = "EDataType";

  public static final String E_PACKAGE = "EPackage";

  public static final String E_ANNOTATION = "EAnnotation";

  public static final String E_ENUM = "EEnum";

  public static final String E_ANNOTATION_LINK = "EAnnotation link";

  public static final String DETAILS_ENTRY = "Details Entry";

  private static String resourceFieldLabel = org.eclipse.emf.cdo.dawn.ui.messages.Messages.DawnCreateNewResourceWizardPage_6;

  public static SWTBotGefEditor openNewEcoreToolsEditor(String diagramResourceName, SWTGefBot bot)
  {
    bot.menu("File").menu("New").menu("Other...").click();

    SWTBotShell shell = bot.shell("New");
    shell.activate();
    bot.tree().expandNode("Dawn Examples").select(DawnEcoreTestUtil.CREATION_WIZARD_NAME_GMF);
    bot.button("Next >").click();
    bot.button("Finish").click();
    SWTBotGefEditor editor = bot.gefEditor("default");
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

    SWTBotText fileSemanticNameLabel = bot.textWithLabel(resourceFieldLabel);
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
      return ((LineBorder)figure.getBorder()).getColor().equals(DawnColorConstants.COLOR_DELETE_CONFLICT);
    }
    return false;
  }

  public static List<SWTBotGefEditPart> getAllConnections(final SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> aClassEditParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        if (item instanceof EClassEditPart)
        {
          return true;
        }
        return false;
      }

      public void describeTo(Description description)
      {
      }
    });

    List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();
    for (SWTBotGefEditPart editPart : aClassEditParts)
    {
      ret.addAll(editPart.sourceConnections());
    }

    for (SWTBotGefEditPart editPart : aClassEditParts)
    {
      ret.addAll(editPart.targetConnections());
    }

    return ret;
  }

  public static List<SWTBotGefEditPart> getAllSourceConnections(final SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> aClassEditParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        if (item instanceof EClassEditPart)
        {
          return true;
        }
        return false;
      }

      public void describeTo(Description description)
      {
      }
    });

    List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();
    for (SWTBotGefEditPart editPart : aClassEditParts)
    {
      ret.addAll(editPart.sourceConnections());
    }

    return ret;
  }

  public static List<SWTBotGefEditPart> getAllTargetConnections(final SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> aClassEditParts = editor.editParts(new AbstractMatcher<AClassEditPart>()
    {
      @Override
      protected boolean doMatch(Object item)
      {
        if (item instanceof EClassEditPart)
        {
          return true;
        }
        return false;
      }

      public void describeTo(Description description)
      {
      }
    });

    List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();

    for (SWTBotGefEditPart editPart : aClassEditParts)
    {
      ret.addAll(editPart.targetConnections());
    }

    return ret;
  }
}
