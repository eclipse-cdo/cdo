/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withMnemonic;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.gmf.synchronize.DawnConflictHelper;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.providers.IViewProvider;
import org.eclipse.gmf.runtime.diagram.core.services.ViewService;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Martin Fluegge
 */
public class DawnSWTBotUtil
{
  private static final String LABEL_OK = "OK";

  private static final String LABEL_OTHERS = "Other...";

  private static final String LABEL_WINDOW = "Window";

  private static final String LABEL_SHOW_VIEW = "Show View";

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

  public static void setConnectorType(SWTWorkbenchBot bot, String serverName, String serverPort, String repository,
      String protocol)
  {
    bot.menu(LABEL_WINDOW).menu("Preferences").click();
    SWTBotShell shell = bot.shell("Preferences");
    shell.activate();

    bot.tree().select("Dawn Remote Preferences");

    SWTBotText serverNameLabel = bot.textWithLabel("Server Name:");
    SWTBotText serverPortLabel = bot.textWithLabel("Server Port:");
    SWTBotText repositoryLabel = bot.textWithLabel("Repository:");
    SWTBotText fileNameLabel = bot.textWithLabel("Protocol:");

    serverNameLabel.setText(serverName);
    serverPortLabel.setText(serverPort);
    repositoryLabel.setText(repository);
    fileNameLabel.setText(protocol);
    bot.button(LABEL_OK).click();
  }

  public static List<SWTBotGefEditPart> getAllEditParts(SWTBotGefEditor editor)
  {
    List<SWTBotGefEditPart> editParts = editor.editParts(new AbstractMatcher<EditPart>()
        {
      @Override
      protected boolean doMatch(Object item)
      {
        return item instanceof AClassEditPart;
      }

      public void describeTo(Description description)
      {
      }
        });
    return editParts;
  }

  public static List<SWTBotGefEditPart> getConnectionEditParts(SWTBotGefEditor editor, Class<? extends EditPart> clazz)
  {
    List<SWTBotGefEditPart> editParts = getAllEditParts(editor);
    return getConnectionEditParts(editor, clazz, editParts);
  }

  public static List<SWTBotGefEditPart> getConnectionEditParts(SWTBotGefEditor editor, Class<? extends EditPart> clazz,
      List<SWTBotGefEditPart> editParts)
      {

    List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();
    for (SWTBotGefEditPart editPart : editParts)
    {

      for (SWTBotGefConnectionEditPart sourceConnection : editPart.sourceConnections())
      {
        if (clazz.isInstance(sourceConnection.part()))
        {
          ret.add(sourceConnection);
        }
      }
    }
    return ret;
      }

  static Object monitor = new Object();

  /**
   * returns all ConnectionEditparts from teh given editor
   */
  public static List<SWTBotGefEditPart> getAllConnections(final SWTBotGefEditor editor)
  {
    AbstractMatcher<EditPart> matcher = new AbstractMatcher<EditPart>()
        {
      @Override
      protected boolean doMatch(Object item)
      {
        return true;
      }

      public void describeTo(Description description)
      {
      }
        };
        List<SWTBotGefEditPart> aClassEditParts = editor.editParts(matcher);

        List<SWTBotGefEditPart> ret = new ArrayList<SWTBotGefEditPart>();

        for (SWTBotGefEditPart editPart : aClassEditParts)
        {
          ret.addAll(editPart.sourceConnections());
        }
        return ret;
  }

  public static Node createNewNodeRemote(Diagram diagram, AClass newAClass, String type)
  {
    Node newNode = ViewService.createNode(diagram, newAClass, type, PreferencesHint.USE_DEFAULTS);
    return newNode;
  }

  public static Edge createEdgeRemote(Node source, Node target, String type, IElementType elementType,
      List<RelativeBendpoint> bendpoints, IViewProvider viewProvider)
  {
    Edge edge = viewProvider.createEdge(elementType, source.getDiagram(), type, ViewUtil.APPEND, true,
        PreferencesHint.USE_DEFAULTS);

    edge.setTarget(target);
    edge.setSource(source);

    RelativeBendpoints exitingBendpoints = (RelativeBendpoints)edge.getBendpoints();
    @SuppressWarnings("unchecked")
    List<RelativeBendpoint> points = exitingBendpoints.getPoints();
    List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>(points);
    newBendPoints.addAll(1, bendpoints);
    exitingBendpoints.setPoints(newBendPoints);

    return edge;
  }

  public static void addBendPoint(Edge edge, int sourceX, int sourceY, int targetX, int targetY)
  {
    List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>();
    RelativeBendpoint relativeBendpoint = new RelativeBendpoint(sourceX, sourceY, targetX, targetY);
    newBendPoints.add(relativeBendpoint);
    DawnSWTBotUtil.addBendpoints(edge, newBendPoints);
  }

  public static void addBendpoints(final Edge edge, final List<RelativeBendpoint> bendpoints)
  {
    ResourceSet resourceSet = edge.eResource().getResourceSet();

    if (resourceSet instanceof IEditingDomainProvider)
    {
      EditingDomain editingDomain = ((IEditingDomainProvider)resourceSet).getEditingDomain();

      editingDomain.getCommandStack().execute(new RecordingCommand((TransactionalEditingDomain)editingDomain)
      {
        @Override
        protected void doExecute()
        {
          DawnSWTBotUtil.addBendPointsInternal(edge, bendpoints);
        }
      });
    }
    else
    {
      addBendPointsInternal(edge, bendpoints);
    }
  }

