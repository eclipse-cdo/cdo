/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests.ui.util;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefContextMenu;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.part.MultiPageEditorPart;

import org.hamcrest.SelfDescribing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Martin Fluegge
 */
public class DawnSWTBotEMFEditor extends SWTBotEditor
{
  public DawnSWTBotEMFEditor(IEditorReference editorReference, SWTWorkbenchBot bot) throws WidgetNotFoundException
  {
    super(editorReference, bot);
  }

  public DawnSWTBotEMFEditor(IEditorReference editorReference, SWTWorkbenchBot bot, SelfDescribing description)
  {
    super(editorReference, bot, description);
  }

  /**
   * return the tree of the EMF editor
   */
  public SWTBotTree getSelectionPageTree()
  {
    MultiPageEditorPart editor = (MultiPageEditorPart)getReference().getEditor(false);
    SWTBotTree swtBotTree = null;
    try
    {
      setActivePage(editor, 0);

      TreeViewer viewer = (TreeViewer)getCurrentViewer();
      swtBotTree = new SWTBotTree(viewer.getTree());
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }

    return swtBotTree;
  }

  public void setActivePage(int i)
  {
    MultiPageEditorPart editor = (MultiPageEditorPart)getReference().getEditor(false);
    try
    {
      setActivePage(editor, i);
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public DawnSWTBotEMFEditor clickContextMenu(Control control, String text) throws WidgetNotFoundException
  {
    new SWTBotGefContextMenu(control, text).click();
    return this;
  }

  protected Object getSelectedPage(final MultiPageEditorPart editor)
  {
    Object ret = null;

    RunnableWithResult runnable = new RunnableWithResult(ret)
    {
      @Override
      public void run()
      {
        result = editor.getSelectedPage();
      }
    };
    editor.getSite().getShell().getDisplay().syncExec(runnable);

    return runnable.getResult();
  }

  protected Viewer getCurrentViewer()
  {
    final MultiPageEditorPart editor = (MultiPageEditorPart)getReference().getEditor(false);

    IEditorPart ret = null;

    RunnableWithResult runnable = new RunnableWithResult(ret)
    {

      @Override
      public void run()
      {
        try
        {
          Class<?>[] parameterTypes = new Class[0];
          Method method = getMethod(editor, "getViewer", parameterTypes);
          method.setAccessible(true);
          Object[] params = new Object[0];
          result = method.invoke(editor, params);
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }
    };

    editor.getSite().getShell().getDisplay().syncExec(runnable);
    return (Viewer)runnable.getResult();
  }

  protected IEditorPart getActiveEditor()
  {
    final MultiPageEditorPart editor = (MultiPageEditorPart)getReference().getEditor(false);

    IEditorPart ret = null;

    RunnableWithResult runnable = new RunnableWithResult(ret)
    {
      @Override
      public void run()
      {
        try
        {
          Class<?>[] parameterTypes = new Class[0];

          Class<?> superClass = getSuperClass(editor.getClass(), MultiPageEditorPart.class);

          Method method = superClass.getDeclaredMethod("getActiveEditor", parameterTypes);
          method.setAccessible(true);
          Object[] params = new Object[0];
          result = method.invoke(editor, params);
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }
    };

    editor.getSite().getShell().getDisplay().syncExec(runnable);
    return (IEditorPart)runnable.getResult();
  }

  protected void setActivePage(final MultiPageEditorPart editor, final int i) throws SecurityException,
      NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    editor.getSite().getShell().getDisplay().syncExec(new Runnable()
    {
      public void run()
      {
        try
        {
          Class<?>[] parameterTypes = new Class[1];
          parameterTypes[0] = int.class;

          Class<?> superClass = getSuperClass(editor.getClass(), MultiPageEditorPart.class);

          Method method = superClass.getDeclaredMethod("setActivePage", parameterTypes);

          boolean accessible = method.isAccessible();
          method.setAccessible(true);

          Object[] params = new Object[1];
          params[0] = i;
          method.invoke(editor, params);
          method.setAccessible(accessible);
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }
    });
  }

  private Class<?> getSuperClass(Class<?> a, Class<?> b)
  {
    Class<?> ret = a;
    while (ret != b && ret != Object.class)
    {
      ret = ret.getSuperclass();
    }
    return ret;
  }

  protected abstract class RunnableWithResult implements Runnable
  {
    protected Object result;

    public RunnableWithResult(Object result)
    {
      this.result = result;
    }

    public abstract void run();

    public Object getResult()
    {
      return result;
    }
  }

  private Method getMethod(final MultiPageEditorPart editor, String methodName, Class<?>[] parameterTypes)
      throws NoSuchMethodException
  {
    Method method = null;
    Class<?> clazz = editor.getClass();
    while (method == null && clazz != Object.class)
    {
      try
      {
        method = clazz.getDeclaredMethod(methodName, parameterTypes);
      }
      catch (NoSuchMethodException ex)
      {
        clazz = clazz.getSuperclass();
      }
    }
    return method;
  }

  public void setText(final SWTBotTreeItem item, final String text)
  {
    MultiPageEditorPart editor = (MultiPageEditorPart)getReference().getEditor(false);
    Display display = editor.getEditorSite().getShell().getDisplay();

    display.asyncExec(new Runnable()
    {
      public void run()
      {
        item.widget.setData(text);
      }
    });
  }
}
