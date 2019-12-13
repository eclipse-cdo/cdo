/*
 * Copyright (c) 2008-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOElement.StateProvider;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.pref.OMPreferencesChangeEvent;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

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

  public static final String DEFAULT_DECORATION = DECORATION_PROPOSALS[0] + "  " + DECORATION_PROPOSALS[2]; //$NON-NLS-1$

  public static final String NO_DECORATION = DECORATION_PROPOSALS[0];

  private static final String[] DECORATION_ARGS = { "{0}", "{1}", "{2}", "{3,date} {3,time,HH:mm:ss:SSS}", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      "{4,date} {4,time,HH:mm:ss:SSS}" }; //$NON-NLS-1$

  public static final String DECORATOR_ID = "org.eclipse.emf.cdo.ui.CDOLabelDecorator"; //$NON-NLS-1$

  private static final Image LOCK_OVERLAY = SharedIcons.getImage(SharedIcons.OVR_LOCK);

  private static final Image LOCK_SELF_OVERLAY = SharedIcons.getImage(SharedIcons.OVR_LOCK_SELF);

  private String pattern;

  private IListener preferenceListener = new IListener()
  {
    @Override
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

  public CDOLabelDecorator(String pattern)
  {
    this.pattern = pattern;
  }

  @Override
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

  @Override
  public Image decorateImage(Image image, Object element)
  {
    try
    {
      image = decorate(image, element);
    }
    catch (RuntimeException ignore)
    {
    }

    return image;
  }

  @Override
  public String decorateText(String text, Object element)
  {
    try
    {
      if (pattern != null)
      {
        CDOElement cdoElement = AdapterUtil.adapt(element, CDOElement.class);
        if (cdoElement != null)
        {
          element = cdoElement.getDelegate();
        }

        if (element instanceof EObject)
        {
          EObject eObject = (EObject)element;
          InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(eObject, false);
          if (cdoObject != null)
          {
            CDOID id = cdoObject.cdoID();
            String state = getObjectState(cdoObject);

            CDORevision rev = cdoObject.cdoRevision();
            long created = rev == null ? CDORevision.UNSPECIFIED_DATE : rev.getTimeStamp();
            long revised = rev == null ? CDORevision.UNSPECIFIED_DATE : rev.getRevised();
            text = MessageFormat.format(pattern, text, id, state, created, revised).trim();
          }
        }
      }
    }
    catch (RuntimeException ignore)
    {
    }

    return text;
  }

  @Override
  public boolean isLabelProperty(Object element, String property)
  {
    return false;
  }

  @Override
  public void addListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles them.
  }

  @Override
  public void removeListener(ILabelProviderListener listener)
  {
    // Ignore listeners, DecoratorManager handles them.
  }

  /**
   * @since 4.4
   */
  protected String getObjectState(InternalCDOObject object)
  {
    CDOState state = null;

    StateProvider stateProvider = AdapterUtil.adapt(object, StateProvider.class);
    if (stateProvider != null)
    {
      state = stateProvider.getState(object);
    }
    else
    {
      state = object.cdoState();
    }

    if (state == null)
    {
      return "";
    }

    return state.toString().toLowerCase();
  }

  /**
   * @since 4.4
   */
  public static Image decorate(Image image, Object element)
  {
    if (element instanceof EObject)
    {
      EObject eObject = (EObject)element;
      CDOObject cdoObject = CDOUtil.getCDOObject(eObject, false);
      if (cdoObject != null)
      {
        CDOLockState lockState = cdoObject.cdoLockState();
        if (lockState != null)
        {
          CDOLockOwner owner = lockState.getWriteLockOwner();
          if (owner != null)
          {
            if (owner.equals(cdoObject.cdoView()))
            {
              image = OM.getOverlayImage(image, LOCK_SELF_OVERLAY, 10, 0);
            }
            else
            {
              image = OM.getOverlayImage(image, LOCK_OVERLAY, 10, 0);
            }
          }
        }
      }
    }

    // Use default
    return image;
  }
}
