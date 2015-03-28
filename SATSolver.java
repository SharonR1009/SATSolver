import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;


public class SATSolver {
	public static void main(String[] args) throws IOException {
		String inputPath = args[0];
		String outputPath = args[1];
		Double probabilityOfRandomWalk = Double.parseDouble(args[2]);
		Integer numberOfFlips = Integer.parseInt(args[3]);
		
		
		parseInput(inputPath,outputPath,probabilityOfRandomWalk,numberOfFlips);
		
	}
	//
	static void parseInput(String inputPath,String outputPath, Double probabilityOfRandomWalk, Integer numberOfFlips) throws IOException{
		
		File file = new File(inputPath);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		String string = "";
		int i, j = 0;
		tempString = reader.readLine();
		String test[] = tempString.split(" ");
		//System.out.println(test[0] + " " + test[1]);
		
		while((tempString = reader.readLine()) != null)
		{
			string = string + tempString;
		}
		string = string.replaceAll(" ", "");
		
		//i = Integer.parseInt(String.valueOf(string.charAt(0)));
		//j = Integer.parseInt(String.valueOf(string.charAt(1)));
		i = Integer.parseInt(test[0]);
		j = Integer.parseInt(test[1]);
		int[][] Matrix = new int[i][i];
		//System.out.println(i + " " + j);
		
		int r = 0;
		for(int m=0; m<i; m++)
		{
			for(int n=0; n<i; n++)
			{
				if(string.charAt(r) == '-')
				{
					Matrix[m][n] = Integer.parseInt(String.valueOf("-1"));
					r = r+2;
				}
				else
				{
					Matrix[m][n] = Integer.parseInt(String.valueOf(string.charAt(r)));
					r++;
				}
			}
		}
		
		
		KnowledgeBase kb = new KnowledgeBase();
		//Constraint 1
		for(int p = 1;p< i+1;p++)
		{
			String fact = " ";
			for(int q = 1;q<j ;q++)
			{
				fact = "(X"+p+q+" OR ";
			}
			fact = fact + "X"+p+j+")";
			//System.out.println(fact);
			kb.tell(fact);
		}
		for(int a = 1; a < i+1; a++)
		{
			for(int p1 = 1;p1<j ;p1++)
			{
				String fact = " ";
				for(int q1 = p1+1; q1< j+1; q1++)
				{
					fact = "((NOT X"+a+p1+") OR (NOT X"+a+q1+"))";
					//System.out.println(fact);
					kb.tell(fact);
				}
			}
		}
		//yes list
		for(int m1= 0;m1< i-1;m1++)
		{
			
			for(int n1=m1+1;n1 < i; n1++)
			{
				String fact1 = " ";
				String fact2 = " ";
				if(Matrix[m1][n1] == 1)
				{
					int a = m1+1;
					int b = n1+1;
					for(int p = 1;p<j+1 ;p++)
					{
						fact1 = "((NOT X"+a+p+") OR X"+b+p+")";
						//System.out.println(fact1);
						kb.tell(fact1);
						fact2 = "((NOT X"+b+p+") OR X"+a+p+")";
						//System.out.println(fact2);
						kb.tell(fact2);
					}
					
				}
			}
		}
		//no list
		
		for(int m1= 0;m1< i-1;m1++)
		{
			for(int n1=m1+1;n1 < i; n1++)
			{
				String fact = " ";
				if(Matrix[m1][n1] == -1)
				{
					int a = m1+1;
					int b = n1+1;
					for(int p=1; p<j+1; p++)
					{
						fact = "((NOT X"+a+p+") OR (NOT X"+b+p+"))";
						//System.out.println(fact);
						kb.tell(fact);
					}
					
				}
			}
		}
		
		reader.close();
        
		PLResolution plr = new PLResolution();
		boolean result = plr.plResolution(kb);
		
		File f = new File(outputPath);
        f.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
		
		if(result == false)
		{
			System.out.println("1");
			
			WalkSAT walkSAT = new WalkSAT();
			Model m = walkSAT.findModelFor(kb.asSentence().toString(), numberOfFlips, probabilityOfRandomWalk);
			if (m == null) {
				System.out.println("failure");
			} else {
				m.print();
			}
		}else{
			System.out.println("0");
		}
		
	}
}

abstract class Lexer {
	protected abstract Token nextToken();
    
	protected Reader input;
    
	protected int lookAhead = 1;
    
	protected int[] lookAheadBuffer;
    
	/**
	 * Sets the character stream of the lexical analyzer.
	 *
	 * @param inputString
	 *            a sequence of characters to be converted into a sequence of
	 *            tokens.
	 */
	public void setInput(String inputString) {
		lookAheadBuffer = new int[lookAhead];
		this.input = new StringReader(inputString);
		fillLookAheadBuffer();
	}
    
	/**
	 * Sets the character stream and look ahead buffer to <code>null</code>.
	 */
	public void clear() {
		this.input = null;
		lookAheadBuffer = null;
	}
    
