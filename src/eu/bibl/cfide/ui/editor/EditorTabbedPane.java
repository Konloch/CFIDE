package eu.bibl.cfide.ui.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import eu.bibl.banalysis.asm.ClassNode;
import eu.bibl.banalysis.storage.classes.ClassContainer;
import eu.bibl.bio.jfile.classloader.JarClassLoader;
import eu.bibl.cfide.context.CFIDEContext;
import eu.bibl.cfide.engine.compiler.CompilerException;
import eu.bibl.cfide.engine.launch.JarLauncher;
import eu.bibl.cfide.engine.launch.dump.CustomJarDumper;
import eu.bibl.cfide.ui.tree.ClassTreeNode;

public class EditorTabbedPane extends JTabbedPane implements ActionListener {
	
	private static final long serialVersionUID = 9106124854514247948L;
	
	protected final CFIDEContext context;
	protected final Map<String, EditorTextTab> tabs;
	
	public EditorTabbedPane(CFIDEContext context) {
		this.context = context;
		tabs = new HashMap<String, EditorTextTab>();
		setFocusable(false);
		
		// createSearchToolBar();
		// setComponentPopupMenu(popup);
	}
	
	private JTextField searchField;
	private JCheckBox regexCB;
	private JCheckBox matchCaseCB;
	private JToolBar theToolBar;
	
	protected void createSearchToolBar() {
		final JDialog dialog = new JDialog(context.frame);
		theToolBar = new JToolBar() {
			private static final long serialVersionUID = -1935295602908748811L;
			
			@Override
			public Container getParent() {
				return dialog;
			}
		};
		searchField = new JTextField(30);
		theToolBar.add(searchField);
		final JButton nextButton = new JButton("Find Next");
		nextButton.setFocusable(false);
		nextButton.setActionCommand("FindNext");
		nextButton.addActionListener(this);
		theToolBar.add(nextButton);
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
		theToolBar.add(prevButton);
		regexCB = new JCheckBox("Regex");
		theToolBar.add(regexCB);
		matchCaseCB = new JCheckBox("Match Case");
		theToolBar.add(matchCaseCB);
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
	
	public EditorTextTab createTextTab(String className, CFIDEContext context) {
		EditorTextTab textTab = tabs.get(className);
		if (textTab == null) {
			// ISSUE #1: https://github.com/TheBiblMan/CFIDE/issues/1
			String simpleName = ClassTreeNode.getClassName(className);
			tabs.put(className, textTab = new EditorTextTab(simpleName, context));
			addTab(simpleName, textTab);
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
	
	public void compileAndDump(final CFIDEContext context, final File file, final boolean start, final String mainClass) {
		new Thread() {
			@Override
			public void run() {
				final ClassContainer cc = context.jarDownloader.getContents();
				for (String tabName : tabs.keySet()) {
					EditorTextTab tab = tabs.get(tabName);
					ClassNode[] classes;
					try {
						classes = context.compiler.compile(tab.getTextArea().getText());
					} catch (CompilerException e1) {
						JOptionPane.showMessageDialog(EditorTabbedPane.this, e1.getMessage(), "Compiler error", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
						return;
					}
					for (ClassNode cn : classes) {
						cc.addClass(cn);
					}
				}
				
				final JarClassLoader jcl = context.jarDownloader.getClassLoader();
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