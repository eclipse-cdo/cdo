/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator;

import org.eclipse.emf.cdo.dawn.examples.acore.ACoreRoot;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttribute2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AAttributeEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAggregationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassAssociationsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassCompositionsEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassImplementedInterfacesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AClassSubClassesEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.ACoreRootEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AInterfaceNameEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperation2EditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.edit.parts.AOperationEditPart;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreDiagramEditorPlugin;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreElementTypes;
import org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers.AcoreParserProvider;

import org.eclipse.gmf.runtime.common.ui.services.parser.IParser;
import org.eclipse.gmf.runtime.common.ui.services.parser.ParserOptions;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

/**
 * @generated
 */
public class AcoreNavigatorLabelProvider extends LabelProvider implements ICommonLabelProvider, ITreePathLabelProvider
{

  /**
   * @generated
   */
  static
  {
    AcoreDiagramEditorPlugin.getInstance().getImageRegistry().put("Navigator?UnknownElement", //$NON-NLS-1$
        ImageDescriptor.getMissingImageDescriptor());
    AcoreDiagramEditorPlugin.getInstance().getImageRegistry().put("Navigator?ImageNotFound", //$NON-NLS-1$
        ImageDescriptor.getMissingImageDescriptor());
  }

  /**
   * @generated
   */
  public void updateLabel(ViewerLabel label, TreePath elementPath)
  {
    Object element = elementPath.getLastSegment();
    if (element instanceof AcoreNavigatorItem && !isOwnView(((AcoreNavigatorItem)element).getView()))
    {
      return;
    }
    label.setText(getText(element));
    label.setImage(getImage(element));
  }

  /**
   * @generated
   */
  @Override
  public Image getImage(Object element)
  {
    if (element instanceof AcoreNavigatorGroup)
    {
      AcoreNavigatorGroup group = (AcoreNavigatorGroup)element;
      return AcoreDiagramEditorPlugin.getInstance().getBundledImage(group.getIcon());
    }

    if (element instanceof AcoreNavigatorItem)
    {
      AcoreNavigatorItem navigatorItem = (AcoreNavigatorItem)element;
      if (!isOwnView(navigatorItem.getView()))
      {
        return super.getImage(element);
      }
      return getImage(navigatorItem.getView());
    }

    return super.getImage(element);
  }

  /**
   * @generated
   */
  public Image getImage(View view)
  {
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case AAttributeEditPart.VISUAL_ID:
      return getImage("Navigator?Node?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AAttribute", //$NON-NLS-1$
          AcoreElementTypes.AAttribute_3001);
    case AOperation2EditPart.VISUAL_ID:
      return getImage("Navigator?Node?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AOperation", //$NON-NLS-1$
          AcoreElementTypes.AOperation_3004);
    case AClassSubClassesEditPart.VISUAL_ID:
      return getImage("Navigator?Link?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass?subClasses", //$NON-NLS-1$
          AcoreElementTypes.AClassSubClasses_4001);
    case AClassCompositionsEditPart.VISUAL_ID:
      return getImage("Navigator?Link?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass?compositions", //$NON-NLS-1$
          AcoreElementTypes.AClassCompositions_4005);
    case AInterfaceEditPart.VISUAL_ID:
      return getImage("Navigator?TopLevelNode?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AInterface", //$NON-NLS-1$
          AcoreElementTypes.AInterface_2001);
    case AClassAggregationsEditPart.VISUAL_ID:
      return getImage("Navigator?Link?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass?aggregations", //$NON-NLS-1$
          AcoreElementTypes.AClassAggregations_4004);
    case AClassImplementedInterfacesEditPart.VISUAL_ID:
      return getImage(
          "Navigator?Link?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass?implementedInterfaces", //$NON-NLS-1$
          AcoreElementTypes.AClassImplementedInterfaces_4002);
    case AAttribute2EditPart.VISUAL_ID:
      return getImage("Navigator?Node?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AAttribute", //$NON-NLS-1$
          AcoreElementTypes.AAttribute_3003);
    case AClassEditPart.VISUAL_ID:
      return getImage("Navigator?TopLevelNode?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass", //$NON-NLS-1$
          AcoreElementTypes.AClass_2002);
    case ACoreRootEditPart.VISUAL_ID:
      return getImage("Navigator?Diagram?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?ACoreRoot", //$NON-NLS-1$
          AcoreElementTypes.ACoreRoot_1000);
    case AClassAssociationsEditPart.VISUAL_ID:
      return getImage("Navigator?Link?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AClass?associations", //$NON-NLS-1$
          AcoreElementTypes.AClassAssociations_4003);
    case AOperationEditPart.VISUAL_ID:
      return getImage("Navigator?Node?http://www.eclipse.org/emf/cdo/dawn/examples/2010/ACore?AOperation", //$NON-NLS-1$
          AcoreElementTypes.AOperation_3002);
    }
    return getImage("Navigator?UnknownElement", null); //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private Image getImage(String key, IElementType elementType)
  {
    ImageRegistry imageRegistry = AcoreDiagramEditorPlugin.getInstance().getImageRegistry();
    Image image = imageRegistry.get(key);
    if (image == null && elementType != null && AcoreElementTypes.isKnownElementType(elementType))
    {
      image = AcoreElementTypes.getImage(elementType);
      imageRegistry.put(key, image);
    }

    if (image == null)
    {
      image = imageRegistry.get("Navigator?ImageNotFound"); //$NON-NLS-1$
      imageRegistry.put(key, image);
    }
    return image;
  }

