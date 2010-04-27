package edacc.manageDB.InstanceParser;

/**
 * A class for parsing CNF instance files.
 *
 * @author Raffael Bild
 */


import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.lang.Integer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.NullPointerException;


public class InstanceParser
{

	/**
	 * A class for turning Strings into a sequence of "tokens",
	 * that means substrings without whitespace characters.
	 */
	private class Tokenizer
	{

		private String line;
		private int linePos;

		/**
		 * Construct a new Tokenizer.
		 *
	 	 * @param line The String that should be turned into a sequence of tokens
	 	 */
		public Tokenizer(String line)
		{
			this.line=line;
			linePos=0;
		}

		/**
		 * Fetch the next token.
		 *
	 	 * @return The next token or null if there's no token left
	 	 */
		public String nextToken()
		{
			//Skip whitespaces
			for(; linePos<line.length(); ++linePos) {
				char c=line.charAt(linePos);
				if(!java.lang.Character.isWhitespace(c))
					break;
			}

			//Try to read a token
			int offs=linePos;
			for(; linePos<line.length(); ++linePos) {
				char c=line.charAt(linePos);
				if(java.lang.Character.isWhitespace(c))
					break;
			}

			if(linePos>offs) {
				//We've read a token
				return line.substring(offs, linePos);
			}

			//There is no token left
			return null;
		}

	}

	public int n;          //The number of variables
	public int m;          //The number of clauses
	public int k;          //The number of literals in the longest clauses
	public float r;        //The ratio (m/n)
	public String name;    //The basename of the file
	public String message; //Information about the parser run

	/**
	 * Construct a new InstanceParser.
	 *
	 * @param filePath The path of a CNF file
	 */
	public InstanceParser(String filePath)
		throws NullPointerException, FileNotFoundException, IOException, InstanceException
	{
		Integer pn=null, pm=null;
		String pLine=null;

		n=0;
		m=0;
		k=0;
		r=0;

		File file = new File(filePath);
		name=file.getName();

		//Iterate over every line
		LineNumberReader lr = new LineNumberReader(new FileReader(file));
		String line;
		while((line=lr.readLine())!=null) {
			Tokenizer t = new Tokenizer(line);
			String token;
			int clauseLen=0;
			boolean afterZero=false;

			//Iterate over every token in the current line
			while((token=t.nextToken())!=null) {
				if(afterZero) {
					throw new InstanceException("\"0\" is not the last token in line "+lr.getLineNumber());
				}
				if(token.compareTo("c")==0) {
					//This is a comment line
					break;
				} else if(token.compareTo("p")==0) {
					//This is a p line
					pLine=line;
					String next=t.nextToken();
					if(next==null || next.compareTo("cnf")!=0)
						throw new InstanceException("Token \"cnf\" expected in line "+lr.getLineNumber());

					try {
						pn=Integer.parseInt(t.nextToken());
					} catch(java.lang.NumberFormatException e) {
						throw new InstanceException("Number of variables expected in line "+lr.getLineNumber());
					}

					try {
						pm=Integer.parseInt(t.nextToken());
					} catch(java.lang.NumberFormatException e) {
						throw new InstanceException("Number of clauses expected in line "+lr.getLineNumber());
					}

					break;
				} else {
					//This is a clause
					Integer nr;
					try {
						nr=Integer.parseInt(token);
					} catch(java.lang.NumberFormatException e) {
						throw new InstanceException("Non-numeric token \""+token+"\" found in file: \""+filePath+"\" at line "+lr.getLineNumber());
					}

					if(nr.intValue()==0) {
						afterZero=true;
						++m;
						continue;
					}

					int absVal=java.lang.Math.abs(nr.intValue());
					if(absVal>n)
						n=absVal;
					++clauseLen;
				}
			}
			if(clauseLen>k)
				k=clauseLen;
		}

		if(pLine!=null) {
			//We have a p-line
			String err="file: "+filePath+" content not concordant with parameter line: "+pLine+"\ninstance has "+n+" variales and "
			           +m+" clauses. The Parameter line specifies : "+pn.intValue()+" variables and "+pm.intValue()+" clauses";
			if(pn.intValue()>=n) {
				n=pn.intValue();
			} else {
				throw new InstanceException(err);
			}
			if(pm.intValue()!=m) {
				throw new InstanceException(err);
			}
			message="file: "+filePath+" parsed succesfully!";
		} else {
			//We don't have a p-line
			message = "file: "+filePath+" parameter line is missing computing parameters from content";
		}

		if(n==0) {
			throw new InstanceException("The file doesn't contain any clauses");
		}
		r = ((float)m) / ((float)n);
	}
}

