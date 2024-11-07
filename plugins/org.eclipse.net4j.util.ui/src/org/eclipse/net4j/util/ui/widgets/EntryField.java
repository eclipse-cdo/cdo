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

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.widgets.EntryControlAdvisor.ControlConfig;
import org.eclipse.net4j.util.ui.widgets.ImageButton.SelectionMode;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Eike Stepper
 * @since 3.19
 */
public final class EntryField extends Composite
{
  private static final int MIN_CONTROL_HEIGHT = 15;

  private static final int MAX_CONTROL_HEIGHT = 120;

  private static final int RADIUS = 36;

  private static final int MARGIN = RADIUS / 4;

  private static final int MARGIN_TWICE = MARGIN * 2;

  private static final String TOOLTIP_PREVIEW = OM.BUNDLE.getTranslationSupport().getString("entry.field.preview");

  private static final String TOOLTIP_EDIT = OM.BUNDLE.getTranslationSupport().getString("entry.field.edit");

  private final FieldConfig config;

  private final Color whiteColor;

  private final ImageButton[] extraButtons;

  private final ImageButton modeButton;

  private Control control;

  private Mode mode = new EditMode();

  private String initialEntry;

  private String entry;

  private boolean empty;

  private boolean dirty;

  private int lastControlHeight;

  public EntryField(Composite parent, int style, FieldConfig config)
  {
    super(parent, style | SWT.DOUBLE_BUFFERED);

    config = new FieldConfig(config);
    config.setEntryControlConfig(interceptModifyHandler(config.getEntryControlConfig()));
    this.config = config;

    mode = config.isInitialPreviewMode() ? new PreviewMode() : new EditMode();
    whiteColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);

    addPaintListener(e -> {
      Rectangle box = getClientArea();
      e.gc.setAntialias(SWT.ON);
      mode.paintRoundBackground(e.gc, box);
    });

    ButtonAdvisor[] buttonAdvisors = config.getExtraButtonAdvisors();
    extraButtons = new ImageButton[ObjectUtil.size(buttonAdvisors)];

    setLayout(GridLayoutFactory.fillDefaults().margins(MARGIN, MARGIN).numColumns(2 + extraButtons.length).create());

