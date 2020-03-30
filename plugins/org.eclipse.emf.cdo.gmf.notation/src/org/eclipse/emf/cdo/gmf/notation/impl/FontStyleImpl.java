/******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.FontStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Font Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#getFontColor <em>Font Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#getFontName <em>Font Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#getFontHeight <em>Font Height</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#isBold <em>Bold</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#isItalic <em>Italic</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#isUnderline <em>Underline</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FontStyleImpl#isStrikeThrough <em>Strike Through</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class FontStyleImpl extends CDOObjectImpl implements FontStyle
{
  /**
  * The default value of the '{@link #getFontColor() <em>Font Color</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getFontColor()
  * @generated
  * @ordered
  */
  protected static final int FONT_COLOR_EDEFAULT = 0;

  /**
  * The default value of the '{@link #getFontName() <em>Font Name</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getFontName()
  * @generated
  * @ordered
  */
  protected static final String FONT_NAME_EDEFAULT = "Tahoma"; //$NON-NLS-1$

  /**
  * The default value of the '{@link #getFontHeight() <em>Font Height</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getFontHeight()
  * @generated
  * @ordered
  */
  protected static final int FONT_HEIGHT_EDEFAULT = 9;

  /**
  * The default value of the '{@link #isBold() <em>Bold</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isBold()
  * @generated
  * @ordered
  */
  protected static final boolean BOLD_EDEFAULT = false;

  /**
  * The default value of the '{@link #isItalic() <em>Italic</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isItalic()
  * @generated
  * @ordered
  */
  protected static final boolean ITALIC_EDEFAULT = false;

  /**
  * The default value of the '{@link #isUnderline() <em>Underline</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isUnderline()
  * @generated
  * @ordered
  */
  protected static final boolean UNDERLINE_EDEFAULT = false;

  /**
  * The default value of the '{@link #isStrikeThrough() <em>Strike Through</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isStrikeThrough()
  * @generated
  * @ordered
  */
  protected static final boolean STRIKE_THROUGH_EDEFAULT = false;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected FontStyleImpl()
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
    return NotationPackage.Literals.FONT_STYLE;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public String getFontName()
  {
    return (String)eDynamicGet(NotationPackage.FONT_STYLE__FONT_NAME, NotationPackage.Literals.FONT_STYLE__FONT_NAME, true, true);
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
  public void setFontNameGen(String newFontName)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__FONT_NAME, NotationPackage.Literals.FONT_STYLE__FONT_NAME, newFontName);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getFontHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.FONT_STYLE__FONT_HEIGHT, NotationPackage.Literals.FONT_STYLE__FONT_HEIGHT, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setFontHeight(int newFontHeight)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__FONT_HEIGHT, NotationPackage.Literals.FONT_STYLE__FONT_HEIGHT, new Integer(newFontHeight));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isBold()
  {
    return ((Boolean)eDynamicGet(NotationPackage.FONT_STYLE__BOLD, NotationPackage.Literals.FONT_STYLE__BOLD, true, true)).booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setBold(boolean newBold)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__BOLD, NotationPackage.Literals.FONT_STYLE__BOLD, new Boolean(newBold));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isItalic()
  {
    return ((Boolean)eDynamicGet(NotationPackage.FONT_STYLE__ITALIC, NotationPackage.Literals.FONT_STYLE__ITALIC, true, true)).booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setItalic(boolean newItalic)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__ITALIC, NotationPackage.Literals.FONT_STYLE__ITALIC, new Boolean(newItalic));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isUnderline()
  {
    return ((Boolean)eDynamicGet(NotationPackage.FONT_STYLE__UNDERLINE, NotationPackage.Literals.FONT_STYLE__UNDERLINE, true, true)).booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setUnderline(boolean newUnderline)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__UNDERLINE, NotationPackage.Literals.FONT_STYLE__UNDERLINE, new Boolean(newUnderline));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public boolean isStrikeThrough()
  {
    return ((Boolean)eDynamicGet(NotationPackage.FONT_STYLE__STRIKE_THROUGH, NotationPackage.Literals.FONT_STYLE__STRIKE_THROUGH, true, true)).booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setStrikeThrough(boolean newStrikeThrough)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__STRIKE_THROUGH, NotationPackage.Literals.FONT_STYLE__STRIKE_THROUGH, new Boolean(newStrikeThrough));
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
    case NotationPackage.FONT_STYLE__FONT_COLOR:
      return new Integer(getFontColor());
    case NotationPackage.FONT_STYLE__FONT_NAME:
      return getFontName();
    case NotationPackage.FONT_STYLE__FONT_HEIGHT:
      return new Integer(getFontHeight());
    case NotationPackage.FONT_STYLE__BOLD:
      return isBold() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.FONT_STYLE__ITALIC:
      return isItalic() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.FONT_STYLE__UNDERLINE:
      return isUnderline() ? Boolean.TRUE : Boolean.FALSE;
    case NotationPackage.FONT_STYLE__STRIKE_THROUGH:
      return isStrikeThrough() ? Boolean.TRUE : Boolean.FALSE;
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
    case NotationPackage.FONT_STYLE__FONT_COLOR:
      setFontColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.FONT_STYLE__FONT_NAME:
      setFontName((String)newValue);
      return;
    case NotationPackage.FONT_STYLE__FONT_HEIGHT:
      setFontHeight(((Integer)newValue).intValue());
      return;
    case NotationPackage.FONT_STYLE__BOLD:
      setBold(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.FONT_STYLE__ITALIC:
      setItalic(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.FONT_STYLE__UNDERLINE:
      setUnderline(((Boolean)newValue).booleanValue());
      return;
    case NotationPackage.FONT_STYLE__STRIKE_THROUGH:
      setStrikeThrough(((Boolean)newValue).booleanValue());
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
    case NotationPackage.FONT_STYLE__FONT_COLOR:
      setFontColor(FONT_COLOR_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__FONT_NAME:
      setFontName(FONT_NAME_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__FONT_HEIGHT:
      setFontHeight(FONT_HEIGHT_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__BOLD:
      setBold(BOLD_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__ITALIC:
      setItalic(ITALIC_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__UNDERLINE:
      setUnderline(UNDERLINE_EDEFAULT);
      return;
    case NotationPackage.FONT_STYLE__STRIKE_THROUGH:
      setStrikeThrough(STRIKE_THROUGH_EDEFAULT);
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
    case NotationPackage.FONT_STYLE__FONT_COLOR:
      return getFontColor() != FONT_COLOR_EDEFAULT;
    case NotationPackage.FONT_STYLE__FONT_NAME:
      return FONT_NAME_EDEFAULT == null ? getFontName() != null : !FONT_NAME_EDEFAULT.equals(getFontName());
    case NotationPackage.FONT_STYLE__FONT_HEIGHT:
      return getFontHeight() != FONT_HEIGHT_EDEFAULT;
    case NotationPackage.FONT_STYLE__BOLD:
      return isBold() != BOLD_EDEFAULT;
    case NotationPackage.FONT_STYLE__ITALIC:
      return isItalic() != ITALIC_EDEFAULT;
    case NotationPackage.FONT_STYLE__UNDERLINE:
      return isUnderline() != UNDERLINE_EDEFAULT;
    case NotationPackage.FONT_STYLE__STRIKE_THROUGH:
      return isStrikeThrough() != STRIKE_THROUGH_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getFontColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.FONT_STYLE__FONT_COLOR, NotationPackage.Literals.FONT_STYLE__FONT_COLOR, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setFontColor(int newFontColor)
  {
    eDynamicSet(NotationPackage.FONT_STYLE__FONT_COLOR, NotationPackage.Literals.FONT_STYLE__FONT_COLOR, new Integer(newFontColor));
  }

} // FontStyleImpl