  private static void addBendPointsInternal(final Edge edge, final List<RelativeBendpoint> bendpoints)
  {
    RelativeBendpoints exitingBendpoints = (RelativeBendpoints)edge.getBendpoints();
    @SuppressWarnings("unchecked")
    List<RelativeBendpoint> points = exitingBendpoints.getPoints();
    List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>(points);
    newBendPoints.addAll(1, bendpoints);
    exitingBendpoints.setPoints(newBendPoints);
  }

  public static Object showsConflict(EditPart editPart)
  {
    return DawnConflictHelper.isConflicted((EObject)editPart.getModel());
  }

  public static void moveNodeRemotely(Node nodeA, int x, int y)
  {
    Bounds bounds = (Bounds)nodeA.getLayoutConstraint();
    bounds.setX(x);
    bounds.setY(y);
  }

  public static void resizeNodeRemotely(Node nodeA, int height, int width)
  {
    Bounds bounds = (Bounds)nodeA.getLayoutConstraint();
    bounds.setHeight(height);
    bounds.setWidth(width);
  }

  public static boolean checkNodePosition(Node node, int x, int y)
  {
    boolean ret = true;
    Bounds bounds = (Bounds)node.getLayoutConstraint();
    ret = bounds.getX() == x && ret;
    ret = bounds.getY() == y && ret;

    return ret;
  }

  public static boolean checkNodeSize(Node node, int height, int width)
  {
    boolean ret = true;
    Bounds bounds = (Bounds)node.getLayoutConstraint();
    ret = bounds.getHeight() == height && ret;
    ret = bounds.getWidth() == width && ret;

    return ret;
  }

  public static boolean checkNodePosistionAndSize(Node node, int x, int y, int height, int width)
  {
    boolean ret = true;
    ret = checkNodePosition(node, x, y) && ret;
    ret = checkNodeSize(node, height, width) && ret;

    return ret;
  }

  public static SWTBotView openView(SWTWorkbenchBot bot, String categoryName, String viewName)
  {
    bot.menu(LABEL_WINDOW).menu(LABEL_SHOW_VIEW).menu(LABEL_OTHERS).click();

    SWTBotShell shell = bot.shell(LABEL_SHOW_VIEW);
    shell.activate();
    bot.tree().expandNode(categoryName).select(viewName);
    bot.button(LABEL_OK).click();

    return bot.activeView();
  }

  public static SWTBotMenu findContextMenu(final AbstractSWTBot<?> bot, final String... texts)
  {
    final Matcher<?>[] matchers = new Matcher<?>[texts.length];
    for (int i = 0; i < texts.length; i++)
    {
      matchers[i] = allOf(instanceOf(MenuItem.class), withMnemonic(texts[i]));
    }

    final MenuItem menuItem = UIThreadRunnable.syncExec(new WidgetResult<MenuItem>()
        {
      public MenuItem run()
      {
        MenuItem menuItem = null;
        Control control = (Control)bot.widget;
        Menu menu = control.getMenu();
        for (int i = 0; i < matchers.length; i++)
        {
          menuItem = show(menu, matchers[i]);
          if (menuItem != null)
          {
            menu = menuItem.getMenu();
          }
        }

        return menuItem;
      }
        });
    if (menuItem == null)
    {
      throw new WidgetNotFoundException("Could not find menu: " + Arrays.asList(texts));
    }

    return new SWTBotMenu(menuItem);
  }

  private static MenuItem show(final Menu menu, final Matcher<?> matcher)
  {
    if (menu != null)
    {
      menu.notifyListeners(SWT.Show, new Event());
      MenuItem[] items = menu.getItems();
      for (final MenuItem menuItem : items)
      {
        if (matcher.matches(menuItem))
        {
          return menuItem;
        }
      }
      menu.notifyListeners(SWT.Hide, new Event());
    }
    return null;
  }

  public static void setAutomaticBuild(SWTWorkbenchBot bot, boolean enabled)
  {
    SWTBotMenu menu = bot.menu("Project").menu("Build Automatically");
    // if(menu.isEnabled()&&enabled)
    {
      menu.click();
    }
  }

  // public static Edge createEdgeRemote(Node source, Node target, String type)
  // {
  // Edge newEdge = ViewService.createEdge(source, target, type, PreferencesHint.USE_DEFAULTS);
  //
  // return newEdge;
  // }
  //
  // public static Edge createEdgeRemote(Node source, Node target, EObject model, String type)
  // {
  // Edge newEdge = ViewService.createEdge(source, target, model, type, PreferencesHint.USE_DEFAULTS);
  // return newEdge;
  // }
  //
  // public static Edge createEdgeRemote(Node source, Node target, String type, List<RelativeBendpoint> bendPoints)
  // {
  // Edge edge = createEdgeRemote(source, target, type);
  // RelativeBendpoints exitingBendpoints = (RelativeBendpoints)edge.getBendpoints();
  //
  // @SuppressWarnings("unchecked")
  // List<RelativeBendpoint> points = exitingBendpoints.getPoints();
  // List<RelativeBendpoint> newBendPoints = new ArrayList<RelativeBendpoint>(points);
  // newBendPoints.addAll(1, bendPoints);
  // exitingBendpoints.setPoints(newBendPoints);
  //
  // return edge;
  // }
}