    for (int i = 0; i < extraButtons.length; i++)
    {
      ButtonAdvisor buttonAdvisor = buttonAdvisors[i];
      extraButtons[i] = new ImageButton(this, buttonAdvisor.getHoverImage(), buttonAdvisor.getGrayImage());
      extraButtons[i].setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).create());
      extraButtons[i].setSelectionMode(SelectionMode.MouseDown);
      extraButtons[i].setSelectionRunnable(buttonAdvisor);
      extraButtons[i].setToolTipText(buttonAdvisor.getToolTipText());
      buttonAdvisor.customize(extraButtons[i]);
    }

    modeButton = new ImageButton(this, OM.getImage("icons/preview_entry.png"));
    modeButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.END).create());
    modeButton.setSelectionMode(SelectionMode.MouseDown);
    modeButton.setSelectionRunnable(() -> setPreviewMode(!isPreviewMode()));
    mode.updateButtons();

    control = createControl(mode);

    entry = StringUtil.safe(mode.getEntryFromControl());
    initialEntry = entry;

    empty = StringUtil.isEmpty(entry);
    mode.updateModeButtonVisibility();
  }

  public Control getControl()
  {
    return control;
  }

  public String getEntry()
  {
    return entry;
  }

  public void setEntry(String entry)
  {
    entry = StringUtil.safe(entry);

    if (!Objects.equals(entry, this.entry))
    {
      mode.setEntryToControl(entry);
      this.entry = entry;
    }

    setEmpty(StringUtil.isEmpty(entry));
    resetDirty();
  }

  public boolean isEmpty()
  {
    return empty;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void resetDirty()
  {
    initialEntry = entry;
    setDirty(false);
  }

  public boolean isPreviewMode()
  {
    return mode instanceof PreviewMode;
  }

  public void setPreviewMode(boolean previewMode)
  {
    if (config.getPreviewProvider() == null || previewMode == isPreviewMode())
    {
      return;
    }

    mode = mode.toggleMode();
    mode.updateButtons();

    control.setFocus();
    updateControlVerticalBar(lastControlHeight);

    layoutParent();
    redraw();

    Consumer<EntryField> previewModeHandler = config.getPreviewModeHandler();
    if (previewModeHandler != null)
    {
      previewModeHandler.accept(this);
    }
  }

  public boolean isExtraButtonVisible(int index)
  {
    return extraButtons[index].isVisible();
  }

  public void setExtraButtonVisible(int index, boolean visible)
  {
    if (visible != isExtraButtonVisible(index))
    {
      GridData gridData = (GridData)extraButtons[index].getLayoutData();
      gridData.exclude = !visible;

      extraButtons[index].setVisible(visible);
      layout(true);
    }
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
    control.setBackground(config.getEntryBackground());

    ImageButton firstButton = modeButton;
    if (extraButtons.length != 0)
    {
      firstButton = extraButtons[0];
    }

    control.moveAbove(firstButton);
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

        if (originalModifyHandler != null)
        {
          originalModifyHandler.accept(control);
        }

        setEmpty(StringUtil.isEmpty(newEntry));
        setDirty(!Objects.equals(newEntry, initialEntry));
      }
    });

    return config;
  }

  private void setEmpty(boolean empty)
  {
    if (empty != this.empty)
    {
      this.empty = empty;
      mode.updateModeButtonVisibility();

      Consumer<EntryField> emptyHandler = config.getEmptyHandler();
      if (emptyHandler != null)
      {
        emptyHandler.accept(this);
      }
    }
  }

  private void setDirty(boolean dirty)
  {
    if (dirty != this.dirty)
    {
      this.dirty = dirty;

      Consumer<EntryField> dirtyHandler = config.getDirtyHandler();
      if (dirtyHandler != null)
      {
        dirtyHandler.accept(this);
      }
    }
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
    requestLayout();
    getParent().requestLayout();

    // layout(true);
    // getParent().layout(true);
  }

  /**
   * @author Eike Stepper
   */
  public interface ButtonAdvisor extends Runnable
  {
    public String getToolTipText();

    public Image getHoverImage();

    public default Image getGrayImage()
    {
      return null;
    }

    public default void customize(ImageButton button)
    {
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class FieldConfig
  {
    private Color entryBackground;

    private EntryControlAdvisor entryControlAdvisor;

    private ControlConfig entryControlConfig;

    private String emptyHint;

    private UnaryOperator<String> previewProvider;

    private boolean initialPreviewMode;

    private ButtonAdvisor[] extraButtonAdvisors;

    private Consumer<EntryField> emptyHandler;

    private Consumer<EntryField> dirtyHandler;

    private Consumer<EntryField> previewModeHandler;

    public FieldConfig()
    {
    }

    public FieldConfig(FieldConfig source)
    {
      entryBackground = source.entryBackground;
      entryControlAdvisor = source.entryControlAdvisor;
      entryControlConfig = new ControlConfig(source.entryControlConfig);
      emptyHint = source.emptyHint;
      previewProvider = source.previewProvider;
      initialPreviewMode = source.initialPreviewMode;
      extraButtonAdvisors = source.extraButtonAdvisors == null ? null : Arrays.copyOf(source.extraButtonAdvisors, source.extraButtonAdvisors.length);
      emptyHandler = source.emptyHandler;
      dirtyHandler = source.dirtyHandler;
      previewModeHandler = source.previewModeHandler;
    }

    public Color getEntryBackground()
    {
      return entryBackground;
    }

    public void setEntryBackground(Color entryBackground)
    {
      this.entryBackground = entryBackground;
    }

    public EntryControlAdvisor getEntryControlAdvisor()
    {
      return entryControlAdvisor;
    }

    public void setEntryControlAdvisor(EntryControlAdvisor entryControlAdvisor)
    {
      this.entryControlAdvisor = entryControlAdvisor;
    }

    public ControlConfig getEntryControlConfig()
    {
      return entryControlConfig;
    }

    public void setEntryControlConfig(ControlConfig entryControlConfig)
    {
      this.entryControlConfig = entryControlConfig;
    }

    public String getEmptyHint()
    {
      return emptyHint;
    }

    public void setEmptyHint(String emptyHint)
    {
      this.emptyHint = emptyHint;
    }

    public UnaryOperator<String> getPreviewProvider()
    {
      return previewProvider;
    }

    public void setPreviewProvider(UnaryOperator<String> previewProvider)
    {
      this.previewProvider = previewProvider;
    }

    public boolean isInitialPreviewMode()
    {
      return initialPreviewMode;
    }

    public void setInitialPreviewMode(boolean initialPreviewMode)
    {
      this.initialPreviewMode = initialPreviewMode;
    }

    public ButtonAdvisor[] getExtraButtonAdvisors()
    {
      return extraButtonAdvisors;
    }

    public void setExtraButtonAdvisors(ButtonAdvisor... extraButtonAdvisors)
    {
      this.extraButtonAdvisors = extraButtonAdvisors;
    }

    public Consumer<EntryField> getEmptyHandler()
    {
      return emptyHandler;
    }

    public void setEmptyHandler(Consumer<EntryField> emptyHandler)
    {
      this.emptyHandler = emptyHandler;
    }

    public Consumer<EntryField> getDirtyHandler()
    {
      return dirtyHandler;
    }

    public void setDirtyHandler(Consumer<EntryField> dirtyHandler)
    {
      this.dirtyHandler = dirtyHandler;
    }

    public Consumer<EntryField> getPreviewModeHandler()
    {
      return previewModeHandler;
    }

    public void setPreviewModeHandler(Consumer<EntryField> previewModeHandler)
    {
      this.previewModeHandler = previewModeHandler;
    }
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

    public void updateModeButtonVisibility();

    public void updateButtons();

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
      gc.setBackground(config.getEntryBackground());
      gc.fillRoundRectangle(box.x, box.y, box.width - 1, box.height - 1, RADIUS, RADIUS);
    }

    @Override
    public Control doCreateControl()
    {
      Control control = config.getEntryControlAdvisor().createControl(EntryField.this, config.getEntryControlConfig());
      if (control instanceof Scrollable)
      {
        ScrollBar verticalBar = ((Scrollable)control).getVerticalBar();
        if (verticalBar != null)
        {
          verticalBar.setVisible(false);
        }
      }

      String emptyHint = config.getEmptyHint();
      if (!StringUtil.isEmpty(emptyHint))
      {
        control.addPaintListener(e -> {
          if (StringUtil.isEmpty(entry))
          {
            e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
            e.gc.drawText(emptyHint, 3, 0);
          }
        });
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
      return config.getEntryControlAdvisor().getEntry(control);
    }

    @Override
    public void setEntryToControl(String entry)
    {
      config.getEntryControlAdvisor().setEntry(control, entry);
    }

    @Override
    public void updateModeButtonVisibility()
    {
      modeButton.setVisible(!empty);
    }

    @Override
    public void updateButtons()
    {
      Color background = config.getEntryBackground();

      for (int i = 0; i < extraButtons.length; i++)
      {
        extraButtons[i].setBackground(background);
      }

      modeButton.setHoverImage(OM.getImage("icons/preview_entry.png"));
      modeButton.setToolTipText(TOOLTIP_PREVIEW);
      modeButton.setBackground(background);
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
      gc.setForeground(config.getEntryBackground());
      gc.drawRoundRectangle(box.x, box.y, box.width - 1, box.height - 1, RADIUS, RADIUS);
    }

    @Override
    public Control doCreateControl()
    {
      Browser browser = new Browser(EntryField.this, SWT.EDGE);
      browser.addProgressListener(ProgressListener.completedAdapter(event -> {
        if (mode != this)
        {
          return;
        }

        int width = Math.max(wHint, 10);
        browser.setSize(width, 10);

        controlHeight = ((Double)browser.evaluate("return document.body.scrollHeight;")).intValue();
        layoutParent();
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
      String html = config.getPreviewProvider().apply(entry);

      Browser browser = (Browser)control;
      browser.setText(html, true);
      browser.setVisible(!StringUtil.isEmpty(html.trim()));
    }

    @Override
    public void updateModeButtonVisibility()
    {
      modeButton.setVisible(true);
    }

    @Override
    public void updateButtons()
    {
      for (int i = 0; i < extraButtons.length; i++)
      {
        extraButtons[i].setBackground(whiteColor);
      }

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
