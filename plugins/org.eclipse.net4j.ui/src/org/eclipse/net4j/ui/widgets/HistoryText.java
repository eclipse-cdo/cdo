/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Eike Stepper
 */
public class HistoryText extends Composite
{
  private CCombo combo;

  public HistoryText(Composite parent, int style)
  {
    super(parent, SWT.NONE);
    combo = new CCombo(this, style);
  }

  public CCombo getCombo()
  {
    return combo;
  }

  public void add(String string, int index)
  {
    combo.add(string, index);
  }

  public void add(String string)
  {
    combo.add(string);
  }

  @Override
  public void addControlListener(ControlListener listener)
  {
    combo.addControlListener(listener);
  }

  @Override
  public void addDisposeListener(DisposeListener listener)
  {
    combo.addDisposeListener(listener);
  }

  @Override
  public void addDragDetectListener(DragDetectListener listener)
  {
    combo.addDragDetectListener(listener);
  }

  @Override
  public void addFocusListener(FocusListener listener)
  {
    combo.addFocusListener(listener);
  }

  @Override
  public void addHelpListener(HelpListener listener)
  {
    combo.addHelpListener(listener);
  }

  @Override
  public void addKeyListener(KeyListener listener)
  {
    combo.addKeyListener(listener);
  }

  @Override
  public void addListener(int eventType, Listener listener)
  {
    combo.addListener(eventType, listener);
  }

  @Override
  public void addMenuDetectListener(MenuDetectListener listener)
  {
    combo.addMenuDetectListener(listener);
  }

  public void addModifyListener(ModifyListener listener)
  {
    combo.addModifyListener(listener);
  }

  @Override
  public void addMouseListener(MouseListener listener)
  {
    combo.addMouseListener(listener);
  }

  @Override
  public void addMouseMoveListener(MouseMoveListener listener)
  {
    combo.addMouseMoveListener(listener);
  }

  @Override
  public void addMouseTrackListener(MouseTrackListener listener)
  {
    combo.addMouseTrackListener(listener);
  }

  @Override
  public void addMouseWheelListener(MouseWheelListener listener)
  {
    combo.addMouseWheelListener(listener);
  }

  @Override
  public void addPaintListener(PaintListener listener)
  {
    combo.addPaintListener(listener);
  }

  public void addSelectionListener(SelectionListener listener)
  {
    combo.addSelectionListener(listener);
  }

  @Override
  public void addTraverseListener(TraverseListener listener)
  {
    combo.addTraverseListener(listener);
  }

  public void addVerifyListener(VerifyListener listener)
  {
    combo.addVerifyListener(listener);
  }

  @Override
  public void changed(Control[] changed)
  {
    combo.changed(changed);
  }

  public void clearSelection()
  {
    combo.clearSelection();
  }

  @Override
  public Point computeSize(int hint, int hint2, boolean changed)
  {
    return combo.computeSize(hint, hint2, changed);
  }

  @Override
  public Point computeSize(int hint, int hint2)
  {
    return combo.computeSize(hint, hint2);
  }

  @Override
  public Rectangle computeTrim(int x, int y, int width, int height)
  {
    return combo.computeTrim(x, y, width, height);
  }

  public void copy()
  {
    combo.copy();
  }

  public void cut()
  {
    combo.cut();
  }

  public void deselect(int index)
  {
    combo.deselect(index);
  }

  public void deselectAll()
  {
    combo.deselectAll();
  }

  @Override
  public void dispose()
  {
    combo.dispose();
  }

  @Override
  public boolean dragDetect(Event event)
  {
    return combo.dragDetect(event);
  }

  @Override
  public boolean dragDetect(MouseEvent event)
  {
    return combo.dragDetect(event);
  }

  @Override
  public boolean equals(Object obj)
  {
    return combo.equals(obj);
  }

  @Override
  public boolean forceFocus()
  {
    return combo.forceFocus();
  }

  @Override
  public Accessible getAccessible()
  {
    return combo.getAccessible();
  }

