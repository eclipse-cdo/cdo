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

import org.eclipse.emf.cdo.dawn.examples.acore.AClass;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.synchronize.DawnConflictHelper;

import org.eclipse.emf.ecore.EObject;

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
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;

import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.List;

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

  public static void addBendpoints(Edge edge, List<RelativeBendpoint> bendpoints)
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
