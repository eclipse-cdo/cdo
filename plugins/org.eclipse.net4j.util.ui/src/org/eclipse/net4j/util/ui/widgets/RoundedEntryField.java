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

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.EntryControlAdvisor;
import org.eclipse.net4j.util.ui.EntryControlAdvisor.ControlConfig;
import org.eclipse.net4j.util.ui.widgets.ImageButton.SelectionMode;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public class RoundedEntryField extends Composite
{
  private static final int MIN_CONTROL_HEIGHT = 15;

  private static final int MAX_CONTROL_HEIGHT = 120;

  private static final int RADIUS = 36;

  private static final int MARGIN = RADIUS / 4;

  private static final int MARGIN_TWICE = MARGIN * 2;

  private static final String TOOLTIP_PREVIEW = OM.BUNDLE.getTranslationSupport().getString("entry.field.preview");

  private static final String TOOLTIP_EDIT = OM.BUNDLE.getTranslationSupport().getString("entry.field.edit");

  private final Color entryBackground;

  private final EntryControlAdvisor entryControlAdvisor;

  private final ControlConfig entryControlConfig;

  private final UnaryOperator<String> previewProvider;

  private final Color whiteColor;

  private final ImageButton modeButton;

  private Control control;

  private Mode mode = new EditMode();

  private String entry;

  private String initialEntry;

  private int lastControlHeight;

  public RoundedEntryField(Composite parent, int style, Color entryBackground, EntryControlAdvisor entryControlAdvisor, ControlConfig entryControlConfig,
      UnaryOperator<String> previewProvider, boolean previewMode)
  {
    super(parent, style | SWT.DOUBLE_BUFFERED);
    this.entryBackground = entryBackground;
    this.entryControlAdvisor = entryControlAdvisor;
    this.entryControlConfig = interceptModifyHandler(entryControlConfig);
    this.previewProvider = previewProvider;

    mode = previewMode ? new PreviewMode() : new EditMode();
    whiteColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);

    addPaintListener(e -> {
      Rectangle box = getClientArea();
      e.gc.setAntialias(SWT.ON);
      mode.paintRoundBackground(e.gc, box);
    });

    setLayout(GridLayoutFactory.fillDefaults().margins(MARGIN, MARGIN).numColumns(2).create());

    modeButton = new ImageButton(this, OM.getImage("icons/preview_entry.png"));
    modeButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).create());
    modeButton.setSelectionMode(SelectionMode.MouseDown);
    modeButton.setSelectionRunnable(() -> setPreviewMode(!isPreviewMode()));
    mode.updateModeButton();

    control = createControl(mode);
  }

  public final Color getEntryBackground()
  {
    return entryBackground;
  }

  public final Control getControl()
  {
    return control;
  }

  public final UnaryOperator<String> getPreviewProvider()
  {
    return previewProvider;
  }

  public final String getEntry()
  {
    if (entry == null)
    {
      entry = StringUtil.safe(mode.getEntryFromControl());
    }

    return entry;
  }

  public final void setEntry(String entry)
  {
    entry = StringUtil.safe(entry);
    initialEntry = entry;

    if (!Objects.equals(entry, this.entry))
    {
      mode.setEntryToControl(entry);
      this.entry = entry;
    }
  }

  public boolean isModified()
  {
    return !Objects.equals(getEntry(), initialEntry);
  }

  public final boolean isPreviewMode()
  {
    return mode instanceof PreviewMode;
  }

  public final void setPreviewMode(boolean previewMode)
  {
    if (previewProvider == null || previewMode == isPreviewMode())
    {
      return;
    }

    mode = mode.toggleMode();
    mode.updateModeButton();

    control.setFocus();
    updateControlVerticalBar(lastControlHeight);

    layoutParent();
    redraw();
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    if (wHint <= 1)
    {
      return new Point(0, 0);
    }

    if (hHint == SWT.DEFAULT)
    {
      hHint = mode.computeControlHeight(wHint);
      hHint = Math.max(hHint, MIN_CONTROL_HEIGHT);
      hHint = Math.min(hHint, MAX_CONTROL_HEIGHT);
    }

    return new Point(wHint + MARGIN_TWICE, hHint + MARGIN_TWICE);
  }

  @Override
  public boolean setFocus()
  {
    return control.setFocus();
  }

  private Control createControl(Mode mode)
  {
    if (control != null)
    {
      control.dispose();
    }

    Control control = mode.doCreateControl();
    control.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
    control.setBackground(entryBackground);
    control.moveAbove(modeButton);
    return control;
  }

  private ControlConfig interceptModifyHandler(ControlConfig entryControlConfig)
  {
    ControlConfig config = new ControlConfig(entryControlConfig);
    Consumer<Control> originalModifyHandler = config.getModifyHandler();

    config.setModifyHandler(control -> {
      String newEntry = StringUtil.safe(mode.getEntryFromControl());
      if (!newEntry.equals(entry))
      {
        entry = newEntry;

        Point oldSize = getSize();
        Point newSize = computeSize(oldSize.x, SWT.DEFAULT, false);
        if (newSize.y != oldSize.y)
        {
          layoutParent();
        }

        updateControlVerticalBar(lastControlHeight);
        originalModifyHandler.accept(control);
      }
    });

    return config;
  }

  private void updateControlVerticalBar(int controlHeight)
  {
    if (mode instanceof EditMode && control instanceof Scrollable)
    {
      ScrollBar verticalBar = ((Scrollable)control).getVerticalBar();
      if (verticalBar != null)
      {
        verticalBar.setVisible(controlHeight > MAX_CONTROL_HEIGHT);
      }
    }
  }

  private void layoutParent()
  {
    layout(true);
    getParent().layout(true);
  }

  /**
   * @author Eike Stepper
   */
  private interface Mode
  {
    public void paintRoundBackground(GC gc, Rectangle box);

    public Control doCreateControl();

    /**
     * @param wHint a value greater than 1, especially not <code>SWT.DEFAULT</code>.
     */
    public int computeControlHeight(int wHint);

    public String getEntryFromControl();

    public void setEntryToControl(String entry);

    public void updateModeButton();

    public Mode toggleMode();
  }

  /**
   * @author Eike Stepper
   */
  private final class EditMode implements Mode
  {
    public EditMode()
    {
    }

    @Override
    public void paintRoundBackground(GC gc, Rectangle box)
    {
      gc.setBackground(entryBackground);
      gc.fillRoundRectangle(box.x, box.y, box.width - 1, box.height - 1, RADIUS, RADIUS);
    }

    @Override
    public Control doCreateControl()
    {
      Control control = entryControlAdvisor.createControl(RoundedEntryField.this, entryControlConfig);
      if (control instanceof Scrollable)
      {
        ScrollBar verticalBar = ((Scrollable)control).getVerticalBar();
        if (verticalBar != null)
        {
          verticalBar.setVisible(false);
        }
      }
      return control;
    }

    @Override
    public int computeControlHeight(int wHint)
    {
      lastControlHeight = control.computeSize(wHint, SWT.DEFAULT).y;
      return lastControlHeight;
    }

    @Override
    public String getEntryFromControl()
    {
      return entryControlAdvisor.getEntry(control);
    }

    @Override
    public void setEntryToControl(String entry)
    {
      entryControlAdvisor.setEntry(control, entry);
    }

    @Override
    public void updateModeButton()
    {
      modeButton.setHoverImage(OM.getImage("icons/preview_entry.png"));
      modeButton.setToolTipText(TOOLTIP_PREVIEW);
      modeButton.setBackground(entryBackground);
    }

    /**
     * {@link EditMode} --> {@link PreviewMode}.
     */
    @Override
    public Mode toggleMode()
    {
      String entry = getEntry();

      PreviewMode previewMode = new PreviewMode();
      control = createControl(previewMode);
      previewMode.setEntryToControl(entry);
      return previewMode;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class PreviewMode implements Mode
  {
    private int wHint = SWT.DEFAULT;

    private int controlHeight = 10;

    private String controlEntry;

    public PreviewMode()
    {
    }

    @Override
    public void paintRoundBackground(GC gc, Rectangle box)
    {
      gc.setBackground(whiteColor);
      gc.setForeground(entryBackground);
      gc.drawRoundRectangle(box.x, box.y, box.width - 1, box.height - 1, RADIUS, RADIUS);
    }

    @Override
    public Control doCreateControl()
    {
      Browser browser = new Browser(RoundedEntryField.this, SWT.EDGE);
      browser.addProgressListener(ProgressListener.completedAdapter(event -> {
        if (mode != this)
        {
          return;
        }

        int width = Math.max(wHint, 10);
        browser.setSize(width, 10);

        controlHeight = ((Double)browser.evaluate("return document.body.scrollHeight;")).intValue();
        getParent().layout(true);
      }));

      return browser;
    }

    @Override
    public int computeControlHeight(int wHint)
    {
      this.wHint = wHint;
      return controlHeight;
    }

    @Override
    public String getEntryFromControl()
    {
      return controlEntry;
    }

    @Override
    public void setEntryToControl(String entry)
    {
      controlEntry = entry;
      String html = previewProvider.apply(entry);

      Browser browser = (Browser)control;
      browser.setText(html, true);
      browser.setVisible(!StringUtil.isEmpty(html.trim()));
    }

    @Override
    public void updateModeButton()
    {
      modeButton.setHoverImage(OM.getImage("icons/edit_entry.png"));
      modeButton.setToolTipText(TOOLTIP_EDIT);
      modeButton.setBackground(whiteColor);
    }

    /**
     * {@link PreviewMode} --> {@link EditMode}.
     */
    @Override
    public Mode toggleMode()
    {
      String entry = getEntry();

      EditMode editMode = new EditMode();
      control = createControl(editMode);
      editMode.setEntryToControl(entry);
      return editMode;
    }
  }
}
