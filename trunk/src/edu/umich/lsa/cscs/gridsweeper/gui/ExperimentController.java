package edu.umich.lsa.cscs.gridsweeper.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import edu.umich.lsa.cscs.gridsweeper.*;

/**
 * @author Ed Baskerville
 *
 */
public class ExperimentController
{
	final static int MENU_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	private static GridSweeperApp app;
	
	static
	{
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		app = GridSweeperApp.getSharedInstance();
	}
	
	Experiment exp;
	JFrame frame;
	JMenuBar menuBar;
	JTabbedPane tabPane;
	JPanel modelPanel;
	JPanel paramPanel;
	JPanel runsPanel;
	
	public ExperimentController()
	{
		this(new Experiment());
	}
	
	public ExperimentController(Experiment exp)
	{
		this.exp = exp;
		
		if(exp.getName() == null)
		{
			exp.setName("Untitled Experiment " + app.getUntitledCounter());
		}
		
		buildFrame();
		buildMenuBar();
		showFrame();
	}
	
	private void buildFrame()
	{
		frame = new JFrame(exp.getName());
		
		tabPane = new JTabbedPane();
		{
			modelPanel = new JPanel();
			{
				modelPanel.setName("Model");
			}
			tabPane.add(modelPanel);
			
			paramPanel = new JPanel();
			{
				paramPanel.setName("Parameters");
			}
			tabPane.add(paramPanel);
			
			runsPanel = new JPanel();
			{
				runsPanel.setName("Runs");
			}
			tabPane.add(runsPanel);
		}
		frame.add(tabPane);
	}
	
	private void buildMenuBar()
	{
		menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		{
			JMenuItem newItem = new JMenuItem("New");
			newItem.setAccelerator(getMenuStroke(KeyEvent.VK_N));
			fileMenu.add(newItem);
			
			JMenuItem openItem = new JMenuItem("Open...");
			openItem.setAccelerator(getMenuStroke(KeyEvent.VK_O));
			fileMenu.add(openItem);
			
			JMenu openRecentMenu = new JMenu("Open Recent");
			fileMenu.add(openRecentMenu);
			
			fileMenu.add(new JSeparator());
			
			JMenuItem closeItem = new JMenuItem("Close");
			closeItem.setAccelerator(getMenuStroke(KeyEvent.VK_W));
			fileMenu.add(closeItem);
			
			JMenuItem saveItem = new JMenuItem("Save");
			saveItem.setAccelerator(getMenuStroke(KeyEvent.VK_S));
			fileMenu.add(saveItem);
			
			JMenuItem saveAsItem = new JMenuItem("Save As...");
			saveAsItem.setAccelerator(getMenuStroke(KeyEvent.VK_S, InputEvent.SHIFT_MASK));
			fileMenu.add(saveAsItem);
			
			JMenuItem saveAllItem = new JMenuItem("Save All");
			fileMenu.add(saveAllItem);
			
			JMenuItem revertToSavedItem = new JMenuItem("Revert to Saved");
			fileMenu.add(revertToSavedItem);
		}
		menuBar.add(fileMenu);
		
		JMenu editMenu = new JMenu("Edit");
		{
			JMenuItem undoItem = new JMenuItem("Undo");
			undoItem.setAccelerator(getMenuStroke(KeyEvent.VK_Z));
			editMenu.add(undoItem);
			
			JMenuItem redoItem = new JMenuItem("Redo");
			redoItem.setAccelerator(getMenuStroke(KeyEvent.VK_Z, InputEvent.SHIFT_MASK));
			editMenu.add(redoItem);
			
			editMenu.add(new JSeparator());
			
			JMenuItem cutItem = new JMenuItem("Cut");
			cutItem.setAccelerator(getMenuStroke(KeyEvent.VK_X));
			editMenu.add(cutItem);
			
			JMenuItem copyItem = new JMenuItem("Copy");
			copyItem.setAccelerator(getMenuStroke(KeyEvent.VK_C));
			editMenu.add(copyItem);
			
			JMenuItem pasteItem = new JMenuItem("Paste");
			pasteItem.setAccelerator(getMenuStroke(KeyEvent.VK_V));
			editMenu.add(pasteItem);
			
			JMenuItem deleteItem = new JMenuItem("Delete");
			editMenu.add(deleteItem);
			
			JMenuItem selectAllItem = new JMenuItem("Select All");
			selectAllItem.setAccelerator(getMenuStroke(KeyEvent.VK_A));
			editMenu.add(selectAllItem);
		}
		menuBar.add(editMenu);
		
		JMenu expMenu = new JMenu("Experiment");
		{
			JMenuItem modelItem = new JMenuItem("Show Model Settings");
			expMenu.add(modelItem);
			
			JMenuItem paramItem = new JMenuItem("Show Parameter Settings");
			expMenu.add(paramItem);
			
			JMenuItem runsItem = new JMenuItem("Show Runs");
			expMenu.add(runsItem);
			
			expMenu.add(new JSeparator());
			
			JMenuItem runItem = new JMenuItem("Run...");
			runItem.setAccelerator(getMenuStroke(KeyEvent.VK_R));
			expMenu.add(runItem);
			
			JMenuItem dryRunItem = new JMenuItem("Dry Run...");
			dryRunItem.setAccelerator(getMenuStroke(KeyEvent.VK_R, InputEvent.SHIFT_MASK));
			expMenu.add(dryRunItem);
		}
		menuBar.add(expMenu);
		
		JMenu windowMenu = new JMenu("Window");
		{
			
		}
		menuBar.add(windowMenu);
		
		JMenu helpMenu = new JMenu("Help");
		{
			
		}
		menuBar.add(helpMenu);
		
		frame.setJMenuBar(menuBar);
	}
	
	private KeyStroke getMenuStroke(int key)
	{
		return getMenuStroke(key, 0);
	}
	
	private KeyStroke getMenuStroke(int key, int mod)
	{
		return KeyStroke.getKeyStroke(key, MENU_MASK | mod);		
	}
	
	private void showFrame()
	{
		frame.setVisible(true);
	}
}