	/*
	 * Stores the next character in the lookahead buffer to make parsing action
	 * decisions.
	 */
	protected void fillLookAheadBuffer() {
		try {
			lookAheadBuffer[0] = (char) input.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
	}
    
	/*
	 * Returns the character at the specified position in the lookahead buffer.
	 */
	protected char lookAhead(int position) {
		return (char) lookAheadBuffer[position - 1];
	}
    
	/*
	 * Returns true if the end of the stream has been reached.
	 */
	protected boolean isEndOfFile(int i) {
		return (-1 == i);
	}
    
	/*
	 * Loads the next character into the lookahead buffer if the end of the
	 * stream has not already been reached.
	 */
	protected void loadNextCharacterFromInput() {
        
		boolean eofEncountered = false;
		for (int i = 0; i < lookAhead - 1; i++) {
            
			lookAheadBuffer[i] = lookAheadBuffer[i + 1];
			if (isEndOfFile(lookAheadBuffer[i])) {
				eofEncountered = true;
				break;
			}
		}
		if (!eofEncountered) {
			try {
				lookAheadBuffer[lookAhead - 1] = input.read();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
	}
    
	protected void consume() {
		loadNextCharacterFromInput();
	}
}

interface LogicTokenTypes {
	static final int SYMBOL = 1;
    
	static final int LPAREN = 2;
    
	static final int RPAREN = 3;
    
	static final int COMMA = 4;
    
	static final int CONNECTOR = 5;
    
	static final int QUANTIFIER = 6;
    
	static final int PREDICATE = 7;
    
	static final int FUNCTION = 8;
    
	static final int VARIABLE = 9;
    
	static final int CONSTANT = 10;
    
	static final int TRUE = 11;
    
	static final int FALSE = 12;
    
	static final int EQUALS = 13;
    
	static final int WHITESPACE = 1000;
    
	static final int EOI = 9999;
}

abstract class Parser {
    
	protected Lexer lexer;
    
	protected Token[] lookAheadBuffer;
    
	protected int lookAhead = 3;
    
	public abstract ParseTreeNode parse(String input);
    
	/*
	 * Stores the next token in the lookahead buffer to make parsing action
	 * decisions.
	 */
	protected void fillLookAheadBuffer() {
		for (int i = 0; i < lookAhead; i++) {
			lookAheadBuffer[i] = lexer.nextToken();
		}
	}
    
	/*
	 * Returns the token at the specified position in the lookahead buffer.
	 */
	protected Token lookAhead(int i) {
		return lookAheadBuffer[i - 1];
	}
    
	protected void consume() {
		loadNextTokenFromInput();
	}
    
	/*
	 * Loads the next token into the lookahead buffer if the end of the stream
	 * has not already been reached.
	 */
	protected void loadNextTokenFromInput() {
        
		boolean eoiEncountered = false;
		for (int i = 0; i < lookAhead - 1; i++) {
            
			lookAheadBuffer[i] = lookAheadBuffer[i + 1];
			if (isEndOfInput(lookAheadBuffer[i])) {
				eoiEncountered = true;
				break;
			}
		}
		if (!eoiEncountered) {
			try {
				lookAheadBuffer[lookAhead - 1] = lexer.nextToken();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
	}
    
	/*
	 * Returns true if the end of the stream has been reached.
	 */
	protected boolean isEndOfInput(Token t) {
		return (t.getType() == LogicTokenTypes.EOI);
	}
    
	protected void match(String terminalSymbol) {
		if (lookAhead(1).getText().equals(terminalSymbol)) {
			consume();
		} else {
			throw new RuntimeException(
                                       "Syntax error detected at match. Expected "
                                       + terminalSymbol + " but got "
                                       + lookAhead(1).getText());
		}
        
	}
}

interface ParseTreeNode {
    
}
class Token {
	private String text;
    
	private int type;
    
	/**
	 * Constructs a token from the specified token-name and attribute-value
	 *
	 * @param type
	 *            the token-name
	 * @param text
	 *            the attribute-value
	 */
	public Token(int type, String text) {
		this.type = type;
		this.text = text;
	}
    
	/**
	 * Returns the attribute-value of this token.
	 *
	 * @return the attribute-value of this token.
	 */
	public String getText() {
		return text;
	}
    
	/**
	 * Returns the token-name of this token.
	 *
	 * @return the token-name of this token.
	 */
	public int getType() {
		return type;
	}
    
	@Override
	public boolean equals(Object o) {
        
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		Token other = (Token) o;
		return ((other.type == type) && (other.text.equals(text)));
	}
    
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + type;
		result = 37 * result + text.hashCode();
		return 17;
	}
    
	@Override
	public String toString() {
		return "[ " + type + " " + text + " ]";
	}
}
interface Visitor {
    
}
class DPLL {
    
	private static final Converter<Symbol> SYMBOL_CONVERTER = new Converter<Symbol>();
    
	/**
	 * Returns <code>true</code> if the specified sentence is satisfiable. A
	 * sentence is satisfiable if it is true in, or satisfied by, some model.
	 *
	 * @param s
	 *            a sentence in propositional logic
	 *
	 * @return <code>true</code> if the specified sentence is satisfiable.
	 */
	public boolean dpllSatisfiable(Sentence s) {
        
		return dpllSatisfiable(s, new Model());
	}
    
	/**
	 * Returns <code>true</code> if the specified sentence is satisfiable. A
	 * sentence is satisfiable if it is true in, or satisfied by, some model.
	 *
	 * @param string
	 *            a String representation of a Sentence in propositional logic
	 *
	 * @return <code>true</code> if the specified sentence is satisfiable.
	 */
	public boolean dpllSatisfiable(String string) {
		Sentence sen = (Sentence) new PEParser().parse(string);
		return dpllSatisfiable(sen, new Model());
	}
    
	/**
	 * Returns <code>true</code> if the specified sentence is satisfiable. A
	 * sentence is satisfiable if it is true in, or satisfied by, some model.
	 *
	 * @param s
	 *            a sentence in propositional logic
	 * @param m
	 *            a model the sentence must be true in
	 *
	 * @return <code>true</code> if the specified sentence is satisfiable.
	 */
	public boolean dpllSatisfiable(Sentence s, Model m) {
		Set<Sentence> clauses = new CNFClauseGatherer()
        .getClausesFrom(new CNFTransformer().transform(s));
		List<Symbol> symbols = SYMBOL_CONVERTER.setToList(new SymbolCollector()
                                                          .getSymbolsIn(s));
		// System.out.println(" numberOfSymbols = " + symbols.size());
		return dpll(clauses, symbols, m);
	}
    
	public List<Sentence> clausesWithNonTrueValues(List<Sentence> clauseList,
                                                   Model model) {
		List<Sentence> clausesWithNonTrueValues = new ArrayList<Sentence>();
		for (int i = 0; i < clauseList.size(); i++) {
			Sentence clause = clauseList.get(i);
			if (!(isClauseTrueInModel(clause, model))) {
				if (!(clausesWithNonTrueValues.contains(clause))) {// defensive
					// programming not really necessary
					clausesWithNonTrueValues.add(clause);
				}
			}
            
		}
		return clausesWithNonTrueValues;
	}
    
	public SymbolValuePair findPureSymbolValuePair(List<Sentence> clauseList,
                                                   Model model, List<Symbol> symbols) {
		List<Sentence> clausesWithNonTrueValues = clausesWithNonTrueValues(
                                                                           clauseList, model);
		Sentence nonTrueClauses = LogicUtils.chainWith("AND",
                                                       clausesWithNonTrueValues);
		// System.out.println("Unsatisfied clauses = "
		// + clausesWithNonTrueValues.size());
		Set<Symbol> symbolsAlreadyAssigned = model.getAssignedSymbols();
        
		// debug
		// List symList = asList(symbolsAlreadyAssigned);
		//
		// System.out.println(" assignedSymbols = " + symList.size());
		// if (symList.size() == 52) {
		// System.out.println("untrue clauses = " + clausesWithNonTrueValues);
		// System.out.println("model= " + model);
		// }
        
		// debug
		List<Symbol> purePositiveSymbols = SYMBOL_CONVERTER.setToList(SetOps
                                                                      .difference(new SymbolClassifier()
                                                                                  .getPurePositiveSymbolsIn(nonTrueClauses),
                                                                                  symbolsAlreadyAssigned));
        
		List<Symbol> pureNegativeSymbols = SYMBOL_CONVERTER.setToList(SetOps
                                                                      .difference(new SymbolClassifier()
                                                                                  .getPureNegativeSymbolsIn(nonTrueClauses),
                                                                                  symbolsAlreadyAssigned));
		// if none found return "not found
		if ((purePositiveSymbols.size() == 0)
            && (pureNegativeSymbols.size() == 0)) {
			return new SymbolValuePair();// automatically set to null values
		} else {
			if (purePositiveSymbols.size() > 0) {
				Symbol symbol = new Symbol(
                                           (purePositiveSymbols.get(0)).getValue());
				if (pureNegativeSymbols.contains(symbol)) {
					throw new RuntimeException("Symbol " + symbol.getValue()
                                               + "misclassified");
				}
				return new SymbolValuePair(symbol, true);
			} else {
				Symbol symbol = new Symbol(
                                           (pureNegativeSymbols.get(0)).getValue());
				if (purePositiveSymbols.contains(symbol)) {
					throw new RuntimeException("Symbol " + symbol.getValue()
                                               + "misclassified");
				}
				return new SymbolValuePair(symbol, false);
			}
		}
	}
    
	//
	// PRIVATE METHODS
	//
    
	private boolean dpll(Set<Sentence> clauses, List<Symbol> symbols,
                         Model model) {
		// List<Sentence> clauseList = asList(clauses);
		List<Sentence> clauseList = new Converter<Sentence>()
        .setToList(clauses);
		// System.out.println("clauses are " + clauses.toString());
		// if all clauses are true return true;
		if (areAllClausesTrue(model, clauseList)) {
			// System.out.println(model.toString());
			return true;
		}
		// if even one clause is false return false
		if (isEvenOneClauseFalse(model, clauseList)) {
			// System.out.println(model.toString());
			return false;
		}
		// System.out.println("At least one clause is unknown");
		// try to find a unit clause
		SymbolValuePair svp = findPureSymbolValuePair(clauseList, model,
                                                      symbols);
		if (svp.notNull()) {
			List<Symbol> newSymbols = new ArrayList<Symbol>(symbols);
			newSymbols.remove(new Symbol(svp.symbol.getValue()));
			Model newModel = model.extend(new Symbol(svp.symbol.getValue()),
                                          svp.value.booleanValue());
			return dpll(clauses, newSymbols, newModel);
		}
        
		SymbolValuePair svp2 = findUnitClause(clauseList, model, symbols);
		if (svp2.notNull()) {
			List<Symbol> newSymbols = new ArrayList<Symbol>(symbols);
			newSymbols.remove(new Symbol(svp2.symbol.getValue()));
			Model newModel = model.extend(new Symbol(svp2.symbol.getValue()),
                                          svp2.value.booleanValue());
			return dpll(clauses, newSymbols, newModel);
		}
        
		Symbol symbol = (Symbol) symbols.get(0);
		// System.out.println("default behaviour selecting " + symbol);
		List<Symbol> newSymbols = new ArrayList<Symbol>(symbols);
		newSymbols.remove(0);
		return (dpll(clauses, newSymbols, model.extend(symbol, true)) || dpll(
                                                                              clauses, newSymbols, model.extend(symbol, false)));
	}
    
	private boolean isEvenOneClauseFalse(Model model, List<Sentence> clauseList) {
		for (int i = 0; i < clauseList.size(); i++) {
			Sentence clause = clauseList.get(i);
			if (model.isFalse(clause)) {
				// System.out.println(clause.toString() + " is false");
				return true;
			}
            
		}
        
		return false;
	}
    
	private boolean areAllClausesTrue(Model model, List<Sentence> clauseList) {
        
		for (int i = 0; i < clauseList.size(); i++) {
			Sentence clause = clauseList.get(i);
			// System.out.println("evaluating " + clause.toString());
			if (!isClauseTrueInModel(clause, model)) { // ie if false or
				// UNKNOWN
				// System.out.println(clause.toString()+ " is not true");
				return false;
			}
            
		}
		return true;
	}
    
	private boolean isClauseTrueInModel(Sentence clause, Model model) {
		List<Symbol> positiveSymbols = SYMBOL_CONVERTER
        .setToList(new SymbolClassifier().getPositiveSymbolsIn(clause));
		List<Symbol> negativeSymbols = SYMBOL_CONVERTER
        .setToList(new SymbolClassifier().getNegativeSymbolsIn(clause));
        
		for (Symbol symbol : positiveSymbols) {
			if ((model.isTrue(symbol))) {
				return true;
			}
		}
		for (Symbol symbol : negativeSymbols) {
			if ((model.isFalse(symbol))) {
				return true;
			}
		}
		return false;
        
	}
    
	private SymbolValuePair findUnitClause(List<Sentence> clauseList,
                                           Model model, List<Symbol> symbols) {
		for (int i = 0; i < clauseList.size(); i++) {
			Sentence clause = (Sentence) clauseList.get(i);
			if ((clause instanceof Symbol)
                && (!(model.getAssignedSymbols().contains(clause)))) {
				// System.out.println("found unit clause - assigning");
				return new SymbolValuePair(new Symbol(
                                                      ((Symbol) clause).getValue()), true);
			}
            
			if (clause instanceof UnarySentence) {
				UnarySentence sentence = (UnarySentence) clause;
				Sentence negated = sentence.getNegated();
				if ((negated instanceof Symbol)
                    && (!(model.getAssignedSymbols().contains(negated)))) {
					// System.out.println("found unit clause type 2 -
					// assigning");
					return new SymbolValuePair(new Symbol(
                                                          ((Symbol) negated).getValue()), false);
				}
			}
            
		}
        
		return new SymbolValuePair();// failed to find any unit clause;
        
	}
    
	public class SymbolValuePair {
		public Symbol symbol;// public to avoid unnecessary get and set
        
		// accessors
        
		public Boolean value;
        
		public SymbolValuePair() {
			// represents "No Symbol found with a boolean value that makes all
			// its literals true
			symbol = null;
			value = null;
		}
        
		public SymbolValuePair(Symbol symbol, boolean bool) {
			// represents "Symbol found with a boolean value that makes all
			// its literals true
			this.symbol = symbol;
			value = new Boolean(bool);
		}
        
		public boolean notNull() {
			return (symbol != null) && (value != null);
		}
        
		@Override
		public String toString() {
			String symbolString, valueString;
			if (symbol == null) {
				symbolString = "NULL";
			} else {
				symbolString = symbol.toString();
			}
			if (value == null) {
				valueString = "NULL";
			} else {
				valueString = value.toString();
			}
			return symbolString + " -> " + valueString;
		}
	}
}
class KnowledgeBase {
	private List<Sentence> sentences;
    
	private PEParser parser;
    
	public KnowledgeBase() {
		sentences = new ArrayList<Sentence>();
		parser = new PEParser();
	}
    
	/**
	 * Adds the specified sentence to the knowledge base.
	 *
	 * @param aSentence
	 *            a fact to be added to the knowledge base.
	 */
	public void tell(String aSentence) {
		Sentence sentence = (Sentence) parser.parse(aSentence);
		if (!(sentences.contains(sentence))) {
			sentences.add(sentence);
		}
	}
    
	/**
	 * Each time the agent program is called, it TELLS the knowledge base what
	 * it perceives.
	 *
	 * @param percepts
	 *            what the agent perceives
	 */
	public void tellAll(String[] percepts) {
		for (int i = 0; i < percepts.length; i++) {
			tell(percepts[i]);
		}
        
	}
    
	/**
	 * Returns the number of sentences in the knowledge base.
	 *
	 * @return the number of sentences in the knowledge base.
	 */
	public int size() {
		return sentences.size();
	}
    
	/**
	 * Returns the list of sentences in the knowledge base chained together as a
	 * single sentence.
	 *
	 * @return the list of sentences in the knowledge base chained together as a
	 *         single sentence.
	 */
	public Sentence asSentence() {
		return LogicUtils.chainWith("AND", sentences);
	}
    
	/**
	 * Returns the answer to the specified question using the DPLL algorithm.
	 *
	 * @param queryString
	 *            a question to ASK the knowledge base
	 *
	 * @return the answer to the specified question using the DPLL algorithm.
	 */
	public boolean askWithDpll(String queryString) {
		Sentence query = null, cnfForm = null;
		try {
			// just a check to see that the query is well formed
			query = (Sentence) parser.parse(queryString);
		} catch (Exception e) {
			System.out.println("error parsing query" + e.getMessage());
		}
        
		Sentence kbSentence = asSentence();
		Sentence kbPlusQuery = null;
		if (kbSentence != null) {
			kbPlusQuery = (Sentence) parser.parse(" ( " + kbSentence.toString()
                                                  + " AND (NOT " + queryString + " ))");
		} else {
			kbPlusQuery = query;
		}
		try {
			cnfForm = new CNFTransformer().transform(kbPlusQuery);
			// System.out.println(cnfForm.toString());
		} catch (Exception e) {
			System.out.println("error converting kb +  query to CNF"
                               + e.getMessage());
            
		}
		return !new DPLL().dpllSatisfiable(cnfForm);
	}
    
	/**
	 * Returns the answer to the specified question using the TT-Entails
	 * algorithm.
	 *
	 * @param queryString
	 *            a question to ASK the knowledge base
	 *
	 * @return the answer to the specified question using the TT-Entails
	 *         algorithm.
	 */
	public boolean askWithTTEntails(String queryString) {
        
		return new TTEntails().ttEntails(this, queryString);
	}
    
	@Override
	public String toString() {
		if (sentences.size() == 0) {
			return "";
		} else
			return asSentence().toString();
	}
    
	/**
	 * Returns the list of sentences in the knowledge base.
	 *
	 * @return the list of sentences in the knowledge base.
	 */
	public List<Sentence> getSentences() {
		return sentences;
	}
}
class LogicUtils {
    
	public static Sentence chainWith(String connector, List<Sentence> sentences) {
		if (sentences.size() == 0) {
			return null;
		} else if (sentences.size() == 1) {
			return sentences.get(0);
		} else {
			Sentence soFar = sentences.get(0);
			for (int i = 1; i < sentences.size(); i++) {
				Sentence next = sentences.get(i);
				soFar = new BinarySentence(connector, soFar, next);
			}
			return soFar;
		}
	}
}
class Model implements PLVisitor {
    
	private HashMap<Symbol, Boolean> h = new HashMap<Symbol, Boolean>();
    
	public Model() {
        
	}
    
	public Boolean getStatus(Symbol symbol) {
		return h.get(symbol);
	}
    
	public boolean isTrue(Symbol symbol) {
		return Boolean.TRUE.equals(h.get(symbol));
	}
    
	public boolean isFalse(Symbol symbol) {
		return Boolean.FALSE.equals(h.get(symbol));
	}
    
	public Model extend(Symbol symbol, boolean b) {
		Model m = new Model();
		m.h.putAll(this.h);
		m.h.put(symbol, b);
		return m;
	}
    
	public boolean isTrue(Sentence clause) {
		return Boolean.TRUE.equals(clause.accept(this, null));
	}
    
	public boolean isFalse(Sentence clause) {
		return Boolean.FALSE.equals(clause.accept(this, null));
	}
    
	public boolean isUnknown(Sentence clause) {
		return null == clause.accept(this, null);
	}
    
	public Model flip(Symbol s) {
		if (isTrue(s)) {
			return extend(s, false);
		}
		if (isFalse(s)) {
			return extend(s, true);
		}
		return this;
	}
    
	public Set<Symbol> getAssignedSymbols() {
		return Collections.unmodifiableSet(h.keySet());
	}
    
	public void print() {
		/*for (Map.Entry<Symbol, Boolean> e : h.entrySet()) {
         System.out.print(e.getKey() + " = " + e.getValue() + " ");
         }
         System.out.println();*/
		//
		int M[][] = new int[50][50];
		int ma = 0;
		int mb = 0;
		for (Map.Entry<Symbol, Boolean> e : h.entrySet()) {
			String c = e.getKey().toString().replaceAll("X", "");
			int a = Integer.parseInt(c.substring(1, 2))-1;
			int b = Integer.parseInt(c.substring(0, 1))-1;
			//System.out.println(a + " " + b);
			
			if(e.getValue() == false)
				M[a][b] = 0;
			else if(e.getValue() == true)
				M[a][b] = 1;
			
			if(ma < a)
				ma = a;
			if(mb < b)
				mb = b;
		}
		for(int i=0; i< ma+1;i++)
		{
			for(int j =0; j<mb+1;j++)
				System.out.print(M[i][j]+" ");
			System.out.println("\n");
		}
	}
    
	@Override
	public String toString() {
		return h.toString();
	}
    
	//
	// START-PLVisitor
	@Override
	public Object visitSymbol(Symbol s, Object arg) {
		return getStatus(s);
	}
    
	@Override
	public Object visitTrueSentence(TrueSentence ts, Object arg) {
		return Boolean.TRUE;
	}
    
	@Override
	public Object visitFalseSentence(FalseSentence fs, Object arg) {
		return Boolean.FALSE;
	}
    
	@Override
	public Object visitNotSentence(UnarySentence fs, Object arg) {
		Object negatedValue = fs.getNegated().accept(this, null);
		if (negatedValue != null) {
			return new Boolean(!((Boolean) negatedValue).booleanValue());
		} else {
			return null;
		}
	}
    
	@Override
	public Object visitBinarySentence(BinarySentence bs, Object arg) {
		Boolean firstValue = (Boolean) bs.getFirst().accept(this, null);
		Boolean secondValue = (Boolean) bs.getSecond().accept(this, null);
		if ((firstValue == null) || (secondValue == null)) {
			// strictly not true for or/and
			// -FIX later
			return null;
		} else {
			String operator = bs.getOperator();
			if (operator.equals("AND")) {
				return firstValue && secondValue;
			} else if (operator.equals("OR")) {
				return firstValue || secondValue;
			} else if (operator.equals("=>")) {
				return !(firstValue && !secondValue);
			} else if (operator.equals("<=>")) {
				return firstValue.equals(secondValue);
			}
			return null;
		}
	}
    
	@Override
	public Object visitMultiSentence(MultiSentence fs, Object argd) {
		// TODO remove this?
		return null;
	}
	// END-PLVisitor
	//
}
class PLFCEntails {
    
	private Hashtable<HornClause, Integer> count;
    
	private Hashtable<Symbol, Boolean> inferred;
    
	private Stack<Symbol> agenda;
    
	public PLFCEntails() {
		count = new Hashtable<HornClause, Integer>();
		inferred = new Hashtable<Symbol, Boolean>();
		agenda = new Stack<Symbol>();
	}
    
	/**
	 * Return the answer to the specified question using the PL-FC-Entails
	 * algorithm
	 *
	 * @param kb
	 *            the knowledge base, a set of propositional definite clauses
	 * @param s
	 *            the query, a proposition symbol
	 *
	 * @return the answer to the specified question using the PL-FC-Entails
	 *         algorithm
	 */
	public boolean plfcEntails(KnowledgeBase kb, String s) {
		return plfcEntails(kb, new Symbol(s));
	}
    
	/**
	 * Return the answer to the specified question using the PL-FC-Entails
	 * algorithm
	 *
	 * @param kb
	 *            the knowledge base, a set of propositional definite clauses
	 * @param q
	 *            the query, a proposition symbol
	 *
	 * @return the answer to the specified question using the PL-FC-Entails
	 *         algorithm
	 */
	public boolean plfcEntails(KnowledgeBase kb, Symbol q) {
		List<HornClause> hornClauses = asHornClauses(kb.getSentences());
		while (agenda.size() != 0) {
			Symbol p = agenda.pop();
			while (!inferred(p)) {
				inferred.put(p, Boolean.TRUE);
                
				for (int i = 0; i < hornClauses.size(); i++) {
					HornClause hornClause = hornClauses.get(i);
					if (hornClause.premisesContainsSymbol(p)) {
						decrementCount(hornClause);
						if (countisZero(hornClause)) {
							if (hornClause.head().equals(q)) {
								return true;
							} else {
								agenda.push(hornClause.head());
							}
						}
					}
				}
			}
		}
		return false;
	}
    
	private List<HornClause> asHornClauses(List<Sentence> sentences) {
		List<HornClause> hornClauses = new ArrayList<HornClause>();
		for (int i = 0; i < sentences.size(); i++) {
			Sentence sentence = sentences.get(i);
			HornClause clause = new HornClause(sentence);
			hornClauses.add(clause);
		}
		return hornClauses;
	}
    
	private boolean countisZero(HornClause hornClause) {
        
		return (count.get(hornClause)).intValue() == 0;
	}
    
	private void decrementCount(HornClause hornClause) {
		int value = (count.get(hornClause)).intValue();
		count.put(hornClause, new Integer(value - 1));
        
	}
    
	private boolean inferred(Symbol p) {
		Object value = inferred.get(p);
		return ((value == null) || value.equals(Boolean.TRUE));
	}
    
	public class HornClause {
		List<Symbol> premiseSymbols;
        
		Symbol head;
        
		/**
		 * Constructs a horn clause from the specified sentence.
		 *
		 * @param sentence
		 *            a sentence in propositional logic
		 */
		public HornClause(Sentence sentence) {
			if (sentence instanceof Symbol) {
				head = (Symbol) sentence;
				agenda.push(head);
				premiseSymbols = new ArrayList<Symbol>();
				count.put(this, new Integer(0));
				inferred.put(head, Boolean.FALSE);
			} else if (!isImpliedSentence(sentence)) {
				throw new RuntimeException("Sentence " + sentence
                                           + " is not a horn clause");
                
			} else {
				BinarySentence bs = (BinarySentence) sentence;
				head = (Symbol) bs.getSecond();
				inferred.put(head, Boolean.FALSE);
				Set<Symbol> symbolsInPremise = new SymbolCollector()
                .getSymbolsIn(bs.getFirst());
				Iterator<Symbol> iter = symbolsInPremise.iterator();
				while (iter.hasNext()) {
					inferred.put(iter.next(), Boolean.FALSE);
				}
				premiseSymbols = new Converter<Symbol>()
                .setToList(symbolsInPremise);
				count.put(this, new Integer(premiseSymbols.size()));
			}
            
		}
        
		private boolean isImpliedSentence(Sentence sentence) {
			return ((sentence instanceof BinarySentence) && ((BinarySentence) sentence)
					.getOperator().equals("=>"));
		}
        
		/**
		 * Returns the conclusion of this horn clause. In horn form, the premise
		 * is called the body, and the conclusion is called the head.
		 *
		 * @return the conclusion of this horn clause.
		 */
		public Symbol head() {
            
			return head;
		}
        
		/**
		 * Return <code>true</code> if the premise of this horn clause contains
		 * the specified symbol.
		 *
		 * @param q
		 *            a symbol in propositional logic
		 *
		 * @return <code>true</code> if the premise of this horn clause contains
		 *         the specified symbol.
		 */
		public boolean premisesContainsSymbol(Symbol q) {
			return premiseSymbols.contains(q);
		}
        
		/**
		 * Returns a list of all the symbols in the premise of this horn clause
		 *
		 * @return a list of all the symbols in the premise of this horn clause
		 */
		public List<Symbol> getPremiseSymbols() {
			return premiseSymbols;
		}
        
		@Override
		public boolean equals(Object o) {
            
			if (this == o) {
				return true;
			}
			if ((o == null) || (this.getClass() != o.getClass())) {
				return false;
			}
			HornClause ohc = (HornClause) o;
			if (premiseSymbols.size() != ohc.premiseSymbols.size()) {
				return false;
			}
			for (Symbol s : premiseSymbols) {
				if (!ohc.premiseSymbols.contains(s)) {
					return false;
				}
			}
            
			return true;
		}
        
		@Override
		public int hashCode() {
			int result = 17;
			for (Symbol s : premiseSymbols) {
				result = 37 * result + s.hashCode();
			}
			return result;
		}
        
		@Override
		public String toString() {
			return premiseSymbols.toString() + " => " + head;
		}
	}
}
class PLResolution {
    
	/**
	 * Returns the answer to the specified question using PL-Resolution.
	 *
	 * @param kb
	 *            the knowledge base, a sentence in propositional logic
	 * @param alpha
	 *            the query, a sentence in propositional logic
	 *
	 * @return the answer to the specified question using PL-Resolution.
	 */
	/*public boolean plResolution(KnowledgeBase kb, String alpha) {
     return plResolution(kb, (Sentence) new PEParser().parse(alpha));
     }*/
    
    
	/**
	 * Returns the answer to the specified question using PL-Resolution.
	 *
	 * @param kb
	 *            the knowledge base, a sentence in propositional logic
	 * @param alpha
	 *            the query, a sentence in propositional logic
	 *
	 * @return the answer to the specified question using PL-Resolution.
	 */
	/*public boolean plResolution(KnowledgeBase kb, Sentence alpha) {
     Sentence kBAndNotAlpha = new BinarySentence("AND", kb.asSentence(),
     new UnarySentence(alpha));
     Set<Sentence> clauses = new CNFClauseGatherer()
     .getClausesFrom(new CNFTransformer().transform(kBAndNotAlpha));
     
     clauses = filterOutClausesWithTwoComplementaryLiterals(clauses);
     Set<Sentence> newClauses = new HashSet<Sentence>();
     while (true) {
     List<List<Sentence>> pairs = getCombinationPairs(new Converter<Sentence>()
     .setToList(clauses));
     
     for (int i = 0; i < pairs.size(); i++) {
     List<Sentence> pair = pairs.get(i);
     // System.out.println("pair number" + i+" of "+pairs.size());
     Set<Sentence> resolvents = plResolve(pair.get(0), pair.get(1));
     resolvents = filterOutClausesWithTwoComplementaryLiterals(resolvents);
     
     if (resolvents.contains(new Symbol("EMPTY_CLAUSE"))) {
     return true;
     }
     newClauses = SetOps.union(newClauses, resolvents);
     // System.out.println("clauseslist size = " +clauses.size());
     
     }
     if (SetOps.intersection(newClauses, clauses).size() == newClauses
     .size()) {// subset test
     return false;
     }
     clauses = SetOps.union(newClauses, clauses);
     clauses = filterOutClausesWithTwoComplementaryLiterals(clauses);
     }
     
     }*/
	public boolean plResolution(KnowledgeBase kb) {
		Set<Sentence> clauses = new CNFClauseGatherer()
        .getClausesFrom(new CNFTransformer().transform(kb.asSentence()));
		
		clauses = filterOutClausesWithTwoComplementaryLiterals(clauses);
		Set<Sentence> newClauses = new HashSet<Sentence>();
		while (true) {
			List<List<Sentence>> pairs = getCombinationPairs(new Converter<Sentence>()
                                                             .setToList(clauses));
            
			for (int i = 0; i < pairs.size(); i++) {
				List<Sentence> pair = pairs.get(i);
				// System.out.println("pair number" + i+" of "+pairs.size());
				Set<Sentence> resolvents = plResolve(pair.get(0), pair.get(1));
				resolvents = filterOutClausesWithTwoComplementaryLiterals(resolvents);
                
				if (resolvents.contains(new Symbol("EMPTY_CLAUSE"))) {
					return true;
				}
				newClauses = SetOps.union(newClauses, resolvents);
				// System.out.println("clauseslist size = " +clauses.size());
                
			}
			if (SetOps.intersection(newClauses, clauses).size() == newClauses
                .size()) {// subset test
				return false;
			}
			clauses = SetOps.union(newClauses, clauses);
			clauses = filterOutClausesWithTwoComplementaryLiterals(clauses);
		}
        
	}
	
	//
	//
	public Set<Sentence> plResolve(Sentence clause1, Sentence clause2) {
		Set<Sentence> resolvents = new HashSet<Sentence>();
		ClauseSymbols cs = new ClauseSymbols(clause1, clause2);
		Iterator<Symbol> iter = cs.getComplementedSymbols().iterator();
		while (iter.hasNext()) {
			Symbol symbol = iter.next();
			resolvents.add(createResolventClause(cs, symbol));
		}
        
		return resolvents;
	}
    
	/*public boolean plResolution(String kbs, String alphaString) {
     KnowledgeBase kb = new KnowledgeBase();
     kb.tell(kbs);
     Sentence alpha = (Sentence) new PEParser().parse(alphaString);
     return plResolution(kb, alpha);
     }*/
    
    
	//
	// PRIVATE METHODS
	//
    
	private Set<Sentence> filterOutClausesWithTwoComplementaryLiterals(
                                                                       Set<Sentence> clauses) {
		Set<Sentence> filtered = new HashSet<Sentence>();
		SymbolClassifier classifier = new SymbolClassifier();
		Iterator<Sentence> iter = clauses.iterator();
		while (iter.hasNext()) {
			Sentence clause = iter.next();
			Set<Symbol> positiveSymbols = classifier
            .getPositiveSymbolsIn(clause);
			Set<Symbol> negativeSymbols = classifier
            .getNegativeSymbolsIn(clause);
			if ((SetOps.intersection(positiveSymbols, negativeSymbols).size() == 0)) {
				filtered.add(clause);
			}
		}
		return filtered;
	}
    
	private Sentence createResolventClause(ClauseSymbols cs, Symbol toRemove) {
		List<Symbol> positiveSymbols = new Converter<Symbol>().setToList(SetOps
                                                                         .union(cs.clause1PositiveSymbols, cs.clause2PositiveSymbols));
		List<Symbol> negativeSymbols = new Converter<Symbol>().setToList(SetOps
                                                                         .union(cs.clause1NegativeSymbols, cs.clause2NegativeSymbols));
		if (positiveSymbols.contains(toRemove)) {
			positiveSymbols.remove(toRemove);
		}
		if (negativeSymbols.contains(toRemove)) {
			negativeSymbols.remove(toRemove);
		}
        
		Collections.sort(positiveSymbols, new SymbolComparator());
		Collections.sort(negativeSymbols, new SymbolComparator());
        
		List<Sentence> sentences = new ArrayList<Sentence>();
		for (int i = 0; i < positiveSymbols.size(); i++) {
			sentences.add(positiveSymbols.get(i));
		}
		for (int i = 0; i < negativeSymbols.size(); i++) {
			sentences.add(new UnarySentence(negativeSymbols.get(i)));
		}
		if (sentences.size() == 0) {
			return new Symbol("EMPTY_CLAUSE"); // == empty clause
		} else {
			return LogicUtils.chainWith("OR", sentences);
		}
        
	}
    
	private List<List<Sentence>> getCombinationPairs(List<Sentence> clausesList) {
		// int odd = clausesList.size() % 2;
		// int midpoint = 0;
		// if (odd == 1) {
		// midpoint = (clausesList.size() / 2) + 1;
		// } else {
		// midpoint = (clausesList.size() / 2);
		// }
        
		List<List<Sentence>> pairs = new ArrayList<List<Sentence>>();
		for (int i = 0; i < clausesList.size(); i++) {
			for (int j = i; j < clausesList.size(); j++) {
				List<Sentence> pair = new ArrayList<Sentence>();
				Sentence first = clausesList.get(i);
				Sentence second = clausesList.get(j);
                
				if (!(first.equals(second))) {
					pair.add(first);
					pair.add(second);
					pairs.add(pair);
				}
			}
		}
		return pairs;
	}
    
	class ClauseSymbols {
		Set<Symbol> clause1Symbols, clause1PositiveSymbols,
        clause1NegativeSymbols;
        
		Set<Symbol> clause2Symbols, clause2PositiveSymbols,
        clause2NegativeSymbols;
        
		Set<Symbol> positiveInClause1NegativeInClause2,
        negativeInClause1PositiveInClause2;
        
		public ClauseSymbols(Sentence clause1, Sentence clause2) {
            
			SymbolClassifier classifier = new SymbolClassifier();
            
			clause1Symbols = classifier.getSymbolsIn(clause1);
			clause1PositiveSymbols = classifier.getPositiveSymbolsIn(clause1);
			clause1NegativeSymbols = classifier.getNegativeSymbolsIn(clause1);
            
			clause2Symbols = classifier.getSymbolsIn(clause2);
			clause2PositiveSymbols = classifier.getPositiveSymbolsIn(clause2);
			clause2NegativeSymbols = classifier.getNegativeSymbolsIn(clause2);
            
			positiveInClause1NegativeInClause2 = SetOps.intersection(
                                                                     clause1PositiveSymbols, clause2NegativeSymbols);
			negativeInClause1PositiveInClause2 = SetOps.intersection(
                                                                     clause1NegativeSymbols, clause2PositiveSymbols);
            
		}
        
		public Set<Symbol> getComplementedSymbols() {
			return SetOps.union(positiveInClause1NegativeInClause2,
                                negativeInClause1PositiveInClause2);
		}
        
	}
}
class TTEntails {
    
	/**
	 * Returns the answer to the specified question using the TT-Entails
	 * algorithm.
	 *
	 * @param kb
	 *            a knowledge base to ASK
	 * @param alpha
	 *            a question to ASK the knowledge base
	 *
	 * @return the answer to the specified question using the TT-Entails
	 *         algorithm.
	 */
	public boolean ttEntails(KnowledgeBase kb, String alpha) {
		Sentence kbSentence = kb.asSentence();
		Sentence querySentence = (Sentence) new PEParser().parse(alpha);
		SymbolCollector collector = new SymbolCollector();
		Set<Symbol> kbSymbols = collector.getSymbolsIn(kbSentence);
		Set<Symbol> querySymbols = collector.getSymbolsIn(querySentence);
		Set<Symbol> symbols = SetOps.union(kbSymbols, querySymbols);
		List<Symbol> symbolList = new Converter<Symbol>().setToList(symbols);
		return ttCheckAll(kbSentence, querySentence, symbolList, new Model());
	}
    
	public boolean ttCheckAll(Sentence kbSentence, Sentence querySentence,
                              List<Symbol> symbols, Model model) {
		if (symbols.isEmpty()) {
			if (model.isTrue(kbSentence)) {
				// System.out.println("#");
				return model.isTrue(querySentence);
			} else {
				// System.out.println("0");
				return true;
			}
		} else {
			Symbol symbol = Util.first(symbols);
			List<Symbol> rest = Util.rest(symbols);
            
			Model trueModel = model.extend(new Symbol(symbol.getValue()), true);
			Model falseModel = model.extend(new Symbol(symbol.getValue()),
                                            false);
			return (ttCheckAll(kbSentence, querySentence, rest, trueModel) && (ttCheckAll(
                                                                                          kbSentence, querySentence, rest, falseModel)));
		}
	}
}
class WalkSAT {
	private Model myModel;
    
	private Random random = new Random();
    
	/**
	 * Returns a satisfying model or failure (null).
	 *
	 * @param logicalSentence
	 *            a set of clauses in propositional logic
	 * @param numberOfFlips
	 *            number of flips allowed before giving up
	 * @param probabilityOfRandomWalk
	 *            the probability of choosing to do a "random walk" move,
	 *            typically around 0.5
	 *
	 * @return a satisfying model or failure (null).
	 */
	public Model findModelFor(String logicalSentence, int numberOfFlips,
                              double probabilityOfRandomWalk) {
		myModel = new Model();
		Sentence s = (Sentence) new PEParser().parse(logicalSentence);
		CNFTransformer transformer = new CNFTransformer();
		CNFClauseGatherer clauseGatherer = new CNFClauseGatherer();
		SymbolCollector sc = new SymbolCollector();
        
		List<Symbol> symbols = new Converter<Symbol>().setToList(sc
                                                                 .getSymbolsIn(s));
		for (int i = 0; i < symbols.size(); i++) {
			Symbol sym = (Symbol) symbols.get(i);
			myModel = myModel.extend(sym, Util.randomBoolean());
		}
		List<Sentence> clauses = new Converter<Sentence>()
        .setToList(clauseGatherer.getClausesFrom(transformer
                                                 .transform(s)));
        
		for (int i = 0; i < numberOfFlips; i++) {
			if (getNumberOfClausesSatisfiedIn(
                                              new Converter<Sentence>().listToSet(clauses), myModel) == clauses
                .size()) {
				return myModel;
			}
			Sentence clause = clauses.get(random.nextInt(clauses.size()));
            
			List<Symbol> symbolsInClause = new Converter<Symbol>().setToList(sc
                                                                             .getSymbolsIn(clause));
			if (random.nextDouble() >= probabilityOfRandomWalk) {
				Symbol randomSymbol = symbolsInClause.get(random
                                                          .nextInt(symbolsInClause.size()));
				myModel = myModel.flip(randomSymbol);
			} else {
				Symbol symbolToFlip = getSymbolWhoseFlipMaximisesSatisfiedClauses(
                                                                                  new Converter<Sentence>().listToSet(clauses),
                                                                                  symbolsInClause, myModel);
				myModel = myModel.flip(symbolToFlip);
			}
            
		}
		return null;
	}
    
	private Symbol getSymbolWhoseFlipMaximisesSatisfiedClauses(
                                                               Set<Sentence> clauses, List<Symbol> symbols, Model model) {
		if (symbols.size() > 0) {
			Symbol retVal = symbols.get(0);
			int maxClausesSatisfied = 0;
			for (int i = 0; i < symbols.size(); i++) {
				Symbol sym = symbols.get(i);
				if (getNumberOfClausesSatisfiedIn(clauses, model.flip(sym)) > maxClausesSatisfied) {
					retVal = sym;
					maxClausesSatisfied = getNumberOfClausesSatisfiedIn(
                                                                        clauses, model.flip(sym));
				}
			}
			return retVal;
		} else {
			return null;
		}
        
	}
    
	private int getNumberOfClausesSatisfiedIn(Set<Sentence> clauses, Model model) {
		int retVal = 0;
		Iterator<Sentence> i = clauses.iterator();
		while (i.hasNext()) {
			Sentence s = i.next();
			if (model.isTrue(s)) {
				retVal += 1;
			}
		}
		return retVal;
	}
}

class AbstractPLVisitor implements PLVisitor {
	private PEParser parser = new PEParser();
    
	public Object visitSymbol(Symbol s, Object arg) {
		return new Symbol(s.getValue());
	}
    
	public Object visitTrueSentence(TrueSentence ts, Object arg) {
		return new TrueSentence();
	}
    
	public Object visitFalseSentence(FalseSentence fs, Object arg) {
		return new FalseSentence();
	}
    
	public Object visitNotSentence(UnarySentence fs, Object arg) {
		return new UnarySentence((Sentence) fs.getNegated().accept(this, arg));
	}
    
	public Object visitBinarySentence(BinarySentence fs, Object arg) {
		return new BinarySentence(fs.getOperator(), (Sentence) fs.getFirst()
                                  .accept(this, arg), (Sentence) fs.getSecond().accept(this, arg));
	}
    
	public Object visitMultiSentence(MultiSentence fs, Object arg) {
		List<Sentence> terms = fs.getSentences();
		List<Sentence> newTerms = new ArrayList<Sentence>();
		for (int i = 0; i < terms.size(); i++) {
			Sentence s = (Sentence) terms.get(i);
			Sentence subsTerm = (Sentence) s.accept(this, arg);
			newTerms.add(subsTerm);
		}
		return new MultiSentence(fs.getOperator(), newTerms);
	}
    
	protected Sentence recreate(Object ast) {
		return (Sentence) parser.parse(((Sentence) ast).toString());
	}
}
class PELexer extends Lexer {
    
	Set<String> connectors;
    
	public PELexer() {
		connectors = new HashSet<String>();
		connectors.add("NOT");
		connectors.add("AND");
		connectors.add("OR");
		connectors.add("=>");
		connectors.add("<=>");
	}
    
	/**
	 * Constructs a propositional expression lexer with the specified character
	 * stream.
	 *
	 * @param inputString
	 *            a sequence of characters to be converted into a sequence of
	 *            tokens.
	 */
	public PELexer(String inputString) {
		this();
		setInput(inputString);
	}
    
	/**
	 * Returns the next token from the character stream.
	 *
	 * @return the next token from the character stream.
	 */
	@Override
	public Token nextToken() {
		if (lookAhead(1) == '(') {
			consume();
			return new Token(LogicTokenTypes.LPAREN, "(");
            
		} else if (lookAhead(1) == ')') {
			consume();
			return new Token(LogicTokenTypes.RPAREN, ")");
		} else if (identifierDetected()) {
			return symbol();
            
		} else if (Character.isWhitespace(lookAhead(1))) {
			consume();
			return nextToken();
			// return whiteSpace();
		} else if (lookAhead(1) == (char) -1) {
			return new Token(LogicTokenTypes.EOI, "EOI");
		} else {
			throw new RuntimeException("Lexing error on character "
                                       + lookAhead(1));
		}
	}
    
	private boolean identifierDetected() {
		return (Character.isJavaIdentifierStart((char) lookAheadBuffer[0]))
        || partOfConnector();
	}
    
	private boolean partOfConnector() {
		return (lookAhead(1) == '=') || (lookAhead(1) == '<')
        || (lookAhead(1) == '>');
	}
    
	private Token symbol() {
		StringBuffer sbuf = new StringBuffer();
		while ((Character.isLetterOrDigit(lookAhead(1)))
               || (lookAhead(1) == '=') || (lookAhead(1) == '<')
               || (lookAhead(1) == '>')) {
			sbuf.append(lookAhead(1));
			consume();
		}
		String symbol = sbuf.toString();
		if (isConnector(symbol)) {
			return new Token(LogicTokenTypes.CONNECTOR, sbuf.toString());
		} else if (symbol.equalsIgnoreCase("true")) {
			return new Token(LogicTokenTypes.TRUE, "TRUE");
		} else if (symbol.equalsIgnoreCase("false")) {
			return new Token(LogicTokenTypes.FALSE, "FALSE");
		} else {
			return new Token(LogicTokenTypes.SYMBOL, sbuf.toString());
		}
        
	}
    
	@SuppressWarnings("unused")
	private Token connector() {
		StringBuffer sbuf = new StringBuffer();
		while (Character.isLetterOrDigit(lookAhead(1))) {
			sbuf.append(lookAhead(1));
			consume();
		}
		return new Token(LogicTokenTypes.CONNECTOR, sbuf.toString());
	}
    
	@SuppressWarnings("unused")
	private Token whiteSpace() {
		StringBuffer sbuf = new StringBuffer();
		while (Character.isWhitespace(lookAhead(1))) {
			sbuf.append(lookAhead(1));
			consume();
		}
		return new Token(LogicTokenTypes.WHITESPACE, sbuf.toString());
        
	}
    
	private boolean isConnector(String aSymbol) {
		return (connectors.contains(aSymbol));
	}
}
class PEParser extends Parser {
    
	public PEParser() {
		lookAheadBuffer = new Token[lookAhead];
	}
    
	@Override
	public ParseTreeNode parse(String inputString) {
		lexer = new PELexer(inputString);
		fillLookAheadBuffer();
		return parseSentence();
	}
    
	private TrueSentence parseTrue() {
		consume();
		return new TrueSentence();
	}
    
	private FalseSentence parseFalse() {
		consume();
		return new FalseSentence();
	}
    
	private Symbol parseSymbol() {
		String sym = lookAhead(1).getText();
		consume();
		return new Symbol(sym);
	}
    
	private AtomicSentence parseAtomicSentence() {
		Token t = lookAhead(1);
		if (t.getType() == LogicTokenTypes.TRUE) {
			return parseTrue();
		} else if (t.getType() == LogicTokenTypes.FALSE) {
			return parseFalse();
		} else if (t.getType() == LogicTokenTypes.SYMBOL) {
			return parseSymbol();
		} else {
			throw new RuntimeException(
                                       "Error in parseAtomicSentence with Token " + lookAhead(1));
		}
	}
    
	private UnarySentence parseNotSentence() {
		match("NOT");
		Sentence sen = parseSentence();
		return new UnarySentence(sen);
	}
    
	private MultiSentence parseMultiSentence() {
		consume();
		String connector = lookAhead(1).getText();
		consume();
		List<Sentence> sentences = new ArrayList<Sentence>();
		while (lookAhead(1).getType() != LogicTokenTypes.RPAREN) {
			Sentence sen = parseSentence();
			// consume();
			sentences.add(sen);
		}
		match(")");
		return new MultiSentence(connector, sentences);
	}
    
	private Sentence parseSentence() {
		if (detectAtomicSentence()) {
			return parseAtomicSentence();
		} else if (detectBracket()) {
			return parseBracketedSentence();
		} else if (detectNOT()) {
			return parseNotSentence();
		} else {
            
			throw new RuntimeException("Parser Error Token = " + lookAhead(1));
		}
	}
    
	private boolean detectNOT() {
		return (lookAhead(1).getType() == LogicTokenTypes.CONNECTOR)
        && (lookAhead(1).getText().equals("NOT"));
	}
    
	private Sentence parseBracketedSentence() {
        
		if (detectMultiOperator()) {
			return parseMultiSentence();
		} else {
			match("(");
			Sentence one = parseSentence();
			if (lookAhead(1).getType() == LogicTokenTypes.RPAREN) {
				match(")");
				return one;
			} else if ((lookAhead(1).getType() == LogicTokenTypes.CONNECTOR)
                       && (!(lookAhead(1).getText().equals("Not")))) {
				String connector = lookAhead(1).getText();
				consume(); // connector
				Sentence two = parseSentence();
				match(")");
				return new BinarySentence(connector, one, two);
			}
            
		}
		throw new RuntimeException(
                                   " Runtime Exception at Bracketed Expression with token "
                                   + lookAhead(1));
	}
    
	private boolean detectMultiOperator() {
		return (lookAhead(1).getType() == LogicTokenTypes.LPAREN)
        && ((lookAhead(2).getText().equals("AND")) || (lookAhead(2)
                                                       .getText().equals("OR")));
	}
    
	private boolean detectBracket() {
		return lookAhead(1).getType() == LogicTokenTypes.LPAREN;
	}
    
	private boolean detectAtomicSentence() {
		int type = lookAhead(1).getType();
		return (type == LogicTokenTypes.TRUE)
        || (type == LogicTokenTypes.FALSE)
        || (type == LogicTokenTypes.SYMBOL);
	}
}
interface PLVisitor extends Visitor {
	public Object visitSymbol(Symbol s, Object arg);
    
	public Object visitTrueSentence(TrueSentence ts, Object arg);
    
	public Object visitFalseSentence(FalseSentence fs, Object arg);
    
	public Object visitNotSentence(UnarySentence fs, Object arg);
    
	public Object visitBinarySentence(BinarySentence fs, Object arg);
    
	public Object visitMultiSentence(MultiSentence fs, Object arg);
}
abstract class AtomicSentence extends Sentence {
    
}
class BinarySentence extends ComplexSentence {
	private String operator;
    
	private Sentence first;
    
	private Sentence second;
    
	public BinarySentence(String operator, Sentence first, Sentence second) {
		this.operator = operator;
		this.first = first;
		this.second = second;
        
	}
    
	public Sentence getFirst() {
		return first;
	}
    
	public String getOperator() {
		return operator;
	}
    
	public Sentence getSecond() {
		return second;
	}
    
	@Override
	public boolean equals(Object o) {
        
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		BinarySentence bs = (BinarySentence) o;
		return ((bs.getOperator().equals(getOperator()))
				&& (bs.getFirst().equals(first)) && (bs.getSecond()
                                                     .equals(second)));
        
	}
    
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + first.hashCode();
		result = 37 * result + second.hashCode();
		return result;
	}
    
	@Override
	public String toString() {
		return " ( " + first.toString() + " " + operator + " "
        + second.toString() + " )";
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitBinarySentence(this, arg);
	}
    
	public boolean isOrSentence() {
		return (getOperator().equals("OR"));
	}
    
	public boolean isAndSentence() {
		return (getOperator().equals("AND"));
	}
    
	public boolean isImplication() {
		return (getOperator().equals("=>"));
	}
    
	public boolean isBiconditional() {
		return (getOperator().equals("<=>"));
	}
    
	public boolean firstTermIsAndSentence() {
		return (getFirst() instanceof BinarySentence)
        && (((BinarySentence) getFirst()).isAndSentence());
	}
    
	public boolean secondTermIsAndSentence() {
		return (getSecond() instanceof BinarySentence)
        && (((BinarySentence) getSecond()).isAndSentence());
	}
}
abstract class ComplexSentence extends Sentence {
    
}
class FalseSentence extends AtomicSentence {
	@Override
	public String toString() {
		return "FALSE";
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitFalseSentence(this, arg);
	}
}
class MultiSentence extends ComplexSentence {
	private String operator;
    
	private List<Sentence> sentences;
    
	public MultiSentence(String operator, List<Sentence> sentences) {
		this.operator = operator;
		this.sentences = sentences;
	}
    
	public String getOperator() {
		return operator;
	}
    
	public List<Sentence> getSentences() {
		return sentences;
	}
    
	@Override
	public boolean equals(Object o) {
        
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		MultiSentence sen = (MultiSentence) o;
		return ((sen.getOperator().equals(getOperator())) && (sen
                                                              .getSentences().equals(getSentences())));
        
	}
    
	@Override
	public int hashCode() {
		int result = 17;
		for (Sentence s : sentences) {
			result = 37 * result + s.hashCode();
		}
		return result;
	}
    
	@Override
	public String toString() {
		String part1 = "( " + getOperator() + " ";
		for (int i = 0; i < getSentences().size(); i++) {
			part1 = part1 + sentences.get(i).toString() + " ";
		}
		return part1 + " ) ";
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitMultiSentence(this, arg);
	}
}
abstract class Sentence implements ParseTreeNode {
    
	public abstract Object accept(PLVisitor plv, Object arg);
}
class Symbol extends AtomicSentence {
	private String value;
    
	public Symbol(String value) {
		this.value = value;
	}
    
	public String getValue() {
		return value;
	}
    
	@Override
	public boolean equals(Object o) {
        
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		Symbol sym = (Symbol) o;
		return (sym.getValue().equals(getValue()));
        
	}
    
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + value.hashCode();
		return result;
	}
    
	@Override
	public String toString() {
		return getValue();
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitSymbol(this, arg);
	}
}
class SymbolComparator implements Comparator<Symbol> {
    
	public int compare(Symbol one, Symbol two) {
		return one.getValue().compareTo(two.getValue());
	}
}
class TrueSentence extends AtomicSentence {
    
	@Override
	public String toString() {
		return "TRUE";
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitTrueSentence(this, arg);
	}
}
class UnarySentence extends ComplexSentence {
	private Sentence negated;
    
	public Sentence getNegated() {
		return negated;
	}
    
	public UnarySentence(Sentence negated) {
		this.negated = negated;
	}
    
	@Override
	public boolean equals(Object o) {
        
		if (this == o) {
			return true;
		}
		if ((o == null) || (this.getClass() != o.getClass())) {
			return false;
		}
		UnarySentence ns = (UnarySentence) o;
		return (ns.negated.equals(negated));
        
	}
    
	@Override
	public int hashCode() {
		int result = 17;
		result = 37 * result + negated.hashCode();
		return result;
	}
    
	@Override
	public String toString() {
		return " ( NOT " + negated.toString() + " ) ";
	}
    
	@Override
	public Object accept(PLVisitor plv, Object arg) {
		return plv.visitNotSentence(this, arg);
	}
}
class Converter<T> {
    
	/**
	 * Converts a Set into a List
	 *
	 * @param set
	 *            a collection of unique objects
	 *
	 * @return a new list containing the elements of the specified set, in the
	 *         order they are returned by the set's iterator.
	 */
	public List<T> setToList(Set<T> set) {
		List<T> retVal = new ArrayList<T>(set);
		return retVal;
	}
    
	/**
	 * Converts a List into a Set
	 *
	 * @param l
	 *            a list of objects, possibly containing duplicates
	 * @return a new set containing the unique elements of the specified list.
	 */
	public Set<T> listToSet(List<T> l) {
		Set<T> retVal = new HashSet<T>(l);
		return retVal;
	}
}

class SetOps {
    
	/**
	 *
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the union of s1 and s2. (The union of two sets is the set
	 *         containing all of the elements contained in either set.)
	 */
	public static <T> Set<T> union(Set<T> s1, Set<T> s2) {
		Set<T> union = new LinkedHashSet<T>(s1);
		union.addAll(s2);
		return union;
	}
    
	/**
	 *
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the intersection of s1 and s2. (The intersection of two sets is
	 *         the set containing only the elements common to both sets.)
	 */
	public static <T> Set<T> intersection(Set<T> s1, Set<T> s2) {
		Set<T> intersection = new LinkedHashSet<T>(s1);
		intersection.retainAll(s2);
		return intersection;
	}
    
	/**
	 *
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the (asymmetric) set difference of s1 and s2. (For example, the
	 *         set difference of s1 minus s2 is the set containing all of the
	 *         elements found in s1 but not in s2.)
	 */
	public static <T> Set<T> difference(Set<T> s1, Set<T> s2) {
		Set<T> difference = new LinkedHashSet<T>(s1);
		difference.removeAll(s2);
		return difference;
	}
}

class Util {
	public static final String NO = "No";
	public static final String YES = "Yes";
	//
	private static Random _r = new Random();
    
	/**
	 * Get the first element from a list.
	 *
	 * @param l
	 *            the list the first element is to be extracted from.
	 * @return the first element of the passed in list.
	 */
	public static <T> T first(List<T> l) {
		return l.get(0);
	}
    
	/**
	 * Get a sublist of all of the elements in the list except for first.
	 *
	 * @param l
	 *            the list the rest of the elements are to be extracted from.
	 * @return a list of all of the elements in the passed in list except for
	 *         the first element.
	 */
	public static <T> List<T> rest(List<T> l) {
		return l.subList(1, l.size());
	}
    
	/**
	 * Create a Map<K, V> with the passed in keys having their values
	 * initialized to the passed in value.
	 *
	 * @param keys
	 *            the keys for the newly constructed map.
	 * @param value
	 *            the value to be associated with each of the maps keys.
	 * @return a map with the passed in keys initialized to value.
	 */
	public static <K, V> Map<K, V> create(Collection<K> keys, V value) {
		Map<K, V> map = new LinkedHashMap<K, V>();
        
		for (K k : keys) {
			map.put(k, value);
		}
        
		return map;
	}
    
	/**
	 * Randomly select an element from a list.
	 *
	 * @param <T>
	 *            the type of element to be returned from the list l.
	 * @param l
	 *            a list of type T from which an element is to be selected
	 *            randomly.
	 * @return a randomly selected element from l.
	 */
	public static <T> T selectRandomlyFromList(List<T> l) {
		return l.get(_r.nextInt(l.size()));
	}
    
	public static boolean randomBoolean() {
		int trueOrFalse = _r.nextInt(2);
		return (!(trueOrFalse == 0));
	}
    
	public static double[] normalize(double[] probDist) {
		int len = probDist.length;
		double total = 0.0;
		for (double d : probDist) {
			total = total + d;
		}
        
		double[] normalized = new double[len];
		if (total != 0) {
			for (int i = 0; i < len; i++) {
				normalized[i] = probDist[i] / total;
			}
		}
        
		return normalized;
	}
    
	public static List<Double> normalize(List<Double> values) {
		double[] valuesAsArray = new double[values.size()];
		for (int i = 0; i < valuesAsArray.length; i++) {
			valuesAsArray[i] = values.get(i);
		}
		double[] normalized = normalize(valuesAsArray);
		List<Double> results = new ArrayList<Double>();
		for (int i = 0; i < normalized.length; i++) {
			results.add(normalized[i]);
		}
		return results;
	}
    
	public static int min(int i, int j) {
		return (i > j ? j : i);
	}
    
	public static int max(int i, int j) {
		return (i < j ? j : i);
	}
    
	public static int max(int i, int j, int k) {
		return max(max(i, j), k);
	}
    
	public static int min(int i, int j, int k) {
		return min(min(i, j), k);
	}
    
	public static <T> T mode(List<T> l) {
		Hashtable<T, Integer> hash = new Hashtable<T, Integer>();
		for (T obj : l) {
			if (hash.containsKey(obj)) {
				hash.put(obj, hash.get(obj).intValue() + 1);
			} else {
				hash.put(obj, 1);
			}
		}
        
		T maxkey = hash.keySet().iterator().next();
		for (T key : hash.keySet()) {
			if (hash.get(key) > hash.get(maxkey)) {
				maxkey = key;
			}
		}
		return maxkey;
	}
    
	public static String[] yesno() {
		return new String[] { YES, NO };
	}
    
	public static double log2(double d) {
		return Math.log(d) / Math.log(2);
	}
    
	public static double information(double[] probabilities) {
		double total = 0.0;
		for (double d : probabilities) {
			total += (-1.0 * log2(d) * d);
		}
		return total;
	}
    
	public static <T> List<T> removeFrom(List<T> list, T member) {
		List<T> newList = new ArrayList<T>(list);
		newList.remove(member);
		return newList;
	}
    
	public static <T extends Number> double sumOfSquares(List<T> list) {
		double accum = 0;
		for (T item : list) {
			accum = accum + (item.doubleValue() * item.doubleValue());
		}
		return accum;
	}
    
	public static String ntimes(String s, int n) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < n; i++) {
			buf.append(s);
		}
		return buf.toString();
	}
    
