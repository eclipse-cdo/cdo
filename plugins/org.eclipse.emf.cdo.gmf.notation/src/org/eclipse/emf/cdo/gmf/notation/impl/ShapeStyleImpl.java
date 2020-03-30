/******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.DescriptionStyle;
import org.eclipse.gmf.runtime.notation.FillStyle;
import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RoundedCornersStyle;
import org.eclipse.gmf.runtime.notation.ShapeStyle;
import org.eclipse.gmf.runtime.notation.datatype.GradientData;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Shape Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getFillColor <em>Fill Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getTransparency <em>Transparency</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getGradient <em>Gradient</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getLineColor <em>Line Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getLineWidth <em>Line Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ShapeStyleImpl#getRoundedBendpointsRadius <em>Rounded Bendpoints Radius</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class ShapeStyleImpl extends FontStyleImpl implements ShapeStyle
{

  /**
  * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getDescription()
  * @generated
  * @ordered
  */
  protected static final String DESCRIPTION_EDEFAULT = ""; //$NON-NLS-1$

  /**
   * The default value of the '{@link #getFillColor() <em>Fill Color</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFillColor()
   * @generated
   * @ordered
   */
  protected static final int FILL_COLOR_EDEFAULT = 16777215;

  /**
  * The default value of the '{@link #getTransparency() <em>Transparency</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getTransparency()
  * @generated
  * @ordered
  */
  protected static final int TRANSPARENCY_EDEFAULT = -1;

  /**
  * The default value of the '{@link #getGradient() <em>Gradient</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getGradient()
  * @generated
  * @ordered
  */
  protected static final GradientData GRADIENT_EDEFAULT = null;

  /**
  * The default value of the '{@link #getLineColor() <em>Line Color</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getLineColor()
  * @generated
  * @ordered
  */
  protected static final int LINE_COLOR_EDEFAULT = 11579568;

  /**
  * The default value of the '{@link #getLineWidth() <em>Line Width</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getLineWidth()
  * @generated
  * @ordered
  */
  protected static final int LINE_WIDTH_EDEFAULT = -1;

  /**
  * The default value of the '{@link #getRoundedBendpointsRadius() <em>Rounded Bendpoints Radius</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getRoundedBendpointsRadius()
  * @generated
  * @ordered
  */
  protected static final int ROUNDED_BENDPOINTS_RADIUS_EDEFAULT = 0;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected ShapeStyleImpl()
  {
    super();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.SHAPE_STYLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public void setFontName(String newFontName)
  {
    setFontNameGen(newFontName == null ? null : newFontName.intern());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getDescription()
  {
    return (String)eDynamicGet(NotationPackage.SHAPE_STYLE__DESCRIPTION, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setDescription(String newDescription)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__DESCRIPTION, NotationPackage.Literals.DESCRIPTION_STYLE__DESCRIPTION, newDescription);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getFillColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE_STYLE__FILL_COLOR, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setFillColor(int newFillColor)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__FILL_COLOR, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, new Integer(newFillColor));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getTransparency()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE_STYLE__TRANSPARENCY, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setTransparency(int newTransparency)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__TRANSPARENCY, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, new Integer(newTransparency));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public GradientData getGradient()
  {
    return (GradientData)eDynamicGet(NotationPackage.SHAPE_STYLE__GRADIENT, NotationPackage.Literals.FILL_STYLE__GRADIENT, true, true);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setGradient(GradientData newGradient)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__GRADIENT, NotationPackage.Literals.FILL_STYLE__GRADIENT, newGradient);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getLineColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setLineColor(int newLineColor)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, new Integer(newLineColor));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getLineWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setLineWidth(int newLineWidth)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, new Integer(newLineWidth));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getRoundedBendpointsRadius()
  {
    return ((Integer)eDynamicGet(NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS,
        NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setRoundedBendpointsRadius(int newRoundedBendpointsRadius)
  {
    eDynamicSet(NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS, NotationPackage.Literals.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS,
        new Integer(newRoundedBendpointsRadius));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE_STYLE__FONT_COLOR:
      return new Integer(getFontColor());
    case NotationPackage.SHAPE_STYLE__FONT_NAME:
      return getFontName();
    case NotationPackage.SHAPE_STYLE__FONT_HEIGHT:
      return new Integer(getFontHeight());
    case NotationPackage.SHAPE_STYLE__BOLD:
      return isBold() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE_STYLE__ITALIC:
      return isItalic() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE_STYLE__UNDERLINE:
      return isUnderline() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE_STYLE__STRIKE_THROUGH:
      return isStrikeThrough() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.SHAPE_STYLE__DESCRIPTION:
      return getDescription();
    case NotationPackage.SHAPE_STYLE__FILL_COLOR:
      return new Integer(getFillColor());
    case NotationPackage.SHAPE_STYLE__TRANSPARENCY:
      return new Integer(getTransparency());
    case NotationPackage.SHAPE_STYLE__GRADIENT:
      return getGradient();
    case NotationPackage.SHAPE_STYLE__LINE_COLOR:
      return new Integer(getLineColor());
    case NotationPackage.SHAPE_STYLE__LINE_WIDTH:
      return new Integer(getLineWidth());
    case NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return new Integer(getRoundedBendpointsRadius());
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE_STYLE__FONT_COLOR:
      setFontColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__FONT_NAME:
      setFontName((String)newValue);
      return;
    case NotationPackage.SHAPE_STYLE__FONT_HEIGHT:
      setFontHeight(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__BOLD:
      setBold(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE_STYLE__ITALIC:
      setItalic(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE_STYLE__UNDERLINE:
      setUnderline(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE_STYLE__STRIKE_THROUGH:
      setStrikeThrough(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.SHAPE_STYLE__DESCRIPTION:
      setDescription((String)newValue);
      return;
    case NotationPackage.SHAPE_STYLE__FILL_COLOR:
      setFillColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__TRANSPARENCY:
      setTransparency(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__GRADIENT:
      setGradient((GradientData)newValue);
      return;
    case NotationPackage.SHAPE_STYLE__LINE_COLOR:
      setLineColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__LINE_WIDTH:
      setLineWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(((Integer)newValue).intValue());
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE_STYLE__FONT_COLOR:
      setFontColor(FONT_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__FONT_NAME:
      setFontName(FONT_NAME_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__FONT_HEIGHT:
      setFontHeight(FONT_HEIGHT_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__BOLD:
      setBold(BOLD_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__ITALIC:
      setItalic(ITALIC_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__UNDERLINE:
      setUnderline(UNDERLINE_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__STRIKE_THROUGH:
      setStrikeThrough(STRIKE_THROUGH_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__DESCRIPTION:
      setDescription(DESCRIPTION_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__FILL_COLOR:
      setFillColor(FILL_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__TRANSPARENCY:
      setTransparency(TRANSPARENCY_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__GRADIENT:
      setGradient(GRADIENT_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__LINE_COLOR:
      setLineColor(LINE_COLOR_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__LINE_WIDTH:
      setLineWidth(LINE_WIDTH_EDEFAULT);
      return;
    case NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      setRoundedBendpointsRadius(ROUNDED_BENDPOINTS_RADIUS_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
  * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.SHAPE_STYLE__FONT_COLOR:
      return getFontColor() != FONT_COLOR_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__FONT_NAME:
      return FONT_NAME_EDEFAULT == null ? getFontName() != null : !FONT_NAME_EDEFAULT.equals(getFontName());
    case NotationPackage.SHAPE_STYLE__FONT_HEIGHT:
      return getFontHeight() != FONT_HEIGHT_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__BOLD:
      return isBold() != BOLD_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__ITALIC:
      return isItalic() != ITALIC_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__UNDERLINE:
      return isUnderline() != UNDERLINE_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__STRIKE_THROUGH:
      return isStrikeThrough() != STRIKE_THROUGH_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__DESCRIPTION:
      return DESCRIPTION_EDEFAULT == null ? getDescription() != null : !DESCRIPTION_EDEFAULT.equals(getDescription());
    case NotationPackage.SHAPE_STYLE__FILL_COLOR:
      return getFillColor() != FILL_COLOR_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__TRANSPARENCY:
      return getTransparency() != TRANSPARENCY_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__GRADIENT:
      return GRADIENT_EDEFAULT == null ? getGradient() != null : !GRADIENT_EDEFAULT.equals(getGradient());
    case NotationPackage.SHAPE_STYLE__LINE_COLOR:
      return getLineColor() != LINE_COLOR_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__LINE_WIDTH:
      return getLineWidth() != LINE_WIDTH_EDEFAULT;
    case NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS:
      return getRoundedBendpointsRadius() != ROUNDED_BENDPOINTS_RADIUS_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass)
  {
    if (baseClass == DescriptionStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE_STYLE__DESCRIPTION:
        return NotationPackage.DESCRIPTION_STYLE__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == FillStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE_STYLE__FILL_COLOR:
        return NotationPackage.FILL_STYLE__FILL_COLOR;
      case NotationPackage.SHAPE_STYLE__TRANSPARENCY:
        return NotationPackage.FILL_STYLE__TRANSPARENCY;
      case NotationPackage.SHAPE_STYLE__GRADIENT:
        return NotationPackage.FILL_STYLE__GRADIENT;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE_STYLE__LINE_COLOR:
        return NotationPackage.LINE_STYLE__LINE_COLOR;
      case NotationPackage.SHAPE_STYLE__LINE_WIDTH:
        return NotationPackage.LINE_STYLE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (derivedFeatureID)
      {
      case NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass)
  {
    if (baseClass == DescriptionStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.DESCRIPTION_STYLE__DESCRIPTION:
        return NotationPackage.SHAPE_STYLE__DESCRIPTION;
      default:
        return -1;
      }
    }
    if (baseClass == FillStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.FILL_STYLE__FILL_COLOR:
        return NotationPackage.SHAPE_STYLE__FILL_COLOR;
      case NotationPackage.FILL_STYLE__TRANSPARENCY:
        return NotationPackage.SHAPE_STYLE__TRANSPARENCY;
      case NotationPackage.FILL_STYLE__GRADIENT:
        return NotationPackage.SHAPE_STYLE__GRADIENT;
      default:
        return -1;
      }
    }
    if (baseClass == LineStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.LINE_STYLE__LINE_COLOR:
        return NotationPackage.SHAPE_STYLE__LINE_COLOR;
      case NotationPackage.LINE_STYLE__LINE_WIDTH:
        return NotationPackage.SHAPE_STYLE__LINE_WIDTH;
      default:
        return -1;
      }
    }
    if (baseClass == RoundedCornersStyle.class)
    {
      switch (baseFeatureID)
      {
      case NotationPackage.ROUNDED_CORNERS_STYLE__ROUNDED_BENDPOINTS_RADIUS:
        return NotationPackage.SHAPE_STYLE__ROUNDED_BENDPOINTS_RADIUS;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ShapeStyleImpl
