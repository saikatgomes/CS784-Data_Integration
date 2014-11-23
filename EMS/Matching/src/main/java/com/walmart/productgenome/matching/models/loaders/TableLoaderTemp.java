package com.walmart.productgenome.matching.models.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;
import com.walmart.productgenome.matching.models.loaders.Loader.Mode;

/**
 * Reads a source that is in .table format.
 *
 * @author Sanjib Das
 * @see Loader
 */
public class TableLoaderTemp {

	/** The reader for the source file. */
	private Reader m_sourceReader = null;

	/** The parser for the .table file */
	private TableReader m_TableReader = null;
	
	private Table m_structure = null;
	private String m_File = null;
	private Mode retrievalMode = null;
		  
	/**
	   * Reads data from a .table file, either in incremental or batch mode. <p/>
	   *
	   * Typical code for batch usage:
	   * <pre>
	   * BufferedReader reader = new BufferedReader(new FileReader("/some/where/file.table"));
	   * TableReader tableReader = new TableReader(reader);
	   * Table table = tableReader.getTable();
	   * </pre>
	   *
	   * Typical code for incremental usage:
	   * <pre>
	   * BufferedReader reader = new BufferedReader(new FileReader("/some/where/file.table"));
	   * TableReader tableReader = new TableReader(reader, 1000);
	   * Table table = tableReader.getStructure();
	   * Tuple tuple;
	   * while ((tuple = tableReader.readTuple(table)) != null) {
	   *   table.addTuple(tuple);
	   * }
	   * </pre>
	   *
	   * @author Sanjib Das
	   */
	  public static class TableReader {

	    /** the actual table */
	    protected Table m_Data;

	    /** the number of lines read so far */
	    protected int m_Lines;

	    /**
	     * Reads the table completely from the reader. The table can be accessed
	     * via the <code>getTable()</code> method.
	     *
	     * @param reader		the reader to use
	     * @throws IOException	if something goes wrong
	     * @see			#getData()
	     */
	    public TableReader(Reader reader) throws IOException {
	    
	      readInfo(1000);
	     
	      Tuple tuple;
	      while ((tuple = readTuple(m_Data)) != null) {
	        m_Data.addTuple(tuple);
	      };
	    }

	    /**
	     * Reads only the info and reserves the specified space for table.
	     * Further tuples can be read via <code>readTuple()</code>.
	     *
	     * @param reader			the reader to use
	     * @param capacity 			the capacity of the new table
	     * @throws IOException		if something goes wrong
	     * @throws IllegalArgumentException	if capacity is negative
	     * @see				#getStructure()
	     * @see				#readTuple(Table)
	     */
	    public TableReader(Reader reader, int capacity) throws IOException {
	      if (capacity < 0)
		throw new IllegalArgumentException("Capacity has to be positive!");

	      readInfo(capacity);
	    }

	    /**
	     * Reads the table without info according to the specified template.
	     * The table can be accessed via the <code>getTable()</code> method.
	     *
	     * @param reader		the reader to use
	     * @param template		the template header
	     * @param lines		the lines read so far
	     * @throws IOException	if something goes wrong
	     * @see			#getData()
	     */
	    public TableReader(Reader reader, Table template, int lines) throws IOException {
	      this(reader, template, lines, 100);

	      Tuple tuple;
	      while ((tuple = readTuple(m_Data)) != null) {
	        m_Data.addTuple(tuple);
	      };
	    }

	    /**
	     * Initializes the reader without reading the header according to the
	     * specified template. The data must be read via the
	     * <code>readTuple()</code> method.
	     *
	     * @param reader		the reader to use
	     * @param template		the template header
	     * @param lines		the lines read so far
	     * @param capacity 		the capacity of the new table
	     * @throws IOException	if something goes wrong
	     * @see			#getData()
	     */
	    public TableReader(Reader reader, Table template, int lines, int capacity) throws IOException {
	      m_Lines     = lines;
	      //TODO: m_Data = new Table(template, capacity);
	    }

	    /**
	     * TODO: returns the current line number
	     *
	     * @return			the current line number
	     */
	    public int getLineNo() {
	      return -1;
	    }

	    /**
	     * Reads a single Tuple and returns it.
	     *
	     * @param structure 	the table info
	     * @return 			null if end of file has been reached
	     * @throws IOException 	if the information is not read
	     * successfully
	     */
	    public Tuple readTuple(Table structure) throws IOException {
	      return readTuple(structure, true);
	    }

	    /**
	     * Reads a single tuple and returns it.
	     *
	     * @param structure 	the table info
	     * @param flag 		if method should test for carriage return after
	     * 				each tuple
	     * @return 			null if end of file has been reached
	     * @throws IOException 	if the information is not read
	     * successfully
	     */
	    public Tuple readTuple(Table structure, boolean flag) throws IOException {
	      return getTuple(structure, flag);
	    }

	    /**
	     * Reads a single tuple and returns it.
	     *
	     * @param structure 	the table info
	     * @param flag 		if method should test for carriage return after
	     * 				each instance
	     * @return 			null if end of file has been reached
	     * @throws IOException 	if the information is not read
	     * 				successfully
	     */
	    protected Tuple getTuple(Table structure, boolean flag) throws IOException {
	      m_Data = structure;
	      // TODO: put the logic here
	      Tuple tuple = null;
	      return tuple;
	    }

