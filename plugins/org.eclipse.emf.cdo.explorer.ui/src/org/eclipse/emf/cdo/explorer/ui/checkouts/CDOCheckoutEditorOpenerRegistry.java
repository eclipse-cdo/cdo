/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.InteractiveConflictHandlerSelector;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.transaction.CDOHandlingConflictResolver;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOCheckoutEditorOpenerRegistry extends Container<CDOCheckoutEditorOpener>
{
  public static final CDOCheckoutEditorOpenerRegistry INSTANCE = new CDOCheckoutEditorOpenerRegistry();

  private static final String EXT_POINT = "editorOpeners"; //$NON-NLS-1$

  private final Map<String, CDOCheckoutEditorOpener> editorOpeners = new HashMap<String, CDOCheckoutEditorOpener>();

  public CDOCheckoutEditorOpenerRegistry()
  {
    addEditorOpener(new CDOModelEditorOpener());
  }

  public IEditorPart openEditor(IWorkbenchPage page, URI uri)
  {
    if (uri == null)
    {
      return null;
    }

    for (CDOCheckoutEditorOpener editorOpener : getEditorOpeners(uri))
    {
      IEditorPart editor = editorOpener.openEditor(page, uri);
      if (editor != null)
      {
        return editor;
      }
    }

    return null;
  }

  public CDOCheckoutEditorOpener getEditorOpener(String id)
  {
    synchronized (editorOpeners)
    {
      return editorOpeners.get(id);
    }
  }

  public CDOCheckoutEditorOpener[] getEditorOpeners(URI uri)
  {
    List<CDOCheckoutEditorOpener> result = new ArrayList<CDOCheckoutEditorOpener>();

    synchronized (editorOpeners)
    {
      for (CDOCheckoutEditorOpener editorOpener : editorOpeners.values())
      {
        if (editorOpener.matchesRegex(uri))
        {
          result.add(editorOpener);
        }
      }
    }

    // Sort highest priority first
    Collections.sort(result, new Comparator<CDOCheckoutEditorOpener>()
    {
      public int compare(CDOCheckoutEditorOpener o1, CDOCheckoutEditorOpener o2)
      {
        return -Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
      }
    });

    return result.toArray(new CDOCheckoutEditorOpener[result.size()]);
  }

  public void addEditorOpener(CDOCheckoutEditorOpener editorOpener)
  {
    boolean added;
    synchronized (editorOpeners)
    {
      String id = editorOpener.getID();
      added = !editorOpeners.containsKey(id);
      if (added)
      {
        editorOpeners.put(id, editorOpener);
      }
    }

    if (added)
    {
      fireElementAddedEvent(editorOpener);
    }
  }

  public void removeEditorOpener(CDOCheckoutEditorOpener editorOpener)
  {
    boolean removed;
    synchronized (editorOpeners)
    {
      String id = editorOpener.getID();
      removed = editorOpeners.remove(id) != null;
    }

    if (removed)
    {
      fireElementRemovedEvent(editorOpener);
    }
  }

  public CDOCheckoutEditorOpener[] getElements()
  {
    synchronized (editorOpeners)
    {
      return editorOpeners.values().toArray(new CDOCheckoutEditorOpener[editorOpeners.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (editorOpeners)
    {
      return editorOpeners.isEmpty();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      try
      {
        readExtensions();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }
  }

  public void readExtensions()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
    for (IConfigurationElement element : configurationElements)
    {
      try
      {
        EditorOpenerDescriptor descriptor = new EditorOpenerDescriptor(element);
        addEditorOpener(descriptor);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CDOModelEditorOpener extends CDOCheckoutEditorOpener.Default
  {
    private static final String REGEX = "cdo\\.checkout://.*";

    public CDOModelEditorOpener()
    {
      super(CDOEditorUtil.EDITOR_ID, "CDO Editor", OM.getImageDescriptor("icons/cdo_editor.gif"), REGEX, 100);
    }

    @Override
    protected IEditorPart doOpenEditor(final IWorkbenchPage page, URI uri)
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(uri);
      final CDOView view = checkout.openView();

      if (view instanceof CDOTransaction)
      {
        CDOHandlingConflictResolver conflictResolver = new CDOHandlingConflictResolver();
        conflictResolver.setConflictHandlerSelector(new InteractiveConflictHandlerSelector());

        CDOTransaction transaction = (CDOTransaction)view;
        transaction.options().addConflictResolver(conflictResolver);
      }

      final IEditorPart editor = openEditor(page, view, CDOURIUtil.extractResourcePath(uri));
      page.addPartListener(new IPartListener()
      {
        public void partClosed(IWorkbenchPart part)
        {
          if (part == editor)
          {
            try
            {
              view.close();
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
            }
            finally
            {
              page.removePartListener(this);
            }
          }
        }

        public void partOpened(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partDeactivated(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partBroughtToTop(IWorkbenchPart part)
        {
          // Do nothing.
        }

        public void partActivated(IWorkbenchPart part)
        {
          // Do nothing.
        }
      });

      return editor;
    }

    private IEditorPart openEditor(IWorkbenchPage page, CDOView view, String resourcePath)
    {
      try
      {
        String editorID = CDOEditorUtil.getEditorID();

        IEditorReference[] references = CDOEditorUtil.findEditor(page, view, resourcePath);
        for (IEditorReference reference : references)
        {
          if (editorID.equals(reference.getId()))
          {
            IEditorPart editor = references[0].getEditor(true);
            page.activate(editor);
            return editor;
          }
        }

        IEditorInput input = CDOEditorUtil.createCDOEditorInput(view, resourcePath, false);
        return page.openEditor(input, editorID);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class EditorOpenerDescriptor extends CDOCheckoutEditorOpener.Default
  {
    private IConfigurationElement element;

    public EditorOpenerDescriptor(IConfigurationElement element)
    {
      super(getID(element), getName(element), getIcon(element), getRegex(element), getPriority(element));
      this.element = element;

      if (StringUtil.isEmpty(element.getAttribute("class"))) //$NON-NLS-1$
      {
        throw new IllegalArgumentException(MessageFormat.format("Class not defined for extension {0}", element)); //$NON-NLS-1$
      }
    }

    @Override
    protected IEditorPart doOpenEditor(IWorkbenchPage page, URI uri)
    {
      return getEditorOpener().openEditor(page, uri);
    }

    private CDOCheckoutEditorOpener getEditorOpener()
    {
      try
      {
        return (CDOCheckoutEditorOpener)element.createExecutableExtension("class"); //$NON-NLS-1$
      }
      catch (CoreException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    private static String getID(IConfigurationElement element)
    {
      String value = element.getAttribute("id"); //$NON-NLS-1$
      if (StringUtil.isEmpty(value))
      {
        throw new IllegalArgumentException(MessageFormat.format("ID not defined for extension {0}", element)); //$NON-NLS-1$
      }

      return value;
    }

    private static String getName(IConfigurationElement element)
    {
      String value = element.getAttribute("name"); //$NON-NLS-1$
      if (StringUtil.isEmpty(value))
      {
        throw new IllegalArgumentException(MessageFormat.format("Name not defined for extension {0}", element)); //$NON-NLS-1$
      }

      return value;
    }

    private static ImageDescriptor getIcon(IConfigurationElement element)
    {
      String icon = element.getAttribute("icon"); //$NON-NLS-1$
      if (icon != null)
      {
        try
        {
          return AbstractUIPlugin.imageDescriptorFromPlugin(element.getNamespaceIdentifier(), icon);
        }
        catch (Exception ex)
        {
          //$FALL-THROUGH$
        }
      }

      return null;
    }

    private static String getRegex(IConfigurationElement element)
    {
      String value = element.getAttribute("regex"); //$NON-NLS-1$
      if (StringUtil.isEmpty(value))
      {
        throw new IllegalArgumentException(MessageFormat.format("Regex not defined for extension {0}", element)); //$NON-NLS-1$
      }

      return value;
    }

    private static int getPriority(IConfigurationElement element)
    {
      try
      {
        String value = element.getAttribute("priority"); //$NON-NLS-1$
        return Integer.parseInt(value);
      }
      catch (Exception ex)
      {
        return DEFAULT_PRIORITY;
      }
    }
  }
}