	public static void checkForNanOrInfinity(double d) {
		if (Double.isNaN(d)) {
			throw new RuntimeException("Not a Number");
		}
		if (Double.isInfinite(d)) {
			throw new RuntimeException("Infinite Number");
		}
	}
    
	public static int randomNumberBetween(int i, int j) {
		/* i,j bothinclusive */
		return _r.nextInt(j - i + 1) + i;
	}
    
	public static double calculateMean(List<Double> lst) {
		Double sum = 0.0;
		for (Double d : lst) {
			sum = sum + d.doubleValue();
		}
		return sum / lst.size();
	}
    
	public static double calculateStDev(List<Double> values, double mean) {
        
		int listSize = values.size();
        
		Double sumOfDiffSquared = 0.0;
		for (Double value : values) {
			double diffFromMean = value - mean;
			sumOfDiffSquared += ((diffFromMean * diffFromMean) / (listSize - 1));
			// division moved here to avoid sum becoming too big if this
			// doesn't work use incremental formulation
            
		}
		double variance = sumOfDiffSquared;
		// (listSize - 1);
		// assumes at least 2 members in list.
		return Math.sqrt(variance);
	}
    
	public static List<Double> normalizeFromMeanAndStdev(List<Double> values,
                                                         double mean, double stdev) {
		List<Double> normalized = new ArrayList<Double>();
		for (Double d : values) {
			normalized.add((d - mean) / stdev);
		}
		return normalized;
	}
    
