/******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.gmf.runtime.notation.DescriptionStyle;
import org.eclipse.gmf.runtime.notation.FillStyle;
import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.LayoutConstraint;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RoundedCornersStyle;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.gmf.runtime.notation.ShapeStyle;
import org.eclipse.gmf.runtime.notation.Style;
import org.eclipse.gmf.runtime.notation.datatype.GradientData;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Shape</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getFontColor <em>Font Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getFontName <em>Font Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getFontHeight <em>Font Height</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#isBold <em>Bold</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#isItalic <em>Italic</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#isUnderline <em>Underline</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#isStrikeThrough <em>Strike Through</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getFillColor <em>Fill Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getTransparency <em>Transparency</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getGradient <em>Gradient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getLineColor <em>Line Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getLineWidth <em>Line Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeImpl#getRoundedBendpointsRadius <em>Rounded Bendpoints Radius</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ShapeImpl extends NodeImpl implements Shape
{
  /**
   * The default value of the '{@link #getFontColor() <em>Font Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getFontColor()
   * @generated
   * @ordered
   */
  protected static final int FONT_COLOR_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getFontName() <em>Font Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getFontName()
   * @generated
   * @ordered
   */
  protected static final String FONT_NAME_EDEFAULT = "Tahoma"; //$NON-NLS-1$

  /**
   * The default value of the '{@link #getFontHeight() <em>Font Height</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getFontHeight()
   * @generated
   * @ordered
   */
  protected static final int FONT_HEIGHT_EDEFAULT = 9;

  /**
   * The default value of the '{@link #isBold() <em>Bold</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #isBold()
   * @generated
   * @ordered
   */
  protected static final boolean BOLD_EDEFAULT = false;

  /**
   * The default value of the '{@link #isItalic() <em>Italic</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isItalic()
   * @generated
   * @ordered
   */
  protected static final boolean ITALIC_EDEFAULT = false;

  /**
   * The default value of the '{@link #isUnderline() <em>Underline</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isUnderline()
   * @generated
   * @ordered
   */
  protected static final boolean UNDERLINE_EDEFAULT = false;

  /**
   * The default value of the '{@link #isStrikeThrough() <em>Strike Through</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isStrikeThrough()
   * @generated
   * @ordered
   */
  protected static final boolean STRIKE_THROUGH_EDEFAULT = false;

  /**
   * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getDescription()
   * @generated
   * @ordered
   */
  protected static final String DESCRIPTION_EDEFAULT = ""; //$NON-NLS-1$

  /**
   * The default value of the '{@link #getFillColor() <em>Fill Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getFillColor()
   * @generated
   * @ordered
   */
  protected static final int FILL_COLOR_EDEFAULT = 16777215;

  /**
   * The default value of the '{@link #getTransparency() <em>Transparency</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getTransparency()
   * @generated
   * @ordered
   */
  protected static final int TRANSPARENCY_EDEFAULT = -1;

  /**
   * The default value of the '{@link #getGradient() <em>Gradient</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getGradient()
   * @generated
   * @ordered
   */
  protected static final GradientData GRADIENT_EDEFAULT = null;

  /**
   * The default value of the '{@link #getLineColor() <em>Line Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineColor()
   * @generated
   * @ordered
   */
  protected static final int LINE_COLOR_EDEFAULT = 11579568;

  /**
   * The default value of the '{@link #getLineWidth() <em>Line Width</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineWidth()
   * @generated
   * @ordered
   */
  protected static final int LINE_WIDTH_EDEFAULT = -1;

  /**
   * The default value of the '{@link #getRoundedBendpointsRadius() <em>Rounded
   * Bendpoints Radius</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @see #getRoundedBendpointsRadius()
   * @generated
   * @ordered
   * @since 1.4
   */
  protected static final int ROUNDED_BENDPOINTS_RADIUS_EDEFAULT = 0;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ShapeImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.SHAPE;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getFontColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__FONT_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_COLOR, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFontColor(int newFontColor)
  {
    eDynamicSet(NotationPackage.SHAPE__FONT_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_COLOR, new Integer(newFontColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getFontName()
  {
    return (String)eDynamicGet(NotationPackage.SHAPE__FONT_NAME - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_NAME, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated NOT
   */
  @Override
  public void setFontName(String newFontName)
  {
    setFontNameGen(newFontName == null ? null : newFontName.intern());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setFontNameGen(String newFontName)
  {
    eDynamicSet(NotationPackage.SHAPE__FONT_NAME - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_NAME, newFontName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getFontHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__FONT_HEIGHT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_HEIGHT, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFontHeight(int newFontHeight)
  {
    eDynamicSet(NotationPackage.SHAPE__FONT_HEIGHT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__FONT_HEIGHT, new Integer(newFontHeight));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isBold()
  {
    return ((Boolean)eDynamicGet(NotationPackage.SHAPE__BOLD - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__BOLD, true, true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setBold(boolean newBold)
  {
    eDynamicSet(NotationPackage.SHAPE__BOLD - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__BOLD, new Boolean(newBold));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isItalic()
  {
    return ((Boolean)eDynamicGet(NotationPackage.SHAPE__ITALIC - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__ITALIC, true, true))
        .booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setItalic(boolean newItalic)
  {
    eDynamicSet(NotationPackage.SHAPE__ITALIC - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__ITALIC, new Boolean(newItalic));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isUnderline()
  {
    return ((Boolean)eDynamicGet(NotationPackage.SHAPE__UNDERLINE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__UNDERLINE, true, true))
        .booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setUnderline(boolean newUnderline)
  {
    eDynamicSet(NotationPackage.SHAPE__UNDERLINE - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__UNDERLINE, new Boolean(newUnderline));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isStrikeThrough()
  {
    return ((Boolean)eDynamicGet(NotationPackage.SHAPE__STRIKE_THROUGH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__STRIKE_THROUGH, true,
        true)).booleanValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStrikeThrough(boolean newStrikeThrough)
  {
    eDynamicSet(NotationPackage.SHAPE__STRIKE_THROUGH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FONT_STYLE__STRIKE_THROUGH,
        new Boolean(newStrikeThrough));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return (String)eDynamicGet(NotationPackage.SHAPE__DESCRIPTION - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDescription(String newDescription)
  {
    eDynamicSet(NotationPackage.SHAPE__DESCRIPTION - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, newDescription);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getFillColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__FILL_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFillColor(int newFillColor)
  {
    eDynamicSet(NotationPackage.SHAPE__FILL_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, new Integer(newFillColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getTransparency()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__TRANSPARENCY - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTransparency(int newTransparency)
  {
    eDynamicSet(NotationPackage.SHAPE__TRANSPARENCY - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, new Integer(newTransparency));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GradientData getGradient()
  {
    return (GradientData)eDynamicGet(NotationPackage.SHAPE__GRADIENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__GRADIENT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setGradient(GradientData newGradient)
  {
    eDynamicSet(NotationPackage.SHAPE__GRADIENT - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.FILL_STYLE__GRADIENT, newGradient);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__LINE_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineColor(int newLineColor)
  {
    eDynamicSet(NotationPackage.SHAPE__LINE_COLOR - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, new Integer(newLineColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__LINE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, true, true))
        .intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineWidth(int newLineWidth)
  {
    eDynamicSet(NotationPackage.SHAPE__LINE_WIDTH - ESTATIC_FEATURE_COUNT, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, new Integer(newLineWidth));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getRoundedBendpointsRadius()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRoundedBendpointsRadius(int newRoundedBendpointsRadius)
  {
    eDynamicSet(NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS - ESTATIC_FEATURE_COUNT,
        NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS, new Integer(newRoundedBendpointsRadius));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE__EANNOTATIONS:
      return getEAnnotations();
    case NotationPackage.SHAPE__VISIBLE:
      return isVisible() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__TYPE:
      return getType();
    case NotationPackage.SHAPE__MUTABLE:
      return isMutable() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__SOURCE_EDGES:
      return getSourceEdges();
    case NotationPackage.SHAPE__TARGET_EDGES:
      return getTargetEdges();
    case NotationPackage.SHAPE__PERSISTED_CHILDREN:
      return getPersistedChildren();
    case NotationPackage.SHAPE__STYLES:
      return getStyles();
    case NotationPackage.SHAPE__ELEMENT:
      if (resolve)
      {
        return getElement();
      }
      return basicGetElement();
    case NotationPackage.SHAPE__DIAGRAM:
      if (resolve)
      {
        return getDiagram();
      }
      return basicGetDiagram();
    case NotationPackage.SHAPE__TRANSIENT_CHILDREN:
      return getTransientChildren();
    case NotationPackage.SHAPE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint();
    case NotationPackage.SHAPE__FONT_COLOR:
      return new Integer(getFontColor());
    case NotationPackage.SHAPE__FONT_NAME:
      return getFontName();
    case NotationPackage.SHAPE__FONT_HEIGHT:
      return new Integer(getFontHeight());
    case NotationPackage.SHAPE__BOLD:
      return isBold() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__ITALIC:
      return isItalic() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__UNDERLINE:
      return isUnderline() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__STRIKE_THROUGH:
      return isStrikeThrough() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE__DESCRIPTION:
      return getDescription();
    case NotationPackage.SHAPE__FILL_COLOR:
      return new Integer(getFillColor());
    case NotationPackage.SHAPE__TRANSPARENCY:
      return new Integer(getTransparency());
    case NotationPackage.SHAPE__GRADIENT:
      return getGradient();
    case NotationPackage.SHAPE__LINE_COLOR:
      return new Integer(getLineColor());
    case NotationPackage.SHAPE__LINE_WIDTH:
      return new Integer(getLineWidth());
    case NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS:
      return new Integer(getRoundedBendpointsRadius());
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE__EANNOTATIONS:
      getEAnnotations().clear();
      getEAnnotations().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__VISIBLE:
      setVisible(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__TYPE:
      setType((String)newValue);
      return;
    case NotationPackage.SHAPE__MUTABLE:
      setMutable(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__SOURCE_EDGES:
      getSourceEdges().clear();
      getSourceEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__TARGET_EDGES:
      getTargetEdges().clear();
      getTargetEdges().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      getPersistedChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__STYLES:
      getStyles().clear();
      getStyles().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__ELEMENT:
      setElement((EObject)newValue);
      return;
    case NotationPackage.SHAPE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      getTransientChildren().addAll((Collection)newValue);
      return;
    case NotationPackage.SHAPE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)newValue);
      return;
    case NotationPackage.SHAPE__FONT_COLOR:
      setFontColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__FONT_NAME:
      setFontName((String)newValue);
      return;
    case NotationPackage.SHAPE__FONT_HEIGHT:
      setFontHeight(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__BOLD:
      setBold(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__ITALIC:
      setItalic(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__UNDERLINE:
      setUnderline(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__STRIKE_THROUGH:
      setStrikeThrough(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE__DESCRIPTION:
      setDescription((String)newValue);
      return;
    case NotationPackage.SHAPE__FILL_COLOR:
      setFillColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__TRANSPARENCY:
      setTransparency(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__GRADIENT:
      setGradient((GradientData)newValue);
      return;
    case NotationPackage.SHAPE__LINE_COLOR:
      setLineColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__LINE_WIDTH:
      setLineWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(((Integer)newValue).intValue());
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE__EANNOTATIONS:
      getEAnnotations().clear();
      return;
    case NotationPackage.SHAPE__VISIBLE:
      setVisible(VISIBLE_EDEFAULT);
      return;
    case NotationPackage.SHAPE__TYPE:
      setType(TYPE_EDEFAULT);
      return;
    case NotationPackage.SHAPE__MUTABLE:
      setMutable(MUTABLE_EDEFAULT);
      return;
    case NotationPackage.SHAPE__SOURCE_EDGES:
      getSourceEdges().clear();
      return;
    case NotationPackage.SHAPE__TARGET_EDGES:
      getTargetEdges().clear();
      return;
    case NotationPackage.SHAPE__PERSISTED_CHILDREN:
      getPersistedChildren().clear();
      return;
    case NotationPackage.SHAPE__STYLES:
      getStyles().clear();
      return;
    case NotationPackage.SHAPE__ELEMENT:
      unsetElement();
      return;
    case NotationPackage.SHAPE__TRANSIENT_CHILDREN:
      getTransientChildren().clear();
      return;
    case NotationPackage.SHAPE__LAYOUT_CONSTRAINT:
      setLayoutConstraint((LayoutConstraint)null);
      return;
    case NotationPackage.SHAPE__FONT_COLOR:
      setFontColor(FONT_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE__FONT_NAME:
      setFontName(FONT_NAME_EDEFAULT);
      return;
    case NotationPackage.SHAPE__FONT_HEIGHT:
      setFontHeight(FONT_HEIGHT_EDEFAULT);
      return;
    case NotationPackage.SHAPE__BOLD:
      setBold(BOLD_EDEFAULT);
      return;
    case NotationPackage.SHAPE__ITALIC:
      setItalic(ITALIC_EDEFAULT);
      return;
    case NotationPackage.SHAPE__UNDERLINE:
      setUnderline(UNDERLINE_EDEFAULT);
      return;
    case NotationPackage.SHAPE__STRIKE_THROUGH:
      setStrikeThrough(STRIKE_THROUGH_EDEFAULT);
      return;
    case NotationPackage.SHAPE__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
      return;
    case NotationPackage.SHAPE__FILL_COLOR:
      setFillColor(FILL_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE__TRANSPARENCY:
      setTransparency(TRANSPARENCY_EDEFAULT);
      return;
    case NotationPackage.SHAPE__GRADIENT:
      setGradient(GRADIENT_EDEFAULT);
      return;
    case NotationPackage.SHAPE__LINE_COLOR:
      setLineColor(LINE_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE__LINE_WIDTH:
      setLineWidth(LINE_WIDTH_EDEFAULT);
      return;
    case NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(ROUNDED_BENDPOINTS_RADIUS_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE__EANNOTATIONS:
      return !getEAnnotations().isEmpty();
    case NotationPackage.SHAPE__VISIBLE:
      return isVisible() != VISIBLE_EDEFAULT;
    case NotationPackage.SHAPE__TYPE:
      return TYPE_EDEFAULT == null ? getType() != null : !TYPE_EDEFAULT.equals(getType());
    case NotationPackage.SHAPE__MUTABLE:
      return isMutable() != MUTABLE_EDEFAULT;
    case NotationPackage.SHAPE__SOURCE_EDGES:
      return !getSourceEdges().isEmpty();
    case NotationPackage.SHAPE__TARGET_EDGES:
      return !getTargetEdges().isEmpty();
    case NotationPackage.SHAPE__PERSISTED_CHILDREN:
      return !getPersistedChildren().isEmpty();
    case NotationPackage.SHAPE__STYLES:
      return !getStyles().isEmpty();
    case NotationPackage.SHAPE__ELEMENT:
      return isSetElement();
    case NotationPackage.SHAPE__DIAGRAM:
      return basicGetDiagram() != null;
    case NotationPackage.SHAPE__TRANSIENT_CHILDREN:
      return !getTransientChildren().isEmpty();
    case NotationPackage.SHAPE__LAYOUT_CONSTRAINT:
      return getLayoutConstraint() != null;
    case NotationPackage.SHAPE__FONT_COLOR:
      return getFontColor() != FONT_COLOR_EDEFAULT;
    case NotationPackage.SHAPE__FONT_NAME:
      return FONT_NAME_EDEFAULT == null ? getFontName() != null : !FONT_NAME_EDEFAULT.equals(getFontName());
    case NotationPackage.SHAPE__FONT_HEIGHT:
      return getFontHeight() != FONT_HEIGHT_EDEFAULT;
    case NotationPackage.SHAPE__BOLD:
      return isBold() != BOLD_EDEFAULT;
    case NotationPackage.SHAPE__ITALIC:
      return isItalic() != ITALIC_EDEFAULT;
    case NotationPackage.SHAPE__UNDERLINE:
      return isUnderline() != UNDERLINE_EDEFAULT;
    case NotationPackage.SHAPE__STRIKE_THROUGH:
      return isStrikeThrough() != STRIKE_THROUGH_EDEFAULT;
    case NotationPackage.SHAPE__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? getDescription() != null : !DESCRIPTION_EDEFAULT.equals(getDescription());
    case NotationPackage.SHAPE__FILL_COLOR:
      return getFillColor() != FILL_COLOR_EDEFAULT;
    case NotationPackage.SHAPE__TRANSPARENCY:
      return getTransparency() != TRANSPARENCY_EDEFAULT;
    case NotationPackage.SHAPE__GRADIENT:
      return GRADIENT_EDEFAULT == null ? getGradient() != null : !GRADIENT_EDEFAULT.equals(getGradient());
    case NotationPackage.SHAPE__LINE_COLOR:
      return getLineColor() != LINE_COLOR_EDEFAULT;
    case NotationPackage.SHAPE__LINE_WIDTH:
      return getLineWidth() != LINE_WIDTH_EDEFAULT;
    case NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS:
      return getRoundedBendpointsRadius() != ROUNDED_BENDPOINTS_RADIUS_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == FontStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE__FONT_COLOR:
        return NotationPackage.FONT_STYLE__FONT_COLOR;
      case NotationPackage.SHAPE__FONT_NAME:
        return NotationPackage.FONT_STYLE__FONT_NAME;
      case NotationPackage.SHAPE__FONT_HEIGHT:
        return NotationPackage.FONT_STYLE__FONT_HEIGHT;
      case NotationPackage.SHAPE__BOLD:
        return NotationPackage.FONT_STYLE__BOLD;
      case NotationPackage.SHAPE__ITALIC:
        return NotationPackage.FONT_STYLE__ITALIC;
      case NotationPackage.SHAPE__UNDERLINE:
        return NotationPackage.FONT_STYLE__UNDERLINE;
      case NotationPackage.SHAPE__STRIKE_THROUGH:
        return NotationPackage.FONT_STYLE__STRIKE_THROUGH;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE__DESCRIPTION:
        return NotationPackage.DESCRIPTION_STYLE__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == FillStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE__FILL_COLOR:
        return NotationPackage.FILL_STYLE__FILL_COLOR;
      case NotationPackage.SHAPE__TRANSPARENCY:
        return NotationPackage.FILL_STYLE__TRANSPARENCY;
      case NotationPackage.SHAPE__GRADIENT:
        return NotationPackage.FILL_STYLE__GRADIENT;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE__LINE_COLOR:
        return NotationPackage.LINE_STYLE__LINE_COLOR;
      case NotationPackage.SHAPE__LINE_WIDTH:
        return NotationPackage.LINE_STYLE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    if (baseClass == ShapeStyle.class)
    {
      switch (derivedFeatureID)
      {
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == Style.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    if (baseClass == FontStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.FONT_STYLE__FONT_COLOR:
        return NotationPackage.SHAPE__FONT_COLOR;
      case NotationPackage.FONT_STYLE__FONT_NAME:
        return NotationPackage.SHAPE__FONT_NAME;
      case NotationPackage.FONT_STYLE__FONT_HEIGHT:
        return NotationPackage.SHAPE__FONT_HEIGHT;
      case NotationPackage.FONT_STYLE__BOLD:
        return NotationPackage.SHAPE__BOLD;
      case NotationPackage.FONT_STYLE__ITALIC:
        return NotationPackage.SHAPE__ITALIC;
      case NotationPackage.FONT_STYLE__UNDERLINE:
        return NotationPackage.SHAPE__UNDERLINE;
      case NotationPackage.FONT_STYLE__STRIKE_THROUGH:
        return NotationPackage.SHAPE__STRIKE_THROUGH;
      default:
        return -1;
      }
    }
    if (baseClass == DescriptionStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.DESCRIPTION_STYLE__DESCRIPTION:
        return NotationPackage.SHAPE__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == FillStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.FILL_STYLE__FILL_COLOR:
        return NotationPackage.SHAPE__FILL_COLOR;
      case NotationPackage.FILL_STYLE__TRANSPARENCY:
        return NotationPackage.SHAPE__TRANSPARENCY;
      case NotationPackage.FILL_STYLE__GRADIENT:
        return NotationPackage.SHAPE__GRADIENT;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.LINE_STYLE__LINE_COLOR:
        return NotationPackage.SHAPE__LINE_COLOR;
      case NotationPackage.LINE_STYLE__LINE_WIDTH:
        return NotationPackage.SHAPE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.SHAPE__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    if (baseClass == ShapeStyle.class)
    {
      switch (baseFeatureID)
      {
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ShapeImpl