	    /**
	     * Reads and stores info of a .table file.
	     *
	     * @param capacity 		the number of tuples to reserve
	     * @throws IOException 	if the information is not read
	     * 				successfully
	     */
	    protected void readInfo(int capacity) throws IOException {
	      m_Lines = 0;
	      String tableName = "";
	      // TODO: m_Data = new Table();
	    }

	    /**
	     * Returns the table info and empty set of tuples
	     *
	     * @return			the table info
	     */
	    public Table getStructure() {
	      return new Table(m_Data);
	    }

	    /**
	     * Returns the table that was read
	     *
	     * @return			the table
	     */
	    public Table getTable() {
	      return m_Data;
	    }

	  }

	  /**
	   * Returns a string describing this Loader
	   * @return a description of the Loader suitable for
	   * displaying in the gui
	   */
	  public String globalInfo() {
	    return "Reads a source that is in .table format";
	  }

	  /**
	   * Resets the Loader ready to read a new .table file or the
	   * same file again.
	   *
	   * @throws IOException if something goes wrong
	   */
	  public void reset() throws IOException {
	    m_structure = null;
	    m_TableReader = null;
	    setRetrievalMode(Mode.NONE);

	    if (m_File != null && !(new File(m_File).isDirectory())) {
	      setFile(new File(m_File));
	    }
	    
	  }

	  /**
	   * get the File specified as the source
	   *
	   * @return the source file
	   */
	  public File retrieveFile() {
	    return new File(m_File);
	  }

	  /**
	   * sets the source File
	   *
	   * @param file the source file
	   * @throws IOException if an error occurs
	   */
	  public void setFile(File file) throws IOException {
	    m_File = file.getPath();
	    // TODO: setSource(file);
	  }
	  
	  /**
	   * Resets the Loader object and sets the source of the data set to be
	   * the supplied InputStream.
	   *
	   * @param in the source InputStream.
	   * @throws IOException always thrown.
	   */
	  public void setSource(InputStream in) throws IOException {
	    m_File = (new File(System.getProperty("user.dir"))).getAbsolutePath();
	    m_sourceReader = new BufferedReader(new InputStreamReader(in));
	  }

	  /**
	   * Determines and returns (if possible) the structure (internally the
	   * header) of the data set as an empty set of instances.
	   *
	   * @return the structure of the data set as an empty set of Instances
	   * @throws IOException if an error occurs
	   */
	  public Table getStructure() throws IOException {

	    if (m_structure == null) {
	      if (m_sourceReader == null) {
	        throw new IOException("No source has been specified");
	      }
	      try {
		m_TableReader = new TableReader(m_sourceReader, 1);
		m_structure  = m_TableReader.getStructure();
	      } catch (Exception ex) {
		throw new IOException("Unable to determine structure as table (Reason: " + ex.toString() + ").");
	      }
	    }

	    return m_structure;
	  }

	  /**
	   * Return the full data set. If the structure hasn't yet been determined
	   * by a call to getStructure then method should do so before processing
	   * the rest of the data set.
	   *
	   * @return the structure of the data set as an empty set of Instances
	   * @throws IOException if there is no source or parsing fails
	   */
	  public Table getTable() throws IOException {

	    Table table = null;
	    try {
	      if (m_sourceReader == null) {
	        throw new IOException("No source has been specified");
	      }
	      if (getRetrievalMode() == Mode.INCREMENTAL) {
	        throw new IOException("Cannot mix getting tuples in both incremental and batch modes");
	      }
	      setRetrievalMode(Mode.BATCH);
	      if (m_structure == null) {
	        getStructure();
	      }

	      // Read all tuples
	      Tuple tuple;
	      table = new Table(m_structure);
	      while ((tuple = m_TableReader.readTuple(m_structure)) != null)
	        table.addTuple(tuple);
	    } finally {
	      // close the stream
	      m_sourceReader.close();
	    }
	    
	    return table;
	  }

	  /**
	   * Read the data set incrementally---get the next instance in the data
	   * set or returns null if there are no
	   * more instances to get. If the structure hasn't yet been
	   * determined by a call to getStructure then method should do so before
	   * returning the next instance in the data set.
	   *
	   * @param structure the dataset header information, will get updated in
	   * case of string or relational attributes
	   * @return the next instance in the data set as an Instance object or null
	   * if there are no more instances to be read
	   * @throws IOException if there is an error during parsing
	   */
	  public Tuple getNextTuple(Table structure) throws IOException {

	    m_structure = structure;

	    if (getRetrievalMode() == Mode.BATCH) {
	      throw new IOException("Cannot mix getting tuples in both incremental and batch modes");
	    }
	    setRetrievalMode(Mode.INCREMENTAL);

	    Tuple current = null;
	    if (m_sourceReader != null)
	      current = m_TableReader.readTuple(m_structure);

	    if ((m_sourceReader != null) && (current == null)) {
	      try {
	        // close the stream
	        m_sourceReader.close();
	        m_sourceReader = null;
	        //        reset();
	      } catch (Exception ex) {
	        ex.printStackTrace();
	      }
	    }
	    return current;
	  }
	  
	  public Mode getRetrievalMode(){
		  return retrievalMode;
	  }
	  
	  public void setRetrievalMode(Mode retrievalMode){
		  this.retrievalMode = retrievalMode;
	  }
}
