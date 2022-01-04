/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.provider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.StyledString;
import org.eclipse.emf.edit.provider.StyledString.Style;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.swt.graphics.Font;

/**
 * @author Eike Stepper
 */
public final class URIStyler
{
  public static final int WORKSPACE = 0;

  public static final int EXTERNAL = 1;

  public static final int MISSING = 2;

  private static final URI COLOR_WORKSPACE = URI.createURI("color://rgb/0/0/0");

  private static final URI COLOR_EXTERNAL = URI.createURI("color://rgb/128/128/150");

  private static final URI COLOR_MISSING = URI.createURI("color://rgb/220/60/60");

  private static final URI FONT_BOLD = URI.createURI("font:////bold");

  private static final Style[][] STYLES = { styles(COLOR_WORKSPACE), styles(COLOR_EXTERNAL), styles(COLOR_MISSING) };

  private static final StyledString EMPTY = new StyledString();

  private URIStyler()
  {
  }

  public static void setDefaultFont(Font font)
  {
    EMFLabelProvider.INSTANCE.setDefaultFont(font);
  }

  public static StyledString getEMFStyledURI(URI uri, int colorIndex)
  {
    if (uri == null)
    {
      return EMPTY;
    }

    String fragment = uri.fragment();
    uri = uri.trimFragment();
    String lastSegment = uri.lastSegment();
    uri = uri.trimSegments(1).appendSegment("");

    StyledString styledLabel = new StyledString();
    styledLabel.append(uri.toString(), STYLES[colorIndex][0]);
    styledLabel.append(lastSegment, STYLES[colorIndex][1]);
    styledLabel.append(fragment, STYLES[colorIndex][0]);
    return styledLabel;
  }

  public static org.eclipse.jface.viewers.StyledString getJFaceStyledURI(URI uri, int colorIndex)
  {
    if (EMFLabelProvider.INSTANCE.getDefaultFont() == null)
    {
      throw new IllegalStateException("setDefaultFont() must be called");
    }

    StyledString emfStyledURI = getEMFStyledURI(uri, colorIndex);
    return EMFLabelProvider.INSTANCE.toJFaceStyledString(emfStyledURI);
  }

  private static Style[] styles(URI color)
  {
    return new Style[] { //
        Style.newBuilder().setForegroundColor(color).toStyle(), //
        Style.newBuilder().setForegroundColor(color).setFont(FONT_BOLD).toStyle() //
    };
  }

  /**
   * @author Eike Stepper
   */
  private static final class EMFLabelProvider extends AdapterFactoryLabelProvider
  {
    private static final EMFLabelProvider INSTANCE = new EMFLabelProvider();

    private EMFLabelProvider()
    {
      super(null);
    }

    /**
     * Make public.
     */
    @Override
    public org.eclipse.jface.viewers.StyledString toJFaceStyledString(StyledString styledString)
    {
      return super.toJFaceStyledString(styledString);
    }
  }
}