	public static double generateRandomDoubleBetween(double lowerLimit,
                                                     double upperLimit) {
        
		return lowerLimit + ((upperLimit - lowerLimit) * _r.nextDouble());
	}
}
class AndDetector implements PLVisitor {
    
	public Object visitSymbol(Symbol s, Object arg) {
        
		return new Boolean(false);
	}
    
	public Object visitTrueSentence(TrueSentence ts, Object arg) {
		return new Boolean(false);
	}
    
	public Object visitFalseSentence(FalseSentence fs, Object arg) {
		return new Boolean(false);
	}
    
	public Object visitNotSentence(UnarySentence fs, Object arg) {
		return fs.getNegated().accept(this, null);
	}
    
	public Object visitBinarySentence(BinarySentence fs, Object arg) {
		if (fs.isAndSentence()) {
			return new Boolean(true);
		} else {
			boolean first = ((Boolean) fs.getFirst().accept(this, null))
            .booleanValue();
			boolean second = ((Boolean) fs.getSecond().accept(this, null))
            .booleanValue();
			return new Boolean((first || second));
		}
	}
    
	public Object visitMultiSentence(MultiSentence fs, Object arg) {
		throw new RuntimeException("can't handle multisentences");
	}
    
	public boolean containsEmbeddedAnd(Sentence s) {
		return ((Boolean) s.accept(this, null)).booleanValue();
	}
}
class BasicTraverser implements PLVisitor {
    
