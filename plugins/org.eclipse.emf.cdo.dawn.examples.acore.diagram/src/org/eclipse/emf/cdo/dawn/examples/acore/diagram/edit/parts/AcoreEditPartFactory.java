/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 * 
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts;

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ITextAwareEditPart;
import org.eclipse.gmf.runtime.draw2d.ui.figures.WrappingLabel;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

/**
 * @generated
 */
public class AcoreEditPartFactory implements EditPartFactory
{

  /**
   * @generated
   */
  public EditPart createEditPart(EditPart context, Object model)
  {
    if (model instanceof View)
    {
      View view = (View)model;
      switch (AcoreVisualIDRegistry.getVisualID(view))
      {

      case ACoreRootEditPart.VISUAL_ID:
        return new ACoreRootEditPart(view);

      case AInterfaceEditPart.VISUAL_ID:
        return new AInterfaceEditPart(view);

      case AInterfaceNameEditPart.VISUAL_ID:
        return new AInterfaceNameEditPart(view);

      case AClassEditPart.VISUAL_ID:
        return new AClassEditPart(view);

      case AClassNameEditPart.VISUAL_ID:
        return new AClassNameEditPart(view);

      case AAttributeEditPart.VISUAL_ID:
        return new AAttributeEditPart(view);

      case AOperationEditPart.VISUAL_ID:
        return new AOperationEditPart(view);

      case AAttribute2EditPart.VISUAL_ID:
        return new AAttribute2EditPart(view);

      case AOperation2EditPart.VISUAL_ID:
        return new AOperation2EditPart(view);

      case AInterfaceAAttributeInterfaceCompartmentEditPart.VISUAL_ID:
        return new AInterfaceAAttributeInterfaceCompartmentEditPart(view);

      case AInterfaceAOperationInterfaceCompartmentEditPart.VISUAL_ID:
        return new AInterfaceAOperationInterfaceCompartmentEditPart(view);

      case AClassAAttributeCompartmentEditPart.VISUAL_ID:
        return new AClassAAttributeCompartmentEditPart(view);

      case AClassAOperationClassCompartmentEditPart.VISUAL_ID:
        return new AClassAOperationClassCompartmentEditPart(view);

      case AClassSubClassesEditPart.VISUAL_ID:
        return new AClassSubClassesEditPart(view);

      case AClassImplementedInterfacesEditPart.VISUAL_ID:
        return new AClassImplementedInterfacesEditPart(view);

      case AClassAssociationsEditPart.VISUAL_ID:
        return new AClassAssociationsEditPart(view);

      case AClassAggregationsEditPart.VISUAL_ID:
        return new AClassAggregationsEditPart(view);

      case AClassCompositionsEditPart.VISUAL_ID:
        return new AClassCompositionsEditPart(view);

      }
    }
    return createUnrecognizedEditPart(context, model);
  }

  /**
   * @generated
   */
  private EditPart createUnrecognizedEditPart(EditPart context, Object model)
  {
    // Handle creation of unrecognized child node EditParts here
    return null;
  }

  /**
   * @generated
   */
  public static CellEditorLocator getTextCellEditorLocator(ITextAwareEditPart source)
  {
    if (source.getFigure() instanceof WrappingLabel)
      return new TextCellEditorLocator((WrappingLabel)source.getFigure());
    else
    {
      return new LabelCellEditorLocator((Label)source.getFigure());
    }
  }

  /**
   * @generated
   */
  static private class TextCellEditorLocator implements CellEditorLocator
  {

    /**
     * @generated
     */
    private WrappingLabel wrapLabel;

    /**
     * @generated
     */
    public TextCellEditorLocator(WrappingLabel wrapLabel)
    {
      this.wrapLabel = wrapLabel;
    }

    /**
     * @generated
     */
    public WrappingLabel getWrapLabel()
    {
      return wrapLabel;
    }

    /**
     * @generated
     */
    public void relocate(CellEditor celleditor)
    {
      Text text = (Text)celleditor.getControl();
      Rectangle rect = getWrapLabel().getTextBounds().getCopy();
      getWrapLabel().translateToAbsolute(rect);
      if (!text.getFont().isDisposed())
      {
        if (getWrapLabel().isTextWrapOn() && getWrapLabel().getText().length() > 0)
        {
          rect.setSize(new Dimension(text.computeSize(rect.width, SWT.DEFAULT)));
        }
        else
        {
          int avr = FigureUtilities.getFontMetrics(text.getFont()).getAverageCharWidth();
          rect.setSize(new Dimension(text.computeSize(SWT.DEFAULT, SWT.DEFAULT)).expand(avr * 2, 0));
        }
      }
      if (!rect.equals(new Rectangle(text.getBounds())))
      {
        text.setBounds(rect.x, rect.y, rect.width, rect.height);
      }
    }
  }

  /**
   * @generated
   */
  private static class LabelCellEditorLocator implements CellEditorLocator
  {

    /**
     * @generated
     */
    private Label label;

    /**
     * @generated
     */
    public LabelCellEditorLocator(Label label)
    {
      this.label = label;
    }

    /**
     * @generated
     */
    public Label getLabel()
    {
      return label;
    }

    /**
     * @generated
     */
    public void relocate(CellEditor celleditor)
    {
      Text text = (Text)celleditor.getControl();
      Rectangle rect = getLabel().getTextBounds().getCopy();
      getLabel().translateToAbsolute(rect);
      if (!text.getFont().isDisposed())
      {
        int avr = FigureUtilities.getFontMetrics(text.getFont()).getAverageCharWidth();
        rect.setSize(new Dimension(text.computeSize(SWT.DEFAULT, SWT.DEFAULT)).expand(avr * 2, 0));
      }
      if (!rect.equals(new Rectangle(text.getBounds())))
      {
        text.setBounds(rect.x, rect.y, rect.width, rect.height);
      }
    }
  }
}
