/*
 * Copyright (c) 2007-2013, 2015, 2016, 2019-2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Christian W. Damus (CEA LIST) - bug 418452
 *    Christian W. Damus (CEA LIST) - bug 418454
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.security.IPasswordCredentialsProvider;
import org.eclipse.net4j.util.ui.security.InteractiveCredentialsProvider;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Eike Stepper
 */
public final class UIUtil
{
  /**
   * @since 3.1
   */
  public static final String ERROR_LOG_ID = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$

  private static Color redColor;

  private static Color grayColor;

  private static Image errorImage;

  private UIUtil()
  {
  }

  /**
   * @since 3.12
   */
  public static Color redColor()
  {
    if (redColor == null)
    {
      redColor = UIUtil.getDisplay().getSystemColor(SWT.COLOR_RED);
    }

    return redColor;
  }

  /**
   * @since 3.12
   */
  public static Color grayColor()
  {
    if (grayColor == null)
    {
      grayColor = UIUtil.getDisplay().getSystemColor(SWT.COLOR_GRAY);
    }

    return grayColor;
  }

  /**
   * @since 3.12
   */
  public static Image errorImage()
  {
    if (errorImage == null)
    {
      errorImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
    }

    return errorImage;
  }

  /**
   * @since 3.1
   */
  public static void copyToClipboard(Display display, String text)
  {
    Clipboard clipboard = null;

    try
    {
      clipboard = new Clipboard(display);
      clipboard.setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
    }
    finally
    {
      if (clipboard != null)
      {
        clipboard.dispose();
      }
    }
  }

  public static void dispose(Font font)
  {
    if (font != null)
    {
      font.dispose();
    }
  }

  public static void dispose(Color color)
  {
    if (color != null)
    {
      color.dispose();
    }
  }

  public static void dispose(Widget widget)
  {
    if (widget != null)
    {
      widget.dispose();
    }
  }

  /**
   * @since 3.3
   */
  public static Font getItalicFont(Control control)
  {
    FontData[] datas = control.getFont().getFontData().clone();
    datas[0].setStyle(SWT.ITALIC);
    Display display = control.getShell().getDisplay();
    Font font = new Font(display, datas);
    return font;
  }

  public static Font getBoldFont(Control control)
  {
    FontData[] datas = control.getFont().getFontData().clone();
    datas[0].setStyle(SWT.BOLD);
    Display display = control.getShell().getDisplay();
    Font font = new Font(display, datas);
    return font;
  }

