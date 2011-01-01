/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.ui;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Martin Fluegge
 */
public abstract class AbstractCDOUITest<T extends SWTWorkbenchBot> extends AbstractCDOTest
{
  private T bot;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    SWTBotPreferences.KEYBOARD_LAYOUT = "EN_US";
    SWTBotPreferences.SCREENSHOTS_DIR = OMPlatform.INSTANCE.getProperty("java.io.tmpdir") + "/cdotests";
    createBot();
  }

  @SuppressWarnings("unchecked")
  protected void createBot()
  {
    setBot((T)new SWTWorkbenchBot());
  }

  protected T getBot()
  {
    return bot;
  }

  protected void setBot(T bot)
  {
    this.bot = bot;
  }

  @Override
  public void tearDown() throws Exception
  {
    super.tearDown();
    closeAllEditors();
  }

  protected void closeAllEditors()
  {
    LatchedRunnable runnable = new LatchedRunnable()
    {
      @Override
      protected void runWithLatch()
      {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
      }
    };

    Display.getDefault().asyncExec(runnable);
    runnable.await();
  }

  @Deprecated
  protected void closeAllEditorsSync()
  {
    UIThreadRunnable.syncExec(new VoidResult()
    {
      public void run()
      {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
      }
    });
  }

  protected void resetWorkbench()
  {
    final CountDownLatch countDownLatch = new CountDownLatch(1);
    UIThreadRunnable.asyncExec(new VoidResult()
    {
      public void run()
      {
        try
        {
          IWorkbench workbench = PlatformUI.getWorkbench();
          IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
          IWorkbenchPage page = workbenchWindow.getActivePage();
          Shell activeShell = Display.getCurrent().getActiveShell();

          if (activeShell != workbenchWindow.getShell() && activeShell != null)
          {
            activeShell.close();
          }

          page.closeAllEditors(false);
          page.resetPerspective();

          String defaultPerspectiveId = workbench.getPerspectiveRegistry().getDefaultPerspective();
          workbench.showPerspective(defaultPerspectiveId, workbenchWindow);

          page.resetPerspective();
        }
        catch (WorkbenchException e)
        {
          throw new RuntimeException(e);
        }
        finally
        {
          countDownLatch.countDown();
        }
      }
    });

    try
    {
      countDownLatch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Walks through the tree and selects the first element which matches the name.
   */
  protected SWTBotTreeItem selectFolder(SWTBotTreeItem[] items, String name, boolean exactMatch)
  {
    SWTBotTreeItem ret = null;
    for (SWTBotTreeItem item : items)
    {
      if (exactMatch)
      {
        if (item.getText().equals(name))
        {
          item.select();
          return item;
        }
      }
      else
      {
        if (item.getText().contains(name))
        {
          item.select();
          return item;
        }
      }

      item.expand();
      ret = selectFolder(item.getItems(), name, exactMatch);
    }
    return ret;
  }

  protected void typeTextToFocusedWidget(String text, SWTBot bot, boolean hitCR)
  {
    Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
    bot.getFocusedWidget();
    keyboard.typeText(text, 50);

    if (hitCR)
    {
      keyboard.pressShortcut(Keystrokes.CR);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class LatchedRunnable implements Runnable
  {
    private CountDownLatch latch = new CountDownLatch(1);

    private Throwable result;

    public LatchedRunnable()
    {
    }

    public void run()
    {
      try
      {
        runWithLatch();
      }
      catch (Throwable t)
      {
        result = t;
      }
      finally
      {
        latch.countDown();
      }
    }

    protected abstract void runWithLatch();

    public void await(long timeout)
    {
      try
      {
        if (!latch.await(timeout, TimeUnit.MILLISECONDS))
        {
          throw new TimeoutRuntimeException("Timeout after " + timeout + " milliseconds");
        }

        if (result instanceof RuntimeException)
        {
          throw (RuntimeException)result;
        }

        if (result instanceof Error)
        {
          throw (Error)result;
        }
      }
      catch (InterruptedException ex)
      {
        throw new RuntimeException(ex);
      }
    }

    public void await()
    {
      await(DEFAULT_TIMEOUT);
    }

    /**
     * @author Eike Stepper
     */
    public static class Delegating extends LatchedRunnable
    {
      private Runnable delegate;

      public Delegating(Runnable delegate)
      {
        this.delegate = delegate;
      }

      @Override
      protected void runWithLatch()
      {
        delegate.run();
      }
    }
  }
}
