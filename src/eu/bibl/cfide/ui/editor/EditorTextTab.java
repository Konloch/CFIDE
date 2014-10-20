package eu.bibl.cfide.ui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import eu.bibl.cfide.ui.UISettings;

public class EditorTextTab extends RTextScrollPane implements MouseListener, ActionListener {
	
	private static final long serialVersionUID = -9001184665877228717L;
	
	protected EditorTabbedPane tabbedPane;
	protected String title;
	protected JPopupMenu popupMenu;
	
	public EditorTextTab(EditorTabbedPane tabbedPane, String title) {
		super(new RSyntaxTextArea());
		this.tabbedPane = tabbedPane;
		this.title = title;
		init();
	}
	
	protected void init() {
		createPopupMenu(); // needs to be first
		createTabPanel();
	}
	
	protected void createPopupMenu() {
		// Close menu popup
		popupMenu = new JPopupMenu();
		// Close this menu button
		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tabbedPane.remove(EditorTextTab.this);
			}
		});
		// Close others menu button
		JMenuItem closeOthers = new JMenuItem("Close Others");
		closeOthers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (EditorTextTab sp : tabbedPane.tabs.values()) {
					if (!EditorTextTab.this.equals(sp)) {
						remove(sp);
					}
				}
			}
		});
		
		// Close all menu button
		JMenuItem closeAll = new JMenuItem("Close All");
		closeAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (EditorTextTab sp : tabbedPane.tabs.values()) {
					tabbedPane.remove(sp);
				}
			}
		});
		
		popupMenu.add(closeMenuItem);
		popupMenu.add(closeOthers);
		popupMenu.add(closeAll);
	}
	
	protected int index;
	protected JPanel tabNamePanel;
	protected JLabel tabNameLabel;
	protected JButton tabCloseButton;
	
	protected void createTabPanel() {
		tabNamePanel = new JPanel(new BorderLayout(5, 2));
		tabNamePanel.setOpaque(false);
		tabNamePanel.setFocusable(false);
		
		tabNameLabel = new JLabel(title);
		tabNameLabel.addMouseListener(this);
		
		tabCloseButton = new JButton(UISettings.CLOSE_BUTTON_ICON);
		tabCloseButton.setFocusable(false);
		tabCloseButton.addActionListener(this);
		tabCloseButton.setSize(UISettings.CLOSE_BUTTON_SIZE);
		tabCloseButton.setPreferredSize(UISettings.CLOSE_BUTTON_SIZE);
		
		tabNamePanel.setComponentPopupMenu(popupMenu);
		tabNamePanel.add(tabNameLabel, BorderLayout.WEST);
		tabNamePanel.add(tabCloseButton);
	}
	
	public void setupFinal() {// called from EditorTabbedPane.createTextTab
		index = tabbedPane.indexOfTab(title);
		tabbedPane.setTabComponentAt(index, tabNamePanel);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		tabbedPane.setSelectedComponent(EditorTextTab.this);
		if (e.getButton() != MouseEvent.BUTTON1) {
			if (popupMenu.isShowing())
				popupMenu.setVisible(false);
			popupMenu.show(tabNamePanel, e.getX(), e.getY());
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		tabbedPane.remove(EditorTextTab.this);
	}
}