package com.walmart.productgenome.matching.models.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;

/** 
 * Interface to something that can load Table from an input source in some
 * format.
 *
 * @author Sanjib Das
 */
public interface Loader {
  
  /** The retrieval modes */
  public enum Mode {
	  NONE,
	  BATCH,
	  INCREMENTAL
  }
  
  /**
   * Sets the retrieval mode. Note: Some loaders may not be
   * able to implement incremental loading.
   *
   * @param mode the retrieval mode
   */
  void setRetrieval(Mode mode);

  /**
   * Resets the Loader object ready to begin loading.
   * If there is an existing source, implementations should
   * attempt to reset in such a fashion as to be able to
   * load from the beginning of the source.
   * @throws Exception if Loader can't be reset for some reason.
   */
  void reset() throws Exception;

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied File object.
   *
   * @param file the File
   * @throws IOException if an error occurs during loading from a File.
   *
   */
  void setSource(File file) throws IOException;

  /**
   * Resets the Loader object and sets the source of the data set to be 
   * the supplied InputStream.
   *
   * @param input the source InputStream
   * @throws IOException if this Loader doesn't support loading from a File.
   */
  void setSource(InputStream input) throws IOException;

  /**
   * Determines and returns (if possible) the structure (internally the 
   * metadata) of the data set as an empty Table.
   *
   * @return the structure of the data set as an empty Table
   * @throws IOException if there is no source or parsing fails
   */
  Table getStructure() throws IOException;

  /**
   * Return the complete Table. If the structure hasn't yet been determined
   * by a call to getStructure then the method should do so before processing
   * the rest of the data set.
   *
   * @return the full data set as a Table object
   * @throws IOException if there is an error during parsing or if 
   * getNextInstance has been called on this source (either incremental
   * or batch loading can be used, not both).
   */
  Table getTable() throws IOException;

  /**
   * Read the table incrementally---get the next tuple in the table
   * or return null if there are no
   * more tuples to get. If the structure hasn't been 
   * determined yet by a call to getStructure then method should do so before
   * returning the next tuple in the table.
   *
   * If it is not possible to read the table incrementally (ie. in cases
   * where the table structure cannot be fully established before all
   * tuples have been seen) then an exception should be thrown.
   *
   * @param structure the table metadata information
   * @return the next tuple in the table as a Tuple object or null
   * if there are no more tuples to be read
   * @throws IOException if there is an error during parsing or if
   * getTable has been called on this source (either incremental
   * or batch loading can be used, not both).
   */
  Tuple getNextTuple(Table structure) throws IOException;
}