  @Override
  public Color getBackground()
  {
    return combo.getBackground();
  }

  @Override
  public Image getBackgroundImage()
  {
    return combo.getBackgroundImage();
  }

  @Override
  public int getBackgroundMode()
  {
    return combo.getBackgroundMode();
  }

  @Override
  public int getBorderWidth()
  {
    return combo.getBorderWidth();
  }

  @Override
  public Rectangle getBounds()
  {
    return combo.getBounds();
  }

  @Override
  public Control[] getChildren()
  {
    return combo.getChildren();
  }

  @Override
  public Rectangle getClientArea()
  {
    return combo.getClientArea();
  }

  @Override
  public Cursor getCursor()
  {
    return combo.getCursor();
  }

  @Override
  public Object getData()
  {
    return combo.getData();
  }

  @Override
  public Object getData(String key)
  {
    return combo.getData(key);
  }

  @Override
  public Display getDisplay()
  {
    return combo.getDisplay();
  }

  @Override
  public boolean getDragDetect()
  {
    return combo.getDragDetect();
  }

  public boolean getEditable()
  {
    return combo.getEditable();
  }

  @Override
  public boolean getEnabled()
  {
    return combo.getEnabled();
  }

  @Override
  public Font getFont()
  {
    return combo.getFont();
  }

  @Override
  public Color getForeground()
  {
    return combo.getForeground();
  }

  @Override
  public ScrollBar getHorizontalBar()
  {
    return combo.getHorizontalBar();
  }

  public String getItem(int index)
  {
    return combo.getItem(index);
  }

  public int getItemCount()
  {
    return combo.getItemCount();
  }

  public int getItemHeight()
  {
    return combo.getItemHeight();
  }

  public String[] getItems()
  {
    return combo.getItems();
  }

  @Override
  public Layout getLayout()
  {
    return combo.getLayout();
  }

  @Override
  public Object getLayoutData()
  {
    return combo.getLayoutData();
  }

  @Override
  public boolean getLayoutDeferred()
  {
    return combo.getLayoutDeferred();
  }

  @Override
  public Point getLocation()
  {
    return combo.getLocation();
  }

  @Override
  public Menu getMenu()
  {
    return combo.getMenu();
  }

  @Override
  public Monitor getMonitor()
  {
    return combo.getMonitor();
  }

  @Override
  public Composite getParent()
  {
    return combo.getParent();
  }

  public Point getSelection()
  {
    return combo.getSelection();
  }

  public int getSelectionIndex()
  {
    return combo.getSelectionIndex();
  }

  @Override
  public Shell getShell()
  {
    return combo.getShell();
  }

  @Override
  public Point getSize()
  {
    return combo.getSize();
  }

  @Override
  public int getStyle()
  {
    return combo.getStyle();
  }

  @Override
  public Control[] getTabList()
  {
    return combo.getTabList();
  }

  public String getText()
  {
    return combo.getText();
  }

  public int getTextHeight()
  {
    return combo.getTextHeight();
  }

  public int getTextLimit()
  {
    return combo.getTextLimit();
  }

  @Override
  public String getToolTipText()
  {
    return combo.getToolTipText();
  }

  @Override
  public ScrollBar getVerticalBar()
  {
    return combo.getVerticalBar();
  }

  @Override
  public boolean getVisible()
  {
    return combo.getVisible();
  }

  public int getVisibleItemCount()
  {
    return combo.getVisibleItemCount();
  }

  @Override
  public int hashCode()
  {
    return combo.hashCode();
  }

  public int indexOf(String string, int start)
  {
    return combo.indexOf(string, start);
  }

  public int indexOf(String string)
  {
    return combo.indexOf(string);
  }

  @Override
  public void internal_dispose_GC(int hdc, GCData data)
  {
    combo.internal_dispose_GC(hdc, data);
  }

  @Override
  public int internal_new_GC(GCData data)
  {
    return combo.internal_new_GC(data);
  }

  @Override
  public boolean isDisposed()
  {
    return combo.isDisposed();
  }

