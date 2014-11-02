package eu.bibl.cfide.ui.editor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.RootPaneContainer;
import javax.swing.plaf.basic.BasicToolBarUI;

import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.bio.jfile.classloader.JarClassLoader;
import eu.bibl.bio.jfile.in.JarDownloader;
import eu.bibl.cfide.config.CFIDEConfig;
import eu.bibl.cfide.engine.compiler.BasicSourceCompiler;
import eu.bibl.cfide.engine.compiler.CompilerException;
import eu.bibl.cfide.engine.launch.JarLauncher;
import eu.bibl.cfide.engine.launch.dump.CustomJarDumper;
import eu.bibl.cfide.ui.IDEFrame;
import eu.bibl.cfide.ui.ProjectPanel;

public class EditorTabbedPane extends JTabbedPane implements ActionListener {
	
	private static final long serialVersionUID = 9106124854514247948L;
	
	protected CFIDEConfig config;
	protected Map<String, EditorTextTab> tabs;
	
	public EditorTabbedPane(CFIDEConfig config) {
		tabs = new HashMap<String, EditorTextTab>();
		setFocusable(false);
		createSearchToolBar();
		// setComponentPopupMenu(popup);
	}
	
	private JTextField searchField;
	private JCheckBox regexCB;
	private JCheckBox matchCaseCB;
	
	protected void createSearchToolBar() {
		JToolBar toolBar = new JToolBar();
		searchField = new JTextField(30);
		toolBar.add(searchField);
		final JButton nextButton = new JButton("Find Next");
		nextButton.setFocusable(false);
		nextButton.setActionCommand("FindNext");
		nextButton.addActionListener(this);
		toolBar.add(nextButton);
		searchField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextButton.doClick(0);
			}
		});
		JButton prevButton = new JButton("Find Previous");
		prevButton.setFocusable(false);
		prevButton.setActionCommand("FindPrev");
		prevButton.addActionListener(this);
		toolBar.add(prevButton);
		regexCB = new JCheckBox("Regex");
		toolBar.add(regexCB);
		matchCaseCB = new JCheckBox("Match Case");
		toolBar.add(matchCaseCB);
		// toolBar.setVisible(true);
		toolBar.setUI(new BasicToolBarUI() {
			@Override
			protected RootPaneContainer createFloatingWindow(JToolBar o) {
				class ToolBarDialog extends JDialog {
					public ToolBarDialog(Frame owner, String title, boolean modal) {
						super(owner, title, modal);
					}
					
					public ToolBarDialog(Dialog owner, String title, boolean modal) {
						super(owner, title, modal);
					}
					
					// Override createRootPane() to automatically resize
					// the frame when contents change
					@Override
					protected JRootPane createRootPane() {
						JRootPane rootPane = new JRootPane() {
							private boolean packing = false;
							
							@Override
							public void validate() {
								super.validate();
								if (!packing) {
									packing = true;
									pack();
									packing = false;
								}
							}
						};
						rootPane.setOpaque(true);
						return rootPane;
					}
				}
				
				JDialog dialog = new JDialog(IDEFrame.getInstance());
				// Window window = IDEFrame.getInstance();
				// if (window instanceof Frame) {
				// dialog = new ToolBarDialog((Frame) window, toolbar.getName(), false);
				// } else if (window instanceof Dialog) {
				// dialog = new ToolBarDialog((Dialog) window, toolbar.getName(), false);
				// } else {
				// dialog = new ToolBarDialog((Frame) null, toolbar.getName(), false);
				// }
				
				dialog.getRootPane().setName("ToolBar.FloatingWindow");
				// dialog.setTitle(toolBar.getName());
				dialog.setResizable(false);
				WindowListener wl = createFrameListener();
				dialog.addWindowListener(wl);
				return dialog;
			}
			
			{
				// createDragWindow(toolBar).setVisible(true);
				// setFloating(true, new Point(10, 10));
			}
		});
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Component selectedComponent = getSelectedComponent();
		if (selectedComponent instanceof EditorTextTab) {
			String cmd = e.getActionCommand();
			boolean forward = cmd.equals("FindNext");
			SearchContext context = new SearchContext();
			String text = searchField.getText();
			if (text.length() == 0) {
				return;
			}
			context.setSearchFor(text);
			context.setMatchCase(matchCaseCB.isSelected());
			context.setRegularExpression(regexCB.isSelected());
			context.setSearchForward(forward);
			context.setWholeWord(false);
			
			SearchResult found = SearchEngine.find(((EditorTextTab) selectedComponent).getTextArea(), context);
			if ((found == null) || found.wasFound()) {
				JOptionPane.showMessageDialog(this, "Text not found");
			} else {
				
			}
		}
	}
	
	public EditorTextTab getTextTab(String className) {
		return tabs.get(className);
	}
	
	public EditorTextTab createTextTab(String className, ProjectPanel projPanel) {
		EditorTextTab textTab = tabs.get(className);
		if (textTab == null) {
			tabs.put(className, textTab = new EditorTextTab(config, this, projPanel, className));
			addTab(className, textTab);
			textTab.setupFinal();
		}
		return textTab;
	}
	
	@Override
	public void remove(Component c) {
		super.remove(c);
	}
	
	@Override
	public void addTab(String title, Component c) {
		super.addTab(title, c);
		revalidate();
	}
	
	public void compileAndDump(final JarDownloader dl, final BasicSourceCompiler<ClassNode[]> compiler, final File file, final boolean start, final String mainClass) {
		new Thread() {
			@Override
			public void run() {
				final ClassContainer cc = dl.getContents();
				for (String tabName : tabs.keySet()) {
					EditorTextTab tab = tabs.get(tabName);
					ClassNode[] classes;
					try {
						classes = compiler.compile(tab.getTextArea().getText());
					} catch (CompilerException e1) {
						JOptionPane.showMessageDialog(EditorTabbedPane.this, e1.getMessage(), "Compiler error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
						return;
					}
					for (ClassNode cn : classes) {
						cc.addClass(cn);
					}
				}
				
				final JarClassLoader jcl = dl.getClassLoader();
				new CustomJarDumper(cc, jcl).dump(file);
				if (start) {
					try {
						JarLauncher.getInstance().start(file, mainClass);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (UnsupportedOperationException e1) {
						e1.printStackTrace();
					}
				}
			}
		}.start();
	}
}