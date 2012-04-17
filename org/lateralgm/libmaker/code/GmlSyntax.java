package org.lateralgm.libmaker.code;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lateralgm.joshedit.DefaultTokenMarker.KeywordSet;

public class GmlSyntax
	{
	public static Construct[] CONSTRUCTS;
	public static Operator[] OPERATORS;
	public static Variable[] VARIABLES;
	public static Constant[] CONSTANTS;
	public static Function[] FUNCTIONS;
	public static final Keyword[][] GML_KEYWORDS = { CONSTRUCTS,FUNCTIONS,VARIABLES,OPERATORS,
			CONSTANTS };

	private static final Color BROWN = new Color(128,0,0);
	private static final Color FUNCTION = new Color(0,0,128);

	static KeywordSet constructs, functions, operators, constants, variables;

	static
		{
		populateFunctions();
		populateKeywords();

		functions = new KeywordSet("Functions",FUNCTION,Font.PLAIN);
		constructs = new KeywordSet("Constructs",Color.BLACK,Font.BOLD);
		operators = new KeywordSet("Operators",Color.BLACK,Font.BOLD);
		constants = new KeywordSet("Constants",BROWN,Font.PLAIN);
		variables = new KeywordSet("Variables",Color.BLUE,Font.ITALIC);
		}

	private static void populateFunctions()
		{
		final String fn2 = "functions.txt";
		InputStream is2 = GmlSyntax.class.getResourceAsStream(fn2);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));

		ArrayList<Function> list = new ArrayList<Function>();

		try
			{
			String func;
			while ((func = br2.readLine()) != null)
				{
				String args = br2.readLine();
				String desc = br2.readLine();
				list.add(new Function(func,args,desc));
				}
			}
		catch (IOException e)
			{
			e.printStackTrace();
			}

		FUNCTIONS = list.toArray(new Function[0]);
		}

	private static void populateKeywords()
		{
		Properties KEYWORD_PROPS = new Properties();
		try
			{
			KEYWORD_PROPS.load(GmlSyntax.class.getResourceAsStream("gmlkeywords.properties")); //$NON-NLS-1$
			}
		catch (IOException e)
			{
			//Silently revert (probably to C++ keywords)
			return;
			}
		String[] s = KEYWORD_PROPS.getProperty("CONSTRUCTS").split("\\s+");
		CONSTRUCTS = new Construct[s.length];
		for (int i = 0; i < s.length; i++)
			CONSTRUCTS[i] = new Construct(s[i]);
		s = KEYWORD_PROPS.getProperty("OPERATORS").split("\\s+");
		OPERATORS = new Operator[s.length];
		for (int i = 0; i < s.length; i++)
			OPERATORS[i] = new Operator(s[i]);
		s = KEYWORD_PROPS.getProperty("VARIABLES").split("\\s+");
		VARIABLES = new Variable[s.length];
		for (int i = 0; i < s.length; i++)
			VARIABLES[i] = new Variable(s[i]);
		s = KEYWORD_PROPS.getProperty("CONSTANTS").split("\\s+");
		CONSTANTS = new Constant[s.length];
		for (int i = 0; i < s.length; i++)
			CONSTANTS[i] = new Constant(s[i]);
		KEYWORD_PROPS.clear();
		}

	private GmlSyntax()
		{
		}

	public abstract static class Keyword
		{
		protected String name;

		public String getName()
			{
			return name;
			}
		}

	public static class Construct extends Keyword
		{
		public Construct(String input)
			{
			name = input;
			}
		}

	public static class Operator extends Keyword
		{
		public Operator(String input)
			{
			name = input;
			}
		}

	public static class Variable extends Keyword
		{
		public final boolean readOnly;
		public final int arraySize;

		public Variable(String input)
			{
			Matcher m = Pattern.compile("(\\w+)(\\[(\\d+)])?(\\*)?").matcher(input);
			if (!m.matches()) System.err.println("Invalid variable: " + input);
			name = m.group(1);
			String s = m.group(3);
			arraySize = s != null ? Integer.valueOf(m.group(3)) : 0;
			readOnly = "*".equals(m.group(4));
			}
		}

	public static class Constant extends Keyword
		{
		public Constant(String input)
			{
			name = input;
			}
		}

	public static class Function extends Keyword
		{
		public final String description;
		public final String[] arguments;
		public final int dynArgIndex;
		public final int dynArgMin;
		public final int dynArgMax;

		public Function(String input)
			{
			//  1   1  23    3 245   5  6   6 7   7 8        84 9   9
			// /(\w+)\(((\w+,)*)((\w+)\{(\d+),(\d+)}((?=\))|,))?(\w+)?\)/
			//   fun  (  arg,     arg  { 0   , 9   }        ,    arg   )
			String re = "(\\w+)\\(((\\w+,)*)((\\w+)\\{(\\d+),(\\d+)}((?=\\))|,))?(\\w+)?\\)";
			Matcher m = Pattern.compile(re).matcher(input);
			if (!m.matches()) System.err.println("Invalid function: " + input);
			name = m.group(1); //the function name
			String a1 = m.group(2); //plain arguments with commas
			String da = m.group(5); //argument with range
			String daMin = m.group(6); //range min
			String daMax = m.group(7); //range max
			String a2 = m.group(9); //last argument
			String[] aa1 = a1.length() > 0 ? a1.split(",") : new String[0];
			arguments = new String[aa1.length + (da != null ? 1 : 0) + (a2 != null ? 1 : 0)];
			System.arraycopy(aa1,0,arguments,0,aa1.length);
			if (da == null)
				{
				dynArgIndex = -1;
				dynArgMin = 0;
				dynArgMax = 0;
				}
			else
				{
				dynArgIndex = aa1.length;
				dynArgMin = Integer.parseInt(daMin);
				dynArgMax = Integer.parseInt(daMax);
				arguments[aa1.length] = da;
				}
			if (a2 != null) arguments[arguments.length - 1] = a2;
			description = "";
			}

		public Function(String func, String args, String desc)
			{
			name = func;
			arguments = args.split(",");
			description = desc;

			dynArgIndex = -1;
			dynArgMin = 0;
			dynArgMax = 0;
			}
		}
	}