  @Override
  public boolean isEnabled()
  {
    return combo.isEnabled();
  }

  @Override
  public boolean isFocusControl()
  {
    return combo.isFocusControl();
  }

  @Override
  public boolean isLayoutDeferred()
  {
    return combo.isLayoutDeferred();
  }

  @Override
  public boolean isListening(int eventType)
  {
    return combo.isListening(eventType);
  }

  @Override
  public boolean isReparentable()
  {
    return combo.isReparentable();
  }

  @Override
  public boolean isVisible()
  {
    return combo.isVisible();
  }

  @Override
  public void layout()
  {
    combo.layout();
  }

  @Override
  public void layout(boolean changed, boolean all)
  {
    combo.layout(changed, all);
  }

  @Override
  public void layout(boolean changed)
  {
    combo.layout(changed);
  }

  @Override
  public void layout(Control[] changed)
  {
    combo.layout(changed);
  }

  @Override
  public void moveAbove(Control control)
  {
    combo.moveAbove(control);
  }

  @Override
  public void moveBelow(Control control)
  {
    combo.moveBelow(control);
  }

  @Override
  public void notifyListeners(int eventType, Event event)
  {
    combo.notifyListeners(eventType, event);
  }

  @Override
  public void pack()
  {
    combo.pack();
  }

  @Override
  public void pack(boolean changed)
  {
    combo.pack(changed);
  }

  public void paste()
  {
    combo.paste();
  }

  @Override
  public void redraw()
  {
    combo.redraw();
  }

  @Override
  public void redraw(int x, int y, int width, int height, boolean all)
  {
    combo.redraw(x, y, width, height, all);
  }

  public void remove(int start, int end)
  {
    combo.remove(start, end);
  }

  public void remove(int index)
  {
    combo.remove(index);
  }

  public void remove(String string)
  {
    combo.remove(string);
  }

  public void removeAll()
  {
    combo.removeAll();
  }

  @Override
  public void removeControlListener(ControlListener listener)
  {
    combo.removeControlListener(listener);
  }

  @Override
  public void removeDisposeListener(DisposeListener listener)
  {
    combo.removeDisposeListener(listener);
  }

  @Override
  public void removeDragDetectListener(DragDetectListener listener)
  {
    combo.removeDragDetectListener(listener);
  }

  @Override
  public void removeFocusListener(FocusListener listener)
  {
    combo.removeFocusListener(listener);
  }

  @Override
  public void removeHelpListener(HelpListener listener)
  {
    combo.removeHelpListener(listener);
  }

  @Override
  public void removeKeyListener(KeyListener listener)
  {
    combo.removeKeyListener(listener);
  }

  @Override
  public void removeListener(int eventType, Listener listener)
  {
    combo.removeListener(eventType, listener);
  }

  @Override
  public void removeMenuDetectListener(MenuDetectListener listener)
  {
    combo.removeMenuDetectListener(listener);
  }

  public void removeModifyListener(ModifyListener listener)
  {
    combo.removeModifyListener(listener);
  }

  @Override
  public void removeMouseListener(MouseListener listener)
  {
    combo.removeMouseListener(listener);
  }

  @Override
  public void removeMouseMoveListener(MouseMoveListener listener)
  {
    combo.removeMouseMoveListener(listener);
  }

  @Override
  public void removeMouseTrackListener(MouseTrackListener listener)
  {
    combo.removeMouseTrackListener(listener);
  }

  @Override
  public void removeMouseWheelListener(MouseWheelListener listener)
  {
    combo.removeMouseWheelListener(listener);
  }

  @Override
  public void removePaintListener(PaintListener listener)
  {
    combo.removePaintListener(listener);
  }

  public void removeSelectionListener(SelectionListener listener)
  {
    combo.removeSelectionListener(listener);
  }

  @Override
  public void removeTraverseListener(TraverseListener listener)
  {
    combo.removeTraverseListener(listener);
  }

  public void removeVerifyListener(VerifyListener listener)
  {
    combo.removeVerifyListener(listener);
  }