	public Object visitSymbol(Symbol s, Object arg) {
		return arg;
	}
    
	public Object visitTrueSentence(TrueSentence ts, Object arg) {
		return arg;
	}
    
	public Object visitFalseSentence(FalseSentence fs, Object arg) {
		return arg;
	}
    
	@SuppressWarnings("unchecked")
	public Object visitNotSentence(UnarySentence ns, Object arg) {
		Set s = (Set) arg;
		return SetOps.union(s, (Set) ns.getNegated().accept(this, arg));
	}
    
	@SuppressWarnings("unchecked")
	public Object visitBinarySentence(BinarySentence bs, Object arg) {
		Set s = (Set) arg;
		Set termunion = SetOps.union((Set) bs.getFirst().accept(this, arg),
                                     (Set) bs.getSecond().accept(this, arg));
		return SetOps.union(s, termunion);
	}
    
	public Object visitMultiSentence(MultiSentence fs, Object arg) {
		throw new RuntimeException("Can't handle MultiSentence");
	}
}

class CNFClauseGatherer extends BasicTraverser {
	AndDetector detector;
    
	public CNFClauseGatherer() {
		detector = new AndDetector();
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public Object visitBinarySentence(BinarySentence bs, Object args) {
        
		Set<Sentence> soFar = (Set<Sentence>) args;
        
		if (detector.containsEmbeddedAnd(bs)) {
			processSubTerm(bs.getSecond(), processSubTerm(bs.getFirst(), soFar));
		} else {
			soFar.add(bs);
		}
        
		return soFar;
        
	}
    
	@SuppressWarnings("unchecked")
	public Set<Sentence> getClausesFrom(Sentence sentence) {
		Set<Sentence> set = new HashSet<Sentence>();
		if (sentence instanceof Symbol) {
			set.add(sentence);
		} else if (sentence instanceof UnarySentence) {
			set.add(sentence);
		} else {
			set = (Set<Sentence>) sentence.accept(this, set);
		}
		return set;
	}
    
	//
	// PRIVATE METHODS
	//
	@SuppressWarnings("unchecked")
	private Set<Sentence> processSubTerm(Sentence s, Set<Sentence> soFar) {
		if (detector.containsEmbeddedAnd(s)) {
			return (Set<Sentence>) s.accept(this, soFar);
		} else {
			soFar.add(s);
			return soFar;
		}
	}
}
class CNFTransformer extends AbstractPLVisitor {
	@Override
	public Object visitBinarySentence(BinarySentence bs, Object arg) {
		if (bs.isBiconditional()) {
			return transformBiConditionalSentence(bs);
		} else if (bs.isImplication()) {
			return transformImpliedSentence(bs);
		} else if (bs.isOrSentence()
                   && (bs.firstTermIsAndSentence() || bs.secondTermIsAndSentence())) {
			return distributeOrOverAnd(bs);
		} else {
			return super.visitBinarySentence(bs, arg);
		}
	}
    
