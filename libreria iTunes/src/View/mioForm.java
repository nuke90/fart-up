package View;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
public class mioForm extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2082566201422126147L;
	JButton ok;
	JButton copiaChiavetta;
	JComboBox<String> combo;
	public JLabel etichetta;
	LayoutManager layout;
	JMenuBar barraMenu;
	JMenu listaHelp,listaFile;
	JMenuItem aboutButton,exitButton;
	JPanel superiore,mediano,inferiore,sottoInferiore;
	
	public mioForm(){
		barraMenu=new JMenuBar();
		
		listaHelp=new JMenu("?");
		listaFile=new JMenu("File");
		
		barraMenu.setMaximumSize(new Dimension(2000, 100));
		
		aboutButton=new JMenuItem("About");
		aboutButton.setName("About");
		
		exitButton=new JMenuItem("Exit");
		exitButton.setName("Exit");
		
		listaFile.add(exitButton);
		listaHelp.add(aboutButton);
		
		
		barraMenu.add(listaFile);
		barraMenu.add(listaHelp);
		
		this.add(barraMenu);
		
		
		
		
		
		superiore=new JPanel();
		inferiore=new JPanel();
		mediano=new JPanel();
		sottoInferiore=new JPanel();
		
		etichetta=new JLabel();
		
		
		copiaChiavetta=new JButton();
		ok=new JButton();
		ok.setName("Copia");
		ok.setText("Copia");
		copiaChiavetta.setName("CopiaChiavetta");
		copiaChiavetta.setText("Copia Multicartella");
		combo=new JComboBox<String>();
		
		layout=new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS);
		
		this.setSize(new Dimension(700, 500));
		
		superiore.add(combo);
		
		mediano.add(etichetta);
		
		inferiore.add(ok);
		inferiore.add(copiaChiavetta);
		
		sottoInferiore.setLayout(new BoxLayout(sottoInferiore, BoxLayout.Y_AXIS));
		sottoInferiore.add(new JLabel("Se si clicca sui processi essi vengono fermati"));
		
		this.add(superiore);
		this.add(mediano);
		this.add(inferiore);
		this.add(sottoInferiore);
		this.setResizable(false);
		this.setVisible(false);
		this.getContentPane().setLayout(layout);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}
	public void setPlaylist(List<String> lista){
		for(String s : lista){
			combo.addItem(s);
		}
	}
	public void setOkButtonListener(ActionListener a){
		ok.addActionListener(a);
	}
	public void setCopiaChiavettaButtonListener(ActionListener a){
		copiaChiavetta.addActionListener(a);
	}
	
	public void setAboutButtonListener(ActionListener a){
		aboutButton.addActionListener(a);
	}
	
	public void setExitButtonListener(ActionListener a){
		exitButton.addActionListener(a);
	}
	
	public void addButtonMediano(String s,ActionListener a){
		JButton provv=new JButton(s);
		provv.setName(s);
		provv.setText(s);
		provv.addActionListener(a);
		sottoInferiore.add(provv);
		sottoInferiore.invalidate();
		sottoInferiore.repaint();
	}
	
	public void removeButtonMediano(String s){
		Component componenti[]=sottoInferiore.getComponents();
		for(Component c:componenti){
			if(c.getName().compareTo(s)==0){
				sottoInferiore.remove(c);
				break;
			}
		}
		sottoInferiore.validate();
		
	}
	
	public void clearButtonMediano(){
		Component componenti[]=sottoInferiore.getComponents();
		for(Component c:componenti){
				sottoInferiore.remove(c);
		}
		
	}
	
	public String getComboChoice(){
		return (String)combo.getSelectedItem();
	}
}
