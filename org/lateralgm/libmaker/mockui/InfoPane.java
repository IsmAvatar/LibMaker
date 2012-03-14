package org.lateralgm.libmaker.mockui;

import static java.lang.Integer.MAX_VALUE;
import static javax.swing.GroupLayout.DEFAULT_SIZE;

import java.awt.Component;
import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.lateralgm.libmaker.Messages;
import org.lateralgm.libmaker.backend.Library;
import org.lateralgm.libmaker.backend.Library.PLibrary;
import org.lateralgm.libmaker.components.NumberField;
import org.lateralgm.libmaker.mockui.MockUI.GroupPanel;

public class InfoPane extends GroupPanel
	{
	private static final long serialVersionUID = 1L;

	JTextField tAuthor, tChanged;
	NumberField tVersion;
	JTextArea tInfo;

	static InfoPane instance;

	public static void showInDialog(Component owner, Library lib)
		{
		/*JDialog d = new JDialog(owner,Messages.getString("InfoPane.DIALOG_TITLE"),true);
		JOptionPane op = new JOptionPane(ep,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION,null,
				Option.values());
		op.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY,this);
		d.add(op);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.pack();
		d.setVisible(true);
		d.setLocationRelativeTo(owner);*/

		if (instance == null) instance = new InfoPane();
		instance.setComponents(lib);
		String title = Messages.getString("InfoPane.DIALOG_TITLE"); //$NON-NLS-1$
		int r = JOptionPane.showConfirmDialog(owner,instance,title,JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (r == JOptionPane.OK_OPTION) instance.commit(lib);
		}

	protected void initKeyComponents()
		{
		tAuthor = new JTextField();
		tVersion = new NumberField(Integer.MIN_VALUE,Integer.MAX_VALUE);
		tChanged = new JTextField();
		tChanged.setEditable(false);
		tInfo = new JTextArea();
		tInfo.setRows(15);
		tInfo.setLineWrap(true);
		}

	@Override
	protected void layoutComponents(GroupLayout layout)
		{
		initKeyComponents();

		JLabel lAuthor = new JLabel(Messages.getString("InfoPane.AUTHOR")); //$NON-NLS-1$
		JLabel lVersion = new JLabel(Messages.getString("InfoPane.VERSION")); //$NON-NLS-1$
		JLabel lChanged = new JLabel(Messages.getString("InfoPane.LASTCHANGED")); //$NON-NLS-1$
		JLabel lInfo = new JLabel(Messages.getString("InfoPane.INFORMATION")); //$NON-NLS-1$

		JScrollPane infoScroll = new JScrollPane(tInfo);

		layout.setHorizontalGroup(layout.createParallelGroup()
		/**/.addGroup(layout.createSequentialGroup()
		/*		*/.addGroup(layout.createParallelGroup()
		/*				*/.addComponent(lAuthor)
		/*				*/.addComponent(lVersion)
		/*				*/.addComponent(lChanged))
		/*		*/.addGroup(layout.createParallelGroup()
		/*				*/.addComponent(tAuthor,DEFAULT_SIZE,240,MAX_VALUE)
		/*				*/.addComponent(tVersion,DEFAULT_SIZE,240,MAX_VALUE)
		/*				*/.addComponent(tChanged,DEFAULT_SIZE,240,MAX_VALUE)))
		/**/.addComponent(lInfo,DEFAULT_SIZE,320,MAX_VALUE)
		/**/.addComponent(infoScroll));
		layout.setVerticalGroup(layout.createSequentialGroup()
		/**/.addGroup(layout.createParallelGroup(Alignment.BASELINE)
		/*		*/.addComponent(lAuthor)
		/*		*/.addComponent(tAuthor))
		/**/.addGroup(layout.createParallelGroup(Alignment.BASELINE)
		/*		*/.addComponent(lVersion)
		/*		*/.addComponent(tVersion))
		/**/.addGroup(layout.createParallelGroup(Alignment.BASELINE)
		/*		*/.addComponent(lChanged)
		/*		*/.addComponent(tChanged))
		/**/.addComponent(lInfo)
		/**/.addComponent(infoScroll));
		}

	public void setComponents(Library lib)
		{
		tAuthor.setText((String) lib.get(PLibrary.AUTHOR));
		tVersion.setValue(lib.get(PLibrary.VERSION));
		tChanged.setText(gmTimeToString((Double) lib.get(PLibrary.CHANGED)));
		tInfo.setText((String) lib.get(PLibrary.INFO));
		}

	public void commit(Library lib)
		{
		lib.put(PLibrary.AUTHOR,tAuthor.getText());
		lib.put(PLibrary.VERSION,tVersion.getIntValue());
		lib.put(PLibrary.INFO,tInfo.getText());
		}

	public static String gmTimeToString(double time)
		{
		Calendar base = Library.gmBaseTime();
		base.setTimeInMillis(base.getTimeInMillis() + ((long) (time * 86400000)));
		return DateFormat.getDateTimeInstance().format(base.getTime());
		}
	}