	@Override
	public Object visitNotSentence(UnarySentence us, Object arg) {
		return transformNotSentence(us);
	}
    
	/**
	 * Returns the specified sentence in conjunctive normal form.
	 *
	 * @param s
	 *            a sentence of propositional logic
	 *
	 * @return the specified sentence in conjunctive normal form.
	 */
	public Sentence transform(Sentence s) {
		Sentence toTransform = s;
		while (!(toTransform.equals(step(toTransform)))) {
			toTransform = step(toTransform);
		}
        
		return toTransform;
	}
    
	private Sentence step(Sentence s) {
		return (Sentence) s.accept(this, null);
	}
    
	private Sentence transformBiConditionalSentence(BinarySentence bs) {
		Sentence first = new BinarySentence("=>", (Sentence) bs.getFirst()
                                            .accept(this, null), (Sentence) bs.getSecond().accept(this,
                                                                                                  null));
		Sentence second = new BinarySentence("=>", (Sentence) bs.getSecond()
                                             .accept(this, null), (Sentence) bs.getFirst()
                                             .accept(this, null));
		return new BinarySentence("AND", first, second);
	}
    
	private Sentence transformImpliedSentence(BinarySentence bs) {
		Sentence first = new UnarySentence((Sentence) bs.getFirst().accept(
                                                                           this, null));
		return new BinarySentence("OR", first, (Sentence) bs.getSecond()
                                  .accept(this, null));
	}
    
