/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;

/**
 * Decorates labels of {@link CDOObject CDO objects} according to a pattern.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 */
public class CDOLabelDecorator implements ILabelDecorator
{
  public static final String[] DECORATION_PROPOSALS = { "${element}", "${id}", "${state}", "${created}", "${revised}" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

  public static final String DEFAULT_DECORATION = DECORATION_PROPOSALS[0]
      + " [" + DECORATION_PROPOSALS[1] + ", " + DECORATION_PROPOSALS[2] + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

  public static final String NO_DECORATION = DECORATION_PROPOSALS[0];

  private static final String[] DECORATION_ARGS = {
      "{0}", "{1}", "{2}", "{3,date} {3,time,HH:mm:ss:SSS}", "{4,date} {4,time,HH:mm:ss:SSS}" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

  public static final String DECORATOR_ID = "org.eclipse.emf.cdo.ui.CDOLabelDecorator"; //$NON-NLS-1$

  private String pattern;

  private IListener preferenceListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      @SuppressWarnings("unchecked")
      OMPreferencesChangeEvent<String> preferenceChangeEvent = (OMPreferencesChangeEvent<String>)event;
      if (OM.PREF_LABEL_DECORATION.getName().equals(preferenceChangeEvent.getPreference().getName()))
      {
        pattern = parsePattern(preferenceChangeEvent.getNewValue());
      }
    }
  };

  public CDOLabelDecorator()
  {
    pattern = parsePattern(OM.PREF_LABEL_DECORATION.getValue());
    OM.PREFS.addListener(preferenceListener);
  }

  public void dispose()
  {
    OM.PREFS.removeListener(preferenceListener);
  }

  public String parsePattern(String unparsedPattern)
  {
    if (ObjectUtil.equals(unparsedPattern, NO_DECORATION))
    {
      return null;
    }

    return StringUtil.replace(unparsedPattern, DECORATION_PROPOSALS, DECORATION_ARGS);
  }

  public CDOLabelDecorator(String pattern)
  {
    this.pattern = pattern;
  }

  public Image decorateImage(Image image, Object element)
  {
    // Use default
    return null;
  }

  public String decorateText(String text, Object element)
  {
    try
    {
      if (pattern != null && element instanceof InternalCDOObject)
      {
        InternalCDOView view = ((InternalCDOObject)element).cdoView();
        InternalCDOObject obj = FSMUtil.adapt(element, view);
        CDORevision rev = obj.cdoRevision();
        long created = rev == null ? CDORevision.UNSPECIFIED_DATE : rev.getTimeStamp();
        long revised = rev == null ? CDORevision.UNSPECIFIED_DATE : rev.getRevised();
        text = MessageFormat.format(pattern, text, obj.cdoID(), obj.cdoState(), created, revised);
      }
    }
    catch (RuntimeException ignore)
    {
    }

    return text;
  }

  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  public void addListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles it
  }

  public void removeListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles it
  }
}
