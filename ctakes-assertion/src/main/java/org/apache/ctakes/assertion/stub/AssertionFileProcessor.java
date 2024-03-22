package org.apache.ctakes.assertion.stub;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

public class AssertionFileProcessor {

	static private final Logger LOGGER = Logger.getLogger( "AssertionFileProcessor" );

	public List<Annotation> processAnnotationFile(File assertionFile) {
		LOGGER.warn("This class cannot be used until CTAKES-76 is implemented.");
		return null;
	}

}