	private Sentence transformNotSentence(UnarySentence us) {
		if (us.getNegated() instanceof UnarySentence) {
			return (Sentence) ((UnarySentence) us.getNegated()).getNegated()
            .accept(this, null);
		} else if (us.getNegated() instanceof BinarySentence) {
			BinarySentence bs = (BinarySentence) us.getNegated();
			if (bs.isAndSentence()) {
				Sentence first = new UnarySentence((Sentence) bs.getFirst()
                                                   .accept(this, null));
				Sentence second = new UnarySentence((Sentence) bs.getSecond()
                                                    .accept(this, null));
				return new BinarySentence("OR", first, second);
			} else if (bs.isOrSentence()) {
				Sentence first = new UnarySentence((Sentence) bs.getFirst()
                                                   .accept(this, null));
				Sentence second = new UnarySentence((Sentence) bs.getSecond()
                                                    .accept(this, null));
				return new BinarySentence("AND", first, second);
			} else {
				return (Sentence) super.visitNotSentence(us, null);
			}
		} else {
			return (Sentence) super.visitNotSentence(us, null);
		}
	}
    
	private Sentence distributeOrOverAnd(BinarySentence bs) {
		BinarySentence andTerm = bs.firstTermIsAndSentence() ? (BinarySentence) bs
        .getFirst() : (BinarySentence) bs.getSecond();
		Sentence otherterm = bs.firstTermIsAndSentence() ? bs.getSecond() : bs
        .getFirst();
		// (alpha or (beta and gamma) = ((alpha or beta) and (alpha or gamma))
		Sentence alpha = (Sentence) otherterm.accept(this, null);
		Sentence beta = (Sentence) andTerm.getFirst().accept(this, null);
		Sentence gamma = (Sentence) andTerm.getSecond().accept(this, null);
		Sentence distributed = new BinarySentence("AND", new BinarySentence(
                                                                            "OR", alpha, beta), new BinarySentence("OR", alpha, gamma));
		return distributed;
	}
}
class NegativeSymbolCollector extends BasicTraverser {
	@SuppressWarnings("unchecked")
	@Override
	public Object visitNotSentence(UnarySentence ns, Object arg) {
		Set<Symbol> s = (Set<Symbol>) arg;
		if (ns.getNegated() instanceof Symbol) {
			s.add((Symbol) ns.getNegated());
		} else {
			s = SetOps
            .union(s, (Set<Symbol>) ns.getNegated().accept(this, arg));
		}
		return s;
	}
    
