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
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.emf.cdo.dawn.tests.ui.util.DawnSWTBotUtil;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.helper.EditorDescriptionHelper;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.ui.AbstractCDOUITest;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import org.junit.Before;

/**
 * @author Martin Fluegge
 */
public abstract class AbstractDawnUITest<T extends SWTWorkbenchBot> extends AbstractCDOUITest<T>
{
  @Override
  @Before
  public void setUp() throws Exception
  {
    super.setUp();
    SWTBotPreferences.SCREENSHOTS_DIR = DawnTestPlatform.instance.getTestFolder();
    resetWorkbench();
    DawnSWTBotUtil.initTest(getBot());
    getBot().viewByTitle("CDO Sessions").close();
  }

  /**
   * This method opens a DawnDiagramEditor specified by the given URI. It automatically finds the right editor by
   * matching the "file extension".
   */
  protected void openEditor(final String resourcePath)
  {
    UIThreadRunnable.syncExec(new VoidResult()
    {
      public void run()
      {
        CDOSession session = openSession();
        CDOView view = session.openView();

        CDOResource resource = view.getResource(resourcePath);

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null)
        {
          IWorkbenchPage page = window.getActivePage();
          String editorID = EditorDescriptionHelper.getEditorIdForDawnEditor(resource.getName());

          try
          {
            DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());

            page.openEditor(editorInput, editorID);
          }
          catch (PartInitException e)
          {
            e.printStackTrace();
          }
        }
      }
    });
  }

  protected boolean resourceExists(String resourcePath)
  {
    CDOSession session = openSession();
    CDOView view = session.openView();

    CDOResource resource = view.getResource(resourcePath);

    return resource != null ? true : false;
  }

  protected void createNode(String type, int xPosition, int yPosition, SWTGefBot bot, SWTBotGefEditor editor)
  {
    editor.activateTool(type);
    editor.click(xPosition, yPosition);
  }

  protected void createNodeWithLabel(String type, int xPosition, int yPosition, String labelText, SWTGefBot bot,
      SWTBotGefEditor editor)
  {
    createNode(type, xPosition, yPosition, bot, editor);
    typeTextToFocusedWidget(labelText, bot, true);
  }

  protected void createEdge(String type, int fromXPosition, int fromYPosition, int toXPosition, int toYPosition,
      SWTBotGefEditor editor)
  {
    editor.activateTool(type);
    editor.drag(fromXPosition, fromYPosition, toXPosition, toYPosition);
  }

  protected void createEdge(String type, Node nodeFrom, Node nodeTo, SWTBotGefEditor editor)
  {
    Bounds boundsA = (Bounds)nodeFrom.getLayoutConstraint();
    Bounds boundsB = (Bounds)nodeTo.getLayoutConstraint();
    createEdge(type, boundsA.getX(), boundsA.getY(), boundsB.getX(), boundsB.getY(), editor);
  }
}
