/*
 * Copyright (c) 2015, 2016, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerElement;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.transfer.CDOTransferElement;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutLabelProvider extends LabelProvider implements ICommonLabelProvider, IColorProvider, IStyledLabelProvider
{
  public static final Image ERROR_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

  private static final IEditorRegistry EDITOR_REGISTRY = PlatformUI.getWorkbench().getEditorRegistry();

  private final EditorRegistryListener editorRegistryListener = new EditorRegistryListener();

  private ICommonContentExtensionSite config;

  private CDOCheckoutStateManager stateManager;

  public CDOCheckoutLabelProvider()
  {
  }

  public CDOCheckoutLabelProvider(CDOCheckoutContentProvider contentProvider)
  {
    stateManager = contentProvider.getStateManager();
  }

  @Override
  public void init(ICommonContentExtensionSite config)
  {
    this.config = config;
    EDITOR_REGISTRY.addPropertyListener(editorRegistryListener);
  }

  @Override
  public void dispose()
  {
    EDITOR_REGISTRY.removePropertyListener(editorRegistryListener);
    super.dispose();
  }

  @Override
  public void saveState(IMemento aMemento)
  {
    // Do nothing.
  }

  @Override
  public void restoreState(IMemento aMemento)
  {
    // Do nothing.
  }

  public void fireLabelProviderChanged()
  {
    fireLabelProviderChanged(new LabelProviderChangedEvent(this));
  }

  @Override
  public Color getForeground(Object object)
  {
    try
    {
      IColorProvider provider = getStateManager().getLabelProvider(object);
      if (provider != null)
      {
        return provider.getForeground(object);
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  @Override
  public Color getBackground(Object object)
  {
    try
    {
      IColorProvider provider = getStateManager().getLabelProvider(object);
      if (provider != null)
      {
        return provider.getBackground(object);
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  @Override
  public String getDescription(Object object)
  {
    if (object instanceof CDOExplorerElement)
    {
      CDOExplorerElement element = (CDOExplorerElement)object;
      return element.getDescription();
    }

    return null;
  }

  @Override
  public StyledString getStyledText(Object object)
  {
    try
    {
      IStyledLabelProvider provider = getStateManager().getLabelProvider(object);
      if (provider != null)
      {
        return provider.getStyledText(object);
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    String text = getText(object);
    return new StyledString(text);
  }

  @Override
  public String getText(Object object)
  {
    if (object instanceof ViewerUtil.Pending)
    {
      ViewerUtil.Pending pending = (ViewerUtil.Pending)object;
      return pending.getText();
    }

    try
    {
      ILabelProvider provider = getStateManager().getLabelProvider(object);
      if (provider != null)
      {
        String text = provider.getText(object);
        if (!StringUtil.isEmpty(text))
        {
          return text;
        }
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    try
    {
      String text = super.getText(object);
      if (!StringUtil.isEmpty(text))
      {
        return text;
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    try
    {
      if (object instanceof EObject)
      {
        EObject eObject = (EObject)object;
        EClass eClass = eObject.eClass();
        String text = getText(eClass);
        if (!StringUtil.isEmpty(text))
        {
          return text;
        }
      }
    }
    catch (Exception ignore)
    {
      //$FALL-THROUGH$
    }

    return object.getClass().getSimpleName();
  }

  @Override
  public Image getImage(Object object)
  {
    if (object instanceof ViewerUtil.Pending)
    {
      return ContainerItemProvider.PENDING_IMAGE;
    }

    if (object instanceof ViewerUtil.Error)
    {
      return ContainerItemProvider.ERROR_IMAGE;
    }

    try
    {
      ILabelProvider provider = getStateManager().getLabelProvider(object);
      if (provider != null)
      {
        return provider.getImage(object);
      }
    }
    catch (Exception ex)
    {
      //$FALL-THROUGH$
    }

    try
    {
      return super.getImage(object);
    }
    catch (Exception ex)
    {
      return ERROR_IMAGE;
    }
  }

  public CDOCheckoutStateManager getStateManager()
  {
    if (stateManager == null)
    {
      String viewerID = config.getService().getViewerId();
      CDOCheckoutContentProvider contentProvider = CDOCheckoutContentProvider.getInstance(viewerID);
      if (contentProvider != null)
      {
        stateManager = contentProvider.getStateManager();
      }
    }

    return stateManager;
  }

  public void setStateManager(CDOCheckoutStateManager stateManager)
  {
    this.stateManager = stateManager;
  }

  /**
   * A {@link IPropertyListener listener} on the platform's {@link IEditorRegistry editor registry} that fires {@link LabelProviderChangedEvent label events}
   * from the associated {@link #getItemProvider() item provider} when {@link CDOTransferElement element} labels need to be updated.
   *
   * @author Eike Stepper
   */
  private class EditorRegistryListener implements IPropertyListener
  {
    @Override
    public void propertyChanged(Object source, int propId)
    {
      if (propId == IEditorRegistry.PROP_CONTENTS)
      {
        fireLabelProviderChanged();
      }
    }
  }
}