  public static Display getDisplay()
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      try
      {
        display = PlatformUI.getWorkbench().getDisplay();
      }
      catch (Throwable ignore)
      {
        //$FALL-THROUGH$
      }
    }

    if (display == null)
    {
      display = Display.getDefault();
    }

    if (display == null)
    {
      display = new Display();
    }

    return display;
  }

  /**
   * @since 3.5
   */
  public static Shell getShell()
  {
    Shell[] shell = { null };
    Display display = getDisplay();

    syncExec(display, () -> {
      shell[0] = display.getActiveShell();

      if (shell[0] == null)
      {
        try
        {
          IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
          if (window == null)
          {
            IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            if (windows.length != 0)
            {
              window = windows[0];
            }
          }

          if (window != null)
          {
            shell[0] = window.getShell();
          }
        }
        catch (Throwable ignore)
        {
          //$FALL-THROUGH$
        }
      }

      if (shell[0] == null)
      {
        Shell[] shells = display.getShells();
        if (shells.length > 0)
        {
          shell[0] = shells[0];
        }
      }
    });

    return shell[0];
  }

  /**
   * @since 2.0
   */
  public static IWorkbench getWorkbench()
  {
    IWorkbench workbench = PlatformUI.getWorkbench();
    if (workbench == null)
    {
      throw new IllegalStateException("No workbench available"); //$NON-NLS-1$
    }

    return workbench;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow()
  {
    IWorkbench workbench = getWorkbench();

    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    if (window == null)
    {
      IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
      if (windows.length != 0)
      {
        window = windows[0];
      }
    }

    if (window == null)
    {
      throw new IllegalStateException("No active window available"); //$NON-NLS-1$
    }

    return window;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPage getActiveWorkbenchPage()
  {
    IWorkbenchWindow window = getActiveWorkbenchWindow();

    IWorkbenchPage page = window.getActivePage();
    if (page == null)
    {
      IWorkbenchPage[] pages = window.getPages();
      if (pages.length != 0)
      {
        page = pages[0];
      }
    }

    if (page == null)
    {
      throw new IllegalStateException("No active page available"); //$NON-NLS-1$
    }

    return page;
  }

  /**
   * @since 2.0
   */
  public static IWorkbenchPart getActiveWorkbenchPart()
  {
    IWorkbenchPart part = getActiveWorkbenchPage().getActivePart();
    if (part == null)
    {
      throw new IllegalStateException("No active part available"); //$NON-NLS-1$
    }

    return part;
  }

  /**
   * @since 3.0
   */
  public static Object getElementIfOne(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      if (ssel.size() == 1)
      {
        return ssel.getFirstElement();
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static Object getElement(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;
      return ssel.getFirstElement();
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static <T> T getElement(ISelection selection, Class<T> type)
  {
    Object element = getElement(selection);
    if (element != null && type.isInstance(element))
    {
      @SuppressWarnings("unchecked")
      T result = (T)element;
      return result;
    }

    return null;
  }

  /**
   * @since 3.5
   */
  public static List<Object> getElements(ISelection selection)
  {
    if (selection instanceof IStructuredSelection)
    {
      IStructuredSelection ssel = (IStructuredSelection)selection;

      @SuppressWarnings("unchecked")
      List<Object> result = ssel.toList();
      return result;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static <T> List<T> getElements(ISelection selection, Class<T> type)
  {
    List<Object> elements = getElements(selection);
    if (elements != null)
    {
      List<T> result = new ArrayList<>();

      for (Object element : elements)
      {
        if (type.isInstance(element))
        {
          @SuppressWarnings("unchecked")
          T match = (T)element;
          result.add(match);
        }
      }

      return result;
    }

    return null;
  }

  /**
   * Like {@link #getElement(ISelection, Class)} except that it attempts to adapt
   * {@link IAdaptable}s to the required {@code type}, if necessary.
   *
   * @since 3.4
   */
  public static <T> T adaptElement(ISelection selection, Class<T> type)
  {
    Object element = getElement(selection);
    return AdapterUtil.adapt(element, type);
  }

  /**
   * Like {@link #getElements(ISelection, Class)} except that it attempts to adapt
   * {@link IAdaptable}s to the required {@code type}, if necessary.
   *
   * @since 3.5
   */
  public static <T> List<T> adaptElements(ISelection selection, Class<T> type)
  {
    List<Object> elements = getElements(selection);
    if (elements != null)
    {
      List<T> result = new ArrayList<>();

      for (Object element : elements)
      {
        T match = AdapterUtil.adapt(element, type);
        if (match != null)
        {
          result.add(match);
        }
      }

      return result;
    }

    return null;
  }

  /**
   * @since 3.1
   */
  public static int setValidationContext(Control control, ValidationContext context)
  {
    int count = 0;
    if (control instanceof ValidationParticipant)
    {
      ValidationParticipant participant = (ValidationParticipant)control;
      if (participant.getValidationContext() == null)
      {
        participant.setValidationContext(context);
        ++count;
      }
    }

    if (control instanceof Composite)
    {
      Composite composite = (Composite)control;
      for (Control child : composite.getChildren())
      {
        count += setValidationContext(child, context);
      }
    }

    return count;
  }

  public static IPasswordCredentialsProvider createInteractiveCredentialsProvider()
  {
    return new InteractiveCredentialsProvider();
  }

  public static Composite createGridComposite(Composite parent, int columns)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(createGridLayout(columns));
    return composite;
  }

  public static GridLayout createGridLayout(int columns)
  {
    GridLayout layout = new GridLayout();
    layout.numColumns = columns;
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    layout.verticalSpacing = 0;
    layout.horizontalSpacing = 0;
    return layout;
  }

  public static GridData createGridData()
  {
    return createGridData(true, true);
  }

  public static GridData createGridData(boolean grabHorizontal, boolean grabVertical)
  {
    return new GridData(SWT.FILL, SWT.FILL, grabHorizontal, grabVertical);
  }

  /**
   * @since 3.4
   */
  public static GridData createGridData(int horizontalSpan, int verticalSpan)
  {
    GridData result = new GridData();
    result.horizontalSpan = horizontalSpan;
    result.verticalSpan = verticalSpan;
    return result;
  }

  /**
   * @since 3.0
   */
  public static GridData createEmptyGridData()
  {
    GridData data = new GridData();
    data.heightHint = 0;
    data.widthHint = 0;
    data.horizontalSpan = 0;
    data.horizontalAlignment = 0;
    data.horizontalIndent = 0;
    data.verticalAlignment = 0;
    data.verticalIndent = 0;
    data.verticalSpan = 0;
    data.minimumHeight = 0;
    data.minimumWidth = 0;
    data.grabExcessHorizontalSpace = false;
    data.grabExcessVerticalSpace = false;
    return data;
  }

  public static void addDecorationMargin(Control control)
  {
    Object data = control.getLayoutData();
    if (data instanceof GridData)
    {
      GridData gd = (GridData)data;
      FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
      FieldDecoration dec = registry.getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
      gd.horizontalIndent = dec.getImage().getBounds().width;
    }
  }

  /**
   * Adds indentation to the control. if indent value is &lt; 0, the control indentation is left unchanged.
   *
   * @since 2.0
   */
  public static void setIndentation(Control control, int horizontalIndent, int verticalIndent)
  {
    if (control == null)
    {
      throw new IllegalArgumentException("control == null"); //$NON-NLS-1$
    }

    Object data = control.getLayoutData();
    if (data instanceof GridData)
    {
      GridData gd = (GridData)data;
      if (verticalIndent >= 0)
      {
        gd.verticalIndent = verticalIndent;
      }

      if (horizontalIndent >= 0)
      {
        gd.horizontalIndent = horizontalIndent;
      }
    }
  }

  /**
   * @since 3.12
   */
  public static <T> T initResource(Function<Display, T> initializer)
  {
    Display display = getDisplay();
    if (display == null)
    {
      OM.LOG.warn("Display is not available");
      return null;
    }

    Object[] result = { null };

    try
    {
      syncExec(() -> result[0] = initializer.apply(display));
    }
    catch (Throwable ex)
    {
      OM.LOG.warn(ex);
    }

    @SuppressWarnings("unchecked")
    T value = (T)result[0];
    return value;
  }

  /**
   * @since 3.5
   */
  public static void syncExec(Runnable runnable)
  {
    Display display = getDisplay();
    if (Display.getCurrent() == display || display == null)
    {
      runnable.run();
    }
    else
    {
      syncExec(display, runnable);
    }
  }

  /**
   * @since 3.5
   */
  public static void syncExec(Display display, Runnable runnable)
  {
    exec(display, true, runnable);
  }

  /**
   * @since 3.9
   */
  public static void asyncExec(Runnable runnable)
  {
    Display display = getDisplay();
    if (Display.getCurrent() == display || display == null)
    {
      runnable.run();
    }
    else
    {
      asyncExec(display, runnable);
    }
  }

  /**
   * @since 3.9
   */
  public static void asyncExec(Display display, Runnable runnable)
  {
    exec(display, false, runnable);
  }

  /**
   * @since 3.9
   */
  public static void exec(Display display, boolean sync, Runnable runnable)
  {
    try
    {
      if (display.isDisposed())
      {
        return;
      }

      Runnable safeRunnable = () -> {
        if (display.isDisposed())
        {
          return;
        }

        try
        {
          runnable.run();
        }
        catch (SWTException ex)
        {
          if (ex.code != SWT.ERROR_WIDGET_DISPOSED)
          {
            throw ex;
          }

          //$FALL-THROUGH$
        }
      };

      if (sync)
      {
        display.syncExec(safeRunnable);
      }
      else
      {
        display.asyncExec(safeRunnable);
      }
    }
    catch (SWTException ex)
    {
      if (ex.code != SWT.ERROR_WIDGET_DISPOSED)
      {
        throw ex;
      }

      //$FALL-THROUGH$
    }
  }

  /**
   * @since 3.3
   */
  public static void runWithProgress(IRunnableWithProgress runnable)
  {
    try
    {
      IRunnableWithProgress op = monitor -> ModalContext.run(runnable, true, monitor, PlatformUI.getWorkbench().getDisplay());
      PlatformUI.getWorkbench().getProgressService().run(false, true, op);
    }
    catch (InvocationTargetException ex)
    {
      OM.LOG.error(ex.getCause());
    }
    catch (InterruptedException ex)
    {
      //$FALL-THROUGH$
    }
  }

  /**
   * @since 3.3
   */
  public static void preserveViewerState(Viewer viewer, Runnable runnable)
  {
    if (viewer == null)
    {
      return;
    }

    try
    {
      asyncExec(viewer.getControl().getDisplay(), () -> {
        ISelection selection = viewer.getSelection();

        // TreePath[] paths = null;
        // if (viewer instanceof TreeViewer)
        // {
        // TreeViewer treeViewer = (TreeViewer)viewer;
        // paths = treeViewer.getExpandedTreePaths();
        // }

        try
        {
          runnable.run();
        }
        catch (RuntimeException ignore)
        {
          // Do nothing
        }
        finally
        {
          // if (paths != null)
          // {
          // ((TreeViewer)viewer).setExpandedElements(paths);
          // }

          viewer.setSelection(selection);
        }
      });
    }
    catch (RuntimeException ignore)
    {
      // Do nothing
    }
  }

  /**
   * @since 2.0
   */
  public static void refreshViewer(Viewer viewer)
  {
    preserveViewerState(viewer, () -> viewer.refresh());
  }

  /**
   * @since 3.3
   */
  public static void refreshElement(StructuredViewer viewer, Object element, boolean updateLabels)
  {
    preserveViewerState(viewer, () -> {
      try
      {
        doRefresh(viewer, element, updateLabels);
      }
      catch (RuntimeException ex)
      {
        if (element != null)
        {
          // An element may have been deactivated - refresh the entire tree
          doRefresh(viewer, null, updateLabels);
        }
      }
    });
  }

  private static void doRefresh(StructuredViewer viewer, Object element, boolean updateLabels)
  {
    if (element != null && element != viewer.getInput())
    {
      viewer.refresh(element, updateLabels);
    }
    else
    {
      viewer.refresh(updateLabels);
    }
  }

  /**
   * @since 3.5
   */
  public static void updateElements(StructuredViewer viewer, Object element)
  {
    try
    {
      Control control = viewer.getControl();
      if (!control.isDisposed())
      {
        asyncExec(control.getDisplay(), () -> {
          try
          {
            if (element instanceof Object[])
            {
              Object[] elements = (Object[])element;
              viewer.update(elements, null);
            }
            else
            {
              viewer.update(element, null);
            }
          }
          catch (Exception ignore)
          {
          }
        });
      }
    }
    catch (Exception ignore)
    {
    }
  }

  /**
   * Shows a message in the StatusBar. Image can be omitted by passing a null parameter
   *
   * @since 2.0
   */
  public static void setStatusBarMessage(String message, Image image)
  {
    syncExec(getDisplay(), () -> {
      try
      {
        IViewSite site = (IViewSite)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
        if (image == null)
        {
          site.getActionBars().getStatusLineManager().setMessage(message);
        }
        else
        {
          site.getActionBars().getStatusLineManager().setMessage(image, message);
        }
      }
      catch (RuntimeException ignore)
      {
        // Do nothing
      }
    });
  }

  /**
   * @since 3.5
   */
  public static void addDragSupport(StructuredViewer viewer)
  {
    viewer.addDragSupport(DND.DROP_LINK | DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { LocalSelectionTransfer.getTransfer() }, new DragSourceAdapter()
    {
      private long lastDragTime;

      @Override
      public void dragStart(DragSourceEvent event)
      {
        lastDragTime = System.currentTimeMillis();
        LocalSelectionTransfer.getTransfer().setSelection(viewer.getSelection());
        LocalSelectionTransfer.getTransfer().setSelectionSetTime(lastDragTime);
      }

      @Override
      public void dragFinished(DragSourceEvent event)
      {
        if (LocalSelectionTransfer.getTransfer().getSelectionSetTime() == lastDragTime)
        {
          LocalSelectionTransfer.getTransfer().setSelection(null);
        }
      }
    });
  }

  /**
   * @since 3.9
   */
  public static boolean isParent(Composite parent, Control controlToCheck)
  {
    if (parent == null || controlToCheck == null)
    {
      throw new IllegalArgumentException("Neither parent nor controlToCheck must be null");
    }

    if (controlToCheck == parent)
    {
      return true;
    }

    Composite tmpParent = controlToCheck.getParent();

    while (tmpParent != parent && tmpParent != null)
    {
      tmpParent = tmpParent.getParent();
    }

    return tmpParent == parent;
  }

  /**
   * @since 3.9
   */
  public static void forEachChild(Composite composite, Consumer<Control> consumer)
  {
    Control[] children = composite.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      Control child = children[i];
      consumer.accept(child);

      if (child instanceof Composite)
      {
        forEachChild((Composite)child, consumer);
      }
    }
  }

  /**
   * @since 3.10
   */
  public static void selectReveal(Object object)
  {
    if (object == null)
    {
      return;
    }

    asyncExec(() -> {
      for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
      {
        IWorkbenchPage page = window.getActivePage();
        if (page != null)
        {
          ISelection selection = new StructuredSelection(object);
          selectReveal(selection, page.getViewReferences());
          selectReveal(selection, page.getEditorReferences());
        }
      }
    });
  }

  private static void selectReveal(ISelection selection, IWorkbenchPartReference[] partRefs)
  {
    for (IWorkbenchPartReference ref : partRefs)
    {
      IWorkbenchPart part = ref.getPart(false);
      if (part != null)
      {
        ISelectionProvider selectionProvider = part.getSite().getSelectionProvider();
        if (selectionProvider != null)
        {
          ISelection oldSelection = selectionProvider.getSelection();

          ISetSelectionTarget selectionTarget = Adapters.adapt(part, ISetSelectionTarget.class);
          if (selectionTarget != null)
          {
            selectionTarget.selectReveal(selection);

            if (!oldSelection.isEmpty())
            {
              ISelection newSelection = selectionProvider.getSelection();
              if (!selection.equals(newSelection))
              {
                selectionTarget.selectReveal(oldSelection);
              }
            }
          }
        }
      }
    }
  }
}