  public void select(int index)
  {
    combo.select(index);
  }

  @Override
  public void setBackground(Color color)
  {
    combo.setBackground(color);
  }

  @Override
  public void setBackgroundImage(Image image)
  {
    combo.setBackgroundImage(image);
  }

  @Override
  public void setBackgroundMode(int mode)
  {
    combo.setBackgroundMode(mode);
  }

  @Override
  public void setBounds(int x, int y, int width, int height)
  {
    combo.setBounds(x, y, width, height);
  }

  @Override
  public void setBounds(Rectangle rect)
  {
    combo.setBounds(rect);
  }

  @Override
  public void setCapture(boolean capture)
  {
    combo.setCapture(capture);
  }

  @Override
  public void setCursor(Cursor cursor)
  {
    combo.setCursor(cursor);
  }

  @Override
  public void setData(Object data)
  {
    combo.setData(data);
  }

  @Override
  public void setData(String key, Object value)
  {
    combo.setData(key, value);
  }

  @Override
  public void setDragDetect(boolean dragDetect)
  {
    combo.setDragDetect(dragDetect);
  }

  public void setEditable(boolean editable)
  {
    combo.setEditable(editable);
  }

  @Override
  public void setEnabled(boolean enabled)
  {
    combo.setEnabled(enabled);
  }

  @Override
  public boolean setFocus()
  {
    return combo.setFocus();
  }

  @Override
  public void setFont(Font font)
  {
    combo.setFont(font);
  }

  @Override
  public void setForeground(Color color)
  {
    combo.setForeground(color);
  }

  public void setItem(int index, String string)
  {
    combo.setItem(index, string);
  }

  public void setItems(String[] items)
  {
    combo.setItems(items);
  }

  @Override
  public void setLayout(Layout layout)
  {
    combo.setLayout(layout);
  }

  @Override
  public void setLayoutData(Object layoutData)
  {
    combo.setLayoutData(layoutData);
  }

  @Override
  public void setLayoutDeferred(boolean defer)
  {
    combo.setLayoutDeferred(defer);
  }

  @Override
  public void setLocation(int x, int y)
  {
    combo.setLocation(x, y);
  }

  @Override
  public void setLocation(Point location)
  {
    combo.setLocation(location);
  }

  @Override
  public void setMenu(Menu menu)
  {
    combo.setMenu(menu);
  }

  @Override
  public boolean setParent(Composite parent)
  {
    return combo.setParent(parent);
  }

  @Override
  public void setRedraw(boolean redraw)
  {
    combo.setRedraw(redraw);
  }

  public void setSelection(Point selection)
  {
    combo.setSelection(selection);
  }

  @Override
  public void setSize(int width, int height)
  {
    combo.setSize(width, height);
  }

  @Override
  public void setSize(Point size)
  {
    combo.setSize(size);
  }

  @Override
  public void setTabList(Control[] tabList)
  {
    combo.setTabList(tabList);
  }

  public void setText(String string)
  {
    combo.setText(string);
  }

  public void setTextLimit(int limit)
  {
    combo.setTextLimit(limit);
  }

  @Override
  public void setToolTipText(String string)
  {
    combo.setToolTipText(string);
  }

  @Override
  public void setVisible(boolean visible)
  {
    combo.setVisible(visible);
  }

  public void setVisibleItemCount(int count)
  {
    combo.setVisibleItemCount(count);
  }

  @Override
  public Point toControl(int x, int y)
  {
    return combo.toControl(x, y);
  }

  @Override
  public Point toControl(Point point)
  {
    return combo.toControl(point);
  }

  @Override
  public Point toDisplay(int x, int y)
  {
    return combo.toDisplay(x, y);
  }

  @Override
  public Point toDisplay(Point point)
  {
    return combo.toDisplay(point);
  }

  @Override
  public String toString()
  {
    return combo.toString();
  }

  @Override
  public boolean traverse(int traversal)
  {
    return combo.traverse(traversal);
  }

  @Override
  public void update()
  {
    combo.update();
  }
}