	@SuppressWarnings("unchecked")
	public Set<Symbol> getNegativeSymbolsIn(Sentence s) {
		return (Set<Symbol>) s.accept(this, new HashSet<Symbol>());
	}
}

class PositiveSymbolCollector extends BasicTraverser {
	@SuppressWarnings("unchecked")
	@Override
	public Object visitSymbol(Symbol symbol, Object arg) {
		Set<Symbol> s = (Set<Symbol>) arg;
		s.add(symbol);// add ALL symbols not discarded by the visitNotSentence
		// mathod
		return arg;
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public Object visitNotSentence(UnarySentence ns, Object arg) {
		Set<Symbol> s = (Set<Symbol>) arg;
		if (ns.getNegated() instanceof Symbol) {
			// do nothing .do NOT add a negated Symbol
		} else {
			s = SetOps
            .union(s, (Set<Symbol>) ns.getNegated().accept(this, arg));
		}
		return s;
	}
    
	@SuppressWarnings("unchecked")
	public Set<Symbol> getPositiveSymbolsIn(Sentence sentence) {
		return (Set<Symbol>) sentence.accept(this, new HashSet<Symbol>());
	}
}
class SymbolClassifier {
    
	public Set<Symbol> getPositiveSymbolsIn(Sentence sentence) {
		return new PositiveSymbolCollector().getPositiveSymbolsIn(sentence);
	}
    
	public Set<Symbol> getNegativeSymbolsIn(Sentence sentence) {
		return new NegativeSymbolCollector().getNegativeSymbolsIn(sentence);
	}
    
	public Set<Symbol> getPureNegativeSymbolsIn(Sentence sentence) {
		Set<Symbol> allNegatives = getNegativeSymbolsIn(sentence);
		Set<Symbol> allPositives = getPositiveSymbolsIn(sentence);
		return SetOps.difference(allNegatives, allPositives);
	}
    
	public Set<Symbol> getPurePositiveSymbolsIn(Sentence sentence) {
		Set<Symbol> allNegatives = getNegativeSymbolsIn(sentence);
		Set<Symbol> allPositives = getPositiveSymbolsIn(sentence);
		return SetOps.difference(allPositives, allNegatives);
	}
    
	public Set<Symbol> getPureSymbolsIn(Sentence sentence) {
		Set<Symbol> allPureNegatives = getPureNegativeSymbolsIn(sentence);
		Set<Symbol> allPurePositives = getPurePositiveSymbolsIn(sentence);
		return SetOps.union(allPurePositives, allPureNegatives);
	}
    
	public Set<Symbol> getImpureSymbolsIn(Sentence sentence) {
		Set<Symbol> allNegatives = getNegativeSymbolsIn(sentence);
		Set<Symbol> allPositives = getPositiveSymbolsIn(sentence);
		return SetOps.intersection(allPositives, allNegatives);
	}
    
	public Set<Symbol> getSymbolsIn(Sentence sentence) {
		return new SymbolCollector().getSymbolsIn(sentence);
	}
}
class SymbolCollector extends BasicTraverser {
    
	@SuppressWarnings("unchecked")
	@Override
	public Object visitSymbol(Symbol s, Object arg) {
		Set<Symbol> symbolsCollectedSoFar = (Set<Symbol>) arg;
		symbolsCollectedSoFar.add(new Symbol(s.getValue()));
		return symbolsCollectedSoFar;
	}
    
	@SuppressWarnings("unchecked")
	public Set<Symbol> getSymbolsIn(Sentence s) {
		if (s == null) {// empty knowledge bases == null fix this later
			return new HashSet<Symbol>();
		}
		return (Set<Symbol>) s.accept(this, new HashSet<Symbol>());
	}
}

