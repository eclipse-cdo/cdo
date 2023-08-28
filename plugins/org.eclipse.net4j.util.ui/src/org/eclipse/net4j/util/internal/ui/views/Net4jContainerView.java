/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.internal.ui.messages.Messages;
import org.eclipse.net4j.util.ui.actions.ToggleAction;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.ManagedContainerItemProvider.Wrapper;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * @author Eike Stepper
 */
public class Net4jContainerView extends ContainerView
{
  private final IAction showFactoriesAction = new ShowFactoriesAction();

  private final IAction decorateElementsAction = new DecorateElementsAction();

  public Net4jContainerView()
  {
  }

  @Override
  public Net4jContainerItemProvider getItemProvider()
  {
    return (Net4jContainerItemProvider)super.getItemProvider();
  }

  @Override
  protected Net4jContainerItemProvider createContainerItemProvider()
  {
    Net4jContainerItemProvider itemProvider = new Net4jContainerItemProvider();
    itemProvider.setShowFactories(OM.PREF_SHOW_CONTAINER_FACTORIES.getValue());
    return itemProvider;
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ILabelDecorator createLabelDecorator()
  {
    return new ILabelDecorator()
    {
      @Override
      public String decorateText(String text, Object element)
      {
        if (OM.PREF_DECORATE_CONTAINER_ELEMENTS.getValue() == Boolean.TRUE)
        {
          Net4jContainerItemProvider itemProvider = getItemProvider();
          if (itemProvider.isShowFactories())
          {
            if (element instanceof Wrapper)
            {
              int elementCount = ((Wrapper)element).getElementCount();
              if (elementCount > 0)
              {
                text += "  [" + elementCount + "]";
              }
            }
          }
          else
          {
            String[] key = getContainer().getElementKey(element);
            if (key != null)
            {
              String productGroup = key[0];
              String factoryType = key[1];
              String description = key[2];

              String decoration = productGroup + ": " + factoryType;
              if (!StringUtil.isEmpty(description))
              {
                decoration += "(" + description + ")";
              }

              text += "  " + decoration;
            }
          }
        }

        return text;
      }

      @Override
      public Image decorateImage(Image image, Object element)
      {
        return null;
      }

      @Override
      public boolean isLabelProperty(Object element, String property)
      {
        return false;
      }

      @Override
      public void addListener(ILabelProviderListener listener)
      {
      }

      @Override
      public void removeListener(ILabelProviderListener listener)
      {
      }

      @Override
      public void dispose()
      {
      }
    };
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    super.fillLocalToolBar(manager);
    manager.add(showFactoriesAction);
    manager.add(decorateElementsAction);
  }

  /**
   * @author Eike Stepper
   */
  private final class ShowFactoriesAction extends ToggleAction
  {
    public ShowFactoriesAction()
    {
      super(Messages.getString("Net4jContainerView_0"), //$NON-NLS-1$
          SharedIcons.getDescriptor(SharedIcons.OBJ_FACTORY), OM.PREF_SHOW_CONTAINER_FACTORIES);
    }

    @Override
    protected void run(boolean checked)
    {
      getItemProvider().setShowFactories(checked);
      refreshViewer(true);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class DecorateElementsAction extends ToggleAction
  {
    public DecorateElementsAction()
    {
      super(Messages.getString("Net4jContainerView_1"), //$NON-NLS-1$
          OM.getImageDescriptor("icons/decorate_container_element.png"), //$NON-NLS-1$
          OM.PREF_DECORATE_CONTAINER_ELEMENTS);
    }

    @Override
    protected void run(boolean checked)
    {
      refreshViewer(true);
    }
  }
}