  /**
   * @generated
   */
  @Override
  public String getText(Object element)
  {
    if (element instanceof AcoreNavigatorGroup)
    {
      AcoreNavigatorGroup group = (AcoreNavigatorGroup)element;
      return group.getGroupName();
    }

    if (element instanceof AcoreNavigatorItem)
    {
      AcoreNavigatorItem navigatorItem = (AcoreNavigatorItem)element;
      if (!isOwnView(navigatorItem.getView()))
      {
        return null;
      }
      return getText(navigatorItem.getView());
    }

    return super.getText(element);
  }

  /**
   * @generated
   */
  public String getText(View view)
  {
    if (view.getElement() != null && view.getElement().eIsProxy())
    {
      return getUnresolvedDomainElementProxyText(view);
    }
    switch (AcoreVisualIDRegistry.getVisualID(view))
    {
    case AAttributeEditPart.VISUAL_ID:
      return getAAttribute_3001Text(view);
    case AOperation2EditPart.VISUAL_ID:
      return getAOperation_3004Text(view);
    case AClassSubClassesEditPart.VISUAL_ID:
      return getAClassSubClasses_4001Text(view);
    case AClassCompositionsEditPart.VISUAL_ID:
      return getAClassCompositions_4005Text(view);
    case AInterfaceEditPart.VISUAL_ID:
      return getAInterface_2001Text(view);
    case AClassAggregationsEditPart.VISUAL_ID:
      return getAClassAggregations_4004Text(view);
    case AClassImplementedInterfacesEditPart.VISUAL_ID:
      return getAClassImplementedInterfaces_4002Text(view);
    case AAttribute2EditPart.VISUAL_ID:
      return getAAttribute_3003Text(view);
    case AClassEditPart.VISUAL_ID:
      return getAClass_2002Text(view);
    case ACoreRootEditPart.VISUAL_ID:
      return getACoreRoot_1000Text(view);
    case AClassAssociationsEditPart.VISUAL_ID:
      return getAClassAssociations_4003Text(view);
    case AOperationEditPart.VISUAL_ID:
      return getAOperation_3002Text(view);
    }
    return getUnknownElementText(view);
  }

  /**
   * @generated
   */
  private String getAAttribute_3001Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AAttribute_3001,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AAttributeEditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 3001); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getAOperation_3004Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AOperation_3004,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AOperation2EditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 3004); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getAClassSubClasses_4001Text(View view)
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private String getAClassCompositions_4005Text(View view)
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private String getAInterface_2001Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AInterface_2001,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AInterfaceNameEditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 5001); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getAClassAggregations_4004Text(View view)
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private String getAClassImplementedInterfaces_4002Text(View view)
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private String getAAttribute_3003Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AAttribute_3003,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AAttribute2EditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 3003); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getAClass_2002Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AClass_2002,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AClassNameEditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 5002); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getACoreRoot_1000Text(View view)
  {
    ACoreRoot domainModelElement = (ACoreRoot)view.getElement();
    if (domainModelElement != null)
    {
      return domainModelElement.getTitle();
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("No domain element for view with visualID = " + 1000); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getAClassAssociations_4003Text(View view)
  {
    return ""; //$NON-NLS-1$
  }

  /**
   * @generated
   */
  private String getAOperation_3002Text(View view)
  {
    IParser parser = AcoreParserProvider.getParser(AcoreElementTypes.AOperation_3002,
        view.getElement() != null ? view.getElement() : view,
            AcoreVisualIDRegistry.getType(AOperationEditPart.VISUAL_ID));
    if (parser != null)
    {
      return parser.getPrintString(new EObjectAdapter(view.getElement() != null ? view.getElement() : view),
          ParserOptions.NONE.intValue());
    }
    else
    {
      AcoreDiagramEditorPlugin.getInstance().logError("Parser was not found for label " + 3002); //$NON-NLS-1$
      return ""; //$NON-NLS-1$
    }
  }

  /**
   * @generated
   */
  private String getUnknownElementText(View view)
  {
    return "<UnknownElement Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @generated
   */
  private String getUnresolvedDomainElementProxyText(View view)
  {
    return "<Unresolved domain element Visual_ID = " + view.getType() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * @generated
   */
  public void init(ICommonContentExtensionSite aConfig)
  {
  }

  /**
   * @generated
   */
  public void restoreState(IMemento aMemento)
  {
  }

  /**
   * @generated
   */
  public void saveState(IMemento aMemento)
  {
  }

  /**
   * @generated
   */
  public String getDescription(Object anElement)
  {
    return null;
  }

  /**
   * @generated
   */
  private boolean isOwnView(View view)
  {
    return ACoreRootEditPart.MODEL_ID.equals(AcoreVisualIDRegistry.getModelID(view));
  }

}
