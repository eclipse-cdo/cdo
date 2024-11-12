/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.widgets;

import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public class EntryControlAdvisor
{
  private static final String ADVISOR_KEY = "net4j.entryControlAdvisor";

  public EntryControlAdvisor()
  {
  }

  public final Control createControl(Composite parent, ControlConfig config)
  {
    Control control = doCreateControl(parent, config);
    control.setData(ADVISOR_KEY, this);
    return control;
  }

  protected Control doCreateControl(Composite parent, ControlConfig config)
  {
    Text text = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
    text.addModifyListener(e -> processModifyEvent(text, config));
    text.addKeyListener(KeyListener.keyPressedAdapter(e -> processKeyEvent(text, config, e)));
    return text;

  }

  public String getEntry(Control control)
  {
    return ((Text)control).getText();
  }

  public void setEntry(Control control, String entry)
  {
    Text text = (Text)control;
    text.setText(entry);
    text.setSelection(entry.length());
  }

  public void renderHTML(Control control, StringBuilder html)
  {
    String entry = getEntry(control);
    entry = entry.replace("\n", "<br>");

    html.append("<p>");
    html.append(entry);
    html.append("</p>");
  }

  protected final void processModifyEvent(Control control, ControlConfig config)
  {
    Consumer<Control> modifyHandler = config.getModifyHandler();
    if (modifyHandler != null)
    {
      modifyHandler.accept(control);
    }
  }

  protected final void processKeyEvent(Control control, ControlConfig config, KeyEvent e)
  {
    Consumer<Control> okHandler = config.getOkHandler();
    if (okHandler != null)
    {
      Predicate<KeyEvent> okDetector = config.getOkDetector();
      if (okDetector != null && okDetector.test(e))
      {
        okHandler.accept(control);
        e.doit = false;
      }
    }
  }

  public static EntryControlAdvisor getAdvisor(Control control)
  {
    return (EntryControlAdvisor)control.getData(ADVISOR_KEY);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ControlConfig
  {
    public static final Predicate<KeyEvent> DEFAULT_OK_DETECTOR = e -> //
    (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) && (e.stateMask & SWT.MODIFIER_MASK) == 0;

    private Predicate<KeyEvent> okDetector = DEFAULT_OK_DETECTOR;

    private Consumer<Control> okHandler;

    private Consumer<Control> modifyHandler;

    public ControlConfig()
    {
    }

    public ControlConfig(ControlConfig source)
    {
      okDetector = source.okDetector;
      okHandler = source.okHandler;
      modifyHandler = source.modifyHandler;
    }

    public Predicate<KeyEvent> getOkDetector()
    {
      return okDetector;
    }

    public void setOkDetector(Predicate<KeyEvent> okDetector)
    {
      this.okDetector = okDetector;
    }

    public Consumer<Control> getOkHandler()
    {
      return okHandler;
    }

    public void setOkHandler(Consumer<Control> okHandler)
    {
      this.okHandler = okHandler;
    }

    public Consumer<Control> getModifyHandler()
    {
      return modifyHandler;
    }

    public void setModifyHandler(Consumer<Control> modifyHandler)
    {
      this.modifyHandler = modifyHandler;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.ui.entryControlAdvisors";

    public static final String DEFAULT_TYPE = "default";

    protected Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public Factory()
    {
      this(DEFAULT_TYPE);
    }

    @Override
    public EntryControlAdvisor create(String description) throws ProductCreationException
    {
      return new EntryControlAdvisor();
    }
  }
}
