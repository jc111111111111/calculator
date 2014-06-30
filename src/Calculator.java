import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/*
 * Name: Justin Coon
 * Date: April 30 2013
 */
public class Calculator extends JFrame
{
	private static final long serialVersionUID = 5566322968333965313L;
	final public static JFrame frame = new JFrame("Calculator");
	public static void createWindow()
	{
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		frame.add(new Calc());
		frame.pack();
		frame.setSize(310, 275);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void main(String[] args)
	{
		createWindow();	
	}
}
class Calc extends JPanel
{
	private static final long serialVersionUID = 8396051296744567245L;
	String text = "";
	boolean clearIt;
	String prevAnswer = "0";
	public Calc()
	{
		super(new GridBagLayout());
		setLayout(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    final JTextArea textArea = new JTextArea();
	    JButton[] buttons = 
	    	   {new JButton("+"), new JButton("7"), new JButton("8"), new JButton("9"), new JButton("Ans"), 
	    		new JButton("-"), new JButton("4"), new JButton("5"), new JButton("6"), new JButton("."),
	    		new JButton("*"), new JButton("1"), new JButton("2"), new JButton("3"), new JButton("C"), 
	    		new JButton("/"), new JButton("("), new JButton("0"), new JButton(")"), new JButton("=")};
	    buttons[4].setPreferredSize(new Dimension(0, 0));
	    c.fill = GridBagConstraints.BOTH;
	    c.weightx = 1;
	    c.weighty = 1;
	    c.anchor = GridBagConstraints.NORTHEAST;
	    c.insets = new Insets(1, 1, 1, 1);
	    for(int x = 0; x < buttons.length; x++)
	    {
	    	buttons[x].setActionCommand(buttons[x].getText());
	    	buttons[x].addActionListener(new ActionListener()
	    	{
	    		public void actionPerformed(ActionEvent e)
	    		{
	    			String action = e.getActionCommand();
    				if(clearIt)
    				{
    					text = "";
    					clearIt = false;
    				}
	    			if(action == "=" || action == "C")
	    			{
	    				if(action == "=")
	    				{
		    				if(checkParentheses(text))
		    				{
			    				clearIt = true;
			    				double answer = solve(text);
			    				if(Math.abs((int) answer - answer) < .0001)
			    					answer = (int) answer;
			    				text = Double.toString(answer);
			   					prevAnswer = text;
		    				}
		   					else
		   					{
		   						clearIt = true;
		   						text = "Error: Mismatching Parentheses";
	    					}
	    				}
		    			if(action == "C") text = "";
	    			}
	    			else if(text.equals("") && (action.equals("+") || action.equals("*") || action.equals("/")) && !action.equals("-")) text += "Ans" + action;
	    			else text += action;
	    			textArea.setText(text);
	    		}
	    	});
		    c.gridx = (x % 5);
		    c.gridy = (x / 5) + 1;
		    add(buttons[x], c);
	    }
	    c.gridwidth = 5;
	    c.gridx = 0;
	    c.gridy = 0;
	    textArea.setFont(new Font("Arial", Font.BOLD, 20));
	    textArea.setEditable(false);
	    add(textArea, c);
	}
	boolean checkParentheses(String in)
	{
		char[] chInput = in.toCharArray();
		int startParentheses = 0;
		int endParentheses = 0;
		for(int x = 0; x < chInput.length; x++)
		{
			if(chInput[x] == '(') startParentheses++;
			if(chInput[x] == ')') endParentheses++;
		}
		if(startParentheses == endParentheses) return true;
		else return false;
	}
	public double solve(String in)
	{
		return doMath(solveParentheses(findAndReplace(in, '-', "+-")));
	}
	String findAndReplace(String in, char find, String replace)
	{
		char[] chInput = in.toCharArray();
		String returnable = chInput[0] + "";
		for(int x = 1; x < chInput.length; x++)
		{
			if(chInput[x] == find && !(chInput[x-1] == '*' || chInput[x-1] == '/' || chInput[x-1] == '+')) returnable += replace;
			else returnable += chInput[x];
		}
		return returnable;
	}
	String solveParentheses(String input)
	{
		if(!input.contains("(") && !input.contains(")")) return input;
		else
		{
			char[] chInput = input.toCharArray();
			int[] startpoints = new int[input.length()];
			int startCounter = 0;
			int layers = 0;
			for(int x = 0; x < chInput.length; x++)
			{
				if(chInput[x] == '(')
				{
					if(layers == 0)
					{
						startpoints[startCounter] = x+1;
						startCounter++;
					}
					layers++;
				}
				if(chInput[x] == ')') layers--;
			}
			int[] endpoints = new int[input.length()];
			layers = 0;
			int endCounter = 0;
			for(int i = 0; i < startpoints.length; i++)
			{
				for(int x = startpoints[i]; x < chInput.length; x++)
				{
					if(chInput[x] == '(') layers++;
					else if(chInput[x] == ')')
					{
						if(layers == 0)
						{
							endpoints[endCounter] = x;
							endCounter++;
						}
						layers--;
					}
				}
			}
			String returnable = input.substring(0, startpoints[0]-1);
			for(int x = 0; x < startCounter; x++)
			{
				if(input.substring(startpoints[x], endpoints[x]).contains("(") || input.substring(startpoints[x], endpoints[x]).contains(")"))
					returnable += solveParentheses(input.substring(startpoints[x], endpoints[x])); 
				else
					returnable += doMath(input.substring(startpoints[x], endpoints[x])); 
				if(x < startCounter-1) 
					returnable += input.substring(endpoints[x]+1, startpoints[x+1]-1);
				else 
					returnable += input.substring(endpoints[x]+1);
			}
			return returnable;
		}
	}
	public double doMath(String input)
	{
		if(input.equals("Ans") || input.equals("-Ans")) return doMath(((input.equals("Ans")) ? "" : "+-") + prevAnswer);
		String[] split;
		double returnable = 0;
		if(input != null && !input.equals(""))
		{
			if(!input.contains("/") && !input.contains("*") && !input.contains("+")) return Double.parseDouble(input);
			else
			{
				if(input.contains("+"))
				{
					split = input.split("[+]+");
					returnable = doMath(split[0]);
					for(int x = 1; x < split.length; x++) returnable += doMath(split[x]);
				}
				else
				{
					if(input.contains("*"))
					{
						split = input.split("[*]+");
						returnable = doMath(split[0]);
						for(int x = 1; x < split.length; x++) returnable *= doMath(split[x]);
					}
					else
					{
						if(input.contains("/"))
						{
							split = input.split("[/]+");
							returnable = doMath(split[0]);
							for(int x = 1; x < split.length; x++) returnable /= doMath(split[x]);
						}
					}
				}
			}
		}
		return returnable;
	}
}
