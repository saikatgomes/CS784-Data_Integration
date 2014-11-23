package com.walmart.productgenome.matching.models.loaders;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.walmart.productgenome.matching.models.data.Table;
import com.walmart.productgenome.matching.models.data.Tuple;

public abstract class AbstractLoader implements Loader {

	/** The current retrieval mode */
	protected Mode m_retrieval;

	/**
	 * Sets the retrieval mode.
	 *
	 * @param mode the retrieval mode
	 */
	public void setRetrieval(Mode mode) {

		m_retrieval = mode;
	}

	/**
	 * Gets the retrieval mode.
	 *
	 * @return the retrieval mode
	 */
	protected Mode getRetrieval() {

		return m_retrieval;
	}

	/**
	 * Default implementation throws an IOException.
	 *
	 * @param file the File
	 * @exception IOException always
	 */
	public void setSource(File file) throws IOException {
		throw new IOException("Setting File as source not supported");
	}

	/**
	 * Default implementation sets retrieval mode to NONE
	 *
	 * @exception never.
	 */
	public void reset() throws Exception {
		m_retrieval = Mode.NONE;
	}

	/**
	 * Default implementation throws an IOException.
	 *
	 * @param input the input stream
	 * @exception IOException always
	 */
	public void setSource(InputStream input) throws IOException {
		throw new IOException("Setting InputStream as source not supported");
	}

	/*
	 * To be overridden.
	 */
	public abstract Table getStructure() throws IOException;

	/*
	 * To be overridden.
	 */
	public abstract Table getTable() throws IOException;

	/*
	 * To be overridden.
	 */
	public abstract Tuple getNextTuple(Table structure) throws IOException;
}
